package com.prismwin.apps.data.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ReminderSchedulerImpl(
    private val context: Context
) : ReminderScheduler {

    override fun scheduleDailyReminder(hourOfDay: Int, minute: Int) {
        val now = LocalDateTime.now()
        var target = now.withHour(hourOfDay).withMinute(minute).withSecond(0).withNano(0)
        if (!target.isAfter(now)) {
            target = target.plusDays(1)
        }

        val initialDelay = Duration.between(now, target)

        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay)
            .addTag(DAILY_REMINDER_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    override fun cancelDailyReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_REMINDER_WORK)
    }

    private companion object {
        const val DAILY_REMINDER_WORK = "daily_challenge_reminder_work"
        const val DAILY_REMINDER_TAG = "daily_challenge_reminder_tag"
    }
}
