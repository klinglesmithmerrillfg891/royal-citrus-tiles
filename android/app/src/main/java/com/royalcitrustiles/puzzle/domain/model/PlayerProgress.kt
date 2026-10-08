package com.royalcitrustiles.puzzle.domain.model

data class PlayerProgress(
    val royalFavour: Int = 0,
    val sealCount: Int = 0,
    val bestStreak: Int = 0,
    val currentStreak: Int = 0,
    val unlockedWing: Int = 0
)
