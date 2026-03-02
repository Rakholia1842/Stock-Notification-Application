package com.stockalert.utils

/**
 * Configuration constants for StockAlert application
 * Modify these values to customize app behavior
 */
object AppConstants {
    // ===== API Configuration =====
    const val YAHOO_FINANCE_BASE_URL = "https://query1.finance.yahoo.com/"
    const val API_TIMEOUT_SECONDS = 30L
    const val NETWORK_RETRY_ATTEMPTS = 3
    
    // ===== Database Configuration =====
    const val DATABASE_NAME = "stock_alert_db"
    const val DATABASE_VERSION = 1
    
    // ===== WorkManager Configuration =====
    const val PRICE_CHECK_WORK_NAME = "price_check_work"
    const val PRICE_CHECK_INTERVAL_MINUTES = 15L // Frequency of price checks
    const val PRICE_CHECK_FLEX_MINUTES = 5L // Flex window for work execution
    
    // ===== Notification Configuration =====
    const val CHANNEL_ID = "stock_alert_channel"
    const val CHANNEL_NAME = "Stock Alerts"
    const val NOTIFICATION_ID_BASE = 1000
    
    // ===== Stock Configuration =====
    val DEFAULT_STOCKS = listOf(
        "RELIANCE", "TCS", "INFY", "HDFC", "ICICIBANK", "AXISBANK",
        "LT", "ITC", "HCLTECH", "BAJAJFINSV", "MARUTI", "SUNPHARMA",
        "SBIN", "KOTAKBANK", "WIPRO", "ASIANPAINT", "BAJAJSSMARTFINSERVE",
        "DMART", "NESTLEIND", "POWERGRID", "DRREDDY"
    )
    
    const val NSE_SUFFIX = ".NS"
    const val BSE_SUFFIX = ".BO"
    
    // ===== UI Configuration =====
    const val SEARCH_RESULT_LIMIT = 10
    const val ALERT_HISTORY_LIMIT = 100
    
    // ===== Price Format Configuration =====
    const val PRICE_DECIMAL_PLACES = 2
    const val PRICE_CURRENCY = "₹"
    
    // ===== Time Configuration =====
    const val ALERT_CHECK_INTERVAL_MS = 15 * 60 * 1000L // 15 minutes
    const val NOTIFICATION_RETRY_DELAY_MS = 1000L // 1 second
    
    // ===== Feature Flags =====
    const val ENABLE_DEBUG_LOGGING = true
    const val ENABLE_NETWORK_LOGGING = true
    const val FORCE_OFFLINE_MODE = false // Set true to test offline functionality
    
    // ===== Error Messages =====
    object ErrorMessages {
        const val NO_INTERNET = "No internet connection. Please check your network."
        const val API_ERROR = "Failed to fetch stock data. Please try again."
        const val DATABASE_ERROR = "Failed to save alert. Please try again."
        const val INVALID_PRICE = "Price must be greater than 0"
        const val INVALID_SYMBOL = "Invalid stock symbol"
        const val INVALID_ALERT_TYPE = "Invalid alert type"
        const val ALERT_NOT_FOUND = "Alert not found"
        const val STOCK_NOT_FOUND = "Stock not found"
        const val UNKNOWN_ERROR = "An unknown error occurred"
    }
    
    // ===== Notification Messages =====
    object NotificationMessages {
        const val ALERT_REACHED = "Alert Reached!"
        const val PRICE_ABOVE = "Price has gone above target"
        const val PRICE_BELOW = "Price has fallen below target"
    }
    
    // ===== UI Text =====
    object UiText {
        const val LOADING = "Loading..."
        const val NO_RESULTS = "No stocks found"
        const val NO_ALERTS = "No alerts yet"
        const val EMPTY_SEARCH = "Search for Indian stocks"
        const val RETRY = "Retry"
        const val CANCEL = "Cancel"
        const val SAVE = "Save"
        const val DELETE = "Delete"
        const val RESET = "Reset"
        const val EDIT = "Edit"
    }
    
    // ===== Date/Time Formats =====
    object DateFormats {
        const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"
        const val DATE_ONLY_FORMAT = "dd/MM/yyyy"
        const val TIME_ONLY_FORMAT = "HH:mm:ss"
    }
}

/**
 * Alert Type Constants
 */
object AlertTypeConstants {
    const val ALERT_TYPE_ABOVE = "ABOVE"
    const val ALERT_TYPE_BELOW = "BELOW"
    
    const val ALERT_DISPLAY_ABOVE = "Price Goes Above"
    const val ALERT_DISPLAY_BELOW = "Price Falls Below"
}

/**
 * Alert Status Constants
 */
object AlertStatusConstants {
    const val STATUS_ACTIVE = "ACTIVE"
    const val STATUS_TRIGGERED = "TRIGGERED"
    
    const val DISPLAY_ACTIVE = "Active"
    const val DISPLAY_TRIGGERED = "Triggered"
}

/**
 * Shared Preferences Keys
 */
object SharedPrefKeys {
    const val PREF_FILE_NAME = "com.stockalert.prefs"
    const val KEY_LAST_SEARCH = "last_search"
    const val KEY_SEARCH_HISTORY = "search_history"
    const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_FIRST_RUN = "first_run"
}

/**
 * Intent/Bundle Keys
 */
object BundleKeys {
    const val STOCK_SYMBOL = "stock_symbol"
    const val ALERT_ID = "alert_id"
    const val STOCK_NAME = "stock_name"
    const val CURRENT_PRICE = "current_price"
    const val TARGET_PRICE = "target_price"
    const val NAVIGATION_TARGET = "navigation_target"
}

/**
 * Navigation Routes
 */
object NavigationRoutes {
    const val HOME = "home"
    const val DETAIL = "detail/{symbol}"
    const val DETAIL_WITH_PARAM = "detail"
    const val ALERTS = "alerts"
}

/**
 * Debug/Logging Configuration
 */
object DebugConfig {
    const val LOG_TAG = "StockAlert"
    const val LOG_API_CALLS = true
    const val LOG_DB_QUERIES = true
    const val LOG_WORKER_EXECUTION = true
    const val LOG_NOTIFICATION_EVENTS = true
}

/**
 * Performance Configuration
 */
object PerformanceConfig {
    const val SCREEN_TRANSITION_DURATION_MS = 300
    const val LOADING_ANIMATION_DURATION_MS = 1500
    const val API_CALL_TIMEOUT_MS = 30000
    const val DB_OPERATION_TIMEOUT_MS = 5000
    const val WORKER_TIMEOUT_MS = 60000 // 1 minute
}

/**
 * Color Codes (can be overridden by theme)
 */
object ColorCodes {
    const val COLOR_POSITIVE = -0xb300c5 // Green
    const val COLOR_NEGATIVE = -0xbb65ca // Red
    const val COLOR_PRIMARY = -0xe56319 // Blue
    const val COLOR_WARNING = -0xbe2600 // Orange
}

/**
 * Volume Formatting Configuration
 */
object VolumeFormat {
    const val BILLION_SUFFIX = "B"
    const val MILLION_SUFFIX = "M"
    const val THOUSAND_SUFFIX = "K"
    
    const val BILLION_THRESHOLD = 1_000_000_000L
    const val MILLION_THRESHOLD = 1_000_000L
    const val THOUSAND_THRESHOLD = 1_000L
}

/**
 * Regex Patterns
 */
object RegexPatterns {
    const val STOCK_SYMBOL_PATTERN = "^[A-Z0-9&-]{1,10}$"
    const val PRICE_PATTERN = "^[0-9]+\\.?[0-9]{0,2}$"
    const val COMPANY_NAME_PATTERN = "^[a-zA-Z0-9\\s&.-]{1,50}$"
}
