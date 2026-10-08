package com.royalcitrustiles.puzzle.domain.usecase

import com.royalcitrustiles.puzzle.domain.model.Board
import com.royalcitrustiles.puzzle.domain.model.BoardPosition

class HasLegalMoveUseCase(
    private val resolveMatches: ResolveMatchesUseCase,
    private val swapTiles: SwapTilesUseCase
) {

    operator fun invoke(board: Board, minMatch: Int): Boolean {
        for (row in 0 until board.rows) {
            for (col in 0 until board.cols) {
                val origin = BoardPosition(row, col)
                if (col + 1 < board.cols && yieldsMatch(board, origin, BoardPosition(row, col + 1), minMatch)) {
                    return true
                }
                if (row + 1 < board.rows && yieldsMatch(board, origin, BoardPosition(row + 1, col), minMatch)) {
                    return true
                }
            }
        }
        return false
    }

    private fun yieldsMatch(
        board: Board,
        first: BoardPosition,
        second: BoardPosition,
        minMatch: Int
    ): Boolean {
        val swapped = swapTiles(board, first, second)
        return !resolveMatches(swapped, minMatch).isEmpty
    }
}
