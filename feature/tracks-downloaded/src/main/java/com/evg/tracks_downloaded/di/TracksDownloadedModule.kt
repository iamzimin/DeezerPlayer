package com.evg.tracks_downloaded.di

import com.evg.database.domain.repository.DatabaseRepository
import com.evg.tracks_downloaded.data.repository.TracksDownloadedRepositoryImpl
import com.evg.tracks_downloaded.domain.repository.TracksDownloadedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TracksDownloadedModule {

    @Provides
    @Singleton
    fun provideTracksDownloadedRepository(
        databaseRepository: DatabaseRepository,
    ): TracksDownloadedRepository {
        return TracksDownloadedRepositoryImpl(
            databaseRepository = databaseRepository,
        )
    }
}