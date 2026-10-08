package com.royalcitrustiles.puzzle.domain.model

data class MatchOutcome(
    val cleared: Set<BoardPosition>,
    val longestLine: Int,
    val clearedByKind: Map<CitrusKind, Int>
) {
    val isEmpty: Boolean get() = cleared.isEmpty()

    companion object {
        val NONE = MatchOutcome(emptySet(), 0, emptyMap())
    }
}
