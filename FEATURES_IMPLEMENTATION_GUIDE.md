# Stock Alert Application - Enhanced Features Implementation Guide

## 📋 Table of Contents
1. [High Priority Features](#high-priority-features)
2. [Medium Priority Features](#medium-priority-features)
3. [Premium Features](#premium-features)
4. [Implementation Details](#implementation-details)
5. [API Integration](#api-integration)

---

## High Priority Features

### 1. Multiple Alert Conditions ✅ IMPLEMENTED

**Alert Types:**
- `ABOVE` - Alert when price goes above threshold
- `BELOW` - Alert when price goes below threshold
- `PERCENTAGE` - Alert on percentage change (e.g., ±5%)
- `VOLUME` - Alert on unusual volume activity
- `MOVING_AVERAGE` - Alert on moving average crossovers

**Implementation Files:**
- `data/db/Entities.kt` - `PriceAlertEntity` with new fields
- `domain/model/Models.kt` - `AlertType` enum with new types
- `data/repository/ExtendedRepositories.kt` - `AlertConditionService`
- `utils/AppConstants.kt` - `AlertTypeConstants` and configuration

**Usage Example:**
```kotlin
// Create a percentage change alert
val alert = PriceAlert(
    id = 1,
    stockSymbol = "RELIANCE",
    stockName = "Reliance Industries",
    targetPrice = 2500.0,
    alertType = AlertType.PERCENTAGE,
    percentageChange = 5.0, // 5% change
    status = AlertStatus.ACTIVE,
    createdAt = System.currentTimeMillis()
)

// Check alert conditions
val conditionService = AlertConditionService(stockRepo, priceHistoryRepo)
val triggered = conditionService.checkPercentageChange("RELIANCE", 2625.0, 5.0)
```

### 2. Portfolio Management ✅ IMPLEMENTED

**Features:**
- Create and manage multiple portfolios
- Track multiple stocks simultaneously
- Portfolio value calculation
- Default portfolio selection
- Watchlists within portfolios

**Implementation Files:**
- `data/db/Entities.kt` - `PortfolioEntity`, `WatchlistEntity`, `WatchlistItemEntity`
- `domain/model/Models.kt` - `Portfolio`, `Watchlist` domain models
- `data/repository/PortfolioRepository.kt` - Complete portfolio management
- `data/db/StockAlertDatabase.kt` - Updated with new DAOs

**Usage Example:**
```kotlin
val portfolioRepo = PortfolioRepository(portfolioDao, watchlistDao, watchlistItemDao, stockDao)

// Create portfolio
val portfolioId = portfolioRepo.createPortfolio(
    Portfolio(
        name = "My Portfolio",
        description = "Primary investment portfolio",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        totalValue = 100000.0
    )
)

// Create watchlist in portfolio
val watchlistId = portfolioRepo.createWatchlist(
    Watchlist(
        name = "Tech Stocks",
        portfolioId = portfolioId.toInt(),
        createdAt = System.currentTimeMillis()
    )
)

// Add stocks to watchlist
portfolioRepo.addStockToWatchlist(watchlistId.toInt(), "INFY")
portfolioRepo.addStockToWatchlist(watchlistId.toInt(), "TCS")
```

### 3. User Preferences & Settings ✅ IMPLEMENTED

**Configuration Options:**
- Notifications enabled/disabled
- Sound and vibration preferences
- Default alert frequency (Real-time, Hourly, Daily, Weekly)
- Quiet hours (snooze notifications 10 PM - 8 AM)
- Currency and decimal formatting
- Data refresh intervals
- Dark mode support

**Implementation Files:**
- `data/preferences/SettingsManager.kt` - DataStore-based preferences
- `data/preferences/NotificationPreferencesRepository.kt` - Per-stock notification settings
- `domain/model/Models.kt` - `UserSettings`, `NotificationSettings` models
- `data/db/Entities.kt` - `UserSettingsEntity`, `NotificationSettingsEntity`

**Usage Example:**
```kotlin
val settingsManager = SettingsManager(context)

// Set global preferences
settingsManager.setDarkModeEnabled(true)
settingsManager.setDefaultAlertFrequency(AlertFrequency.HOURLY)
settingsManager.setRefreshInterval(30)

// Set notification preferences per stock
val notifPrefsRepo = NotificationPreferencesRepository(notificationSettingsDao)
notifPrefsRepo.setNotificationSettings(
    NotificationSettings(
        stockSymbol = "RELIANCE",
        notificationsEnabled = true,
        soundEnabled = true,
        quietHourStart = 22,
        quietHourEnd = 8
    )
)

// Check if should notify
if (notifPrefsRepo.shouldNotify("RELIANCE")) {
    sendNotification()
}
```

---

## Medium Priority Features

### 4. Real-time Price Updates via WebSocket (Implementable) 🔄

**Architecture:**
- Integrate OkHttp WebSocket support
- Create `WebSocketPriceService`
- Fallback to polling if WebSocket unavailable
- Handle reconnection logic

**Integration Points:**
- `data/api/YahooFinanceApi.kt` - Add WebSocket endpoints
- Create `data/service/WebSocketService.kt`
- Update `worker/PriceCheckWorker.kt` - Use WebSocket when available

### 5. Historical Data & Charts ✅ IMPLEMENTED

**Features:**
- Store price history in database
- Calculate moving averages (5-day, 10-day, 20-day, 50-day, 200-day)
- Calculate price volatility
- 52-week high/low tracking
- Date range queries

**Implementation Files:**
- `data/db/Entities.kt` - `PriceHistoryEntity`
- `domain/model/Models.kt` - `PriceHistory`, `PricePoint` models
- `data/repository/PriceHistoryRepository.kt` - Price history management
- `data/db/Dao.kt` - `PriceHistoryDao` with range queries

**Usage Example:**
```kotlin
val priceHistoryRepo = PriceHistoryRepository(priceHistoryDao)

// Insert price history
priceHistoryRepo.insertPriceHistory("RELIANCE", 2500.0, 2510.0, 2490.0, 1000000L)

// Get price history for charts
val history = priceHistoryRepo.getPriceHistory("RELIANCE", limit = 100)
history.prices.forEach { point ->
    println("${point.timestamp}: ${point.price}")
}

// Calculate technical indicators
val ma20 = priceHistoryRepo.calculateMovingAverage("RELIANCE", period = 20)
val volatility = priceHistoryRepo.getPriceVolatility("RELIANCE")
val high52Week = priceHistoryRepo.get52WeekHigh("RELIANCE")
val low52Week = priceHistoryRepo.get52WeekLow("RELIANCE")
```

### 6. News Integration ✅ IMPLEMENTED

**Features:**
- Fetch news articles for specific stocks
- Store news in database
- News feed display
- Multi-source support (News API integration ready)

**Implementation Files:**
- `data/api/ApiModels.kt` - News data models
- `data/api/YahooFinanceApi.kt` - `NewsApi` interface
- `data/repository/ExtendedRepositories.kt` - `NewsRepository`
- `data/db/Entities.kt` - `NewsArticleEntity`

**Usage Example:**
```kotlin
val newsRepo = NewsRepository(newsDao)

// Insert news article
newsRepo.insertNews(
    NewsArticle(
        id = "news_123",
        stockSymbol = "RELIANCE",
        title = "Reliance Q4 Results Beat Expectations",
        summary = "Strong quarterly performance...",
        source = "Reuters",
        url = "https://...",
        publishedAt = System.currentTimeMillis()
    )
)

// Get news for a stock
newsRepo.getNewsByStock("RELIANCE").collect { articles ->
    articles.forEach { article ->
        println("${article.title} - ${article.source}")
    }
}
```

### 7. Multiple Data Source Support ✅ IMPLEMENTED

**Supported Sources:**
- Yahoo Finance (Primary)
- Alpha Vantage
- IEX Cloud
- Finnhub
- Polygon.io

**Implementation Files:**
- `data/api/YahooFinanceApi.kt` - Multiple API interfaces
- `utils/AppConstants.kt` - `DataSourceConstants`, `ApiConfigConstants`
- `di/DependencyInjection.kt` - Multiple Retrofit instances

**Configuration:**
```kotlin
// In AppConstants.kt
object DataSourceConstants {
    const val DATA_SOURCE_YAHOO_FINANCE = "YAHOO_FINANCE"
    const val DATA_SOURCE_ALPHA_VANTAGE = "ALPHA_VANTAGE"
    const val DATA_SOURCE_IEX_CLOUD = "IEX_CLOUD"
    const val DATA_SOURCE_POLYGON = "POLYGON"
    const val DATA_SOURCE_FINNHUB = "FINNHUB"
}
```

### 8. User Authentication (Requires Backend) 🔐

**Implementation approach:**
- Firebase Authentication (Google Sign-In, Email/Password)
- Cloud Sync for user data
- Multi-device support
- Server-side alert management

**Firebase Setup:**
```kotlin
// In build.gradle.kts
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")

// In DependencyInjection.kt - Add Firebase providers
@Provides
fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

@Provides
fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
```

---

## Premium Features

### 9. Price Prediction (ML-based) ✅ IMPLEMENTED

**Features:**
- ML model predictions
- Confidence scores
- Prediction history tracking
- Automatic model versioning

**Implementation Files:**
- `data/repository/ExtendedRepositories.kt` - `PredictionRepository`
- `data/db/Entities.kt` - `PricePredictionEntity`
- `domain/model/Models.kt` - `PricePrediction` model

**Usage Example:**
```kotlin
val predictionRepo = PredictionRepository(predictionDao)

// Insert prediction
predictionRepo.insertPrediction(
    PricePrediction(
        stockSymbol = "RELIANCE",
        predictedPrice = 2750.0,
        confidence = 0.78,
        predictionDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000),
        modelVersion = "v2.1"
    )
)

// Get latest prediction
val prediction = predictionRepo.getLatestPrediction("RELIANCE")
println("Predicted: ${prediction?.formattedPrice} (${prediction?.confidencePercentage})")
```

### 10. Custom Alert Templates ✅ IMPLEMENTED

**Features:**
- Pre-defined alert templates
- Save custom templates
- Quick alert creation from templates
- Template sharing

**Implementation Files:**
- `data/repository/ExtendedRepositories.kt` - `AlertTemplateRepository`
- `data/db/Entities.kt` - `AlertTemplateEntity`
- `domain/model/Models.kt` - `AlertTemplate` model

**Usage Example:**
```kotlin
val templateRepo = AlertTemplateRepository(alertTemplateDao)

// Get default templates
val templates = templateRepo.getDefaultTemplates()
templates.forEach { template ->
    println("${template.name}: ${template.description}")
}

// Create custom template
templateRepo.createTemplate(
    AlertTemplate(
        name = "My Custom Alert",
        alert Type = AlertType.PERCENTAGE,
        percentageChange = 7.5
    )
)
```

### 11. Email/SMS Notifications (Requires Backend)

**Implementation Approach:**
- Email service (SendGrid, AWS SES)
- SMS service (Twilio)
- User preference management
- Notification queue system

**Database Support:**
```kotlin
// Fields already in NotificationSettingsEntity
emailNotifications: Boolean = false
smsNotifications: Boolean = false
```

### 12. Data Export & Reports

**Export Formats:**
- CSV export of alerts and portfolio
- PDF reports
- Chart exports
- Email reports

### 13. Dark Mode ✅ IMPLEMENTED

**Implementation:**
- Preference stored in `UserSettingsEntity`
- Theme switching support
- Stored in DataStore preferences

---

## Implementation Details

### Database Schema

**New Entities Added:**
- `PortfolioEntity` - Portfolio management
- `WatchlistEntity` - Stock watchlists
- `WatchlistItemEntity` - Items in watchlists
- `PriceHistoryEntity` - Historical price data
- `UserSettingsEntity` - User preferences
- `NotificationSettingsEntity` - Per-stock notifications
- `NewsArticleEntity` - News data
- `PricePredictionEntity` - ML predictions
- `AlertTemplateEntity` - Alert templates

### New DAOs

All DAOs are registered in `StockAlertDatabase`:
```kotlin
abstract fun portfolioDao(): PortfolioDao
abstract fun watchlistDao(): WatchlistDao
abstract fun watchlistItemDao(): WatchlistItemDao
abstract fun priceHistoryDao(): PriceHistoryDao
abstract fun userSettingsDao(): UserSettingsDao
abstract fun notificationSettingsDao(): NotificationSettingsDao
abstract fun newsDao(): NewsDao
abstract fun predictionDao(): PredictionDao
abstract fun alertTemplateDao(): AlertTemplateDao
```

### Dependency Injection

All new services are provided by DependencyInjection module:
```kotlin
@Provides fun providePortfolioRepository(...): PortfolioRepository
@Provides fun providePriceHistoryRepository(...): PriceHistoryRepository
@Provides fun provideNewsRepository(...): NewsRepository
@Provides fun providePredictionRepository(...): PredictionRepository
@Provides fun provideAlertTemplateRepository(...): AlertTemplateRepository
@Provides fun provideAlertConditionService(...): AlertConditionService
@Provides fun provideSettingsManager(...): SettingsManager
@Provides fun provideNotificationPreferencesRepository(...): NotificationPreferencesRepository
```

---

## API Integration

### Configuration

Add API keys to your project:

```kotlin
// build.gradle.kts
buildTypes {
    release {
        buildConfigField("String", "ALPHA_VANTAGE_API_KEY", "\"your_key_here\"")
        buildConfigField("String", "IEX_CLOUD_API_KEY", "\"your_key_here\"")
        buildConfigField("String", "FINNHUB_API_KEY", "\"your_key_here\"")
        buildConfigField("String", "POLYGON_API_KEY", "\"your_key_here\"")
        buildConfigField("String", "NEWS_API_KEY", "\"your_key_here\"")
    }
}
```

### Multi-Source Service Implementation

Create a `MultiSourceStockService` for seamless API switching:

```kotlin
class MultiSourceStockService(
    private val yahooApi: YahooFinanceApi,
    private val alphaVantageApi: AlphaVantageApi,
    private val iexApi: IexCloudApi,
    private val settingsManager: SettingsManager
) : StockDataService {
    
    override suspend fun getStockPrice(symbol: String): Result<StockPriceResponse> {
        val source = settingsManager.getDefaultDataSource()
        return when (source) {
            "YAHOO_FINANCE" -> fetchFromYahoo(symbol)
            "ALPHA_VANTAGE" -> fetchFromAlphaVantage(symbol)
            "IEX_CLOUD" -> fetchFromIex(symbol)
            else -> fetchFromYahoo(symbol) // Fallback
        }
    }
}
```

---

## Testing

### Unit Tests for New Features

```kotlin
// Test alert conditions
@Test
fun testPercentageChangeAlert() {
    val result = alertService.checkPercentageChange("RELIANCE", 2625.0, 5.0)
    assertTrue(result)
}

// Test portfolio management
@Test
fun testCreatePortfolio() {
    val id = portfolioRepo.createPortfolio(testPortfolio)
    assertTrue(id > 0)
}

// Test preferences
@Test
fun testNotificationQuietHours() {
    val shouldNotify = notificationPrefsRepo.shouldNotify("RELIANCE")
    assertFalse(shouldNotify) // During quiet hours
}
```

---

## Migration Guide

### Database Update

When deploying v2:
1. Update `StockAlertDatabase.version` from 1 to 2
2. Add migration strategy (currently uses `fallbackToDestructiveMigration()`)
3. For production, create Room migration:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new tables
        database.execSQL("CREATE TABLE portfolio ...")
    }
}

Room.databaseBuilder(context, StockAlertDatabase::class.java, "db")
    .addMigrations(MIGRATION_1_2)
    .build()
```

---

## Next Steps

1. **UI Implementation** - Create Compose screens for new features
2. **Testing** - Add comprehensive unit and integration tests
3. **Documentation** - Update user-facing documentation
4. **Firebase Integration** - Set up authentication and cloud sync
5. **Backend API** - Develop backend for email/SMS notifications
6. **Analytics** - Implement user behavior tracking
7. **Performance** - Optimize database queries and API calls
8. **Security** - Add API key management and encryption

---

## Feature Flags

Control feature availability:
```kotlin
object FeatureFlags {
    const val ENABLE_PORTFOLIO_MANAGEMENT = true
    const val ENABLE_WATCHLISTS = true
    const val ENABLE_MULTIPLE_ALERT_TYPES = true
    const val ENABLE_PRICE_HISTORY = true
    const val ENABLE_NEWS_FEED = true
    const val ENABLE_PRICE_PREDICTIONS = true
    const val ENABLE_ALERT_TEMPLATES = true
    const val ENABLE_EMAIL_NOTIFICATIONS = false
    const val ENABLE_SMS_NOTIFICATIONS = false
    const val ENABLE_USER_AUTHENTICATION = false
    const val ENABLE_DATA_SOURCE_SWITCHING = true
}
```

Use feature flags in code:
```kotlin
if (FeatureFlags.ENABLE_PORTFOLIO_MANAGEMENT) {
    showPortfolioTab()
}
```

---

## Support

For implementation questions or issues, refer to:
- Code documentation in implementation files
- Kotlin docs comments in repositories
- This feature guide
