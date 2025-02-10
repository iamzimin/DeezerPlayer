package com.evg.api.di

import android.content.Context
import com.evg.api.data.repository.DeezerApiRepositoryImpl
import com.evg.api.domain.repository.DeezerApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeezerApiModule {

    @Provides
    @Singleton
    fun provideDeezerApiRetrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    @Provides
    @Singleton
    fun provideDeezerApiRepository(
        //@ApplicationContext context: Context,
        deezerRetrofit: Retrofit,
    ): DeezerApiRepository {
        return DeezerApiRepositoryImpl(
            //context = context,
            deezerRetrofit = deezerRetrofit,
        )
    }
}