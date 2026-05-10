package com.mysterywalk.app.domain.repository

import kotlinx.coroutines.flow.Flow

data class UserProgress(
    val totalXp: Int,
    val currentLevel: Int
)

interface GamificationRepository {
    fun getUserProgress(): Flow<UserProgress>
    suspend fun addXp(xp: Int): UserProgress
}
