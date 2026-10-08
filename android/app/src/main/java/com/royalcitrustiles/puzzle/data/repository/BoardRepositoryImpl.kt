package com.royalcitrustiles.puzzle.data.repository

import com.royalcitrustiles.puzzle.data.sample.SampleData
import com.royalcitrustiles.puzzle.domain.model.ConservatoryWing
import com.royalcitrustiles.puzzle.domain.model.RoyalSeal
import com.royalcitrustiles.puzzle.domain.repository.BoardRepository

class BoardRepositoryImpl : BoardRepository {

    override fun wings(): List<ConservatoryWing> = SampleData.wings

    override fun seals(): List<RoyalSeal> = SampleData.seals

    override fun wingAt(index: Int): ConservatoryWing {
        val wings = SampleData.wings
        val safeIndex = when {
            index < 0 -> 0
            index >= wings.size -> wings.size - 1
            else -> index
        }
        return wings[safeIndex]
    }
}
