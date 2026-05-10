package com.mysterywalk.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1, // Only one row for the user's progress
    val totalXp: Int = 0,
    val currentLevel: Int = 1
)
