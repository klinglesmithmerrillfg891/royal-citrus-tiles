package com.royalcitrustiles.puzzle.presentation.menu

import com.royalcitrustiles.puzzle.domain.model.ConservatoryWing

data class MenuUiState(
    val royalFavour: Int = 0,
    val sealCount: Int = 0,
    val bestStreak: Int = 0,
    val activeWingIndex: Int = 0,
    val wingCards: List<WingCard> = emptyList(),
    val activeWing: ConservatoryWing? = null,
    val reducedMotion: Boolean = false
) {

    data class WingCard(val wing: ConservatoryWing, val state: State)

    enum class State {
        RESTORED,
        CURRENT,
        LOCKED
    }
}
