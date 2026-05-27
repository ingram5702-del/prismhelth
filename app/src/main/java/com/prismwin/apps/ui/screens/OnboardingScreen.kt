package com.prismwin.apps.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prismwin.apps.ui.components.GradientScreen
import com.prismwin.apps.ui.viewmodel.OnboardingPage

@Composable
fun OnboardingScreen(
    page: OnboardingPage,
    pageIndex: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val isLastPage = pageIndex == totalPages - 1

    GradientScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("Prisma Win", style = MaterialTheme.typography.headlineMedium)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(page.imageRes),
                        contentDescription = page.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(page.title, style = MaterialTheme.typography.headlineSmall)
                    Text(page.body, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(totalPages) { index ->
                        val selected = index == pageIndex
                        Spacer(
                            modifier = Modifier
                                .size(if (selected) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                        )
                    }
                }
                Text(
                    text = "Step ${pageIndex + 1}/$totalPages",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        enabled = pageIndex > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                    Button(
                        onClick = if (isLastPage) onFinish else onNext,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isLastPage) "Start" else "Next")
                    }
                }
            }
        }
    }
}
