package com.royalcitrustiles.puzzle.presentation.gameover

import com.royalcitrustiles.puzzle.domain.model.RoundResult

data class GameOverUiState(
    val result: RoundResult? = null,
    val sealTitle: String = "",
    val royalFavour: Int = 0,
    val bestStreak: Int = 0,
    val nextWingIndex: Int = 0,
    val reducedMotion: Boolean = false
) {
    val isSuccess: Boolean get() = result?.success == true
}
