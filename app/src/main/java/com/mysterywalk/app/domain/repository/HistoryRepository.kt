package com.mysterywalk.app.domain.repository

import com.mysterywalk.app.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun saveHistory(history: HistoryEntity)
    fun getAllHistory(): Flow<List<HistoryEntity>>
}
