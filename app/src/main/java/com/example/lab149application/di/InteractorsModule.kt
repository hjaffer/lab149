package com.example.lab149application.di

import com.example.lab149application.business.interactors.GetSnap
import com.example.lab149application.business.interactors.PostSnap
import com.example.lab149application.business.network.NetworkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object InteractorsModule {

    @Singleton
    @Provides
    fun provideGetSnaps(
        networkDataSource: NetworkDataSource
    ): GetSnap {
        return GetSnap(networkDataSource)
    }

    @Singleton
    @Provides
    fun providePostSnap(
        networkDataSource: NetworkDataSource
    ): PostSnap {
        return PostSnap(networkDataSource)
    }
}
