# RTC Smart Driver Integration Guide
## Java Selenium Cucumber Projects

This guide provides step-by-step instructions for integrating RTC Smart Driver into your Java Selenium Cucumber automation projects for automatic locator healing.

---

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Integration Steps](#integration-steps)
4. [Configuration](#configuration)
5. [Usage Examples](#usage-examples)
6. [Troubleshooting](#troubleshooting)
7. [API Reference](#api-reference)
8. [Best Practices](#best-practices)

---

## Overview

RTC Smart Driver provides automatic locator healing for Selenium WebDriver tests. When a locator fails, it automatically attempts to find alternative locators using 7 internal strategies and AI-powered analysis.

### Key Features

- **Automatic Healing**: No code changes required for existing tests
- **Cross-Strategy Healing**: ID → Class, CSS → XPath, etc.
- **AI-Powered Analysis**: Uses Gemini AI for intelligent locator suggestions
- **Minimal Integration**: Just add JAR dependency and configuration
- **Zero Configuration**: Works out of the box with sensible defaults

---

## Prerequisites

### System Requirements

- **Java**: 11 or higher
- **Maven**: 3.6 or higher
- **Selenium**: 4.15.0 or higher
- **Cucumber**: 7.14.0 or higher

### RTC Smart Driver Server

- RTC Smart Driver server must be running on `http://localhost:8080`
- API key must be configured (default: `your-api-key-here`)

---

## Integration Steps

### Step 1: Build RTC Client JAR

From the main RTC project directory:

```bash
cd rtc-automation-ai-plugin
mvn clean package -pl rtc-client -am
```

This creates `rtc-client-2.0.0.jar` in the `rtc-client/target/` directory.

### Step 2: Add JAR to Client Project

1. Create a `lib` directory in your client project root:
   ```bash
   mkdir lib
   ```

2. Copy the JAR file:
   ```bash
   copy rtc-client-2.0.0.jar lib/
   ```

### Step 3: Update Maven POM

Add the following dependency to your `pom.xml`:

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

### Step 4: Create Configuration File

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

### Step 5: Update Your Page Objects

Replace standard Selenium `findElement()` calls with RTC integration:

```java
import com.rtc.client.RtcIntegration;

public class BasePage {
    protected WebDriver driver;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        RtcIntegration.setDriver(driver);
    }
    
    public WebElement findElement(By locator) {
        return RtcIntegration.findElement(locator);
    }
}
```

### Step 6: Update Step Definitions

Use RTC integration in your Cucumber steps:

```java
@When("I try to locate by {string} with value {string}")
public void i_try_to_locate_by_with_value(String type, String value) {
    By locator = getLocator(type, value);
    WebElement element = RtcIntegration.findElement(locator);
    // Element found or healed automatically
}
```

---

## Configuration

### Configuration File Options

| Property | Default | Description |
|----------|---------|-------------|
| `rtc.api.url` | `http://localhost:8080/api/rtc` | RTC API endpoint |
| `rtc.api.key` | `your-api-key-here` | API authentication key |
| `rtc.healing.enabled` | `true` | Enable/disable healing |
| `rtc.healing.timeout` | `30` | Healing request timeout (seconds) |
| `rtc.healing.retry.count` | `1` | Number of healing attempts |
| `rtc.logging.level` | `INFO` | Logging level |
| `rtc.logging.healing` | `true` | Log healing attempts |

### Environment Variables

You can override configuration using environment variables:

```bash
export RTC_API_URL=http://your-server:8080/api/rtc
export RTC_API_KEY=your-actual-api-key
```

---

## Usage Examples

### Basic Usage

```java
// Initialize RTC integration
RtcIntegration.setDriver(driver);

// Find element with automatic healing
WebElement element = RtcIntegration.findElement(By.id("submit-button"));

// If the ID fails, RTC will try alternative locators automatically
```

### Page Object Pattern

```java
public class LoginPage {
    private WebDriver driver;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        RtcIntegration.setDriver(driver);
    }
    
    public WebElement getUsernameField() {
        return RtcIntegration.findElement(By.id("username"));
    }
    
    public WebElement getPasswordField() {
        return RtcIntegration.findElement(By.name("password"));
    }
    
    public WebElement getLoginButton() {
        return RtcIntegration.findElement(By.cssSelector("button[type='submit']"));
    }
}
```

### Cucumber Step Definitions

```java
@When("I click the {string} button")
public void i_click_the_button(String buttonText) {
    By locator = By.xpath("//button[contains(text(),'" + buttonText + "')]");
    WebElement button = RtcIntegration.findElement(locator);
    button.click();
}
```

### Cross-Strategy Healing

RTC automatically tries different locator strategies:

```java
// Original locator (ID)
By originalLocator = By.id("submit-btn");

// If ID fails, RTC might try:
// - By.className("submit-button")
// - By.cssSelector("button[type='submit']")
// - By.xpath("//button[contains(@class,'submit')]")
// - And more...

WebElement element = RtcIntegration.findElement(originalLocator);
```

---

## Troubleshooting

### Common Issues

#### 1. JAR Not Found
```
Error: Could not find artifact com.rtc:rtc-client:jar:2.0.0
```

**Solution**: Ensure the JAR file is in the correct location and the `systemPath` is correct.

#### 2. RTC API Connection Failed
```
Error: RTC API call failed: Connection refused
```

**Solution**: 
- Ensure RTC server is running on the configured URL
- Check network connectivity
- Verify API key is correct

#### 3. Configuration Not Loaded
```
Error: Failed to initialize RTC Smart Driver
```

**Solution**: Ensure `rtc-config.properties` is in `src/test/resources/` directory.

#### 4. Healing Not Working
```
Error: Locator failed even after RTC healing
```

**Solution**: 
- Check RTC server logs
- Verify API key has proper permissions
- Ensure page source is accessible

### Debug Mode

Enable debug logging to see healing attempts:

```properties
rtc.logging.level=DEBUG
rtc.logging.healing=true
```

### Logs to Check

1. **Client Logs**: Check your test execution logs for RTC messages
2. **Server Logs**: Check RTC server logs for API requests
3. **Network Logs**: Verify HTTP requests are reaching the server

---

## API Reference

### RtcIntegration Class

#### Methods

| Method | Description | Parameters | Returns |
|--------|-------------|------------|---------|
| `setDriver(WebDriver)` | Initialize with WebDriver | `WebDriver driver` | `void` |
| `findElement(By)` | Find element with healing | `By locator` | `WebElement` |
| `findElements(By)` | Find elements with healing | `By locator` | `List<WebElement>` |
| `initialize()` | Initialize with default config | None | `void` |
| `initialize(String)` | Initialize with custom config | `String configFile` | `void` |

#### Example Usage

```java
// Initialize
RtcIntegration.setDriver(driver);

// Find single element
WebElement element = RtcIntegration.findElement(By.id("my-button"));

// Find multiple elements
List<WebElement> elements = RtcIntegration.findElements(By.className("menu-item"));

// Custom initialization
RtcIntegration.initialize("custom-rtc-config.properties");
```

### RTC API Endpoints

#### POST /api/rtc/heal-locator

**Request Body:**
```json
{
  "strategy": "id",
  "locator": "submit-button",
  "domSnippet": "<html>...</html>",
  "testCaseName": "Login Test",
  "failureReason": "Element not found",
  "testStep": "Click submit button",
  "testIntent": "Submit form",
  "elementPurpose": "Submit button"
}
```

**Response:**
```json
{
  "success": true,
  "healed_locator": "css:button[type='submit']",
  "healed_strategy": "css",
  "was_repaired": true,
  "repair_confidence": 0.9,
  "healing_method": "ai",
  "reasoning": "Found alternative locator using CSS selector"
}
```

---

## Best Practices

### 1. Page Object Design

```java
public class BasePage {
    protected WebDriver driver;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        RtcIntegration.setDriver(driver);
    }
    
    // Use RTC integration for all element finding
    protected WebElement findElement(By locator) {
        return RtcIntegration.findElement(locator);
    }
}
```

### 2. Error Handling

```java
@When("I click the {string} button")
public void i_click_the_button(String buttonText) {
    try {
        By locator = By.xpath("//button[contains(text(),'" + buttonText + "')]");
        WebElement button = RtcIntegration.findElement(locator);
        button.click();
    } catch (NoSuchElementException e) {
        // RTC healing failed, log and re-throw
        System.err.println("Failed to find button: " + buttonText);
        throw e;
    }
}
```

### 3. Configuration Management

```java
public class RTCConfig {
    private static Properties config;
    
    static {
        config = new Properties();
        try (InputStream input = RTCConfig.class.getClassLoader()
                .getResourceAsStream("rtc-config.properties")) {
            config.load(input);
        } catch (IOException e) {
            System.err.println("Failed to load RTC config: " + e.getMessage());
        }
    }
    
    public static String getApiUrl() {
        return config.getProperty("rtc.api.url", "http://localhost:8080/api/rtc");
    }
    
    public static String getApiKey() {
        return config.getProperty("rtc.api.key", "your-api-key-here");
    }
}
```

### 4. Test Data Management

```java
@Given("I have test data for {string}")
public void i_have_test_data_for(String testCase) {
    // Set up test data
    TestDataManager.setCurrentTestCase(testCase);
    
    // Initialize RTC with test context
    RtcIntegration.setDriver(driver);
}
```

### 5. Reporting Integration

```java
@AfterStep
public void afterStep(Scenario scenario) {
    if (scenario.isFailed()) {
        // Log RTC healing attempts
        System.out.println("RTC healing was attempted for failed step");
    }
}
```

---

## Advanced Configuration

### Custom Healing Strategies

```properties
# Enable specific healing strategies
rtc.healing.strategies=id,class,css,xpath
rtc.healing.cross.strategy=true
rtc.healing.ai.enabled=true
```

### Performance Tuning

```properties
# Optimize for performance
rtc.healing.timeout=15
rtc.healing.retry.count=1
rtc.healing.cache.enabled=true
```

### Security Configuration

```properties
# Secure API communication
rtc.api.key=your-secure-api-key
rtc.api.ssl.enabled=true
rtc.api.timeout=30
```

---

## Migration Guide

### From Standard Selenium

**Before:**
```java
WebElement element = driver.findElement(By.id("submit-button"));
```

**After:**
```java
RtcIntegration.setDriver(driver);
WebElement element = RtcIntegration.findElement(By.id("submit-button"));
```

### From Page Factory

**Before:**
```java
@FindBy(id = "submit-button")
WebElement submitButton;
```

**After:**
```java
public WebElement getSubmitButton() {
    return RtcIntegration.findElement(By.id("submit-button"));
}
```

### From WebDriverWait

**Before:**
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("submit-button")));
```

**After:**
```java
WebElement element = RtcIntegration.findElement(By.id("submit-button"));
// RTC handles waiting and healing automatically
```

---

## Support and Maintenance

### Getting Help

1. **Documentation**: Check this guide and RTC server documentation
2. **Logs**: Enable debug logging for detailed information
3. **Community**: Join RTC community forums
4. **Support**: Contact RTC support team

### Updates

- **JAR Updates**: Replace JAR file and update version in POM
- **Configuration Updates**: Update `rtc-config.properties` as needed
- **API Changes**: Check RTC server changelog for API updates

### Monitoring

Monitor RTC healing effectiveness:

```java
@AfterTest
public void afterTest() {
    // Log healing statistics
    System.out.println("RTC healing attempts: " + RTCStats.getHealingAttempts());
    System.out.println("RTC healing success rate: " + RTCStats.getSuccessRate());
}
```

---

## Conclusion

RTC Smart Driver integration provides automatic locator healing with minimal code changes. Follow this guide to integrate it into your Java Selenium Cucumber projects and enjoy more stable, maintainable test automation.

For questions or support, refer to the troubleshooting section or contact the RTC team.

---

**Version**: 2.0.0  
**Last Updated**: September 2025  
**Compatibility**: Java 11+, Selenium 4.15+, Cucumber 7.14+
