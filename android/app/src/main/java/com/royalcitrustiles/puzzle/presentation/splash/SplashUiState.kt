package com.royalcitrustiles.puzzle.presentation.splash

data class SplashUiState(
    val stage: Stage = Stage.OPENING,
    val elapsedMs: Long = 0L,
    val navigateToMenu: Boolean = false
) {

    val isReady: Boolean get() = stage == Stage.READY

    enum class Stage {
        OPENING,
        WARMING,
        POLISHING,
        READY
    }
}
