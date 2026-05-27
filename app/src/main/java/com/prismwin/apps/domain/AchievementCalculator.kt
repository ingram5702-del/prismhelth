package com.prismwin.apps.domain

object AchievementCalculator {
    fun fromStats(stats: SummaryStats): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_10_games",
                title = "Warm-up",
                description = "Play 10 games",
                unlocked = stats.totalGames >= 10
            ),
            Achievement(
                id = "quick_reflex",
                title = "Lightning",
                description = "Reaction faster than 250 ms",
                unlocked = (stats.bestReactionMs ?: Long.MAX_VALUE) < 250
            ),
            Achievement(
                id = "perfect_target",
                title = "Sharpshooter",
                description = "20+ hits with no misses",
                unlocked = stats.perfectTargetGames > 0
            ),
            Achievement(
                id = "seven_day_streak",
                title = "On a streak",
                description = "7 active days in a row",
                unlocked = stats.streakDays >= 7
            )
        )
    }
}
