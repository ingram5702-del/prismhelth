package com.prismwin.apps

import com.prismwin.apps.domain.AchievementCalculator
import com.prismwin.apps.domain.SummaryStats
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementCalculatorTest {

    @Test
    fun quickReflexUnlockedWhenReactionUnder250() {
        val achievements = AchievementCalculator.fromStats(
            SummaryStats(
                totalGames = 3,
                bestReactionMs = 240,
                perfectTargetGames = 0,
                streakDays = 1
            )
        )

        val quickReflex = achievements.first { it.id == "quick_reflex" }
        assertTrue(quickReflex.unlocked)
    }

    @Test
    fun streakLockedWhenLessThanSevenDays() {
        val achievements = AchievementCalculator.fromStats(
            SummaryStats(
                totalGames = 20,
                bestReactionMs = 210,
                perfectTargetGames = 1,
                streakDays = 4
            )
        )

        val streak = achievements.first { it.id == "seven_day_streak" }
        assertFalse(streak.unlocked)
    }
}
