package com.royalcitrustiles.puzzle.domain.model

data class RoundResult(
    val success: Boolean,
    val wingIndex: Int,
    val wingName: String,
    val score: Int,
    val bestCombo: Int,
    val movesLeft: Int,
    val sealCount: Int
)
