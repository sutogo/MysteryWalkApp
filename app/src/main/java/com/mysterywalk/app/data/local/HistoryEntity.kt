package com.mysterywalk.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val distanceMeters: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val category: String,
    val imageUrl: String?
)
