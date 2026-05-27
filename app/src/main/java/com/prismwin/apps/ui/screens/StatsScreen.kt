package com.prismwin.apps.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prismwin.apps.core.appViewModel
import com.prismwin.apps.domain.ChartPoint
import com.prismwin.apps.domain.GameMode
import com.prismwin.apps.domain.GameResult
import com.prismwin.apps.ui.components.GradientScreen
import com.prismwin.apps.ui.components.MetricPill
import com.prismwin.apps.ui.viewmodel.StatsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun StatsScreen() {
    val viewModel: StatsViewModel = appViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GradientScreen {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Stats", style = MaterialTheme.typography.headlineSmall)
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricPill("Total", state.stats.totalGames.toString(), Modifier.weight(1f))
                            MetricPill(
                                "Reaction",
                                state.stats.bestReactionMs?.let { "$it ms" } ?: "—",
                                Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricPill("Accuracy", state.stats.bestTargetHits.toString(), Modifier.weight(1f))
                            MetricPill("Memory", state.stats.bestSequenceLevel.toString(), Modifier.weight(1f))
                        }
                        Text("Average reaction: ${state.stats.averageReactionMs?.let { "$it ms" } ?: "—"}")
                        Text("Perfect rounds: ${state.stats.perfectTargetGames}")
                        Text("Day streak: ${state.stats.streakDays}")
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val deltaPrefix = if (state.weeklyProgress.delta >= 0) "+" else ""
                        Text("Weekly ranking", style = MaterialTheme.typography.titleMedium)
                        Text("Rank: ${state.weeklyProgress.rankLabel}", fontWeight = FontWeight.SemiBold)
                        Text("Score: ${state.weeklyProgress.thisWeekScore} (last: ${state.weeklyProgress.lastWeekScore})")
                        Text("Δ: $deltaPrefix${state.weeklyProgress.delta}")
                        LinearProgressIndicator(
                            progress = { state.weeklyProgress.rankProgress / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "To ${state.weeklyProgress.nextRankLabel}: ${state.weeklyProgress.rankProgress}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            item {
                TrendChartCard(
                    title = "Reaction over 7 days (average, ms)",
                    points = state.reactionTrend,
                    lineColor = Color(0xFF2F80ED),
                    valueSuffix = "ms"
                )
            }
            item {
                TrendChartCard(
                    title = "Accuracy over 7 days (best hits)",
                    points = state.targetTrend,
                    lineColor = Color(0xFF0D9B5A),
                    valueSuffix = "hits"
                )
            }
            item {
                TrendChartCard(
                    title = "Memory over 7 days (best level)",
                    points = state.sequenceTrend,
                    lineColor = Color(0xFFE27D60),
                    valueSuffix = "level"
                )
            }
            item {
                Text("Recent results", style = MaterialTheme.typography.titleMedium)
            }
            items(state.recentResults, key = { it.id }) { result ->
                ResultRow(result)
            }
        }
    }
}

@Composable
private fun TrendChartCard(
    title: String,
    points: List<ChartPoint>,
    lineColor: Color,
    valueSuffix: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            if (points.isEmpty()) {
                Text("Not enough data")
                return@Column
            }

            val maxValue = points.maxOf { it.value }.coerceAtLeast(1f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val stepX = if (points.size > 1) size.width / (points.size - 1) else 0f
                val path = Path()

                points.forEachIndexed { index, point ->
                    val x = index * stepX
                    val y = size.height - ((point.value / maxValue) * (size.height - 16f))
                    val offset = Offset(x, y)

                    if (index == 0) {
                        path.moveTo(offset.x, offset.y)
                    } else {
                        path.lineTo(offset.x, offset.y)
                    }

                    drawCircle(
                        color = lineColor,
                        radius = 6f,
                        center = offset
                    )
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 5f, cap = StrokeCap.Round)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(points.first().label, style = MaterialTheme.typography.bodySmall)
                Text(points.last().label, style = MaterialTheme.typography.bodySmall)
            }

            val latest = points.last().value.roundToInt()
            Text("Latest value: $latest $valueSuffix", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ResultRow(result: GameResult) {
    val dateText = Instant.ofEpochMilli(result.createdAtEpochMs)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

    val valueText = when (result.mode) {
        GameMode.REACTION -> "${result.reactionTimeMs ?: 0} ms"
        GameMode.TARGET -> "${result.hits ?: 0} hits / ${result.misses ?: 0} misses"
        GameMode.SEQUENCE -> "level ${result.levelReached ?: 0}"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    when (result.mode) {
                        GameMode.REACTION -> "Reaction"
                        GameMode.TARGET -> "Accuracy"
                        GameMode.SEQUENCE -> "Memory"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
                Text(valueText)
            }
            Text(dateText, style = MaterialTheme.typography.bodySmall)
        }
    }
}
