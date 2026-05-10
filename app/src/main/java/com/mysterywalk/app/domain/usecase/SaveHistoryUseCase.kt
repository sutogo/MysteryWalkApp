package com.mysterywalk.app.domain.usecase

import com.mysterywalk.app.data.local.HistoryEntity
import com.mysterywalk.app.domain.repository.HistoryRepository
import javax.inject.Inject

class SaveHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(
        distanceMeters: Int,
        lat: Double,
        lon: Double,
        name: String,
        category: String,
        imageUrl: String?
    ) {
        val history = HistoryEntity(
            timestamp = System.currentTimeMillis(),
            distanceMeters = distanceMeters,
            lat = lat,
            lon = lon,
            name = name,
            category = category,
            imageUrl = imageUrl
        )
        historyRepository.saveHistory(history)
    }
}
