package com.royalcitrustiles.puzzle.domain.usecase

import com.royalcitrustiles.puzzle.domain.model.Board
import com.royalcitrustiles.puzzle.domain.model.BoardPosition
import com.royalcitrustiles.puzzle.domain.model.CitrusKind
import com.royalcitrustiles.puzzle.domain.model.MatchOutcome

class ResolveMatchesUseCase {

    operator fun invoke(board: Board, minMatch: Int): MatchOutcome {
        val cleared = LinkedHashSet<BoardPosition>()
        var longest = 0

        for (row in 0 until board.rows) {
            var start = 0
            while (start < board.cols) {
                val kind = board.tileAt(row, start).kind
                var end = start + 1
                while (end < board.cols && board.tileAt(row, end).kind == kind) {
                    end++
                }
                val length = end - start
                if (length >= minMatch) {
                    if (length > longest) longest = length
                    for (col in start until end) {
                        cleared.add(BoardPosition(row, col))
                    }
                }
                start = end
            }
        }

        for (col in 0 until board.cols) {
            var start = 0
            while (start < board.rows) {
                val kind = board.tileAt(start, col).kind
                var end = start + 1
                while (end < board.rows && board.tileAt(end, col).kind == kind) {
                    end++
                }
                val length = end - start
                if (length >= minMatch) {
                    if (length > longest) longest = length
                    for (row in start until end) {
                        cleared.add(BoardPosition(row, col))
                    }
                }
                start = end
            }
        }

        if (cleared.isEmpty()) {
            return MatchOutcome.NONE
        }

        val byKind = LinkedHashMap<CitrusKind, Int>()
        for (position in cleared) {
            val kind = board.tileAt(position).kind
            byKind[kind] = (byKind[kind] ?: 0) + 1
        }
        return MatchOutcome(cleared, longest, byKind)
    }
}
