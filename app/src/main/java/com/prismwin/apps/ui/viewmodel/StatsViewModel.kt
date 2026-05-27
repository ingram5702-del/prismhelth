package com.prismwin.apps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prismwin.apps.data.repository.GameRepository
import com.prismwin.apps.domain.ChartPoint
import com.prismwin.apps.domain.GameMode
import com.prismwin.apps.domain.GameResult
import com.prismwin.apps.domain.SummaryStats
import com.prismwin.apps.domain.WeeklyProgress
import com.prismwin.apps.domain.WeeklyProgressCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class StatsUiState(
    val stats: SummaryStats = SummaryStats(),
    val recentResults: List<GameResult> = emptyList(),
    val reactionTrend: List<ChartPoint> = emptyList(),
    val targetTrend: List<ChartPoint> = emptyList(),
    val sequenceTrend: List<ChartPoint> = emptyList(),
    val weeklyProgress: WeeklyProgress = WeeklyProgress()
)

class StatsViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                gameRepository.observeSummaryStats(),
                gameRepository.observeRecentResults(limit = 150)
            ) { stats, recent ->
                StatsUiState(
                    stats = stats,
                    recentResults = recent,
                    reactionTrend = buildReactionTrend(recent),
                    targetTrend = buildTargetTrend(recent),
                    sequenceTrend = buildSequenceTrend(recent),
                    weeklyProgress = WeeklyProgressCalculator.build(recent)
                )
            }.collectLatest { merged ->
                _uiState.value = merged
            }
        }
    }

    private fun buildReactionTrend(results: List<GameResult>): List<ChartPoint> {
        val byDate = results
            .filter { it.mode == GameMode.REACTION }
            .groupBy { toDate(it) }

        return recentDays().map { day ->
            val values = byDate[day].orEmpty().mapNotNull { it.reactionTimeMs }
            val avg = if (values.isEmpty()) 0f else values.average().toFloat()
            ChartPoint(label = day.format(LABEL_FORMAT), value = avg)
        }
    }

    private fun buildTargetTrend(results: List<GameResult>): List<ChartPoint> {
        val byDate = results
            .filter { it.mode == GameMode.TARGET }
            .groupBy { toDate(it) }

        return recentDays().map { day ->
            val best = byDate[day].orEmpty().mapNotNull { it.hits }.maxOrNull()?.toFloat() ?: 0f
            ChartPoint(label = day.format(LABEL_FORMAT), value = best)
        }
    }

    private fun buildSequenceTrend(results: List<GameResult>): List<ChartPoint> {
        val byDate = results
            .filter { it.mode == GameMode.SEQUENCE }
            .groupBy { toDate(it) }

        return recentDays().map { day ->
            val best = byDate[day].orEmpty().mapNotNull { it.levelReached }.maxOrNull()?.toFloat() ?: 0f
            ChartPoint(label = day.format(LABEL_FORMAT), value = best)
        }
    }

    private fun recentDays(): List<LocalDate> {
        val today = LocalDate.now()
        return (6 downTo 0).map { today.minusDays(it.toLong()) }
    }

    private fun toDate(result: GameResult): LocalDate {
        return Instant.ofEpochMilli(result.createdAtEpochMs)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private companion object {
        val LABEL_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM")
    }
}
