package com.royalcitrustiles.puzzle.domain.usecase

import com.royalcitrustiles.puzzle.domain.repository.ProgressRepository

class UnlockSealUseCase(private val progressRepository: ProgressRepository) {

    operator fun invoke(wingIndex: Int): Int {
        progressRepository.unlockSeal(wingIndex)
        return progressRepository.unlockedSealIndices().size
    }
}
