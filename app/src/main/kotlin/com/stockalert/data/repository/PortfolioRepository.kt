package com.stockalert.data.repository

import com.stockalert.data.db.*
import com.stockalert.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for portfolio and watchlist management
 */
class PortfolioRepository(
    private val portfolioDao: PortfolioDao,
    private val watchlistDao: WatchlistDao,
    private val watchlistItemDao: WatchlistItemDao,
    private val stockDao: StockDao
) {
    
    // ===== Portfolio Operations =====
    suspend fun createPortfolio(portfolio: Portfolio): Long {
        return portfolioDao.insertPortfolio(
            PortfolioEntity(
                name = portfolio.name,
                description = portfolio.description,
                createdAt = portfolio.createdAt,
                updatedAt = portfolio.updatedAt,
                totalValue = portfolio.totalValue,
                isDefault = portfolio.isDefault
            )
        )
    }

    suspend fun updatePortfolio(portfolio: Portfolio) {
        portfolioDao.updatePortfolio(
            PortfolioEntity(
                id = portfolio.id,
                name = portfolio.name,
                description = portfolio.description,
                createdAt = portfolio.createdAt,
                updatedAt = System.currentTimeMillis(),
                totalValue = portfolio.totalValue,
                isDefault = portfolio.isDefault
            )
        )
    }

    suspend fun updatePortfolioValue(portfolioId: Int, totalValue: Double) {
        portfolioDao.updatePortfolioValue(portfolioId, totalValue)
    }

    suspend fun deletePortfolio(portfolioId: Int) {
        val portfolio = portfolioDao.getPortfolioById(portfolioId)
        portfolio?.let {
            portfolioDao.deletePortfolio(it)
        }
    }

    fun getAllPortfolios(): Flow<List<PortfolioEntity>> {
        return portfolioDao.getAllPortfolios()
    }

    suspend fun getPortfolioById(portfolioId: Int): Portfolio? {
        val entity = portfolioDao.getPortfolioById(portfolioId) ?: return null
        return Portfolio(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            totalValue = entity.totalValue,
            isDefault = entity.isDefault
        )
    }

    suspend fun setDefaultPortfolio(portfolioId: Int? = null) {
        portfolioDao.clearDefaultPortfolio()
        if (portfolioId != null) {
            val portfolio = portfolioDao.getPortfolioById(portfolioId)
            portfolio?.let {
                portfolioDao.updatePortfolio(it.copy(isDefault = true))
            }
        }
    }

    suspend fun getDefaultPortfolio(): Portfolio? {
        val entity = portfolioDao.getDefaultPortfolio() ?: return null
        return Portfolio(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            totalValue = entity.totalValue,
            isDefault = entity.isDefault
        )
    }

    // ===== Watchlist Operations =====
    suspend fun createWatchlist(watchlist: Watchlist): Long {
        return watchlistDao.insertWatchlist(
            WatchlistEntity(
                name = watchlist.name,
                description = watchlist.description,
                createdAt = watchlist.createdAt,
                portfolioId = watchlist.portfolioId
            )
        )
    }

    suspend fun updateWatchlist(watchlist: Watchlist) {
        watchlistDao.updateWatchlist(
            WatchlistEntity(
                id = watchlist.id,
                name = watchlist.name,
                description = watchlist.description,
                createdAt = watchlist.createdAt,
                portfolioId = watchlist.portfolioId
            )
        )
    }

    suspend fun deleteWatchlist(watchlistId: Int) {
        watchlistItemDao.clearWatchlist(watchlistId)
        val watchlist = watchlistDao.getWatchlistById(watchlistId)
        watchlist?.let {
            watchlistDao.deleteWatchlist(it)
        }
    }

    fun getAllWatchlists(): Flow<List<WatchlistEntity>> {
        return watchlistDao.getAllWatchlists()
    }

    fun getWatchlistsByPortfolio(portfolioId: Int): Flow<List<WatchlistEntity>> {
        return watchlistDao.getWatchlistsByPortfolio(portfolioId)
    }

    suspend fun getWatchlistById(watchlistId: Int): Watchlist? {
        val entity = watchlistDao.getWatchlistById(watchlistId) ?: return null
        val items = watchlistItemDao.getWatchlistItems(watchlistId).map { list ->
            list.map { item ->
                stockDao.getStockBySymbol(item.stockSymbol)?.let { stock ->
                    Stock(
                        symbol = stock.symbol,
                        name = stock.name,
                        currentPrice = stock.currentPrice,
                        dayHigh = stock.dayHigh,
                        dayLow = stock.dayLow,
                        volume = stock.volume,
                        previousClose = stock.previousClose,
                        percentChange = ((stock.currentPrice - stock.previousClose) / stock.previousClose) * 100,
                        dataSource = stock.dataSource
                    )
                }
            }.filterNotNull()
        }.run {
            kotlinx.coroutines.flow.first()
        }

        return Watchlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdAt,
            stocks = items,
            portfolioId = entity.portfolioId
        )
    }

    // ===== Watchlist Item Operations =====
    suspend fun addStockToWatchlist(watchlistId: Int, stockSymbol: String) {
        watchlistItemDao.insertWatchlistItem(
            WatchlistItemEntity(
                watchlistId = watchlistId,
                stockSymbol = stockSymbol
            )
        )
    }

    suspend fun removeStockFromWatchlist(watchlistId: Int, stockSymbol: String) {
        val items = watchlistItemDao.getWatchlistItems(watchlistId).run {
            kotlinx.coroutines.flow.first()
        }
        items.find { it.stockSymbol == stockSymbol }?.let {
            watchlistItemDao.deleteWatchlistItem(it)
        }
    }

    suspend fun getWatchlistStocks(watchlistId: Int): List<Stock> {
        return watchlistItemDao.getWatchlistItems(watchlistId).map { items ->
            items.mapNotNull { item ->
                stockDao.getStockBySymbol(item.stockSymbol)?.let { stock ->
                    Stock(
                        symbol = stock.symbol,
                        name = stock.name,
                        currentPrice = stock.currentPrice,
                        dayHigh = stock.dayHigh,
                        dayLow = stock.dayLow,
                        volume = stock.volume,
                        previousClose = stock.previousClose,
                        percentChange = ((stock.currentPrice - stock.previousClose) / stock.previousClose) * 100,
                        dataSource = stock.dataSource
                    )
                }
            }
        }.run {
            kotlinx.coroutines.flow.first()
        }
    }
}
