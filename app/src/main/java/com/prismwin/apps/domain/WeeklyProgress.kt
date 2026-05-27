package com.prismwin.apps.domain

data class WeeklyProgress(
    val thisWeekScore: Int = 0,
    val lastWeekScore: Int = 0,
    val delta: Int = 0,
    val rankLabel: String = "Bronze",
    val rankProgress: Int = 0,
    val nextRankLabel: String = "Silver"
)
