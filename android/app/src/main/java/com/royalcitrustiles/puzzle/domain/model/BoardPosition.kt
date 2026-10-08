package com.royalcitrustiles.puzzle.domain.model

data class BoardPosition(val row: Int, val col: Int) {

    fun isAdjacentTo(other: BoardPosition): Boolean {
        val rowDelta = if (row > other.row) row - other.row else other.row - row
        val colDelta = if (col > other.col) col - other.col else other.col - col
        return rowDelta + colDelta == 1
    }
}
