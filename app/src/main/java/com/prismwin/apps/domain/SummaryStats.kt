package com.prismwin.apps.domain

import java.time.LocalDate

data class SummaryStats(
    val totalGames: Int = 0,
    val bestReactionMs: Long? = null,
    val averageReactionMs: Long? = null,
    val bestTargetHits: Int = 0,
    val bestSequenceLevel: Int = 0,
    val perfectTargetGames: Int = 0,
    val streakDays: Int = 0,
    val playedDates: Set<LocalDate> = emptySet()
)
