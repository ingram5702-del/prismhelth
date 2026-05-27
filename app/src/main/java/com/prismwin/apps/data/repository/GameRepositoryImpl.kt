package com.prismwin.apps.data.repository

import com.prismwin.apps.data.local.GameResultDao
import com.prismwin.apps.data.local.GameResultEntity
import com.prismwin.apps.domain.GameMode
import com.prismwin.apps.domain.GameResult
import com.prismwin.apps.domain.SummaryStats
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(
    private val dao: GameResultDao
) : GameRepository {

    override fun observeSummaryStats(): Flow<SummaryStats> {
        val reactionFlow = combine(
            dao.observeTotalGames(),
            dao.observeBestReaction(GameMode.REACTION.name),
            dao.observeAverageReaction(GameMode.REACTION.name)
        ) { total, bestReaction, avgReaction ->
            Triple(total, bestReaction, avgReaction)
        }

        val skillFlow = combine(
            dao.observeBestHits(GameMode.TARGET.name),
            dao.observeBestLevel(GameMode.SEQUENCE.name),
            dao.observePerfectTargetGames(GameMode.TARGET.name)
        ) { bestHits, bestLevel, perfectTargetGames ->
            Triple(bestHits, bestLevel, perfectTargetGames)
        }

        val aggregateFlow = combine(reactionFlow, skillFlow) { reaction, skill ->
            SummaryStats(
                totalGames = reaction.first,
                bestReactionMs = reaction.second,
                averageReactionMs = reaction.third?.toLong(),
                bestTargetHits = skill.first ?: 0,
                bestSequenceLevel = skill.second ?: 0,
                perfectTargetGames = skill.third
            )
        }

        return combine(aggregateFlow, dao.observeRecent(limit = 365)) { aggregate, recent ->
            val playedDates = recent.mapTo(mutableSetOf()) { entity ->
                Instant.ofEpochMilli(entity.createdAtEpochMs)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }

            aggregate.copy(
                streakDays = calculateStreak(playedDates),
                playedDates = playedDates
            )
        }
    }

    override fun observeRecentResults(limit: Int): Flow<List<GameResult>> {
        return dao.observeRecent(limit).map { entities ->
            entities.map { entity ->
                GameResult(
                    id = entity.id,
                    mode = GameMode.valueOf(entity.mode),
                    reactionTimeMs = entity.reactionTimeMs,
                    hits = entity.hits,
                    misses = entity.misses,
                    levelReached = entity.levelReached,
                    createdAtEpochMs = entity.createdAtEpochMs
                )
            }
        }
    }

    override suspend fun saveReactionResult(reactionTimeMs: Long) {
        dao.insert(
            GameResultEntity(
                mode = GameMode.REACTION.name,
                reactionTimeMs = reactionTimeMs,
                hits = null,
                misses = null,
                levelReached = null,
                createdAtEpochMs = System.currentTimeMillis()
            )
        )
    }

    override suspend fun saveTargetResult(hits: Int, misses: Int) {
        dao.insert(
            GameResultEntity(
                mode = GameMode.TARGET.name,
                reactionTimeMs = null,
                hits = hits,
                misses = misses,
                levelReached = null,
                createdAtEpochMs = System.currentTimeMillis()
            )
        )
    }

    override suspend fun saveSequenceResult(levelReached: Int) {
        dao.insert(
            GameResultEntity(
                mode = GameMode.SEQUENCE.name,
                reactionTimeMs = null,
                hits = null,
                misses = null,
                levelReached = levelReached,
                createdAtEpochMs = System.currentTimeMillis()
            )
        )
    }

    override fun modeLabel(mode: GameMode): String {
        return when (mode) {
            GameMode.REACTION -> "Reaction"
            GameMode.TARGET -> "Accuracy"
            GameMode.SEQUENCE -> "Memory"
        }
    }

    private fun calculateStreak(dates: Set<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        var streak = 0
        var cursor = LocalDate.now()

        while (dates.contains(cursor)) {
            streak += 1
            cursor = cursor.minusDays(1)
        }

        return streak
    }
}
