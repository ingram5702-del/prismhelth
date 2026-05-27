package com.prismwin.apps.domain

data class GameResult(
    val id: Long,
    val mode: GameMode,
    val reactionTimeMs: Long?,
    val hits: Int?,
    val misses: Int?,
    val levelReached: Int?,
    val createdAtEpochMs: Long
)
