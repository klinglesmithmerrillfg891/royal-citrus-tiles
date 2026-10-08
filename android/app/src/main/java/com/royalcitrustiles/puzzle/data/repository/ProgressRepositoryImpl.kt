package com.royalcitrustiles.puzzle.data.repository

import com.royalcitrustiles.puzzle.data.local.PreferencesStorage
import com.royalcitrustiles.puzzle.domain.model.AppSettings
import com.royalcitrustiles.puzzle.domain.model.PlayerProgress
import com.royalcitrustiles.puzzle.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val storage: PreferencesStorage) : ProgressRepository {

    override fun loadProgress(): PlayerProgress = PlayerProgress(
        royalFavour = storage.readInt(PreferencesStorage.KEY_ROYAL_FAVOUR, 0),
        sealCount = storage.readInt(PreferencesStorage.KEY_SEAL_COUNT, 0),
        bestStreak = storage.readInt(PreferencesStorage.KEY_BEST_STREAK, 0),
        currentStreak = storage.readInt(PreferencesStorage.KEY_CURRENT_STREAK, 0),
        unlockedWing = storage.readInt(PreferencesStorage.KEY_UNLOCKED_WING, 0)
    )

    override fun saveProgress(progress: PlayerProgress) {
        storage.writeInt(PreferencesStorage.KEY_ROYAL_FAVOUR, progress.royalFavour)
        storage.writeInt(PreferencesStorage.KEY_SEAL_COUNT, progress.sealCount)
        storage.writeInt(PreferencesStorage.KEY_BEST_STREAK, progress.bestStreak)
        storage.writeInt(PreferencesStorage.KEY_CURRENT_STREAK, progress.currentStreak)
        storage.writeInt(PreferencesStorage.KEY_UNLOCKED_WING, progress.unlockedWing)
    }

    override fun loadSettings(): AppSettings = AppSettings(
        soundEnabled = storage.readBoolean(PreferencesStorage.KEY_SOUND, true),
        hapticsEnabled = storage.readBoolean(PreferencesStorage.KEY_HAPTICS, true),
        reducedMotion = storage.readBoolean(PreferencesStorage.KEY_REDUCED_MOTION, false)
    )

    override fun saveSettings(settings: AppSettings) {
        storage.writeBoolean(PreferencesStorage.KEY_SOUND, settings.soundEnabled)
        storage.writeBoolean(PreferencesStorage.KEY_HAPTICS, settings.hapticsEnabled)
        storage.writeBoolean(PreferencesStorage.KEY_REDUCED_MOTION, settings.reducedMotion)
    }

    override fun unlockedSealIndices(): Set<Int> =
        storage.readIntSet(PreferencesStorage.KEY_SEAL_INDICES)

    override fun unlockSeal(wingIndex: Int) {
        val updated = unlockedSealIndices().toMutableSet()
        updated.add(wingIndex)
        storage.writeIntSet(PreferencesStorage.KEY_SEAL_INDICES, updated)
        storage.writeInt(PreferencesStorage.KEY_SEAL_COUNT, updated.size)
    }

    override fun resetEverything() {
        storage.clear()
    }
}
