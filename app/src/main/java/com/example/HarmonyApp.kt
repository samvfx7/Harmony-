package com.example

import android.app.Application
import com.example.di.AppContainer
import timber.log.Timber

class HarmonyApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appContainer = AppContainer(this)
        Timber.i("Harmony Music Player application initialized")
    }
}
