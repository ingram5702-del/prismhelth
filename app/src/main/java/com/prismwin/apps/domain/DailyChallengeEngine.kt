package com.prismwin.apps.domain

import java.time.LocalDate

object DailyChallengeEngine {

    fun buildChallenges(
        date: LocalDate,
        todayResults: List<GameResult>
    ): List<DailyChallenge> {
        val seed = date.dayOfYear

        val reactionGamesTarget = 3 + (seed % 2)
        val reactionThreshold = 260L + (seed % 5) * 10L
        val targetHitsGoal = 14 + (seed % 5)
        val sequenceGoal = 4 + (seed % 3)

        val reactionResults = todayResults.filter { it.mode == GameMode.REACTION }
        val targetResults = todayResults.filter { it.mode == GameMode.TARGET }
        val sequenceResults = todayResults.filter { it.mode == GameMode.SEQUENCE }

        val reactionCount = reactionResults.size
        val bestReaction = reactionResults.mapNotNull { it.reactionTimeMs }.minOrNull() ?: Long.MAX_VALUE
        val bestTargetHits = targetResults.mapNotNull { it.hits }.maxOrNull() ?: 0
        val bestSequence = sequenceResults.mapNotNull { it.levelReached }.maxOrNull() ?: 0

        return listOf(
            DailyChallenge(
                id = "reaction_sessions",
                title = "Reaction: volume",
                description = "Play $reactionGamesTarget reaction round(s)",
                progress = reactionCount.coerceAtMost(reactionGamesTarget),
                target = reactionGamesTarget,
                isCompleted = reactionCount >= reactionGamesTarget
            ),
            DailyChallenge(
                id = "target_hits",
                title = "Accuracy: max score",
                description = "Get at least $targetHitsGoal hits in one round",
                progress = bestTargetHits.coerceAtMost(targetHitsGoal),
                target = targetHitsGoal,
                isCompleted = bestTargetHits >= targetHitsGoal
            ),
            DailyChallenge(
                id = "sequence_or_reaction",
                title = "Control",
                description = "Either reaction ≤ $reactionThreshold ms or memory level $sequenceGoal",
                progress = when {
                    bestReaction <= reactionThreshold -> 1
                    bestSequence >= sequenceGoal -> 1
                    else -> 0
                },
                target = 1,
                isCompleted = bestReaction <= reactionThreshold || bestSequence >= sequenceGoal
            )
        )
    }
}
