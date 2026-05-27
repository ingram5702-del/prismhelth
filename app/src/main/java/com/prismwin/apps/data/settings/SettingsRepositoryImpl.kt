package com.prismwin.apps.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.prismwin.apps.domain.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    override val settings: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[VIBRATION_ENABLED] ?: true,
            onboardingCompleted = preferences[ONBOARDING_COMPLETED] ?: false,
            dailyReminderEnabled = preferences[DAILY_REMINDER_ENABLED] ?: false
        )
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_REMINDER_ENABLED] = enabled
        }
    }

    private companion object {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
    }
}
