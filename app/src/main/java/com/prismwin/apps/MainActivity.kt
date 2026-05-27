package com.prismwin.apps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.prismwin.apps.ui.navigation.PrismNavHost
import com.prismwin.apps.ui.theme.PrismTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as PrismApplication).appContainer
        lifecycleScope.launch {
            val settings = container.settingsRepository.settings.first()
            if (settings.dailyReminderEnabled) {
                container.reminderScheduler.scheduleDailyReminder(hourOfDay = 20, minute = 0)
            } else {
                container.reminderScheduler.cancelDailyReminder()
            }
        }

        setContent {
            PrismTheme {
                PrismNavHost()
            }
        }
    }
}
