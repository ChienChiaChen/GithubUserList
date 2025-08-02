package com.example.githubuserlist.di

import com.example.githubuserlist.data.api.GitHubApiService
import com.example.githubuserlist.data.repository.GitHubRepository
import com.example.githubuserlist.data.repository.GitHubRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideGitHubRepository(apiService: GitHubApiService): GitHubRepository {
        return GitHubRepositoryImpl(apiService)
    }
} 