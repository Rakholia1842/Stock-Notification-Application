package com.stockalert.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.stockalert.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "stock_alert_channel"
        const val CHANNEL_NAME = "Stock Alerts"
        const val NOTIFICATION_ID_BASE = 1000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifications for stock price alerts"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPriceAlertNotification(
        alertId: Int,
        stockName: String,
        currentPrice: Double,
        targetPrice: Double
    ) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("alertId", alertId)
            putExtra("stock", stockName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alertId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$stockName Alert!")
            .setContentText("Price: ₹${String.format("%.2f", currentPrice)} (Target: ₹${String.format("%.2f", targetPrice)})")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$stockName has reached ₹${String.format("%.2f", currentPrice)}!\nYour target was ₹${String.format("%.2f", targetPrice)}")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        val notificationId = NOTIFICATION_ID_BASE + alertId
        notificationManager.notify(notificationId, notification)
    }
}

/**
 * Broadcast receiver for handling notification actions
 */
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val alertId = intent.getIntExtra("alertId", -1)
            if (alertId != -1) {
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("alertId", alertId)
                }
                context.startActivity(mainIntent)
            }
        }
    }
}
