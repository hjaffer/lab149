package com.example.lab149application.framework.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Lab149Application : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
