package com.prismwin.apps.domain

data class UserSettings(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val dailyReminderEnabled: Boolean = false
)
