package com.stockalert.domain.model

data class Stock(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val previousClose: Double,
    val percentChange: Double
) {
    val formattedPrice: String get() = "₹${String.format("%.2f", currentPrice)}"
    val formattedChange: String get() = String.format("%.2f", percentChange)
    val isPositive: Boolean get() = percentChange >= 0.0
}

data class PriceAlert(
    val id: Int,
    val stockSymbol: String,
    val stockName: String,
    val targetPrice: Double,
    val alertType: AlertType,
    val status: AlertStatus,
    val createdAt: Long,
    val triggeredAt: Long? = null
) {
    val formattedTargetPrice: String get() = "₹${String.format("%.2f", targetPrice)}"
    val formattedCreatedAt: String get() {
        val date = java.util.Date(createdAt)
        return java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(date)
    }
}

enum class AlertType(val displayName: String) {
    ABOVE("Price Goes Above"),
    BELOW("Price Falls Below")
}

enum class AlertStatus(val displayName: String) {
    ACTIVE("Active"),
    TRIGGERED("Triggered")
}
