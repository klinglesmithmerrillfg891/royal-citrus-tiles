package com.royalcitrustiles.puzzle.domain.usecase

import com.royalcitrustiles.puzzle.domain.model.CitrusKind
import com.royalcitrustiles.puzzle.domain.model.DeliveryOrder

class EvaluateOrderUseCase {

    operator fun invoke(order: DeliveryOrder, clearedByKind: Map<CitrusKind, Int>): DeliveryOrder {
        if (clearedByKind.isEmpty()) {
            return order
        }
        val updated = order.requirements.map { requirement ->
            val gained = clearedByKind[requirement.kind] ?: 0
            if (gained == 0) {
                requirement
            } else {
                val total = requirement.collected + gained
                requirement.copy(collected = if (total > requirement.required) requirement.required else total)
            }
        }
        return order.copy(requirements = updated)
    }
}
