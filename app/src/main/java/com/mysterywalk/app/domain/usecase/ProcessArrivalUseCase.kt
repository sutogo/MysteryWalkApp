package com.mysterywalk.app.domain.usecase

import com.mysterywalk.app.domain.repository.GamificationRepository
import javax.inject.Inject

data class ArrivalResult(
    val earnedXp: Int,
    val newTotalXp: Int,
    val isLevelUp: Boolean,
    val newLevel: Int
)

class ProcessArrivalUseCase @Inject constructor(
    private val gamificationRepository: GamificationRepository
) {
    suspend operator fun invoke(walkedDistanceMeters: Int): ArrivalResult {
        // Calculate XP based on distance: 10 XP for every 100 meters walked
        val earnedXp = (walkedDistanceMeters / 100) * 10
        val finalEarnedXp = if (earnedXp <= 0) 10 else earnedXp // Minimum 10 XP

        val oldProgress = gamificationRepository.addXp(0) // Get current without modifying
        val newProgress = gamificationRepository.addXp(finalEarnedXp)

        val isLevelUp = newProgress.currentLevel > oldProgress.currentLevel

        return ArrivalResult(
            earnedXp = finalEarnedXp,
            newTotalXp = newProgress.totalXp,
            isLevelUp = isLevelUp,
            newLevel = newProgress.currentLevel
        )
    }
}
