package com.royalcitrustiles.puzzle.domain.model

data class ConservatoryWing(
    val index: Int,
    val name: String,
    val startingMoves: Int,
    val signatureKind: CitrusKind,
    val order: DeliveryOrder
)
