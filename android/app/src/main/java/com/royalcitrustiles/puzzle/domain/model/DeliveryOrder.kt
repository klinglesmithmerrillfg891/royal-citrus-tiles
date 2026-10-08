package com.royalcitrustiles.puzzle.domain.model

data class DeliveryOrder(val title: String, val requirements: List<OrderRequirement>) {

    val isComplete: Boolean get() = requirements.all { it.isSatisfied }

    val satisfiedCount: Int get() = requirements.count { it.isSatisfied }
}
