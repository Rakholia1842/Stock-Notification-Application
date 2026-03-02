package com.stockalert.data.preferences

import com.stockalert.data.db.NotificationSettingsDao
import com.stockalert.data.db.NotificationSettingsEntity
import com.stockalert.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing notification preferences per stock
 */
class NotificationPreferencesRepository(
    private val notificationSettingsDao: NotificationSettingsDao
) {

    suspend fun setNotificationSettings(settings: NotificationSettings) {
        notificationSettingsDao.insertNotificationSettings(
            NotificationSettingsEntity(
                stockSymbol = settings.stockSymbol,
                notificationsEnabled = settings.notificationsEnabled,
                soundEnabled = settings.soundEnabled,
                vibrationEnabled = settings.vibrationEnabled,
                emailNotifications = settings.emailNotifications,
                smsNotifications = settings.smsNotifications,
                quietHourStart = settings.quietHourStart,
                quietHourEnd = settings.quietHourEnd
            )
        )
    }

    suspend fun getNotificationSettings(symbol: String): NotificationSettings? {
        val entity = notificationSettingsDao.getNotificationSettings(symbol) ?: return null
        return NotificationSettings(
            stockSymbol = entity.stockSymbol,
            notificationsEnabled = entity.notificationsEnabled,
            soundEnabled = entity.soundEnabled,
            vibrationEnabled = entity.vibrationEnabled,
            emailNotifications = entity.emailNotifications,
            smsNotifications = entity.smsNotifications,
            quietHourStart = entity.quietHourStart,
            quietHourEnd = entity.quietHourEnd
        )
    }

    fun getAllNotificationSettings(): Flow<List<NotificationSettings>> {
        return notificationSettingsDao.getAllNotificationSettings().map { entities ->
            entities.map { entity ->
                NotificationSettings(
                    stockSymbol = entity.stockSymbol,
                    notificationsEnabled = entity.notificationsEnabled,
                    soundEnabled = entity.soundEnabled,
                    vibrationEnabled = entity.vibrationEnabled,
                    emailNotifications = entity.emailNotifications,
                    smsNotifications = entity.smsNotifications,
                    quietHourStart = entity.quietHourStart,
                    quietHourEnd = entity.quietHourEnd
                )
            }
        }
    }

    suspend fun deleteNotificationSettings(symbol: String) {
        val settings = notificationSettingsDao.getNotificationSettings(symbol)
        settings?.let {
            notificationSettingsDao.deleteNotificationSettings(it)
        }
    }

    suspend fun enableNotifications(symbol: String, enabled: Boolean) {
        val settings = notificationSettingsDao.getNotificationSettings(symbol)
        if (settings != null) {
            notificationSettingsDao.updateNotificationSettings(
                settings.copy(notificationsEnabled = enabled)
            )
        }
    }

    suspend fun enableSound(symbol: String, enabled: Boolean) {
        val settings = notificationSettingsDao.getNotificationSettings(symbol)
        if (settings != null) {
            notificationSettingsDao.updateNotificationSettings(
                settings.copy(soundEnabled = enabled)
            )
        }
    }

    suspend fun enableVibration(symbol: String, enabled: Boolean) {
        val settings = notificationSettingsDao.getNotificationSettings(symbol)
        if (settings != null) {
            notificationSettingsDao.updateNotificationSettings(
                settings.copy(vibrationEnabled = enabled)
            )
        }
    }

    suspend fun setQuietHours(symbol: String, startHour: Int, endHour: Int) {
        val settings = notificationSettingsDao.getNotificationSettings(symbol)
        if (settings != null) {
            notificationSettingsDao.updateNotificationSettings(
                settings.copy(quietHourStart = startHour, quietHourEnd = endHour)
            )
        }
    }

    suspend fun isInQuietHour(symbol: String): Boolean {
        val settings = getNotificationSettings(symbol) ?: return false
        if (settings.quietHourStart == null || settings.quietHourEnd == null) return false

        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return if (settings.quietHourStart < settings.quietHourEnd) {
            currentHour in settings.quietHourStart until settings.quietHourEnd
        } else {
            currentHour >= settings.quietHourStart || currentHour < settings.quietHourEnd
        }
    }

    suspend fun shouldNotify(symbol: String): Boolean {
        val settings = getNotificationSettings(symbol) ?: return true
        if (!settings.notificationsEnabled) return false
        return !isInQuietHour(symbol)
    }
}
