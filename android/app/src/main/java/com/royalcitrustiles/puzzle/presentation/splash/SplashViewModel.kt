package com.royalcitrustiles.puzzle.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.royalcitrustiles.puzzle.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private var navigationTimer: Job? = null
    private var stageTicker: Job? = null

    fun startCountdown() {
        if (navigationTimer != null) {
            return
        }
        navigationTimer = viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            _uiState.value = _uiState.value.copy(
                stage = SplashUiState.Stage.READY,
                elapsedMs = GameConfig.LOADER_DURATION_MS,
                navigateToMenu = true
            )
        }
        stageTicker = viewModelScope.launch {
            var elapsed = 0L
            while (elapsed < GameConfig.LOADER_DURATION_MS) {
                delay(TICK_MS)
                elapsed += TICK_MS
                if (_uiState.value.navigateToMenu) {
                    return@launch
                }
                val capped = if (elapsed > GameConfig.LOADER_DURATION_MS) {
                    GameConfig.LOADER_DURATION_MS
                } else {
                    elapsed
                }
                _uiState.value = _uiState.value.copy(
                    stage = stageFor(capped),
                    elapsedMs = capped
                )
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToMenu = false)
        stageTicker?.cancel()
        stageTicker = null
    }

    private fun stageFor(elapsed: Long): SplashUiState.Stage {
        val third = GameConfig.LOADER_DURATION_MS / 3
        return when {
            elapsed < third -> SplashUiState.Stage.OPENING
            elapsed < third * 2 -> SplashUiState.Stage.WARMING
            else -> SplashUiState.Stage.POLISHING
        }
    }

    override fun onCleared() {
        navigationTimer?.cancel()
        navigationTimer = null
        stageTicker?.cancel()
        stageTicker = null
        super.onCleared()
    }

    private companion object {
        const val TICK_MS = 400L
    }
}
