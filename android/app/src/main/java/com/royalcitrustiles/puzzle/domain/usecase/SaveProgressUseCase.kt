package com.royalcitrustiles.puzzle.domain.usecase

import com.royalcitrustiles.puzzle.domain.model.PlayerProgress
import com.royalcitrustiles.puzzle.domain.model.RoundResult
import com.royalcitrustiles.puzzle.domain.repository.ProgressRepository

class SaveProgressUseCase(private val progressRepository: ProgressRepository) {

    operator fun invoke(result: RoundResult, wingCount: Int): PlayerProgress {
        val current = progressRepository.loadProgress()
        val streak = if (result.success) current.currentStreak + 1 else 0
        val best = if (streak > current.bestStreak) streak else current.bestStreak
        val nextWing = if (result.success && result.wingIndex + 1 > current.unlockedWing) {
            if (result.wingIndex + 1 >= wingCount) wingCount - 1 else result.wingIndex + 1
        } else {
            current.unlockedWing
        }
        val updated = PlayerProgress(
            royalFavour = current.royalFavour + result.score,
            sealCount = progressRepository.unlockedSealIndices().size,
            bestStreak = best,
            currentStreak = streak,
            unlockedWing = nextWing
        )
        progressRepository.saveProgress(updated)
        return updated
    }
}
