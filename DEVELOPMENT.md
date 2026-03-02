# Setup & Development Guide - StockAlert (v2.0)

## Quick Start

### 1. Clone & Open Project
```bash
# The project is located at:
# d:\Development\Github\Stock Notification Application\

# Open in Android Studio
# File > Open > Navigate to project folder
```

### 2. Initialize Project
```bash
# Sync Gradle (Android Studio will prompt automatically)
# Or manually: Build > Make Project
```

### 3. Run on Emulator/Device
```bash
# Select a device in Android Studio toolbar
# Click Run (Shift + F10) or use:
./gradlew installDebug
```

## Development Environment Setup

### Requirements
- **Android Studio**: 2022.1 or newer (Latest recommended for Compose)
- **SDK**: API 24 (minimum), API 34 (target)
- **Kotlin**: 1.9.0 or newer
- **Gradle**: 8.0+
- **JDK**: Java 11 or later
- **RAM**: 8GB+ recommended for Compose development

### IDE Configuration
1. Open `build.gradle.kts` files
2. All dependencies are pre-configured
3. Hilt annotation processing enabled (kapt)
4. Compose compiler version set correctly

### Recommended IDE Settings
```
Settings > Editor > Code Completion > Kotlin Multiplatform
- Enable MPS-based code completion

Settings > Build, Execution, Deployment > Compiler > Kotlin Compiler
- Target JVM version: 11+
- Incremental compilation: Enabled
```

## Project Architecture (v2.0)

### Layered Architecture

```
┌────────────────────────────────────┐
│  Presentation Layer (UI)           │
│  - Compose Screens                 │
│  - ViewModels (StateFlow)          │
└──────────────┬─────────────────────┘
               │
┌──────────────▼─────────────────────┐
│  Business Logic Layer              │
│  - Repositories (5+)               │
│  - Use Cases/Services              │
│  - Alert Condition Service         │
└──────────────┬─────────────────────┘
               │
┌──────────────▼─────────────────────┐
│  Data Layer                        │
│  - API Integration (6 sources)     │
│  - Room Database (11 entities)     │
│  - DataStore (Preferences)         │
└────────────────────────────────────┘
```

## Key Packages Explained

### `/data` - Data Layer
```
data/
├── api/
│   ├── ApiModels.kt       - DTOs for Yahoo Finance, Alpha Vantage, IEX, etc.
│   └── YahooFinanceApi.kt - Retrofit interfaces for multiple APIs
│
├── db/
│   ├── Entities.kt        - 11 Room @Entity classes
│   ├── Dao.kt             - 11 @Dao interfaces with advanced queries
│   └── StockAlertDatabase.kt - Room database with all DAOs
│
├── preferences/
│   ├── SettingsManager.kt - Global preferences using DataStore
│   └── NotificationPreferencesRepository.kt - Per-stock preferences
│
├── provider/
│   └── StockDataProvider.kt - Content provider
│
└── repository/
    ├── StockRepository.kt - Stock fetching & alert logic
    ├── PortfolioRepository.kt - Portfolio management
    ├── PriceHistoryRepository.kt - Historical data & indicators
    └── ExtendedRepositories.kt - News, Predictions, Templates
```

**Usage Pattern:**
```kotlin
// Repositories coordinate between API and Database
val repository = StockRepository(api, alertDao, stockDao)
val result = repository.getStockPrice("RELIANCE")
```

### `/domain` - Business Models
```
domain/
└── model/
    └── Models.kt - Domain-independent data classes
```

**Key Models (v2.0):**
- `Stock` - Stock data (updated)
- `PriceAlert` - Alert config with 5 alert types
- `Portfolio` - User portfolios (NEW)
- `Watchlist` - Stock watchlists (NEW)
- `PriceHistory` - Historical data (NEW)
- `UserSettings` - Global preferences (NEW)
- `NotificationSettings` - Per-stock settings (NEW)
- `NewsArticle` - News articles (NEW)
- `PricePrediction` - ML predictions (NEW)
- `AlertTemplate` - Alert templates (NEW)

### `/ui` - Presentation Layer
```
ui/
├── home/ - Stock search screens
├── detail/ - Stock details and alert creation
├── alerts/ - Alert management
├── portfolio/ - Portfolio management (UI ready)
├── settings/ - User preferences (UI ready)
├── components/ - Reusable Compose functions
└── theme/ - Material 3 styling
```

**ViewModel Pattern:**
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stockRepository: StockRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<Stock>>(emptyList())
    val searchResults: StateFlow<List<Stock>> = _searchResults.asStateFlow()
    
    fun searchStocks(query: String) = viewModelScope.launch {
        val results = stockRepository.searchStocks(query)
        // Update state
    }
}
```

### `/worker` - Background Tasks
```
worker/
├── PriceCheckWorker.kt - WorkManager task
└── BootCompletedReceiver.kt - Device restart handling
```

**WorkManager Integration:**
```kotlin
val priceCheckWork = PeriodicWorkRequestBuilder<PriceCheckWorker>(
    15, TimeUnit.MINUTES
).setBackoffPolicy(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
    .build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "price_check",
    ExistingPeriodicWorkPolicy.KEEP,
    priceCheckWork
)
```

### `/notification` - Notifications
```
notification/
└── NotificationHelper.kt - Notification creation and management
```

**Features:**
- Channel management
- Notification batching
- Priority handling
- Action buttons

### `/di` - Dependency Injection

**Hilt Modules (v2.0):**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule { /* 6 API instances */ }

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule { /* 11 DAOs */ }

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule { /* 5+ repositories */ }

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule { /* Settings managers */ }

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule { /* Notification helper */ }
```

---

## Database Schema (v2.0)

### New Tables in v2.0
1. `portfolios` - User portfolios
2. `watchlists` - Stock watchlists
3. `watchlist_items` - Items in watchlists
4. `price_history` - Historical OHLCV data
5. `user_settings` - Global app preferences
6. `notification_settings` - Per-stock notification settings
7. `news_articles` - Stock news
8. `price_predictions` - ML model predictions
9. `alert_templates` - Alert templates

### Entity Relationships
```
Portfolio (1) ──────────── (Many) Watchlist
                   │
                   └──────── (Many) WatchlistItem ──── Stock

PriceAlert ───────────── Stock
           └───────────── AlertTemplate (template-based)

PriceHistory ──────────── Stock
NewsArticle ────────────── Stock
PricePrediction ────────── Stock
```

---

## API Integration

### Supported Data Sources (v2.0)

| Source | Status | Auth | Setup |
|--------|--------|------|-------|
| Yahoo Finance | ✅ Active | None | Automatic |
| Alpha Vantage | 🔄 Ready | API Key | build.gradle.kts |
| IEX Cloud | 🔄 Ready | API Key | build.gradle.kts |
| Finnhub | 🔄 Ready | API Key | build.gradle.kts |
| Polygon.io | 🔄 Ready | API Key | build.gradle.kts |
| News API | 🔄 Ready | API Key | build.gradle.kts |

### Adding API Keys
```kotlin
// In build.gradle.kts
buildTypes {
    release {
        buildConfigField("String", "ALPHA_VANTAGE_API_KEY", "\"abc123...\"")
        buildConfigField("String", "NEWS_API_KEY", "\"xyz789...\"")
        // Add other keys
    }
}

// In DependencyInjection.kt
@Provides
fun provideAlphaVantageRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://www.alphavantage.co/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

---

## Testing Guide

### Unit Tests (Repository Layer)
```kotlin
@Test
fun testGetStockPrice() {
    val result = repository.getStockPrice("RELIANCE")
    assertTrue(result.isSuccess)
    assertEquals(result.getOrNull()?.symbol, "RELIANCE")
}

@Test
fun testCreatePortfolio() {
    val id = portfolioRepository.createPortfolio(testPortfolio)
    assertTrue(id > 0)
}
```

### Instrumented Tests (Database)
```kotlin
@RunWith(AndroidJUnit4::class)
class PriceAlertDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: StockAlertDatabase
    private lateinit var alertDao: PriceAlertDao
    
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StockAlertDatabase::class.java
        ).build()
        alertDao = database.priceAlertDao()
    }
}
```

### UI Tests (Compose)
```kotlin
@RunWith(ComposeTestRule::class)
class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()
    
    @Test
    fun testStockSearch() {
        composeRule.setContent {
            HomeScreen(onStockSelected = {})
        }
        // Test UI interactions
    }
}
```

---

## Common Development Tasks

### Adding a New Feature

1. **Create Domain Model** (`Models.kt`)
   ```kotlin
   data class NewFeature(...)
   ```

2. **Create Database Entity** (`Entities.kt`)
   ```kotlin
   @Entity(tableName = "new_feature")
   data class NewFeatureEntity(...)
   ```

3. **Create DAO** (`Dao.kt`)
   ```kotlin
   @Dao
   interface NewFeatureDao {
       @Insert
       suspend fun insert(entity: NewFeatureEntity): Long
       // ... queries
   }
   ```

4. **Add DAO to Database** (`StockAlertDatabase.kt`)
   ```kotlin
   abstract fun newFeatureDao(): NewFeatureDao
   ```

5. **Create Repository**
   ```kotlin
   class NewFeatureRepository(private val dao: NewFeatureDao) {
       // Business logic
   }
   ```

6. **Inject in DependencyInjection.kt**
7. **Update UI Screens**

### Debugging Tips

```kotlin
// Enable verbose logging
adb logcat | grep "StockAlert"

// Access Room database
adb shell am instrument -w -e androidx.test.debug.DEBUG_MODE true \
  com.stockalert.test/androidx.test.runner.AndroidJUnitRunner

// Check SharedPreferences
adb shell run-as com.stockalert cat \
  /data/data/com.stockalert/shared_prefs/user_settings.xml
```

---

## Performance Optimization

### Database Optimization
```kotlin
// Add indexes for frequent queries
@Entity(tableName = "price_alerts", indices = [
    Index(value = ["status"]),
    Index(value = ["stockSymbol"])
])
data class PriceAlertEntity(...)

// Use Flow for reactive updates
val activeAlerts: Flow<List<PriceAlertEntity>> = 
    alertDao.getActiveAlerts()
```

### API Optimization
```kotlin
// Implement caching
val cacheTime = 15 * 60 * 1000 // 15 minutes
val isCached = System.currentTimeMillis() - lastFetchTime < cacheTime
if (isCached) return cachedData
```

### Memory Optimization
```kotlin
// Use proper scope management
viewModelScope.launch { /* Automatically cancelled */ }

// Avoid memory leaks in Compose
DisposableEffect(key1 = Unit) {
    // Setup
    onDispose { /* Cleanup */ }
}
```

---

## Building & Deployment

### Build Variants
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# With flavor
./gradlew assembleProdRelease
```

### Signing Release APK
```bash
./gradlew bundleRelease \
  -Pandroid.injected.signing.store.file=/path/to/keystore.jks \
  -Pandroid.injected.signing.store.password=password \
  -Pandroid.injected.signing.key.alias=key_alias \
  -Pandroid.injected.signing.key.password=key_password
```

---

## Troubleshooting

### Build Issues
- Clear cache: `Build > Clean Project`
- Invalidate cache: `File > Invalidate Caches...`
- Resync: `File > Sync with Gradle Files`

### Runtime Issues
- Check logcat for exceptions
- Use Android Profiler for memory analysis
- Use Layout Inspector for UI debugging

### Database Issues
- Check entity migrations in `StockAlertDatabase`
- Clear app data: adb shell pm clear com.stockalert
- Inspect database with Android Studio Database Inspector

## Project Layout Understanding

### Key Directories

#### `/data` - Data Layer
- **api/** - Retrofit API interfaces and response models
- **db/** - Room database entities, DAOs, and database class
- **repository/** - Repository pattern for data operations

```kotlin
// Example: Fetching stock price
val repository = StockRepository(...)
val result = repository.getStockPrice("RELIANCE") // Returns Result<StockPriceResponse>
```

#### `/domain` - Business Models
- **model/** - Domain-level data classes
- Independent of data source implementation
- Used by ViewModels and UI

```kotlin
// Domain model (independent of DB/API specifics)
data class Stock(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    // ...
)
```

#### `/ui` - Presentation Layer
- **home/** - Search screen
- **detail/** - Stock details and alert creation
- **alerts/** - Alert management
- **components/** - Reusable Compose functions
- **theme/** - Material 3 theming

```kotlin
@Composable
fun HomeScreen(
    onStockSelected: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
)
```

#### `/worker` - Background Tasks
- WorkManager implementation
- Scheduled price checking logic
- Hilt integration for dependency injection

#### `/notification` - Notif...System
- NotificationHelper for creating and showing notifications
- NotificationReceiver for handling notification actions

#### `/di` - Dependency Injection
- Hilt modules for:
  - Network (Retrofit, OkHttp)
  - Database (Room)
  - Repository
  - Notifications

## Modifying Core Features

### 1. Adding a New Stock to Search
**File**: `StockRepository.kt`
```kotlin
// In searchStocks() method, add to commonStocks list
val commonStocks = listOf(
    "RELIANCE", "TCS", "INFY", "HDFC", "ICICIBANK", "AXISBANK",
    "LT", "ITC", "HCLTECH", "BAJAJFINSV", "MARUTI", "SUNPHARMA",
    "SBIN", "KOTAKBANK", "WIPRO", "ASIANPAINT", "BAJAJSSMARTFINSERVE",
    "DMART", "NESTLEIND", "POWERGRID", "DRREDDY",
    "YOUR_NEW_STOCK"  // Add here
)
```

### 2. Changing Background Check Frequency
**File**: `StockAlertApp.kt`
```kotlin
private fun schedulePriceCheckWorker() {
    val priceCheckRequest = PeriodicWorkRequestBuilder<PriceCheckWorker>(
        15,  // Change interval (minutes)
        TimeUnit.MINUTES,
        5,   // Change flex interval
        TimeUnit.MINUTES
    ).build()
    // ...
}
```

### 3. Customizing Notifications
**File**: `NotificationHelper.kt`
```kotlin
fun showPriceAlertNotification(
    alertId: Int,
    stockName: String,
    currentPrice: Double,
    targetPrice: Double
) {
    // Modify notification title, content, or styling
}
```

### 4. Adding New Database Fields
**File**: `Entities.kt`
```kotlin
@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    // Existing fields...
    val newField: String = ""  // Add here
)
```
Then update:
- `Dao.kt` - Update queries if needed
- `Models.kt` - Add to domain model
- `Repository.kt` - Handle in business logic

## Testing Features

### Manual Testing Checklist

#### Stock Search
- [ ] Open HomeScreen
- [ ] Type "RELIANCE" - should find results
- [ ] Type "TCS" - should find results
- [ ] Type random text - should show "No Results"
- [ ] Tap a stock - should navigate to DetailScreen

#### Stock Details
- [ ] Verify all stock info displays correctly
- [ ] Price shows in ₹ format
- [ ] Percentage change is color-coded (green/red)
- [ ] "Set Price Alert" button is tappable

#### Alert Creation
- [ ] Enter target price and select "Price Goes Above"
- [ ] Save and verify in "My Alerts" screen
- [ ] Create another with "Price Falls Below"
- [ ] Verify both alerts appear in list

#### Background Monitoring
```bash
# Check WorkManager logs
adb logcat | grep WorkManager
adb logcat | grep PriceCheckWorker
```

- Manually trigger background task: 
  ```bash
  adb shell cmd jobscheduler run -f com.stockalert 1
  ```

#### Notifications
- [ ] Disable WiFi/mobile to simulate network-less state
- [ ] Manually edit alert price to match current price
- [ ] Trigger WorkManager and watch for notification
- [ ] Tap notification and verify app opens

### Debugging

#### Enable Verbose Logging
**File**: `build.gradle.kts`
```kotlin
// Already configured with basic logging
// For more details, modify HttpLoggingInterceptor level
```

#### Check Database
```bash
# Access device storage
adb shell

# Navigate to app database
cd /data/data/com.stockalert/databases

# View with sqlite3
sqlite3 stock_alert_db
.tables
SELECT * FROM price_alerts;
SELECT * FROM stocks;
```

#### Monitor Network Calls
**File**: `DependencyInjection.kt`
```kotlin
private fun provideOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Set to BODY for detailed logs
    }
    // ...
}
```

## Common Development Tasks

### Task 1: Add a New Alert Type
Currently supports: "ABOVE" and "BELOW"

**Files to modify**:
1. `Models.kt` - Add to AlertType enum
2. `StockRepository.kt` - Add condition logic in `checkAlertCondition()`
3. `DetailScreen.kt` - Add UI option for new type
4. `Database` migration if needed

### Task 2: Change API Provider
Currently uses Yahoo Finance

**Files to modify**:
1. `YahooFinanceApi.kt` - Change interface
2. `ApiModels.kt` - Update response models
3. `DependencyInjection.kt` - Update base URL
4. `StockRepository.kt` - Update parsing logic

### Task 3: Add Stock Watchlist (Future)
**Files to create**:
1. `data/db/WatchlistEntity.kt`
2. `ui/watchlist/WatchlistScreen.kt`
3. `ui/watchlist/WatchlistViewModel.kt`
4. Update `MainActivity.kt` navigation

## Performance Optimization

### Current Optimizations
- ✅ Room database with indexed queries
- ✅ Stock data caching
- ✅ Grouped API calls by symbol
- ✅ Coroutine-based async operations
- ✅ Compose state hoisting

### Potential Future Optimizations
- Implement paging for large alert lists
- Add API response caching with TTL
- Use WorkManager backoff strategies
- Implement local database sync strategies

## Troubleshooting Development Issues

### Issue: Gradle Sync Fails
**Solution**:
```bash
# Clean Gradle cache
rm -rf ~/.gradle/caches

# Rebuild
./gradlew clean build
```

### Issue: Hilt Compilation Errors
**Solution**:
```
Build > Clean Project
Build > Rebuild Project
```

Ensure kapt is running for all @Hilt annotations.

### Issue: WorkManager Not Triggering
**Solution**: 
1. Check if app is in device Doze mode
2. Disable battery optimization for app
3. Verify RECEIVE_BOOT_COMPLETED permission

### Issue: API Returns Empty Results
**Solution**:
- Verify ticker symbol with ".NS" suffix
- Check if market is open (9:15 AM - 3:30 PM IST)
- Monitor logcat for API errors

## Version Control

```bash
# Initialize git (if needed)
git init

# Commit your changes
git add .
git commit -m "Initial StockAlert project"

# Useful gitignore already provided in .gitignore
```

## Building for Release

```bash
# Build release APK
./gradlew assembleRelease

# Output location:
# app/build/outputs/apk/release/app-release.apk

# Sign APK (requires keystore)
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore my-release-key.jks \
  app-release-unsigned.apk alias_name
```

## Documentation Files

- `README.md` - Project overview and features
- `build.gradle.kts` - All dependency versions
- This file - Development guide

## Next Steps for Enhancement

1. **Integrate Firebase**: For cloud notifications
2. **Add Analytics**: Track user behavior
3. **Implement Charts**: Show price history
4. **Add Search History**: Remember recent searches
5. **Create Settings Screen**: Customize alert frequency, notification options
6. **AddMulti-language Support**: Hindi, Marathi, etc.

## Resources

- [Android Developer Guide](https://developer.android.com/docs)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose/documentation)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [WorkManager Best Practices](https://developer.android.com/guide/background)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

---

**Happy coding! 🚀**
