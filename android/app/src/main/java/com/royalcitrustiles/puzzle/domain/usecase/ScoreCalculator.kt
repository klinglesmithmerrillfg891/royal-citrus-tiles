package com.royalcitrustiles.puzzle.domain.usecase

class ScoreCalculator(
    private val baseTileScore: Int,
    private val crownTileScore: Int,
    private val movesBonus: Int,
    private val comboCap: Float
) {

    fun comboMultiplier(cascadeDepth: Int): Float {
        val raw = 1.0f + (cascadeDepth - 1) * 0.5f
        return if (raw > comboCap) comboCap else if (raw < 1.0f) 1.0f else raw
    }

    fun clearScore(tileCount: Int, cascadeDepth: Int, crownCleared: Boolean): Int {
        val unit = if (crownCleared) crownTileScore else baseTileScore
        return (tileCount * unit * comboMultiplier(cascadeDepth)).toInt()
    }

    fun endOfRoundBonus(movesLeft: Int, success: Boolean): Int =
        if (success && movesLeft > 0) movesLeft * movesBonus else 0
}
