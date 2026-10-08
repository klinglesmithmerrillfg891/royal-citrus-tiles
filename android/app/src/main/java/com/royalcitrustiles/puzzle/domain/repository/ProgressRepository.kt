package com.royalcitrustiles.puzzle.domain.repository

import com.royalcitrustiles.puzzle.domain.model.AppSettings
import com.royalcitrustiles.puzzle.domain.model.PlayerProgress

interface ProgressRepository {

    fun loadProgress(): PlayerProgress

    fun saveProgress(progress: PlayerProgress)

    fun loadSettings(): AppSettings

    fun saveSettings(settings: AppSettings)

    fun unlockedSealIndices(): Set<Int>

    fun unlockSeal(wingIndex: Int)

    fun resetEverything()
}
