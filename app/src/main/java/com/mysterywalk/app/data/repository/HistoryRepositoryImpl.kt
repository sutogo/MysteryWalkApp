package com.mysterywalk.app.data.repository

import com.mysterywalk.app.data.local.HistoryDao
import com.mysterywalk.app.data.local.HistoryEntity
import com.mysterywalk.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {
    override suspend fun saveHistory(history: HistoryEntity) {
        historyDao.insertHistory(history)
    }

    override fun getAllHistory(): Flow<List<HistoryEntity>> {
        return historyDao.getAllHistory()
    }
}
