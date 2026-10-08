package com.royalcitrustiles.puzzle.domain.model

data class OrderRequirement(val kind: CitrusKind, val required: Int, val collected: Int = 0) {

    val isSatisfied: Boolean get() = collected >= required

    val clampedCollected: Int get() = if (collected > required) required else collected

    val percent: Int
        get() = if (required <= 0) 100 else (clampedCollected * 100) / required
}
