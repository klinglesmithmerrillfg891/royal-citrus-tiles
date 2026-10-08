package com.royalcitrustiles.puzzle.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.core.config.GameConfig
import com.royalcitrustiles.puzzle.domain.model.Board
import com.royalcitrustiles.puzzle.domain.model.BoardPosition
import com.royalcitrustiles.puzzle.domain.model.CitrusKind
import com.royalcitrustiles.puzzle.domain.model.ConservatoryWing
import com.royalcitrustiles.puzzle.domain.model.DeliveryOrder
import com.royalcitrustiles.puzzle.domain.model.MatchOutcome
import com.royalcitrustiles.puzzle.domain.model.RoundResult
import com.royalcitrustiles.puzzle.domain.repository.BoardRepository
import com.royalcitrustiles.puzzle.domain.repository.ProgressRepository
import com.royalcitrustiles.puzzle.domain.usecase.ApplyGravityUseCase
import com.royalcitrustiles.puzzle.domain.usecase.EvaluateOrderUseCase
import com.royalcitrustiles.puzzle.domain.usecase.GenerateBoardUseCase
import com.royalcitrustiles.puzzle.domain.usecase.HasLegalMoveUseCase
import com.royalcitrustiles.puzzle.domain.usecase.ResolveMatchesUseCase
import com.royalcitrustiles.puzzle.domain.usecase.SaveProgressUseCase
import com.royalcitrustiles.puzzle.domain.usecase.ScoreCalculator
import com.royalcitrustiles.puzzle.domain.usecase.SwapTilesUseCase
import com.royalcitrustiles.puzzle.domain.usecase.UnlockSealUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val boardRepository: BoardRepository,
    private val progressRepository: ProgressRepository,
    private val generateBoard: GenerateBoardUseCase,
    private val swapTiles: SwapTilesUseCase,
    private val resolveMatches: ResolveMatchesUseCase,
    private val applyGravity: ApplyGravityUseCase,
    private val hasLegalMove: HasLegalMoveUseCase,
    private val evaluateOrder: EvaluateOrderUseCase,
    private val scoreCalculator: ScoreCalculator,
    private val saveProgress: SaveProgressUseCase,
    private val unlockSeal: UnlockSealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val random = Random(System.nanoTime())

    private var wing: ConservatoryWing? = null
    private var board: Board? = null
    private var order: DeliveryOrder? = null
    private var selected: BoardPosition? = null
    private var phase = GameUiState.Phase.IDLE
    private var movesLeft = 0
    private var score = 0
    private var bestCombo = 1
    private var crowns = 0
    private var sealCount = 0
    private var reducedMotion = false
    private var announcementRes: Int? = null
    private var rejectedFirst: BoardPosition? = null
    private var rejectedSecond: BoardPosition? = null

    private var backstop: Job? = null
    private var cascade: Job? = null
    private var started = false
    private var closing = false
    private var resultEmitted = false

    fun start(wingIndex: Int) {
        if (started) {
            return
        }
        started = true
        reducedMotion = progressRepository.loadSettings().reducedMotion
        sealCount = progressRepository.unlockedSealIndices().size
        val selectedWing = boardRepository.wingAt(wingIndex)
        wing = selectedWing
        order = selectedWing.order
        movesLeft = selectedWing.startingMoves
        score = 0
        bestCombo = 1
        crowns = GameConfig.STARTING_CROWNS
        selected = null
        phase = GameUiState.Phase.IDLE
        if (!layOutBoard()) {
            return
        }
        publish()
        armBackstop()
    }

    fun retry() {
        if (resultEmitted) {
            return
        }
        closing = false
        phase = GameUiState.Phase.IDLE
        if (!layOutBoard()) {
            return
        }
        publish()
        armBackstop()
    }

    fun onTileTapped(position: BoardPosition) {
        if (closing || phase != GameUiState.Phase.IDLE) {
            return
        }
        val current = board ?: return
        if (!current.holds(position)) {
            return
        }
        rejectedFirst = null
        rejectedSecond = null
        val previous = selected
        if (previous == null || previous == position || !previous.isAdjacentTo(position)) {
            selected = if (previous == position) null else position
            publish()
            return
        }
        selected = null
        val swapped = swapTiles(current, previous, position)
        val outcome = resolveMatches(swapped, GameConfig.MIN_MATCH)
        if (outcome.isEmpty) {
            rejectedFirst = previous
            rejectedSecond = position
            publish()
            return
        }
        board = swapped
        movesLeft -= 1
        runCascade(outcome)
    }

    fun onRejectionConsumed() {
        if (rejectedFirst == null && rejectedSecond == null) {
            return
        }
        rejectedFirst = null
        rejectedSecond = null
        publish()
    }

    fun onUseCrown() {
        if (closing || phase != GameUiState.Phase.IDLE || crowns <= 0) {
            return
        }
        val current = board ?: return
        val activeOrder = order ?: return
        crowns -= 1
        selected = null
        val row = mostUsefulRow(current, activeOrder)
        val cleared = LinkedHashSet<BoardPosition>()
        val byKind = LinkedHashMap<CitrusKind, Int>()
        for (col in 0 until current.cols) {
            val cell = BoardPosition(row, col)
            cleared.add(cell)
            val kind = current.tileAt(cell).kind
            byKind[kind] = (byKind[kind] ?: 0) + 1
        }
        runCascade(MatchOutcome(cleared, current.cols, byKind), crownCleared = true)
    }

    fun onDeliver() {
        if (closing) {
            return
        }
        val activeOrder = order ?: return
        if (!activeOrder.isComplete) {
            return
        }
        finishRound(true)
    }

    private fun layOutBoard(): Boolean {
        val generated = try {
            generateBoard(
                GameConfig.BOARD_ROWS,
                GameConfig.BOARD_COLS,
                GameConfig.MIN_MATCH,
                random
            )
        } catch (e: Exception) {
            null
        }
        if (generated == null) {
            _uiState.value = GameUiState.Error
            return false
        }
        board = generated
        return true
    }

    private fun armBackstop() {
        backstop?.cancel()
        backstop = viewModelScope.launch {
            delay(GameConfig.ROUND_BACKSTOP_MS)
            if (!closing) {
                val activeOrder = order
                finishRound(activeOrder != null && activeOrder.isComplete)
            }
        }
    }

    private fun runCascade(initial: MatchOutcome, crownCleared: Boolean = false) {
        cascade?.cancel()
        cascade = viewModelScope.launch {
            phase = GameUiState.Phase.RESOLVING
            var depth = 1
            var outcome = initial
            var crownStep = crownCleared
            while (!outcome.isEmpty) {
                val working = board ?: break
                val activeOrder = order ?: break
                if (outcome.longestLine >= GameConfig.CROWN_CHARGE_MATCH) {
                    crowns += 1
                }
                score += scoreCalculator.clearScore(outcome.cleared.size, depth, crownStep)
                if (depth > bestCombo) {
                    bestCombo = depth
                }
                order = evaluateOrder(activeOrder, outcome.clearedByKind)
                publish()
                delay(GameConfig.BURST_MS)
                val dropped = applyGravity(working, outcome.cleared, random)
                board = dropped
                publish()
                delay(GameConfig.DROP_MS)
                outcome = resolveMatches(dropped, GameConfig.MIN_MATCH)
                depth += 1
                crownStep = false
            }
            settle()
        }
    }

    private suspend fun settle() {
        if (closing) {
            return
        }
        val activeOrder = order
        if (activeOrder != null && activeOrder.isComplete) {
            finishRound(true)
            return
        }
        if (movesLeft <= 0) {
            finishRound(false)
            return
        }
        var working = board
        if (working != null && !hasLegalMove(working, GameConfig.MIN_MATCH)) {
            phase = GameUiState.Phase.RESHUFFLING
            announcementRes = R.string.game_reshuffled
            publish()
            delay(GameConfig.RESHUFFLE_MS)
            working = try {
                generateBoard(
                    GameConfig.BOARD_ROWS,
                    GameConfig.BOARD_COLS,
                    GameConfig.MIN_MATCH,
                    random
                )
            } catch (e: Exception) {
                working
            }
            board = working
            announcementRes = null
        }
        phase = GameUiState.Phase.IDLE
        publish()
    }

    private fun mostUsefulRow(current: Board, activeOrder: DeliveryOrder): Int {
        val wanted = activeOrder.requirements
            .filter { !it.isSatisfied }
            .map { it.kind }
            .toSet()
        var bestRow = current.rows - 1
        var bestValue = -1
        for (row in 0 until current.rows) {
            var value = 0
            for (col in 0 until current.cols) {
                if (wanted.isEmpty() || wanted.contains(current.tileAt(row, col).kind)) {
                    value += 1
                }
            }
            if (value > bestValue) {
                bestValue = value
                bestRow = row
            }
        }
        return bestRow
    }

    private fun finishRound(success: Boolean) {
        if (closing) {
            return
        }
        closing = true
        backstop?.cancel()
        backstop = null
        selected = null
        phase = GameUiState.Phase.CELEBRATING
        publish()
        viewModelScope.launch {
            delay(GameConfig.CELEBRATION_MS)
            emitResult(success)
        }
    }

    private fun emitResult(success: Boolean) {
        if (resultEmitted) {
            return
        }
        val selectedWing = wing ?: return
        resultEmitted = true
        val safeMoves = if (movesLeft < 0) 0 else movesLeft
        val bonus = scoreCalculator.endOfRoundBonus(safeMoves, success)
        val total = score + bonus
        val seals = if (success) unlockSeal(selectedWing.index) else sealCount
        sealCount = seals
        val result = RoundResult(
            success = success,
            wingIndex = selectedWing.index,
            wingName = selectedWing.name,
            score = total,
            bestCombo = bestCombo,
            movesLeft = safeMoves,
            sealCount = seals
        )
        saveProgress(result, GameConfig.WING_COUNT)
        _uiState.value = GameUiState.Finished(result)
    }

    private fun publish() {
        if (resultEmitted) {
            return
        }
        val current = board ?: return
        val activeOrder = order ?: return
        val selectedWing = wing ?: return
        _uiState.value = GameUiState.Active(
            board = current,
            phase = phase,
            selected = selected,
            movesLeft = if (movesLeft < 0) 0 else movesLeft,
            score = score,
            bestCombo = bestCombo,
            crowns = crowns,
            sealCount = sealCount,
            order = activeOrder,
            wingIndex = selectedWing.index,
            wingName = selectedWing.name,
            wingCount = GameConfig.WING_COUNT,
            canDeliver = activeOrder.isComplete,
            reducedMotion = reducedMotion,
            rejectedFirst = rejectedFirst,
            rejectedSecond = rejectedSecond,
            announcementRes = announcementRes
        )
    }

    override fun onCleared() {
        backstop?.cancel()
        backstop = null
        cascade?.cancel()
        cascade = null
        super.onCleared()
    }
}
