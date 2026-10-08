package com.royalcitrustiles.puzzle.presentation.game

import com.royalcitrustiles.puzzle.domain.model.Board
import com.royalcitrustiles.puzzle.domain.model.BoardPosition
import com.royalcitrustiles.puzzle.domain.model.DeliveryOrder
import com.royalcitrustiles.puzzle.domain.model.RoundResult

sealed interface GameUiState {

    object Loading : GameUiState

    data class Active(
        val board: Board,
        val phase: Phase,
        val selected: BoardPosition?,
        val movesLeft: Int,
        val score: Int,
        val bestCombo: Int,
        val crowns: Int,
        val sealCount: Int,
        val order: DeliveryOrder,
        val wingIndex: Int,
        val wingName: String,
        val wingCount: Int,
        val canDeliver: Boolean,
        val reducedMotion: Boolean,
        val rejectedFirst: BoardPosition?,
        val rejectedSecond: BoardPosition?,
        val announcementRes: Int?
    ) : GameUiState

    data class Finished(val result: RoundResult) : GameUiState

    object Error : GameUiState

    enum class Phase {
        IDLE,
        RESOLVING,
        RESHUFFLING,
        CELEBRATING
    }
}
