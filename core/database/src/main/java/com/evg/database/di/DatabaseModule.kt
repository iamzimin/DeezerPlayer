package com.evg.database.di

import android.content.Context
import androidx.room.Room
import com.evg.database.data.repository.DatabaseRepositoryImpl
import com.evg.database.data.storage.TracksDatabase
import com.evg.database.domain.repository.DatabaseRepository
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
    fun provideTracksDatabase(
        @ApplicationContext context: Context,
    ) : TracksDatabase {
        return Room.databaseBuilder(
            context,
            TracksDatabase::class.java,
            TracksDatabase.DATABASE_NAME,
        ).build()
    }

    @Provides
    @Singleton
    fun provideDatabaseRepository(
        tracksDatabase: TracksDatabase,
    ): DatabaseRepository {
        return DatabaseRepositoryImpl(
            tracksDatabase = tracksDatabase,
        )
    }
}