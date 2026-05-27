package com.prismwin.apps.data.reminder

interface ReminderScheduler {
    fun scheduleDailyReminder(hourOfDay: Int = 20, minute: Int = 0)
    fun cancelDailyReminder()
}
