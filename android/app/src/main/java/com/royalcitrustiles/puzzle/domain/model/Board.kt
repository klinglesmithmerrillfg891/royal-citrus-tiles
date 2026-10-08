package com.royalcitrustiles.puzzle.domain.model

class Board(val rows: Int, val cols: Int, val tiles: List<Tile>) {

    val size: Int get() = rows * cols

    fun indexOf(row: Int, col: Int): Int = row * cols + col

    fun tileAt(row: Int, col: Int): Tile = tiles[indexOf(row, col)]

    fun tileAt(position: BoardPosition): Tile = tileAt(position.row, position.col)

    fun positionOf(index: Int): BoardPosition = BoardPosition(index / cols, index % cols)

    fun holds(position: BoardPosition): Boolean =
        position.row in 0 until rows && position.col in 0 until cols

    fun withTiles(updated: List<Tile>): Board = Board(rows, cols, updated)

    fun toMutableTiles(): MutableList<Tile> = tiles.toMutableList()
}
