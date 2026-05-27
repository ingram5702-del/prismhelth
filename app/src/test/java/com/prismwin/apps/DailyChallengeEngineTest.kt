package com.prismwin.apps

import com.prismwin.apps.domain.DailyChallengeEngine
import com.prismwin.apps.domain.GameMode
import com.prismwin.apps.domain.GameResult
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyChallengeEngineTest {

    @Test
    fun reactionVolumeChallengeTracksProgress() {
        val date = LocalDate.of(2026, 5, 21)
        val challenges = DailyChallengeEngine.buildChallenges(
            date = date,
            todayResults = listOf(
                result(GameMode.REACTION, reaction = 300),
                result(GameMode.REACTION, reaction = 290)
            )
        )

        val challenge = challenges.first { it.id == "reaction_sessions" }
        assertEquals(2, challenge.progress)
        assertTrue(!challenge.isCompleted)
    }

    @Test
    fun controlChallengeCompletesWhenSequenceGoalReached() {
        val date = LocalDate.of(2026, 5, 21)
        val challenges = DailyChallengeEngine.buildChallenges(
            date = date,
            todayResults = listOf(result(GameMode.SEQUENCE, level = 8))
        )

        val challenge = challenges.first { it.id == "sequence_or_reaction" }
        assertEquals(1, challenge.progress)
        assertTrue(challenge.isCompleted)
    }

    private fun result(
        mode: GameMode,
        reaction: Long? = null,
        hits: Int? = null,
        misses: Int? = null,
        level: Int? = null
    ): GameResult {
        return GameResult(
            id = 0,
            mode = mode,
            reactionTimeMs = reaction,
            hits = hits,
            misses = misses,
            levelReached = level,
            createdAtEpochMs = System.currentTimeMillis()
        )
    }
}
