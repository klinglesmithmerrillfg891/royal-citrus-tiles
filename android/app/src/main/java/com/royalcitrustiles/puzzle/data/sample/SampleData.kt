package com.royalcitrustiles.puzzle.data.sample

import com.royalcitrustiles.puzzle.domain.model.CitrusKind
import com.royalcitrustiles.puzzle.domain.model.ConservatoryWing
import com.royalcitrustiles.puzzle.domain.model.DeliveryOrder
import com.royalcitrustiles.puzzle.domain.model.OrderRequirement
import com.royalcitrustiles.puzzle.domain.model.RoyalSeal

object SampleData {

    val wings: List<ConservatoryWing> = listOf(
        ConservatoryWing(
            index = 0,
            name = "PALM COURT",
            startingMoves = 24,
            signatureKind = CitrusKind.ORANGE,
            order = DeliveryOrder(
                title = "PALACE BREAKFAST",
                requirements = listOf(
                    OrderRequirement(CitrusKind.ORANGE, 12),
                    OrderRequirement(CitrusKind.LEMON, 8)
                )
            )
        ),
        ConservatoryWing(
            index = 1,
            name = "LEMON GALLERY",
            startingMoves = 22,
            signatureKind = CitrusKind.LEMON,
            order = DeliveryOrder(
                title = "MARMALADE KITCHEN",
                requirements = listOf(
                    OrderRequirement(CitrusKind.ORANGE, 14),
                    OrderRequirement(CitrusKind.MANDARIN, 10)
                )
            )
        ),
        ConservatoryWing(
            index = 2,
            name = "LIME TERRACE",
            startingMoves = 20,
            signatureKind = CitrusKind.LIME,
            order = DeliveryOrder(
                title = "SUMMER INFUSION",
                requirements = listOf(
                    OrderRequirement(CitrusKind.LIME, 10),
                    OrderRequirement(CitrusKind.LEMON, 10)
                )
            )
        ),
        ConservatoryWing(
            index = 3,
            name = "POMELO HALL",
            startingMoves = 20,
            signatureKind = CitrusKind.GRAPEFRUIT,
            order = DeliveryOrder(
                title = "ROYAL PRESERVE",
                requirements = listOf(
                    OrderRequirement(CitrusKind.GRAPEFRUIT, 12),
                    OrderRequirement(CitrusKind.ORANGE, 8),
                    OrderRequirement(CitrusKind.LIME, 6)
                )
            )
        ),
        ConservatoryWing(
            index = 4,
            name = "MANDARIN ORANGERY",
            startingMoves = 18,
            signatureKind = CitrusKind.MANDARIN,
            order = DeliveryOrder(
                title = "CORONATION PUNCH",
                requirements = listOf(
                    OrderRequirement(CitrusKind.MANDARIN, 14),
                    OrderRequirement(CitrusKind.LIME, 12),
                    OrderRequirement(CitrusKind.GRAPEFRUIT, 8)
                )
            )
        ),
        ConservatoryWing(
            index = 5,
            name = "BERGAMOT ATRIUM",
            startingMoves = 18,
            signatureKind = CitrusKind.LEMON,
            order = DeliveryOrder(
                title = "PERFUMERS REQUEST",
                requirements = listOf(
                    OrderRequirement(CitrusKind.LEMON, 16),
                    OrderRequirement(CitrusKind.GRAPEFRUIT, 12),
                    OrderRequirement(CitrusKind.MANDARIN, 10)
                )
            )
        )
    )

    val seals: List<RoyalSeal> = listOf(
        RoyalSeal(0, "SEAL OF THE PALM COURT", "PALM COURT"),
        RoyalSeal(1, "SEAL OF THE LEMON GALLERY", "LEMON GALLERY"),
        RoyalSeal(2, "SEAL OF THE LIME TERRACE", "LIME TERRACE"),
        RoyalSeal(3, "SEAL OF THE POMELO HALL", "POMELO HALL"),
        RoyalSeal(4, "SEAL OF THE MANDARIN ORANGERY", "MANDARIN ORANGERY"),
        RoyalSeal(5, "SEAL OF THE BERGAMOT ATRIUM", "BERGAMOT ATRIUM")
    )
}
