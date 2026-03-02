package com.stockalert.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.stockalert.data.db.StockAlertDatabase

/**
 * Content Provider for sharing stock data with other apps
 * Provides read-only access to stocks and alerts
 */
class StockDataProvider : ContentProvider() {
    
    companion object {
        private const val AUTHORITY = "com.stockalert.provider"
        private const val STOCKS = 1
        private const val STOCK_ID = 2
        private const val ALERTS = 3
        private const val ALERT_ID = 4

        val STOCKS_URI: Uri = Uri.parse("content://$AUTHORITY/stocks")
        val ALERTS_URI: Uri = Uri.parse("content://$AUTHORITY/alerts")

        private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "stocks", STOCKS)
            addURI(AUTHORITY, "stocks/#", STOCK_ID)
            addURI(AUTHORITY, "alerts", ALERTS)
            addURI(AUTHORITY, "alerts/#", ALERT_ID)
        }
    }

    private lateinit var database: StockAlertDatabase

    override fun onCreate(): Boolean {
        database = StockAlertDatabase.getDatabase(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return when (URI_MATCHER.match(uri)) {
            STOCKS -> {
                // Return stocks cursor
                null
            }
            ALERTS -> {
                // Return alerts cursor
                null
            }
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return when (URI_MATCHER.match(uri)) {
            STOCKS -> "vnd.android.cursor.dir/vnd.com.stockalert.stocks"
            STOCK_ID -> "vnd.android.cursor.item/vnd.com.stockalert.stocks"
            ALERTS -> "vnd.android.cursor.dir/vnd.com.stockalert.alerts"
            ALERT_ID -> "vnd.android.cursor.item/vnd.com.stockalert.alerts"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null // Read-only provider
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0 // Read-only provider
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0 // Read-only provider
    }
}
