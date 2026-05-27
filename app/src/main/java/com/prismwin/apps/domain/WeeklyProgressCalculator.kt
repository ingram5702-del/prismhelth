package com.prismwin.apps.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object WeeklyProgressCalculator {

    fun build(results: List<GameResult>): WeeklyProgress {
        val today = LocalDate.now()
        val thisWeekStart = today.with(DayOfWeek.MONDAY)
        val lastWeekStart = thisWeekStart.minusWeeks(1)
        val lastWeekEnd = thisWeekStart.minusDays(1)

        val thisWeek = results.filter { dateOf(it) >= thisWeekStart }
        val lastWeek = results.filter { dateOf(it) in lastWeekStart..lastWeekEnd }

        val thisScore = weekScore(thisWeek)
        val lastScore = weekScore(lastWeek)
        val delta = thisScore - lastScore

        val rank = when {
            thisScore >= 160 -> "Platinum"
            thisScore >= 110 -> "Gold"
            thisScore >= 60 -> "Silver"
            else -> "Bronze"
        }

        val nextRank = when (rank) {
            "Bronze" -> "Silver"
            "Silver" -> "Gold"
            "Gold" -> "Platinum"
            else -> "Max"
        }

        val currentThreshold = when (rank) {
            "Bronze" -> 0
            "Silver" -> 60
            "Gold" -> 110
            else -> 160
        }
        val nextThreshold = when (rank) {
            "Bronze" -> 60
            "Silver" -> 110
            "Gold" -> 160
            else -> 160
        }

        val progressPercent = if (nextThreshold == currentThreshold) {
            100
        } else {
            (((thisScore - currentThreshold).toFloat() / (nextThreshold - currentThreshold).toFloat()) * 100f)
                .toInt()
                .coerceIn(0, 100)
        }

        return WeeklyProgress(
            thisWeekScore = thisScore,
            lastWeekScore = lastScore,
            delta = delta,
            rankLabel = rank,
            rankProgress = progressPercent,
            nextRankLabel = nextRank
        )
    }

    private fun weekScore(results: List<GameResult>): Int {
        if (results.isEmpty()) return 0

        var score = 0
        results.forEach { result ->
            score += when (result.mode) {
                GameMode.REACTION -> {
                    val reaction = result.reactionTimeMs ?: 999L
                    when {
                        reaction <= 220 -> 18
                        reaction <= 260 -> 14
                        reaction <= 320 -> 10
                        else -> 6
                    }
                }
                GameMode.TARGET -> {
                    val hits = result.hits ?: 0
                    5 + hits
                }
                GameMode.SEQUENCE -> {
                    val level = result.levelReached ?: 0
                    6 + (level * 3)
                }
            }
        }
        return score
    }

    private fun dateOf(result: GameResult): LocalDate {
        return Instant.ofEpochMilli(result.createdAtEpochMs)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
