package com.example.ozonpricetracking

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ozonpricetracking.core.logging.CrashlyticsTree
import com.example.ozonpricetracking.data.worker.PriceUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() , Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        Timber.plant(CrashlyticsTree())

        setupPriceTrackingWorker()
    }


    private fun setupPriceTrackingWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<PriceUpdateWorker>(
            PERIOD, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(PERIOD, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "OzonPriceTrackingWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            repeatingRequest
        )
    }

    companion object {
        const val PERIOD: Long = 6
    }
}
