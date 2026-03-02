package com.stockalert.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.stockalert.domain.model.AlertFrequency
import com.stockalert.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

/**
 * Manages user settings and preferences using DataStore
 */
class SettingsManager(private val context: Context) {
    
    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        private val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        private val DEFAULT_ALERT_FREQUENCY = stringPreferencesKey("default_alert_frequency")
        private val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        private val DECIMAL_PLACES = intPreferencesKey("decimal_places")
        private val REFRESH_INTERVAL_MINUTES = intPreferencesKey("refresh_interval_minutes")
        private val DEFAULT_DATA_SOURCE = stringPreferencesKey("default_data_source")
        private val QUIET_HOUR_START = intPreferencesKey("quiet_hour_start")
        private val QUIET_HOUR_END = intPreferencesKey("quiet_hour_end")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[VIBRATION_ENABLED] ?: true,
            defaultAlertFrequency = AlertFrequency.valueOf(
                preferences[DEFAULT_ALERT_FREQUENCY] ?: AlertFrequency.REAL_TIME.name
            ),
            darkModeEnabled = preferences[DARK_MODE_ENABLED] ?: false,
            currencySymbol = preferences[CURRENCY_SYMBOL] ?: "₹",
            decimalPlaces = preferences[DECIMAL_PLACES] ?: 2,
            refreshIntervalMinutes = preferences[REFRESH_INTERVAL_MINUTES] ?: 15
        )
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun setDefaultAlertFrequency(frequency: AlertFrequency) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_ALERT_FREQUENCY] = frequency.name
        }
    }

    suspend fun setCurrencySymbol(symbol: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_SYMBOL] = symbol
        }
    }

    suspend fun setDecimalPlaces(places: Int) {
        context.dataStore.edit { preferences ->
            preferences[DECIMAL_PLACES] = places
        }
    }

    suspend fun setRefreshInterval(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_INTERVAL_MINUTES] = minutes
        }
    }

    suspend fun setDefaultDataSource(source: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_DATA_SOURCE] = source
        }
    }

    suspend fun setQuietHours(startHour: Int?, endHour: Int?) {
        context.dataStore.edit { preferences ->
            if (startHour != null) preferences[QUIET_HOUR_START] = startHour
            if (endHour != null) preferences[QUIET_HOUR_END] = endHour
        }
    }

    suspend fun getDefaultDataSource(): String {
        return context.dataStore.data.map { preferences ->
            preferences[DEFAULT_DATA_SOURCE] ?: "YAHOO_FINANCE"
        }.run {
            this@SettingsManager.context.dataStore.data.map { 
                it[DEFAULT_DATA_SOURCE] ?: "YAHOO_FINANCE" 
            }.run { 
                // Return first emission
                kotlinx.coroutines.flow.first()
            }
        }
    }
}
