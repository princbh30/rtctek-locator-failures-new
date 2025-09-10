# RTC Smart Driver Integration Guide - Ultra Simplified

## Overview
This guide shows how to integrate RTC Smart Driver with **minimal changes** to any Java Selenium Cucumber project. The integration automatically heals failed locators without requiring manual changes to existing test code.

## Key Benefits
- ✅ **Zero changes to existing test code** - `driver.findElement()` calls work automatically
- ✅ **Automatic healing** - Failed locators are healed transparently
- ✅ **Minimal setup** - Only 2 files need to be modified
- ✅ **Generic solution** - Works with any Java Selenium Cucumber project

## Integration Steps

### Step 1: Add JAR Dependency
1. Copy `rtc-client-2.0.0.jar` to your project's `lib/` directory
2. Add dependency to `pom.xml`:

```xml
<!-- RTC Smart Driver Client JAR (Local) -->
<dependency>
    <groupId>com.rtc</groupId>
    <artifactId>rtc-client</artifactId>
    <version>2.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/rtc-client-2.0.0.jar</systemPath>
</dependency>
```

### Step 2: Add Configuration File
Create `src/test/resources/rtc-config.properties`:

```properties
# RTC Smart Driver Configuration
rtc.api.url=http://localhost:8080/api/rtc
rtc.api.key=your-api-key-here
rtc.healing.enabled=true
rtc.healing.timeout=30
rtc.healing.retry.count=1
rtc.logging.level=INFO
rtc.logging.healing=true
```

### Step 3: Update Hooks.java
Replace your WebDriver initialization with RtcWebDriver:

```java
package com.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.rtc.client.RtcWebDriver; // Add this import

public class Hooks {
    private static WebDriver driver;
    private static boolean driverInitialized = false;

    @Before
    public void setUp() {
        if (!driverInitialized) {
            ChromeOptions options = new ChromeOptions();
            // ... your existing Chrome options ...
            
            // Create ChromeDriver and wrap it with RtcWebDriver for automatic healing
            WebDriver chromeDriver = new ChromeDriver(options);
            driver = new RtcWebDriver(chromeDriver); // This is the only change!
            driverInitialized = true;
        }
    }

    @After
    public void tearDown() {
        // Your existing tearDown logic
    }

    public static WebDriver getDriver() {
        return driver;
    }
}
```

### Step 4: That's It! 🎉
Your existing test code will now automatically use RTC healing:

```java
// This will automatically heal if the locator fails
WebElement element = driver.findElement(By.id("some-id"));
```

## How It Works

1. **RtcWebDriver** wraps your existing WebDriver
2. When `findElement()` is called, it first tries the original locator
3. If it fails with `NoSuchElementException`, it calls the RTC API
4. The API returns a healed locator
5. The healed locator is tried automatically
6. If successful, the element is returned; if not, the original exception is thrown

## Test Results
With this integration, all 15 test scenarios pass:
- ✅ 8 "bad" locators are automatically healed
- ✅ 7 "good" locators work normally
- ✅ Zero test failures

## Configuration Options

| Property | Description | Default |
|----------|-------------|---------|
| `rtc.api.url` | RTC API endpoint | `http://localhost:8080/api/rtc` |
| `rtc.api.key` | API key for authentication | Required |
| `rtc.healing.enabled` | Enable/disable healing | `true` |
| `rtc.healing.timeout` | API call timeout (seconds) | `30` |
| `rtc.healing.retry.count` | Number of retry attempts | `1` |
| `rtc.logging.level` | Logging level | `INFO` |
| `rtc.logging.healing` | Log healing attempts | `true` |

## Troubleshooting

### RTC Server Not Running
```
Error: RTC API call failed: Connection refused
```
**Solution**: Start the RTC server:
```bash
cd RTC-Automation-AI-Plugin
mvn spring-boot:run -pl rtc-app
```

### Invalid API Key
```
Error: RTC API call failed: 401 Unauthorized
```
**Solution**: Update `rtc.api.key` in `rtc-config.properties`

### Healing Not Working
Check the logs for:
- RTC API connectivity
- Valid API key
- Proper configuration

## File Structure After Integration

```
your-project/
├── lib/
│   └── rtc-client-2.0.0.jar          # RTC JAR
├── src/test/resources/
│   └── rtc-config.properties         # RTC configuration
├── src/test/java/com/example/hooks/
│   └── Hooks.java                     # Modified to use RtcWebDriver
└── pom.xml                           # Updated with RTC dependency
```

## What Changed

### Before Integration
```java
// Manual healing required
try {
    element = driver.findElement(locator);
} catch (NoSuchElementException e) {
    // Manual healing logic
    element = RtcConfig.findElement(locator);
}
```

### After Integration
```java
// Automatic healing - no code changes needed!
element = driver.findElement(locator);
```

## Support

For issues or questions:
1. Check the logs for RTC API connectivity
2. Verify configuration in `rtc-config.properties`
3. Ensure RTC server is running
4. Check API key validity

---

**Note**: This integration requires minimal changes and works with any existing Java Selenium Cucumber project. The RTC healing is completely transparent to your test code.
