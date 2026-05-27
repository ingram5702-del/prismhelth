package com.prismwin.apps.domain

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int,
    val target: Int,
    val isCompleted: Boolean
)
