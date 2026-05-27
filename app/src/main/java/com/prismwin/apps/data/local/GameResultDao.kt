package com.prismwin.apps.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: GameResultEntity)

    @Query("SELECT * FROM game_results ORDER BY createdAtEpochMs DESC LIMIT :limit")
    fun observeRecent(limit: Int = 100): Flow<List<GameResultEntity>>

    @Query("SELECT COUNT(*) FROM game_results")
    fun observeTotalGames(): Flow<Int>

    @Query("SELECT MIN(reactionTimeMs) FROM game_results WHERE mode = :mode")
    fun observeBestReaction(mode: String): Flow<Long?>

    @Query("SELECT AVG(reactionTimeMs) FROM game_results WHERE mode = :mode")
    fun observeAverageReaction(mode: String): Flow<Double?>

    @Query("SELECT MAX(hits) FROM game_results WHERE mode = :mode")
    fun observeBestHits(mode: String): Flow<Int?>

    @Query("SELECT MAX(levelReached) FROM game_results WHERE mode = :mode")
    fun observeBestLevel(mode: String): Flow<Int?>

    @Query("SELECT COUNT(*) FROM game_results WHERE mode = :mode AND misses = 0 AND hits >= 20")
    fun observePerfectTargetGames(mode: String): Flow<Int>
}
