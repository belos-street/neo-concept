package com.neoconcept.di

import android.content.Context
import com.neoconcept.data.local.AndroidAssetDataSource
import com.neoconcept.data.local.AssetDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideAssetDataSource(
        @ApplicationContext context: Context,
    ): AssetDataSource = AndroidAssetDataSource(context)
}
