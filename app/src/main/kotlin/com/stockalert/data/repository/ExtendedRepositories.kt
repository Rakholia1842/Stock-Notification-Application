package com.stockalert.data.repository

import com.stockalert.data.db.*
import com.stockalert.domain.model.NewsArticle
import com.stockalert.domain.model.PricePrediction
import com.stockalert.domain.model.AlertTemplate
import com.stockalert.domain.model.AlertType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for news articles
 */
class NewsRepository(
    private val newsDao: NewsDao
) {

    suspend fun insertNews(article: NewsArticle) {
        newsDao.insertNews(
            NewsArticleEntity(
                id = article.id,
                stockSymbol = article.stockSymbol,
                title = article.title,
                summary = article.summary,
                source = article.source,
                url = article.url,
                imageUrl = article.imageUrl,
                publishedAt = article.publishedAt
            )
        )
    }

    fun getNewsByStock(symbol: String, limit: Int = 20): Flow<List<NewsArticle>> {
        return newsDao.getNewsByStock(symbol, limit).map { entities ->
            entities.map { entity ->
                NewsArticle(
                    id = entity.id,
                    stockSymbol = entity.stockSymbol,
                    title = entity.title,
                    summary = entity.summary,
                    source = entity.source,
                    url = entity.url,
                    imageUrl = entity.imageUrl,
                    publishedAt = entity.publishedAt
                )
            }
        }
    }

    fun getLatestNews(limit: Int = 50): Flow<List<NewsArticle>> {
        return newsDao.getLatestNews(limit).map { entities ->
            entities.map { entity ->
                NewsArticle(
                    id = entity.id,
                    stockSymbol = entity.stockSymbol,
                    title = entity.title,
                    summary = entity.summary,
                    source = entity.source,
                    url = entity.url,
                    imageUrl = entity.imageUrl,
                    publishedAt = entity.publishedAt
                )
            }
        }
    }

    suspend fun deleteOldNews(retentionDays: Int = 30) {
        val cutoffTime = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
        newsDao.deleteOldNews(cutoffTime)
    }
}

/**
 * Repository for price predictions (ML-based)
 */
class PredictionRepository(
    private val predictionDao: PredictionDao
) {

    suspend fun insertPrediction(prediction: PricePrediction) {
        predictionDao.insertPrediction(
            PricePredictionEntity(
                stockSymbol = prediction.stockSymbol,
                predictedPrice = prediction.predictedPrice,
                confidence = prediction.confidence,
                predictionDate = prediction.predictionDate,
                modelVersion = prediction.modelVersion
            )
        )
    }

    suspend fun getLatestPrediction(symbol: String): PricePrediction? {
        val entity = predictionDao.getLatestPrediction(symbol) ?: return null
        return PricePrediction(
            stockSymbol = entity.stockSymbol,
            predictedPrice = entity.predictedPrice,
            confidence = entity.confidence,
            predictionDate = entity.predictionDate,
            modelVersion = entity.modelVersion
        )
    }

    suspend fun getPredictionHistory(symbol: String, limit: Int = 10): List<PricePrediction> {
        return predictionDao.getPredictionHistory(symbol, limit).map { entity ->
            PricePrediction(
                stockSymbol = entity.stockSymbol,
                predictedPrice = entity.predictedPrice,
                confidence = entity.confidence,
                predictionDate = entity.predictionDate,
                modelVersion = entity.modelVersion
            )
        }
    }

    suspend fun deleteOldPredictions(retentionDays: Int = 7) {
        val cutoffTime = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
        predictionDao.deleteOldPredictions(cutoffTime)
    }
}

/**
 * Repository for alert templates (Premium feature)
 */
class AlertTemplateRepository(
    private val alertTemplateDao: AlertTemplateDao
) {

    suspend fun createTemplate(template: AlertTemplate): Long {
        return alertTemplateDao.insertTemplate(
            AlertTemplateEntity(
                name = template.name,
                description = template.description,
                alertType = template.alertType.name,
                basePrice = template.basePrice,
                percentageChange = template.percentageChange,
                volumeThreshold = template.volumeThreshold
            )
        )
    }

    suspend fun updateTemplate(template: AlertTemplate) {
        alertTemplateDao.updateTemplate(
            AlertTemplateEntity(
                id = template.id,
                name = template.name,
                description = template.description,
                alertType = template.alertType.name,
                basePrice = template.basePrice,
                percentageChange = template.percentageChange,
                volumeThreshold = template.volumeThreshold
            )
        )
    }

    suspend fun deleteTemplate(templateId: Int) {
        val template = alertTemplateDao.getTemplateById(templateId)
        template?.let {
            alertTemplateDao.deleteTemplate(it)
        }
    }

    suspend fun getTemplateById(templateId: Int): AlertTemplate? {
        val entity = alertTemplateDao.getTemplateById(templateId) ?: return null
        return AlertTemplate(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            alertType = AlertType.valueOf(entity.alertType),
            basePrice = entity.basePrice,
            percentageChange = entity.percentageChange,
            volumeThreshold = entity.volumeThreshold
        )
    }

    fun getAllTemplates(): Flow<List<AlertTemplate>> {
        return alertTemplateDao.getAllTemplates().map { entities ->
            entities.map { entity ->
                AlertTemplate(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    alertType = AlertType.valueOf(entity.alertType),
                    basePrice = entity.basePrice,
                    percentageChange = entity.percentageChange,
                    volumeThreshold = entity.volumeThreshold
                )
            }
        }
    }

    fun getTemplatesByType(alertType: AlertType): Flow<List<AlertTemplate>> {
        return alertTemplateDao.getTemplatesByType(alertType.name).map { entities ->
            entities.map { entity ->
                AlertTemplate(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    alertType = AlertType.valueOf(entity.alertType),
                    basePrice = entity.basePrice,
                    percentageChange = entity.percentageChange,
                    volumeThreshold = entity.volumeThreshold
                )
            }
        }
    }

    // Pre-defined templates
    fun getDefaultTemplates(): List<AlertTemplate> {
        return listOf(
            AlertTemplate(
                name = "Price Alert - 5%",
                description = "Alert when price changes by 5%",
                alertType = AlertType.PERCENTAGE,
                percentageChange = 5.0
            ),
            AlertTemplate(
                name = "Price Alert - 10%",
                description = "Alert when price changes by 10%",
                alertType = AlertType.PERCENTAGE,
                percentageChange = 10.0
            ),
            AlertTemplate(
                name = "Volume Spike Alert",
                description = "Alert on unusual volume activity",
                alertType = AlertType.VOLUME,
                volumeThreshold = 10000000L
            ),
            AlertTemplate(
                name = "Moving Average Crossover",
                description = "Alert when price crosses 50-day MA",
                alertType = AlertType.MOVING_AVERAGE
            )
        )
    }
}

/**
 * Service for advanced alert condition checking
 */
class AlertConditionService(
    private val stockRepository: StockRepository,
    private val priceHistoryRepository: PriceHistoryRepository
) {

    suspend fun checkPercentageChange(
        symbol: String,
        currentPrice: Double,
        targetPercentage: Double
    ): Boolean {
        val stock = stockRepository.getStockPrice(symbol).getOrNull() ?: return false
        val percentChange = ((currentPrice - stock.previousClose) / stock.previousClose) * 100
        return kotlin.math.abs(percentChange) >= targetPercentage
    }

    suspend fun checkVolumeSpikeCondition(
        symbol: String,
        currentVolume: Long,
        volumeMultiplier: Double = 1.5
    ): Boolean {
        val priceHistory = priceHistoryRepository.getPriceHistory(symbol, 20)
        val averageVolume = priceHistory.prices.map { it.volume }.average()
        return currentVolume >= (averageVolume * volumeMultiplier)
    }

    suspend fun checkMovingAverageCrossover(
        symbol: String,
        currentPrice: Double,
        period: Int = 50
    ): Boolean {
        val movingAverage = priceHistoryRepository.calculateMovingAverage(symbol, period)
        return movingAverage.isNotEmpty() && currentPrice > movingAverage.last()
    }

    suspend fun checkPriceAboveThreshold(
        symbol: String,
        thresholdPrice: Double
    ): Boolean {
        val stock = stockRepository.getStockPrice(symbol).getOrNull() ?: return false
        return stock.currentPrice >= thresholdPrice
    }

    suspend fun checkPriceBelowThreshold(
        symbol: String,
        thresholdPrice: Double
    ): Boolean {
        val stock = stockRepository.getStockPrice(symbol).getOrNull() ?: return false
        return stock.currentPrice <= thresholdPrice
    }
}
