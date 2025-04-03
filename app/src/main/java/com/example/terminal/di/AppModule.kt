package com.example.terminal.di

import com.example.terminal.data.network.ApiFactory
import com.example.terminal.data.network.ApiService
import com.example.terminal.data.repository.TerminalRepositoryImpl
import com.example.terminal.domain.repository.TerminalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiFactory(): ApiFactory {
        return ApiFactory()
    }

    @Provides
    @Singleton
    fun provideApiService(apiFactory: ApiFactory): ApiService {
        return apiFactory.apiService
    }

    @Provides
    @Singleton
    fun provideTerminalRepository(
        apiService: ApiService
    ): TerminalRepository {
        return TerminalRepositoryImpl(apiService)
    }
} 