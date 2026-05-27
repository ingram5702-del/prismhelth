package com.prismwin.apps

import com.prismwin.apps.domain.GameMode
import com.prismwin.apps.domain.GameResult
import com.prismwin.apps.domain.WeeklyProgressCalculator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeeklyProgressCalculatorTest {

    @Test
    fun weeklyDeltaPositiveWhenCurrentWeekBetter() {
        val monday = LocalDate.now().with(DayOfWeek.MONDAY)
        val lastMonday = monday.minusWeeks(1)

        val thisWeekResults = listOf(
            result(GameMode.REACTION, monday.plusDays(1), reaction = 230),
            result(GameMode.TARGET, monday.plusDays(2), hits = 18),
            result(GameMode.SEQUENCE, monday.plusDays(3), level = 5)
        )

        val lastWeekResults = listOf(
            result(GameMode.REACTION, lastMonday.plusDays(1), reaction = 340),
            result(GameMode.TARGET, lastMonday.plusDays(2), hits = 8)
        )

        val progress = WeeklyProgressCalculator.build(thisWeekResults + lastWeekResults)

        assertTrue(progress.thisWeekScore > progress.lastWeekScore)
        assertTrue(progress.delta > 0)
    }

    @Test
    fun rankStartsFromBronzeWhenNoData() {
        val progress = WeeklyProgressCalculator.build(emptyList())
        assertEquals("Bronze", progress.rankLabel)
        assertEquals(0, progress.thisWeekScore)
    }

    private fun result(
        mode: GameMode,
        date: LocalDate,
        reaction: Long? = null,
        hits: Int? = null,
        level: Int? = null
    ): GameResult {
        val epoch = date.atTime(12, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return GameResult(
            id = 0,
            mode = mode,
            reactionTimeMs = reaction,
            hits = hits,
            misses = null,
            levelReached = level,
            createdAtEpochMs = epoch
        )
    }
}
