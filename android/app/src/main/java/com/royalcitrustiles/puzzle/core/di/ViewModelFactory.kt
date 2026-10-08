package com.royalcitrustiles.puzzle.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.royalcitrustiles.puzzle.presentation.game.GameViewModel
import com.royalcitrustiles.puzzle.presentation.gameover.GameOverViewModel
import com.royalcitrustiles.puzzle.presentation.menu.MenuViewModel
import com.royalcitrustiles.puzzle.presentation.splash.SplashViewModel

class ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.boardRepository
            )

            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                ServiceLocator.boardRepository,
                ServiceLocator.progressRepository,
                ServiceLocator.generateBoardUseCase,
                ServiceLocator.swapTilesUseCase,
                ServiceLocator.resolveMatchesUseCase,
                ServiceLocator.applyGravityUseCase,
                ServiceLocator.hasLegalMoveUseCase,
                ServiceLocator.evaluateOrderUseCase,
                ServiceLocator.scoreCalculator,
                ServiceLocator.saveProgressUseCase,
                ServiceLocator.unlockSealUseCase
            )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.boardRepository
            )

            else -> throw IllegalArgumentException("Unsupported ViewModel: " + modelClass.name)
        }
        return created as T
    }
}
