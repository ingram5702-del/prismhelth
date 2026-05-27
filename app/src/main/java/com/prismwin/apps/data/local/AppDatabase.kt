package com.prismwin.apps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GameResultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameResultDao(): GameResultDao
}
