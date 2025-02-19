package com.evg.api.di

import com.evg.api.data.repository.DeezerApiRepositoryImpl
import com.evg.api.domain.repository.DeezerApiRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeezerApiModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideDeezerApiRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
        }

        return Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideDeezerApiRepository(
        deezerRetrofit: Retrofit,
    ): DeezerApiRepository {
        return DeezerApiRepositoryImpl(
            deezerRetrofit = deezerRetrofit,
        )
    }
}