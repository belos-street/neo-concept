package com.neoconcept.di

import com.neoconcept.data.repository.ContentRepository
import com.neoconcept.data.repository.ContentRepositoryImpl
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.data.repository.ProgressRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindContentRepository(impl: ContentRepositoryImpl): ContentRepository

    @Binds
    fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository
}
