# Setup & Development Guide - StockAlert

## Quick Start

### 1. Clone & Open Project
```bash
# The project is located at:
# d:\Development\AI\claude\StockAlert\

# Open in Android Studio
# File > Open > Navigate to StockAlert folder
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
- **Android Studio**: 2022.1 or newer
- **SDK**: API 24 (minimum), API 34 (target)
- **Kotlin**: 1.9.0 or newer
- **JDK**: Java 8 or 11
- **RAM**: 4GB+ recommended

### IDE Configuration
1. Open `build.gradle.kts` files
2. All dependencies are pre-configured
3. Hilt annotation processing enabled (kapt)
4. Compose compiler version set to 1.5.3

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
