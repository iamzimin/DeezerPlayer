package com.evg.chart.di

import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.chart.domain.repository.ChartRepository
import com.evg.chart.data.repository.ChartRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChartModule {

    @Provides
    @Singleton
    fun provideChartRepositoryModule(
        apiRepository: DeezerApiRepository,
    ): ChartRepository {
        return ChartRepositoryImpl(
            apiRepository = apiRepository,
        )
    }
}