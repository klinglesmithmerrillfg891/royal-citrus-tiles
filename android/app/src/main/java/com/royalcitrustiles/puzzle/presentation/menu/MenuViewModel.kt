package com.royalcitrustiles.puzzle.presentation.menu

import androidx.lifecycle.ViewModel
import com.royalcitrustiles.puzzle.domain.repository.BoardRepository
import com.royalcitrustiles.puzzle.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.loadProgress()
        val settings = progressRepository.loadSettings()
        val unlockedSeals = progressRepository.unlockedSealIndices()
        val wings = boardRepository.wings()
        val activeIndex = firstUnrestoredIndex(wings.size, unlockedSeals, progress.unlockedWing)
        val cards = wings.map { wing ->
            val state = when {
                unlockedSeals.contains(wing.index) -> MenuUiState.State.RESTORED
                wing.index == activeIndex -> MenuUiState.State.CURRENT
                else -> MenuUiState.State.LOCKED
            }
            MenuUiState.WingCard(wing, state)
        }
        _uiState.value = MenuUiState(
            royalFavour = progress.royalFavour,
            sealCount = unlockedSeals.size,
            bestStreak = progress.bestStreak,
            activeWingIndex = activeIndex,
            wingCards = cards,
            activeWing = boardRepository.wingAt(activeIndex),
            reducedMotion = settings.reducedMotion
        )
    }

    private fun firstUnrestoredIndex(
        wingCount: Int,
        unlockedSeals: Set<Int>,
        unlockedWing: Int
    ): Int {
        for (index in 0 until wingCount) {
            if (!unlockedSeals.contains(index)) {
                return index
            }
        }
        return if (unlockedWing in 0 until wingCount) unlockedWing else 0
    }
}
