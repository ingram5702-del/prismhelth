package com.prismwin.apps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prismwin.apps.core.appViewModel
import com.prismwin.apps.ui.components.GradientScreen
import com.prismwin.apps.ui.viewmodel.ReactionPhase
import com.prismwin.apps.ui.viewmodel.ReactionViewModel

@Composable
fun ReactionScreen(onBack: () -> Unit) {
    val viewModel: ReactionViewModel = appViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GradientScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Mode: Reaction", style = MaterialTheme.typography.headlineSmall)
            Text(state.hint)
            Text("Best result: ${state.bestReactionMs?.let { "$it ms" } ?: "—"}")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        color = phaseColor(state.phase),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { viewModel.onTap() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when (state.phase) {
                        ReactionPhase.READY -> "TAP"
                        ReactionPhase.WAITING -> "Wait"
                        ReactionPhase.TOO_SOON -> "Too soon"
                        ReactionPhase.FINISHED -> "${state.reactionTimeMs} ms"
                        ReactionPhase.IDLE -> "Press Start"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }

            Button(
                onClick = { viewModel.startRound() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start")
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}

private fun phaseColor(phase: ReactionPhase): Color {
    return when (phase) {
        ReactionPhase.IDLE -> Color(0xFF4A5568)
        ReactionPhase.WAITING -> Color(0xFFCC7A00)
        ReactionPhase.READY -> Color(0xFF0D9B5A)
        ReactionPhase.TOO_SOON -> Color(0xFFC53030)
        ReactionPhase.FINISHED -> Color(0xFF2D4A66)
    }
}
