package com.prismwin.apps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.prismwin.apps.ui.viewmodel.SequenceViewModel

@Composable
fun SequenceScreen(onBack: () -> Unit) {
    val viewModel: SequenceViewModel = appViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val baseColors = listOf(
        Color(0xFF2F80ED),
        Color(0xFFE27D60),
        Color(0xFF27AE60),
        Color(0xFFF2C94C)
    )

    GradientScreen {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mode: Memory", style = MaterialTheme.typography.headlineSmall)
            Text("Current level: ${state.level}")
            Text("Best level: ${state.bestLevel}")
            Text(state.message)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0..1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (col in 0..1) {
                            val index = row * 2 + col
                            val highlighted = state.highlightedPad == index
                            Pad(
                                color = if (highlighted) baseColors[index].copy(alpha = 0.4f) else baseColors[index],
                                onTap = { viewModel.onPadTapped(index) }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.level > 0) "Restart" else "Start")
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun Pad(
    color: Color,
    onTap: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(140.dp)
            .background(color = color, shape = RoundedCornerShape(20.dp))
            .clickable { onTap() }
    )
}
