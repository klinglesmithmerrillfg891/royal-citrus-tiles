package com.royalcitrustiles.puzzle.core.ui

import com.royalcitrustiles.puzzle.R
import com.royalcitrustiles.puzzle.domain.model.CitrusKind

object TileArt {

    fun spriteFor(kind: CitrusKind): Int = when (kind) {
        CitrusKind.ORANGE -> R.drawable.sprite_tile_orange
        CitrusKind.LEMON -> R.drawable.sprite_tile_lemon
        CitrusKind.LIME -> R.drawable.sprite_tile_lime
        CitrusKind.GRAPEFRUIT -> R.drawable.sprite_tile_grapefruit
        CitrusKind.MANDARIN -> R.drawable.sprite_tile_mandarin
        CitrusKind.CROWN -> R.drawable.sprite_tile_crown
    }

    fun backgroundFor(kind: CitrusKind): Int = when (kind) {
        CitrusKind.ORANGE -> R.drawable.tile_bg_orange
        CitrusKind.LEMON -> R.drawable.tile_bg_lemon
        CitrusKind.LIME -> R.drawable.tile_bg_lime
        CitrusKind.GRAPEFRUIT -> R.drawable.tile_bg_grapefruit
        CitrusKind.MANDARIN -> R.drawable.tile_bg_mandarin
        CitrusKind.CROWN -> R.drawable.tile_bg_crown
    }

    fun cardColorFor(kind: CitrusKind): Int = when (kind) {
        CitrusKind.ORANGE -> R.color.tile_orange
        CitrusKind.LEMON -> R.color.tile_lemon
        CitrusKind.LIME -> R.color.tile_lime
        CitrusKind.GRAPEFRUIT -> R.color.tile_grapefruit
        CitrusKind.MANDARIN -> R.color.tile_mandarin
        CitrusKind.CROWN -> R.color.tile_crown
    }

    fun accentColorFor(kind: CitrusKind): Int = when (kind) {
        CitrusKind.ORANGE -> R.color.citrus_orange
        CitrusKind.LEMON -> R.color.royal_gold
        CitrusKind.LIME -> R.color.leaf_green
        CitrusKind.GRAPEFRUIT -> R.color.seville_red
        CitrusKind.MANDARIN -> R.color.citrus_orange_dark
        CitrusKind.CROWN -> R.color.royal_gold
    }

    fun labelFor(kind: CitrusKind): Int = when (kind) {
        CitrusKind.ORANGE -> R.string.kind_orange
        CitrusKind.LEMON -> R.string.kind_lemon
        CitrusKind.LIME -> R.string.kind_lime
        CitrusKind.GRAPEFRUIT -> R.string.kind_grapefruit
        CitrusKind.MANDARIN -> R.string.kind_mandarin
        CitrusKind.CROWN -> R.string.kind_crown
    }
}
