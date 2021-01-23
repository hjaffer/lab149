package com.example.lab149application.di

import androidx.fragment.app.FragmentFactory
import com.example.lab149application.framework.presentation.main.MainFragmentFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(ApplicationComponent::class)
object FragmentModule {

    @Singleton
    @Provides
    fun provideMainFragmentFactory(): FragmentFactory {
        return MainFragmentFactory()
    }
}
