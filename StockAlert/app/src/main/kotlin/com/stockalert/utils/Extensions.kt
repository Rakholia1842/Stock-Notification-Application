package com.stockalert.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

/**
 * Extension functions for common operations
 */

// ===== String Extensions =====

fun String.formatAsStockSymbol(): String {
    return if (this.endsWith(AppConstants.NSE_SUFFIX, ignoreCase = true)) {
        this
    } else {
        "$this${AppConstants.NSE_SUFFIX}"
    }
}

fun String.removeMarketSuffix(): String {
    return this.removeSuffix(AppConstants.NSE_SUFFIX)
        .removeSuffix(AppConstants.BSE_SUFFIX)
}

fun String.isValidStockSymbol(): Boolean {
    return Regex(RegexPatterns.STOCK_SYMBOL_PATTERN).matches(this)
}

fun String.isValidPrice(): Boolean {
    return Regex(RegexPatterns.PRICE_PATTERN).matches(this)
}

// ===== Double Extensions =====

fun Double.formatAsPrice(): String {
    return "${AppConstants.PRICE_CURRENCY}${String.format("%.${AppConstants.PRICE_DECIMAL_PLACES}f", this)}"
}

fun Double.formatAsPercentage(): String {
    val sign = if (this >= 0) "+" else ""
    return "$sign${String.format("%.2f", this)}%"
}

fun Double.formatAsVolume(): String {
    return when {
        this >= VolumeFormat.BILLION_THRESHOLD -> 
            "${String.format("%.2f", this / VolumeFormat.BILLION_THRESHOLD)} ${VolumeFormat.BILLION_SUFFIX}"
        this >= VolumeFormat.MILLION_THRESHOLD ->
            "${String.format("%.2f", this / VolumeFormat.MILLION_THRESHOLD)} ${VolumeFormat.MILLION_SUFFIX}"
        this >= VolumeFormat.THOUSAND_THRESHOLD ->
            "${String.format("%.2f", this / VolumeFormat.THOUSAND_THRESHOLD)} ${VolumeFormat.THOUSAND_SUFFIX}"
        else -> this.toLong().toString()
    }
}

fun Double.isPositive(): Boolean = this >= 0.0

fun Double.isNegative(): Boolean = this < 0.0

// ===== Long Extensions =====

fun Long.formatAsPhone(): String {
    if (this > 0) {
        when {
            this >= VolumeFormat.BILLION_THRESHOLD -> 
                return "${String.format("%.2f", this / VolumeFormat.BILLION_THRESHOLD)} ${VolumeFormat.BILLION_SUFFIX}"
            this >= VolumeFormat.MILLION_THRESHOLD ->
                return "${String.format("%.2f", this / VolumeFormat.MILLION_THRESHOLD)} ${VolumeFormat.MILLION_SUFFIX}"
            this >= VolumeFormat.THOUSAND_THRESHOLD ->
                return "${String.format("%.2f", this / VolumeFormat.THOUSAND_THRESHOLD)} ${VolumeFormat.THOUSAND_SUFFIX}"
        }
    }
    return this.toString()
}

fun Long.formatAsDateTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat(DateFormats.DATE_TIME_FORMAT, Locale.getDefault())
    return format.format(date)
}

fun Long.formatAsDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat(DateFormats.DATE_ONLY_FORMAT, Locale.getDefault())
    return format.format(date)
}

fun Long.formatAsTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat(DateFormats.TIME_ONLY_FORMAT, Locale.getDefault())
    return format.format(date)
}

fun Long.formatAsRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000} min ago"
        diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
        diff < 604_800_000 -> "${diff / 86_400_000} days ago"
        else -> this.formatAsDate()
    }
}

// ===== Boolean Extensions =====

fun Boolean.toYesNo(): String = if (this) "Yes" else "No"

fun Boolean.toOnOff(): String = if (this) "On" else "Off"

// ===== List Extensions =====

inline fun <T> List<T>.findIndexed(predicate: (index: Int, T) -> Boolean): Pair<Int, T>? {
    forEachIndexed { index, element ->
        if (predicate(index, element)) {
            return index to element
        }
    }
    return null
}

// ===== Result Extensions =====

fun <T> Result<T>.getOrNull(): T? = getOrNull()

fun <T> Result<T>.getErrorMessage(): String {
    return exceptionOrNull()?.message ?: "Unknown error occurred"
}

inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (isSuccess) {
        action(getOrNull()!!)
    }
    return this
}

inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    if (isFailure) {
        action(exceptionOrNull()!!)
    }
    return this
}

// ===== Validation Extensions =====

fun String.isBlankOrEmpty(): Boolean = this.isBlank() || this.isEmpty()

fun String.requireNotBlank(): String {
    if (this.isBlank()) throw IllegalArgumentException("Text cannot be blank")
    return this
}

fun Double.requirePositive(): Double {
    if (this <= 0) throw IllegalArgumentException("Value must be positive")
    return this
}

fun Double.requireNonNegative(): Double {
    if (this < 0) throw IllegalArgumentException("Value cannot be negative")
    return this
}

// ===== Comparison Extensions =====

fun Double.isNear(other: Double, tolerance: Double = 0.01): Boolean {
    return (this - other).absoluteValue < tolerance
}

fun Double.isGreaterOrEqual(other: Double): Boolean = this >= other

fun Double.isLessOrEqual(other: Double): Boolean = this <= other

fun Double.percentageChange(previousValue: Double): Double {
    if (previousValue == 0.0) return 0.0
    return ((this - previousValue) / previousValue) * 100
}

// ===== Rounding Extensions =====

fun Double.roundTo(decimalPlaces: Int): Double {
    val multiplier = kotlin.math.pow(10.0, decimalPlaces.toDouble())
    return kotlin.math.round(this * multiplier) / multiplier
}

fun Double.ceilTo(decimalPlaces: Int): Double {
    val multiplier = kotlin.math.pow(10.0, decimalPlaces.toDouble())
    return kotlin.math.ceil(this * multiplier) / multiplier
}

fun Double.floorTo(decimalPlaces: Int): Double {
    val multiplier = kotlin.math.pow(10.0, decimalPlaces.toDouble())
    return kotlin.math.floor(this * multiplier) / multiplier
}

// ===== Map/Collection Extensions =====

fun <K, V> Map<K, V>.getOrDefault(key: K, defaultValue: V): V {
    return this[key] ?: defaultValue
}

inline fun <T> Iterable<T>.mapToSet(transform: (T) -> String): Set<String> {
    return this.mapTo(mutableSetOf(), transform)
}

// ===== Null-Safety Extensions =====

fun <T> T?.isNull(): Boolean = this == null

fun <T> T?.isNotNull(): Boolean = this != null

inline fun <T, R> T?.ifNull(block: () -> R): R? {
    return if (this == null) block() else null
}

// ===== Threading Extensions =====

fun (() -> Unit).runAsync() {
    Thread(this).start()
}

// ===== Math Extensions =====

fun Int.isEven(): Boolean = this % 2 == 0

fun Int.isOdd(): Boolean = this % 2 != 0

fun Int.isPositive(): Boolean = this > 0

fun Int.isNegative(): Boolean = this < 0

fun Int.absoluteValue(): Int = kotlin.math.abs(this)
