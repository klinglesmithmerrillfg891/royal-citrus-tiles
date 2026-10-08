package com.royalcitrustiles.puzzle.core.di

import android.content.Context
import com.royalcitrustiles.puzzle.core.config.GameConfig
import com.royalcitrustiles.puzzle.data.local.PreferencesStorage
import com.royalcitrustiles.puzzle.data.repository.BoardRepositoryImpl
import com.royalcitrustiles.puzzle.data.repository.ProgressRepositoryImpl
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

object ServiceLocator {

    private var storage: PreferencesStorage? = null
    private var progress: ProgressRepository? = null
    private var boards: BoardRepository? = null

    val resolveMatchesUseCase: ResolveMatchesUseCase = ResolveMatchesUseCase()

    val swapTilesUseCase: SwapTilesUseCase = SwapTilesUseCase()

    val hasLegalMoveUseCase: HasLegalMoveUseCase =
        HasLegalMoveUseCase(resolveMatchesUseCase, swapTilesUseCase)

    val generateBoardUseCase: GenerateBoardUseCase =
        GenerateBoardUseCase(resolveMatchesUseCase, hasLegalMoveUseCase)

    val applyGravityUseCase: ApplyGravityUseCase = ApplyGravityUseCase()

    val evaluateOrderUseCase: EvaluateOrderUseCase = EvaluateOrderUseCase()

    val scoreCalculator: ScoreCalculator = ScoreCalculator(
        baseTileScore = GameConfig.BASE_TILE_SCORE,
        crownTileScore = GameConfig.CROWN_TILE_SCORE,
        movesBonus = GameConfig.MOVES_BONUS,
        comboCap = GameConfig.COMBO_CAP
    )

    fun init(context: Context) {
        if (storage == null) {
            val created = PreferencesStorage(context)
            storage = created
            progress = ProgressRepositoryImpl(created)
        }
        if (boards == null) {
            boards = BoardRepositoryImpl()
        }
    }

    val progressRepository: ProgressRepository
        get() = progress ?: throw IllegalStateException(NOT_READY)

    val boardRepository: BoardRepository
        get() = boards ?: throw IllegalStateException(NOT_READY)

    val saveProgressUseCase: SaveProgressUseCase
        get() = SaveProgressUseCase(progressRepository)

    val unlockSealUseCase: UnlockSealUseCase
        get() = UnlockSealUseCase(progressRepository)

    val isReady: Boolean
        get() = progress != null && boards != null

    private const val NOT_READY = "ServiceLocator.init must run before dependencies are used"
}
