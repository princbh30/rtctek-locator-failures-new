# RTC Smart Driver Integration Guide
## Java Selenium Cucumber Projects - Simplified Approach

This guide provides **minimal changes** required to integrate RTC Smart Driver into your Java Selenium Cucumber automation projects for automatic locator healing.

---

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Quick Integration (Minimal Changes)](#quick-integration-minimal-changes)
4. [Configuration](#configuration)
5. [Usage Examples](#usage-examples)
6. [Troubleshooting](#troubleshooting)
7. [Best Practices](#best-practices)
8. [FAQ](#faq)

---

## Overview

RTC Smart Driver provides **automatic locator healing** for Selenium WebDriver tests. When a locator fails, it uses AI to suggest alternative locators and automatically retries the test.

### Key Benefits:
- **Minimal Code Changes**: Only 2-3 lines of code changes required
- **Automatic Healing**: No manual intervention needed
- **Cross-Strategy Healing**: Heals ID → XPath, CSS → LinkText, etc.
- **Framework Agnostic**: Works with any Java Selenium framework
- **Easy Integration**: Single configuration class handles everything

---

## Prerequisites

- Java 8 or higher
- Maven 3.6 or higher
- Selenium WebDriver 4.x
- Cucumber 7.x
- RTC Smart Driver JAR file
- RTC Smart Driver server running (for AI healing)

---

## Quick Integration (Minimal Changes)

### Step 1: Add JAR Dependency

Add the RTC Smart Driver JAR to your project:

1. **Create `lib` directory** in your project root:
   ```bash
   mkdir lib
   ```

2. **Copy the JAR file** to the `lib` directory:
   ```bash
   cp rtc-client-2.0.0.jar lib/
   ```

3. **Update `pom.xml`**:
   ```xml
   <dependencies>
       <!-- Your existing dependencies -->
       
       <!-- RTC Smart Driver Client JAR (Local) -->
       <dependency>
           <groupId>com.rtc</groupId>
           <artifactId>rtc-client</artifactId>
           <version>2.0.0</version>
           <scope>system</scope>
           <systemPath>${project.basedir}/lib/rtc-client-2.0.0.jar</systemPath>
       </dependency>
   </dependencies>
   ```

### Step 2: Add Configuration File

Create `src/test/resources/rtc-config.properties`:

```properties
# RTC Smart Driver Configuration
# ================================

# RTC API Configuration
rtc.api.url=http://localhost:8080/api/rtc
rtc.api.key=your-api-key-here

# Healing Configuration
rtc.healing.enabled=true
rtc.healing.timeout=30
rtc.healing.retry.count=1

# Logging Configuration
rtc.logging.level=INFO
rtc.logging.healing=true

# Advanced Configuration (Optional)
rtc.healing.strategies=all
rtc.healing.cross.strategy=true
rtc.healing.ai.enabled=true
```

### Step 3: Create RTC Configuration Class

Create `src/test/java/com/example/config/RtcConfig.java`:

```java
package com.example.config;

import com.rtc.client.RtcIntegration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * RTC Smart Driver Configuration Class
 * 
 * This class provides a single point of configuration for RTC Smart Driver integration.
 * It handles all RTC-related functionality with minimal changes to existing client code.
 */
public class RtcConfig {
    
    private static RtcIntegration rtcIntegration;
    private static boolean initialized = false;
    private static Properties rtcProperties;
    
    /**
     * Initialize RTC Smart Driver with the given WebDriver instance
     */
    public static void initialize(WebDriver driver) {
        if (!initialized) {
            try {
                rtcProperties = loadRtcConfig();
                rtcIntegration = new RtcIntegration(driver, rtcProperties);
                initialized = true;
                System.out.println("🔧 RTC Smart Driver initialized successfully");
            } catch (Exception e) {
                System.err.println("❌ Failed to initialize RTC Smart Driver: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Find element with RTC healing support
     */
    public static WebElement findElement(By locator) {
        if (!initialized) {
            throw new IllegalStateException("RTC not initialized. Call RtcConfig.initialize(driver) first.");
        }
        
        try {
            return rtcIntegration.findElement(locator);
        } catch (NoSuchElementException e) {
            // RTC healing is handled internally by RtcIntegration
            throw e;
        }
    }
    
    /**
     * Find element with RTC healing support (convenience method)
     */
    public static WebElement findElement(WebDriver driver, By locator) {
        initialize(driver);
        return findElement(locator);
    }
    
    /**
     * Check if RTC is initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Load RTC configuration from properties file
     */
    private static Properties loadRtcConfig() {
        Properties config = new Properties();
        try (InputStream input = RtcConfig.class.getClassLoader().getResourceAsStream("rtc-config.properties")) {
            if (input == null) {
                System.err.println("⚠️ RTC config file not found. Using default configuration.");
                // Set default values
                config.setProperty("rtc.api.url", "http://localhost:8080/api/rtc");
                config.setProperty("rtc.api.key", "your-api-key-here");
                config.setProperty("rtc.healing.enabled", "true");
                config.setProperty("rtc.healing.timeout", "30");
                config.setProperty("rtc.healing.retry.count", "1");
                config.setProperty("rtc.logging.level", "INFO");
                config.setProperty("rtc.logging.healing", "true");
            } else {
                config.load(input);
                System.out.println("✅ RTC configuration loaded successfully");
            }
        } catch (IOException ex) {
            System.err.println("❌ Error loading RTC configuration: " + ex.getMessage());
            ex.printStackTrace();
        }
        return config;
    }
}
```

### Step 4: Initialize RTC in Test Setup

**Option A: In Hooks.java (Recommended)**

```java
package com.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.example.config.RtcConfig; // Add this import

public class Hooks {
    private static WebDriver driver;

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // ... your existing setup code ...
        driver = new ChromeDriver(options);
        
        // Initialize RTC Smart Driver - ONLY THIS LINE NEEDED
        RtcConfig.initialize(driver);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }
}
```

**Option B: In TestRunner.java**

```java
@Before
public void setUp() {
    driver = new ChromeDriver();
    RtcConfig.initialize(driver); // ONLY THIS LINE NEEDED
}
```

### Step 5: Replace Locator Calls

In your step definitions, replace `driver.findElement()` calls with `RtcConfig.findElement()`:

**Before:**
```java
@When("I try to locate by {string} with value {string}")
public void i_try_to_locate_by_with_value(String type, String value) {
    By locator = createLocator(type, value);
    try {
        foundElement = driver.findElement(locator); // Old way
        caughtException = null;
    } catch (NoSuchElementException e) {
        caughtException = e;
        foundElement = null;
    }
}
```

**After:**
```java
@When("I try to locate by {string} with value {string}")
public void i_try_to_locate_by_with_value(String type, String value) {
    By locator = createLocator(type, value);
    try {
        foundElement = RtcConfig.findElement(locator); // New way with healing
        caughtException = null;
    } catch (NoSuchElementException e) {
        caughtException = e;
        foundElement = null;
    }
}
```

---

## Configuration

### RTC Configuration Properties

| Property | Description | Default Value |
|----------|-------------|---------------|
| `rtc.api.url` | RTC Smart Driver server URL | `http://localhost:8080/api/rtc` |
| `rtc.api.key` | API key for authentication | `your-api-key-here` |
| `rtc.healing.enabled` | Enable/disable healing | `true` |
| `rtc.healing.timeout` | Timeout for healing requests (seconds) | `30` |
| `rtc.healing.retry.count` | Number of retry attempts | `1` |
| `rtc.logging.level` | Logging level (DEBUG, INFO, WARN, ERROR) | `INFO` |
| `rtc.logging.healing` | Enable healing-specific logs | `true` |

### Advanced Configuration

```properties
# Advanced RTC Configuration
rtc.healing.strategies=all
rtc.healing.cross.strategy=true
rtc.healing.ai.enabled=true
rtc.healing.fallback.enabled=true
rtc.healing.cache.enabled=true
```

---

## Usage Examples

### Basic Usage

```java
// Initialize RTC (usually in @Before method)
RtcConfig.initialize(driver);

// Use RTC for element finding
WebElement element = RtcConfig.findElement(By.id("submit-button"));
element.click();
```

### In Step Definitions

```java
@When("I click the {string} button")
public void i_click_the_button(String buttonText) {
    By locator = By.xpath("//button[contains(text(),'" + buttonText + "')]");
    WebElement button = RtcConfig.findElement(locator); // Automatic healing
    button.click();
}
```

### In Page Objects

```java
public class LoginPage {
    private WebDriver driver;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }
    
    public void enterUsername(String username) {
        WebElement usernameField = RtcConfig.findElement(By.id("username"));
        usernameField.sendKeys(username);
    }
    
    public void clickLogin() {
        WebElement loginButton = RtcConfig.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();
    }
}
```

---

## Troubleshooting

### Common Issues

#### 1. RTC Not Initialized Error
```
IllegalStateException: RTC not initialized. Call RtcConfig.initialize(driver) first.
```
**Solution**: Ensure you call `RtcConfig.initialize(driver)` in your test setup.

#### 2. RTC Server Not Running
```
Connection refused: localhost:8080
```
**Solution**: Start the RTC Smart Driver server before running tests.

#### 3. API Key Not Set
```
401 Unauthorized
```
**Solution**: Set the correct API key in `rtc-config.properties`.

#### 4. Healing Not Working
```
No healing messages in logs
```
**Solution**: Check that `rtc.healing.enabled=true` in configuration.

### Debug Mode

Enable debug logging:

```properties
rtc.logging.level=DEBUG
rtc.logging.healing=true
```

### Check RTC Status

```java
if (RtcConfig.isInitialized()) {
    System.out.println("✅ RTC is initialized");
} else {
    System.out.println("❌ RTC is not initialized");
}
```

---

## Best Practices

### 1. Initialize Early
Always initialize RTC in your test setup (`@Before` method) before any element finding.

### 2. Use Descriptive Locators
```java
// Good
By submitButton = By.id("submit-button");

// Avoid
By button = By.xpath("//button[1]");
```

### 3. Handle Exceptions Gracefully
```java
try {
    WebElement element = RtcConfig.findElement(locator);
    element.click();
} catch (NoSuchElementException e) {
    // Log the error and fail the test appropriately
    System.err.println("Element not found even after healing: " + e.getMessage());
    throw e;
}
```

### 4. Monitor Healing Success
Check logs to see which locators are being healed and adjust your locators accordingly.

### 5. Keep RTC Server Running
Ensure the RTC Smart Driver server is running and accessible during test execution.

---

## FAQ

### Q: How many changes do I need to make to my existing code?
**A**: Only 2-3 lines of code changes:
1. Add `RtcConfig.initialize(driver)` in test setup
2. Replace `driver.findElement()` with `RtcConfig.findElement()`

### Q: Does RTC work with all locator strategies?
**A**: Yes, RTC supports all Selenium locator strategies: ID, Name, ClassName, CSS, XPath, LinkText, PartialLinkText, and TagName.

### Q: Can RTC heal across different strategies?
**A**: Yes, RTC can heal from one strategy to another (e.g., ID → XPath, CSS → LinkText).

### Q: What happens if RTC can't heal a locator?
**A**: RTC will throw a `NoSuchElementException` just like the original Selenium behavior.

### Q: Is RTC compatible with other testing frameworks?
**A**: Yes, RTC works with any Java Selenium framework (TestNG, JUnit, Cucumber, etc.).

### Q: How do I disable RTC for specific tests?
**A**: You can check `RtcConfig.isInitialized()` and use regular `driver.findElement()` if needed.

---

## Summary

The RTC Smart Driver integration requires **minimal changes** to your existing code:

1. **Add JAR dependency** to `pom.xml`
2. **Add configuration file** `rtc-config.properties`
3. **Create RTC configuration class** `RtcConfig.java`
4. **Initialize RTC** in test setup (1 line)
5. **Replace locator calls** with `RtcConfig.findElement()` (1 line per call)

This approach provides **automatic locator healing** with **minimal code changes** and **maximum compatibility** with existing test frameworks.

---

**Need Help?** Contact the RTC Smart Driver team for support and assistance.
