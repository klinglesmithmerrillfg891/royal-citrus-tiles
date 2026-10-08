package com.royalcitrustiles.puzzle.domain.usecase

import com.royalcitrustiles.puzzle.domain.model.Board
import com.royalcitrustiles.puzzle.domain.model.CitrusKind
import com.royalcitrustiles.puzzle.domain.model.Tile
import kotlin.random.Random

class GenerateBoardUseCase(
    private val resolveMatches: ResolveMatchesUseCase,
    private val hasLegalMove: HasLegalMoveUseCase
) {

    operator fun invoke(rows: Int, cols: Int, minMatch: Int, random: Random): Board {
        var attempt = 0
        var candidate = layOut(rows, cols, minMatch, random)
        while (attempt < MAX_ATTEMPTS) {
            val stable = resolveMatches(candidate, minMatch).isEmpty
            if (stable && hasLegalMove(candidate, minMatch)) {
                return candidate
            }
            candidate = layOut(rows, cols, minMatch, random)
            attempt++
        }
        return candidate
    }

    private fun layOut(rows: Int, cols: Int, minMatch: Int, random: Random): Board {
        val palette = CitrusKind.PLAYABLE
        val tiles = ArrayList<Tile>(rows * cols)
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val banned = HashSet<CitrusKind>()
                if (col >= minMatch - 1) {
                    val run = (1 until minMatch).all { step ->
                        tiles[row * cols + col - step].kind == tiles[row * cols + col - 1].kind
                    }
                    if (run) {
                        banned.add(tiles[row * cols + col - 1].kind)
                    }
                }
                if (row >= minMatch - 1) {
                    val run = (1 until minMatch).all { step ->
                        tiles[(row - step) * cols + col].kind == tiles[(row - 1) * cols + col].kind
                    }
                    if (run) {
                        banned.add(tiles[(row - 1) * cols + col].kind)
                    }
                }
                val allowed = palette.filter { !banned.contains(it) }
                val pool = if (allowed.isEmpty()) palette else allowed
                tiles.add(Tile(pool[random.nextInt(pool.size)]))
            }
        }
        return Board(rows, cols, tiles)
    }

    private companion object {
        const val MAX_ATTEMPTS = 60
    }
}
