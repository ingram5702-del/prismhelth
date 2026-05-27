package com.prismwin.apps.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prismwin.apps.core.appViewModel
import com.prismwin.apps.ui.components.GradientScreen
import com.prismwin.apps.ui.components.MetricPill
import com.prismwin.apps.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onOpenReaction: () -> Unit,
    onOpenTarget: () -> Unit,
    onOpenSequence: () -> Unit,
    onOpenStats: () -> Unit
) {
    val viewModel: HomeViewModel = appViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GradientScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Prisma Win", style = MaterialTheme.typography.headlineMedium)
            Text("Train speed, accuracy, and memory", style = MaterialTheme.typography.bodyLarge)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Summary", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricPill("Total games", state.stats.totalGames.toString(), Modifier.weight(1f))
                        MetricPill(
                            "Best reaction",
                            state.stats.bestReactionMs?.let { "$it ms" } ?: "—",
                            Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricPill("Accuracy", state.stats.bestTargetHits.toString(), Modifier.weight(1f))
                        MetricPill("Memory", state.stats.bestSequenceLevel.toString(), Modifier.weight(1f))
                    }
                    MetricPill("Day streak", state.stats.streakDays.toString())
                }
            }

            GameEntryButton(
                title = "Reaction",
                subtitle = "One tap after the signal",
                icon = { Icon(Icons.Default.Bolt, contentDescription = null) },
                onClick = onOpenReaction
            )

            GameEntryButton(
                title = "Accuracy",
                subtitle = "Hit targets in 30 seconds",
                icon = { Icon(Icons.Default.CenterFocusStrong, contentDescription = null) },
                onClick = onOpenTarget
            )

            GameEntryButton(
                title = "Memory",
                subtitle = "Repeat the sequence",
                icon = { Icon(Icons.Default.Memory, contentDescription = null) },
                onClick = onOpenSequence
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Daily challenges", style = MaterialTheme.typography.titleMedium)
                    state.dailyChallenges.forEach { challenge ->
                        val marker = if (challenge.isCompleted) "✓" else "○"
                        Text("$marker ${challenge.title}")
                        Text(
                            "${challenge.description}. ${challenge.progress}/${challenge.target}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        LinearProgressIndicator(
                            progress = {
                                if (challenge.target == 0) 0f
                                else challenge.progress.toFloat() / challenge.target.toFloat()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val deltaPrefix = if (state.weeklyProgress.delta >= 0) "+" else ""
                    Text("Weekly ranking", style = MaterialTheme.typography.titleMedium)
                    Text("Rank: ${state.weeklyProgress.rankLabel}")
                    Text("Weekly score: ${state.weeklyProgress.thisWeekScore}")
                    Text("Last week: ${state.weeklyProgress.lastWeekScore}")
                    Text("Δ: $deltaPrefix${state.weeklyProgress.delta}")
                    LinearProgressIndicator(
                        progress = { state.weeklyProgress.rankProgress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Progress to ${state.weeklyProgress.nextRankLabel}: ${state.weeklyProgress.rankProgress}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Achievements", style = MaterialTheme.typography.titleMedium)
                    state.achievements.forEach { achievement ->
                        val marker = if (achievement.unlocked) "✓" else "○"
                        Text("$marker ${achievement.title}: ${achievement.description}")
                    }
                }
            }

            ElevatedButton(onClick = onOpenStats, modifier = Modifier.fillMaxWidth()) {
                Text("Open full stats")
            }
        }
    }
}

@Composable
private fun GameEntryButton(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    ElevatedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            icon()
            Column {
                Text(title)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
