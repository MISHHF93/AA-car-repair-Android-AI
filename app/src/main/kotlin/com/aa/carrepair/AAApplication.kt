package com.aa.carrepair

import android.app.Application
import androidx.work.Configuration
import com.aa.carrepair.sync.SyncManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AAApplication : Application(), Configuration.Provider {

    @Inject lateinit var syncManager: SyncManager

    override fun onCreate() {
        super.onCreate()
        initLogging()
        syncManager.schedulePeriodicSync()
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
