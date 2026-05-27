package com.prismwin.apps.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prismwin.apps.core.appViewModel
import com.prismwin.apps.ui.components.GradientScreen
import com.prismwin.apps.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = appViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var feedbackText by remember { mutableStateOf("") }
    var feedbackImageUri by remember { mutableStateOf<Uri?>(null) }

    val requestNotificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onDailyReminderChanged(granted)
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        feedbackImageUri = uri
    }

    GradientScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineSmall)

            Card(modifier = Modifier.fillMaxWidth()) {
                RowItem(
                    title = "Sound",
                    checked = state.settings.soundEnabled,
                    onCheckedChange = viewModel::onSoundChanged
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                RowItem(
                    title = "Vibration",
                    checked = state.settings.vibrationEnabled,
                    onCheckedChange = viewModel::onVibrationChanged
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                RowItem(
                    title = "Daily reminders (20:00)",
                    checked = state.settings.dailyReminderEnabled,
                    onCheckedChange = { enabled ->
                        if (!enabled) {
                            viewModel.onDailyReminderChanged(false)
                            return@RowItem
                        }

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            viewModel.onDailyReminderChanged(true)
                            return@RowItem
                        }

                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (granted) {
                            viewModel.onDailyReminderChanged(true)
                        } else {
                            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
            }

            OutlinedButton(
                onClick = viewModel::resetOnboarding,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show onboarding again")
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Feedback", style = MaterialTheme.typography.titleMedium)
                    TextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        label = { Text("Message") }
                    )
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (feedbackImageUri == null) "Attach photo" else "Photo attached")
                    }
                    Button(
                        onClick = {
                            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                                type = if (feedbackImageUri == null) "text/plain" else "image/*"
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_EMAIL))
                                putExtra(Intent.EXTRA_SUBJECT, "Prisma Win feedback")
                                putExtra(Intent.EXTRA_TEXT, feedbackText)
                                feedbackImageUri?.let { uri ->
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                            }
                            context.startActivity(
                                Intent.createChooser(emailIntent, "Send feedback")
                            )
                        },
                        enabled = feedbackText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send email")
                    }
                    Text("Email: $FEEDBACK_EMAIL", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private const val FEEDBACK_EMAIL = "feedback@prism-reaction.example"

@Composable
private fun RowItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
