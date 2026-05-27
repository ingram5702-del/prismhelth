package com.prismwin.apps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prismwin.apps.core.appViewModel
import com.prismwin.apps.ui.components.GradientScreen
import com.prismwin.apps.ui.viewmodel.TargetViewModel

@Composable
fun TargetScreen(onBack: () -> Unit) {
    val viewModel: TargetViewModel = appViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GradientScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mode: Accuracy", style = MaterialTheme.typography.headlineSmall)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Time: ${state.secondsLeft} sec")
                    Text("Hits: ${state.hits} | Misses: ${state.misses}")
                    Text("Accuracy: ${state.accuracy}% | Best result: ${state.bestHits}")
                }
            }

            TargetGrid(
                activeCell = state.activeCell,
                isRunning = state.isRunning,
                onCellTapped = viewModel::onCellTapped
            )

            Button(
                onClick = { viewModel.startGame() },
                enabled = !state.isRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isFinished) "Play again" else "Start")
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun TargetGrid(
    activeCell: Int,
    isRunning: Boolean,
    onCellTapped: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val cellSize = ((maxWidth - 24.dp) / 4).coerceAtMost(82.dp)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(4) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(4) { column ->
                        val index = row * 4 + column
                        val active = isRunning && activeCell == index
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (active) Color(0xFF0D9B5A) else Color(0xFFDFE7EE))
                                .clickable { onCellTapped(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (active) "●" else "",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
