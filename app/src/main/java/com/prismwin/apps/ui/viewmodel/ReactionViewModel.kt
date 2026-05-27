package com.prismwin.apps.ui.viewmodel

import android.os.SystemClock
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

enum class ReactionPhase {
    IDLE,
    WAITING,
    READY,
    TOO_SOON,
    FINISHED
}

data class ReactionUiState(
    val phase: ReactionPhase = ReactionPhase.IDLE,
    val reactionTimeMs: Long? = null,
    val bestReactionMs: Long? = null,
    val hint: String = "Press Start and wait for the signal"
)

class ReactionViewModel(
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReactionUiState())
    val uiState: StateFlow<ReactionUiState> = _uiState.asStateFlow()

    private var startTimestampMs: Long = 0
    private var pendingStartJob: Job? = null

    init {
        viewModelScope.launch {
            gameRepository.observeSummaryStats().collect { stats ->
                _uiState.update { it.copy(bestReactionMs = stats.bestReactionMs) }
            }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect {
                // Keep flow active for future feedback extensions.
            }
        }
    }

    fun startRound() {
        pendingStartJob?.cancel()
        _uiState.update {
            it.copy(
                phase = ReactionPhase.WAITING,
                reactionTimeMs = null,
                hint = "Wait for the green signal"
            )
        }

        pendingStartJob = viewModelScope.launch {
            delay(Random.nextLong(900L, 2600L))
            startTimestampMs = SystemClock.elapsedRealtime()
            _uiState.update {
                it.copy(
                    phase = ReactionPhase.READY,
                    hint = "TAP!"
                )
            }
        }
    }

    fun onTap() {
        when (_uiState.value.phase) {
            ReactionPhase.WAITING -> {
                pendingStartJob?.cancel()
                _uiState.update {
                    it.copy(
                        phase = ReactionPhase.TOO_SOON,
                        hint = "Too soon. Try again"
                    )
                }
            }
            ReactionPhase.READY -> {
                val reaction = (SystemClock.elapsedRealtime() - startTimestampMs).coerceAtLeast(1L)
                viewModelScope.launch {
                    gameRepository.saveReactionResult(reaction)
                }
                _uiState.update {
                    it.copy(
                        phase = ReactionPhase.FINISHED,
                        reactionTimeMs = reaction,
                        hint = "Great"
                    )
                }
            }
            else -> Unit
        }
    }

    override fun onCleared() {
        pendingStartJob?.cancel()
        super.onCleared()
    }
}
