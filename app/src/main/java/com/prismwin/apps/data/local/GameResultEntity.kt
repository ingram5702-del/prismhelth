package com.prismwin.apps.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mode: String,
    val reactionTimeMs: Long?,
    val hits: Int?,
    val misses: Int?,
    val levelReached: Int?,
    val createdAtEpochMs: Long
)
