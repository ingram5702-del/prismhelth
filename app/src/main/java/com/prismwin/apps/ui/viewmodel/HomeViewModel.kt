package com.prismwin.apps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prismwin.apps.data.repository.GameRepository
import com.prismwin.apps.data.settings.SettingsRepository
import com.prismwin.apps.domain.Achievement
import com.prismwin.apps.domain.AchievementCalculator
import com.prismwin.apps.domain.DailyChallenge
import com.prismwin.apps.domain.DailyChallengeEngine
import com.prismwin.apps.domain.GameResult
import com.prismwin.apps.domain.SummaryStats
import com.prismwin.apps.domain.WeeklyProgress
import com.prismwin.apps.domain.WeeklyProgressCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val stats: SummaryStats = SummaryStats(),
    val achievements: List<Achievement> = emptyList(),
    val dailyChallenges: List<DailyChallenge> = emptyList(),
    val weeklyProgress: WeeklyProgress = WeeklyProgress()
)

class HomeViewModel(
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                gameRepository.observeSummaryStats(),
                gameRepository.observeRecentResults(limit = 200)
            ) { stats, recent ->
                val todayResults = filterToday(recent)
                HomeUiState(
                    stats = stats,
                    achievements = AchievementCalculator.fromStats(stats),
                    dailyChallenges = DailyChallengeEngine.buildChallenges(
                        date = LocalDate.now(),
                        todayResults = todayResults
                    ),
                    weeklyProgress = WeeklyProgressCalculator.build(recent)
                )
            }.collectLatest { merged ->
                _uiState.value = merged
            }
        }

        viewModelScope.launch {
            settingsRepository.settings.collect {
                // Keep settings warm for game screens and haptics/sound extension.
            }
        }
    }

    private fun filterToday(results: List<GameResult>): List<GameResult> {
        val today = LocalDate.now()
        return results.filter { result ->
            Instant.ofEpochMilli(result.createdAtEpochMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate() == today
        }
    }
}
