package com.royalcitrustiles.puzzle.domain.usecase

import com.royalcitrustiles.puzzle.domain.model.Board
import com.royalcitrustiles.puzzle.domain.model.BoardPosition

class SwapTilesUseCase {

    operator fun invoke(board: Board, first: BoardPosition, second: BoardPosition): Board {
        if (!board.holds(first) || !board.holds(second)) {
            return board
        }
        val tiles = board.toMutableTiles()
        val firstIndex = board.indexOf(first.row, first.col)
        val secondIndex = board.indexOf(second.row, second.col)
        val carried = tiles[firstIndex]
        tiles[firstIndex] = tiles[secondIndex]
        tiles[secondIndex] = carried
        return board.withTiles(tiles)
    }
}
