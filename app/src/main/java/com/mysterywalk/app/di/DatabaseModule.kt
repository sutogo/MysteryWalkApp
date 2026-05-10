package com.mysterywalk.app.di

import android.content.Context
import androidx.room.Room
import com.mysterywalk.app.data.local.AppDatabase
import com.mysterywalk.app.data.local.BadgeDao
import com.mysterywalk.app.data.local.UserProgressDao
import com.mysterywalk.app.data.repository.GamificationRepositoryImpl
import com.mysterywalk.app.domain.repository.GamificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mystery_walk_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserProgressDao(database: AppDatabase): UserProgressDao {
        return database.userProgressDao()
    }

    @Provides
    fun provideBadgeDao(database: AppDatabase): BadgeDao {
        return database.badgeDao()
    }

    @Provides
    fun provideHistoryDao(database: AppDatabase): com.mysterywalk.app.data.local.HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideGamificationRepository(
        userProgressDao: UserProgressDao
    ): GamificationRepository {
        return GamificationRepositoryImpl(userProgressDao)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        historyDao: com.mysterywalk.app.data.local.HistoryDao
    ): com.mysterywalk.app.domain.repository.HistoryRepository {
        return com.mysterywalk.app.data.repository.HistoryRepositoryImpl(historyDao)
    }
}
