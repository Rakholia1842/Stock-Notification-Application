package com.stockalert

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.stockalert.worker.PriceCheckWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class StockAlertApp : Application() {
    override fun onCreate() {
        super.onCreate()
        schedulePriceCheckWorker()
    }

    private fun schedulePriceCheckWorker() {
        val priceCheckRequest = PeriodicWorkRequestBuilder<PriceCheckWorker>(
            15, // interval
            TimeUnit.MINUTES, // interval timeunit
            5, // flexInterval
            TimeUnit.MINUTES // flexInterval timeunit
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "price_check_work",
            ExistingPeriodicWorkPolicy.KEEP,
            priceCheckRequest
        )
    }
}
