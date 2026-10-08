package com.royalcitrustiles.puzzle.domain.usecase

import com.royalcitrustiles.puzzle.domain.model.Board
import com.royalcitrustiles.puzzle.domain.model.BoardPosition
import com.royalcitrustiles.puzzle.domain.model.CitrusKind
import com.royalcitrustiles.puzzle.domain.model.Tile
import kotlin.random.Random

class ApplyGravityUseCase {

    operator fun invoke(board: Board, cleared: Set<BoardPosition>, random: Random): Board {
        if (cleared.isEmpty()) {
            return board
        }
        val tiles = board.toMutableTiles()
        val palette = CitrusKind.PLAYABLE
        for (col in 0 until board.cols) {
            val survivors = ArrayList<Tile>(board.rows)
            for (row in board.rows - 1 downTo 0) {
                if (!cleared.contains(BoardPosition(row, col))) {
                    survivors.add(tiles[board.indexOf(row, col)])
                }
            }
            var cursor = board.rows - 1
            for (tile in survivors) {
                tiles[board.indexOf(cursor, col)] = tile
                cursor--
            }
            while (cursor >= 0) {
                tiles[board.indexOf(cursor, col)] = Tile(palette[random.nextInt(palette.size)])
                cursor--
            }
        }
        return board.withTiles(tiles)
    }
}
