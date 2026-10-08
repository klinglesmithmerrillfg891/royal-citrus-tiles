package com.royalcitrustiles.puzzle.presentation.gameover

import androidx.lifecycle.ViewModel
import com.royalcitrustiles.puzzle.core.config.GameConfig
import com.royalcitrustiles.puzzle.domain.model.RoundResult
import com.royalcitrustiles.puzzle.domain.repository.BoardRepository
import com.royalcitrustiles.puzzle.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val progressRepository: ProgressRepository,
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameOverUiState())
    val uiState: StateFlow<GameOverUiState> = _uiState.asStateFlow()

    private var loaded = false

    fun load(result: RoundResult) {
        if (loaded) {
            return
        }
        loaded = true
        val progress = progressRepository.loadProgress()
        val settings = progressRepository.loadSettings()
        val seal = boardRepository.seals().firstOrNull { it.wingIndex == result.wingIndex }
        val nextWing = if (result.success) {
            if (result.wingIndex + 1 >= GameConfig.WING_COUNT) 0 else result.wingIndex + 1
        } else {
            result.wingIndex
        }
        _uiState.value = GameOverUiState(
            result = result,
            sealTitle = seal?.title ?: result.wingName,
            royalFavour = progress.royalFavour,
            bestStreak = progress.bestStreak,
            nextWingIndex = nextWing,
            reducedMotion = settings.reducedMotion
        )
    }
}
