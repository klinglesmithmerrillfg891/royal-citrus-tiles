package com.royalcitrustiles.puzzle.domain.model

enum class CitrusKind {
    ORANGE,
    LEMON,
    LIME,
    GRAPEFRUIT,
    MANDARIN,
    CROWN;

    companion object {
        val PLAYABLE: List<CitrusKind> = listOf(ORANGE, LEMON, LIME, GRAPEFRUIT, MANDARIN)
    }
}
