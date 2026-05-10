package com.mysterywalk.app.data.repository

import com.mysterywalk.app.data.local.UserProgressDao
import com.mysterywalk.app.data.local.UserProgressEntity
import com.mysterywalk.app.domain.repository.GamificationRepository
import com.mysterywalk.app.domain.repository.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamificationRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao
) : GamificationRepository {

    override fun getUserProgress(): Flow<UserProgress> {
        return userProgressDao.getUserProgress().map { entity ->
            if (entity == null) {
                UserProgress(totalXp = 0, currentLevel = 1)
            } else {
                UserProgress(totalXp = entity.totalXp, currentLevel = entity.currentLevel)
            }
        }
    }

    override suspend fun addXp(xp: Int): UserProgress {
        val currentEntity = userProgressDao.getUserProgressSync() ?: UserProgressEntity()
        val newXp = currentEntity.totalXp + xp
        
        // Simple calculation: 100 XP per level. level = 1 + (totalXp / 100)
        val newLevel = 1 + (newXp / 100)

        val updatedEntity = currentEntity.copy(totalXp = newXp, currentLevel = newLevel)
        userProgressDao.insertOrUpdate(updatedEntity)

        return UserProgress(totalXp = newXp, currentLevel = newLevel)
    }
}
