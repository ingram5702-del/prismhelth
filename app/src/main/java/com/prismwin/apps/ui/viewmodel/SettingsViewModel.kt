package com.prismwin.apps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prismwin.apps.data.reminder.ReminderScheduler
import com.prismwin.apps.data.settings.SettingsRepository
import com.prismwin.apps.domain.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: UserSettings = UserSettings()
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun onSoundChanged(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSoundEnabled(enabled)
        }
    }

    fun onVibrationChanged(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setVibrationEnabled(enabled)
        }
    }

    fun onDailyReminderChanged(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDailyReminderEnabled(enabled)
            if (enabled) {
                reminderScheduler.scheduleDailyReminder(hourOfDay = 20, minute = 0)
            } else {
                reminderScheduler.cancelDailyReminder()
            }
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(false)
        }
    }
}
