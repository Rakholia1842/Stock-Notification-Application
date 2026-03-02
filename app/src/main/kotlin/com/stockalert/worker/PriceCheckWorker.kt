package com.stockalert.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stockalert.data.repository.StockRepository
import com.stockalert.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PriceCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: StockRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val activeAlerts = repository.getActiveAlertsOnce()

            if (activeAlerts.isEmpty()) {
                return Result.success()
            }

            // Group alerts by stock symbol
            val alertsByStock = activeAlerts.groupBy { it.stockSymbol }

            for ((symbol, stockAlerts) in alertsByStock) {
                val priceResult = repository.getStockPrice(symbol)

                val stockPrice = priceResult.getOrNull()
                if (stockPrice != null) {
                    for (alert in stockAlerts) {
                        val conditionMet = repository.checkAlertCondition(
                            currentPrice = stockPrice.currentPrice,
                            targetPrice = alert.targetPrice,
                            alertType = alert.alertType
                        )

                        if (conditionMet) {
                            // Trigger notification
                            notificationHelper.showPriceAlertNotification(
                                alertId = alert.id,
                                stockName = alert.stockName,
                                currentPrice = stockPrice.currentPrice,
                                targetPrice = alert.targetPrice
                            )

                            // Mark alert as triggered
                            repository.markAlertAsTriggered(alert.id)
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
