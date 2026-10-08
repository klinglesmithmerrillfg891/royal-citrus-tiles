package com.royalcitrustiles.puzzle.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesStorage(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = preferences.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean =
        preferences.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun readIntSet(key: String): Set<Int> {
        val raw = preferences.getStringSet(key, emptySet()) ?: emptySet()
        return raw.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun writeIntSet(key: String, values: Set<Int>) {
        preferences.edit().putStringSet(key, values.map { it.toString() }.toSet()).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    companion object {
        const val STORE_NAME = "royal_citrus_prefs"
        const val KEY_ROYAL_FAVOUR = "royal_favour"
        const val KEY_SEAL_COUNT = "seal_count"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_CURRENT_STREAK = "current_streak"
        const val KEY_UNLOCKED_WING = "unlocked_wing"
        const val KEY_SEAL_INDICES = "seal_indices"
        const val KEY_SOUND = "sound_enabled"
        const val KEY_HAPTICS = "haptics_enabled"
        const val KEY_REDUCED_MOTION = "reduced_motion"
    }
}
