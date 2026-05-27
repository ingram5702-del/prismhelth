package com.prismwin.apps.data.repository

import com.prismwin.apps.domain.GameMode
import com.prismwin.apps.domain.GameResult
import com.prismwin.apps.domain.SummaryStats
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun observeSummaryStats(): Flow<SummaryStats>
    fun observeRecentResults(limit: Int = 100): Flow<List<GameResult>>

    suspend fun saveReactionResult(reactionTimeMs: Long)
    suspend fun saveTargetResult(hits: Int, misses: Int)
    suspend fun saveSequenceResult(levelReached: Int)

    fun modeLabel(mode: GameMode): String
}
