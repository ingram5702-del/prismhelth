package com.prismwin.apps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prismwin.apps.data.repository.GameRepository
import com.prismwin.apps.data.settings.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TargetUiState(
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val secondsLeft: Int = 30,
    val hits: Int = 0,
    val misses: Int = 0,
    val activeCell: Int = 0,
    val bestHits: Int = 0
) {
    val accuracy: Int
        get() {
            val total = hits + misses
            if (total == 0) return 0
            return ((hits.toDouble() / total.toDouble()) * 100).toInt()
        }
}

class TargetViewModel(
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TargetUiState())
    val uiState: StateFlow<TargetUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            gameRepository.observeSummaryStats().collect { stats ->
                _uiState.update { it.copy(bestHits = stats.bestTargetHits) }
            }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect {
                // Keep settings active in memory.
            }
        }
    }

    fun startGame() {
        timerJob?.cancel()
        _uiState.value = TargetUiState(
            isRunning = true,
            secondsLeft = 30,
            activeCell = Random.nextInt(CELL_COUNT),
            bestHits = _uiState.value.bestHits
        )

        timerJob = viewModelScope.launch {
            repeat(30) {
                delay(1000)
                val next = _uiState.value.secondsLeft - 1
                _uiState.update { it.copy(secondsLeft = next) }
            }
            finishGame()
        }
    }

    fun onCellTapped(index: Int) {
        val state = _uiState.value
        if (!state.isRunning) return

        if (index == state.activeCell) {
            _uiState.update {
                it.copy(
                    hits = it.hits + 1,
                    activeCell = nextCell(it.activeCell)
                )
            }
        } else {
            _uiState.update { it.copy(misses = it.misses + 1) }
        }
    }

    private fun finishGame() {
        val state = _uiState.value
        if (!state.isRunning) return

        viewModelScope.launch {
            gameRepository.saveTargetResult(
                hits = state.hits,
                misses = state.misses
            )
        }

        _uiState.update {
            it.copy(
                isRunning = false,
                isFinished = true,
                activeCell = -1
            )
        }
    }

    private fun nextCell(current: Int): Int {
        var next = Random.nextInt(CELL_COUNT)
        while (next == current) {
            next = Random.nextInt(CELL_COUNT)
        }
        return next
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val CELL_COUNT = 16
    }
}
