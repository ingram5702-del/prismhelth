package com.prismwin.apps

import android.app.Application
import androidx.room.Room
import com.prismwin.apps.data.local.AppDatabase
import com.prismwin.apps.data.reminder.ReminderScheduler
import com.prismwin.apps.data.reminder.ReminderSchedulerImpl
import com.prismwin.apps.data.repository.GameRepository
import com.prismwin.apps.data.repository.GameRepositoryImpl
import com.prismwin.apps.data.settings.SettingsRepository
import com.prismwin.apps.data.settings.SettingsRepositoryImpl
import com.prismwin.apps.data.web.FirebaseConfigInitializer
import com.prismwin.apps.data.web.FirestoreWebConfigRepository
import com.prismwin.apps.data.web.WebConfigRepository

class PrismApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}

interface AppContainer {
    val gameRepository: GameRepository
    val settingsRepository: SettingsRepository
    val reminderScheduler: ReminderScheduler
    val webConfigRepository: WebConfigRepository
}

private class DefaultAppContainer(application: Application) : AppContainer {
    private val database: AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "prism_database"
    ).build()

    override val gameRepository: GameRepository = GameRepositoryImpl(database.gameResultDao())
    override val settingsRepository: SettingsRepository = SettingsRepositoryImpl(application)
    override val reminderScheduler: ReminderScheduler = ReminderSchedulerImpl(application)
    override val webConfigRepository: WebConfigRepository = FirestoreWebConfigRepository(
        FirebaseConfigInitializer(application)
    )
}
