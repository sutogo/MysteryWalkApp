package com.mysterywalk.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserProgressEntity::class, BadgeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
    abstract fun badgeDao(): BadgeDao
}
