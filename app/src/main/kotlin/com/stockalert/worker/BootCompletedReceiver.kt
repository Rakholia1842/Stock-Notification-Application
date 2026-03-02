package com.stockalert.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.BackoffPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.stockalert.utils.AppConstants
import java.util.concurrent.TimeUnit

/**
 * Receiver for handling device boot completion
 * Re-schedules all work requests after device restart
 */
class BootCompletedReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            schedulePeriodicWork(context)
        }
    }

    private fun schedulePeriodicWork(context: Context) {
        val priceCheckWorkRequest = PeriodicWorkRequestBuilder<PriceCheckWorker>(
            AppConstants.PRICE_CHECK_INTERVAL_MINUTES,
            TimeUnit.MINUTES,
            AppConstants.PRICE_CHECK_FLEX_MINUTES,
            TimeUnit.MINUTES
        )
            .setBackoffPolicy(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            AppConstants.PRICE_CHECK_WORK_NAME,
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            priceCheckWorkRequest
        )
    }
}
