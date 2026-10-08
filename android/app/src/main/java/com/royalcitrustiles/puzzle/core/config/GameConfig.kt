package com.royalcitrustiles.puzzle.core.config

object GameConfig {

    const val LOADER_DURATION_MS = 8000L
    const val ROUND_BACKSTOP_MS = 32000L
    const val CELEBRATION_MS = 600L
    const val BURST_MS = 240L
    const val DROP_MS = 220L
    const val SWAP_MS = 200L
    const val RESHUFFLE_MS = 400L

    const val BOARD_COLS = 6
    const val BOARD_ROWS = 7
    const val BOARD_FRAME_DP = 8
    const val BOARD_MAX_WIDTH_DP = 380
    const val BOARD_SIDE_MARGIN_DP = 32

    const val MIN_MATCH = 3
    const val CROWN_MATCH = 4
    const val CROWN_CHARGE_MATCH = 5
    const val STARTING_CROWNS = 2

    const val BASE_TILE_SCORE = 40
    const val CROWN_TILE_SCORE = 55
    const val MOVES_BONUS = 120
    const val COMBO_CAP = 3.0f

    const val WING_COUNT = 6
    const val MAX_REQUIREMENT_CHIPS = 3
}
