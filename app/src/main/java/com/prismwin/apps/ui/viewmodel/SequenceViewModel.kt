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

enum class SequencePhase {
    IDLE,
    SHOWING,
    INPUT,
    FINISHED
}

data class SequenceUiState(
    val phase: SequencePhase = SequencePhase.IDLE,
    val level: Int = 0,
    val bestLevel: Int = 0,
    val highlightedPad: Int? = null,
    val message: String = "Press Start",
    val progress: Int = 0
)

class SequenceViewModel(
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SequenceUiState())
    val uiState: StateFlow<SequenceUiState> = _uiState.asStateFlow()

    private val sequence: MutableList<Int> = mutableListOf()
    private var showJob: Job? = null

    init {
        viewModelScope.launch {
            gameRepository.observeSummaryStats().collect { stats ->
                _uiState.update { it.copy(bestLevel = stats.bestSequenceLevel) }
            }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect {
                // Keep settings stream active.
            }
        }
    }

    fun startGame() {
        sequence.clear()
        _uiState.update {
            it.copy(
                phase = SequencePhase.IDLE,
                level = 0,
                progress = 0,
                message = "Watch the sequence"
            )
        }
        nextRound()
    }

    fun onPadTapped(index: Int) {
        val state = _uiState.value
        if (state.phase != SequencePhase.INPUT) return

        val expected = sequence[state.progress]
        if (index == expected) {
            val nextProgress = state.progress + 1
            if (nextProgress == sequence.size) {
                _uiState.update {
                    it.copy(
                        phase = SequencePhase.SHOWING,
                        level = sequence.size,
                        progress = 0,
                        message = "Correct, next level"
                    )
                }
                viewModelScope.launch {
                    delay(700)
                    nextRound()
                }
            } else {
                _uiState.update { it.copy(progress = nextProgress) }
            }
        } else {
            endGame(levelReached = (sequence.size - 1).coerceAtLeast(0))
        }
    }

    private fun nextRound() {
        showJob?.cancel()
        sequence += Random.nextInt(PAD_COUNT)
        showJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    phase = SequencePhase.SHOWING,
                    highlightedPad = null,
                    progress = 0,
                    message = "Memorize"
                )
            }
            delay(400)
            sequence.forEach { pad ->
                _uiState.update { it.copy(highlightedPad = pad) }
                delay(450)
                _uiState.update { it.copy(highlightedPad = null) }
                delay(220)
            }
            _uiState.update {
                it.copy(
                    phase = SequencePhase.INPUT,
                    level = sequence.size,
                    message = "Repeat"
                )
            }
        }
    }

    private fun endGame(levelReached: Int) {
        showJob?.cancel()
        viewModelScope.launch {
            gameRepository.saveSequenceResult(levelReached)
        }
        _uiState.update {
            it.copy(
                phase = SequencePhase.FINISHED,
                level = levelReached,
                highlightedPad = null,
                message = "Mistake. Level $levelReached"
            )
        }
    }

    override fun onCleared() {
        showJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val PAD_COUNT = 4
    }
}
