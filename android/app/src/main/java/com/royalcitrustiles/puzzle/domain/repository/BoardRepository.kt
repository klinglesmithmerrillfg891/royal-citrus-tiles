package com.royalcitrustiles.puzzle.domain.repository

import com.royalcitrustiles.puzzle.domain.model.ConservatoryWing
import com.royalcitrustiles.puzzle.domain.model.RoyalSeal

interface BoardRepository {

    fun wings(): List<ConservatoryWing>

    fun seals(): List<RoyalSeal>

    fun wingAt(index: Int): ConservatoryWing
}
