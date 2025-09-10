package com.example.config;

import com.rtc.client.RtcIntegration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Properties;
import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import java.time.Duration;

/**
 * RTC Smart Driver Configuration Class
 * 
 * This class provides a single point of configuration for RTC Smart Driver integration.
 * It handles all RTC-related functionality with minimal changes to existing client code.
 * 
 * Features:
 * - Automatic locator healing using AI
 * - Cross-strategy healing (ID → CSS, XPath → LinkText, etc.)
 * - Centralized configuration management
 * - Minimal client-side changes required
 * 
 * Usage:
 * 1. Call RtcConfig.initialize(driver) in your test setup
 * 2. Replace driver.findElement() calls with RtcConfig.findElement()
 * 3. All RTC healing happens automatically
 */
public class RtcConfig {
    
    private static RtcIntegration rtcIntegration;
    private static boolean initialized = false;
    private static Properties rtcProperties;
    private static WebDriverWait wait;
    
    /**
     * Initialize RTC Smart Driver with the given WebDriver instance
     * 
     * @param driver The WebDriver instance to use for RTC integration
     */
    public static void initialize(WebDriver driver) {
        if (!initialized) {
            try {
                rtcProperties = loadRtcConfig();
                rtcIntegration = new RtcIntegration();
                rtcIntegration.setDriver(driver);
                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                initialized = true;
                System.out.println("🔧 RTC Smart Driver initialized successfully");
                System.out.println("🔧 RTC HTTP integration initialized");
            } catch (Exception e) {
                System.err.println("❌ Failed to initialize RTC Smart Driver: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Find element with RTC healing support
     * 
     * @param locator The locator to find the element with
     * @return WebElement if found, throws NoSuchElementException if not found
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
     * Find element with explicit wait and RTC healing support
     * 
     * @param locator The locator to find the element with
     * @return WebElement if found, throws NoSuchElementException if not found
     */
    public static WebElement findElementWithWait(By locator) {
        if (!initialized) {
            throw new IllegalStateException("RTC not initialized. Call RtcConfig.initialize(driver) first.");
        }
        
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            // Fallback to RTC healing
            return findElement(locator);
        }
    }
    
    /**
     * Find elements with RTC healing support
     * 
     * @param locator The locator to find elements with
     * @return List of WebElements found
     */
    public static List<WebElement> findElements(By locator) {
        if (!initialized) {
            throw new IllegalStateException("RTC not initialized. Call RtcConfig.initialize(driver) first.");
        }
        
        try {
            return rtcIntegration.findElements(locator);
        } catch (Exception e) {
            System.err.println("❌ Error finding elements: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Find element with RTC healing support (convenience method)
     * 
     * @param driver The WebDriver instance
     * @param locator The locator to find the element with
     * @return WebElement if found, throws NoSuchElementException if not found
     */
    public static WebElement findElement(WebDriver driver, By locator) {
        initialize(driver);
        return findElement(locator);
    }
    
    /**
     * Check if RTC is initialized
     * 
     * @return true if RTC is initialized, false otherwise
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get RTC configuration property
     * 
     * @param key The property key
     * @return The property value or null if not found
     */
    public static String getProperty(String key) {
        return rtcProperties != null ? rtcProperties.getProperty(key) : null;
    }
    
    /**
     * Get RTC configuration property with default value
     * 
     * @param key The property key
     * @param defaultValue The default value if property not found
     * @return The property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return rtcProperties != null ? rtcProperties.getProperty(key, defaultValue) : defaultValue;
    }
    
    /**
     * Check if RTC healing is enabled
     * 
     * @return true if healing is enabled, false otherwise
     */
    public static boolean isHealingEnabled() {
        return "true".equalsIgnoreCase(getProperty("rtc.healing.enabled", "true"));
    }
    
    /**
     * Reset RTC configuration (useful for testing)
     */
    public static void reset() {
        rtcIntegration = null;
        initialized = false;
        rtcProperties = null;
        wait = null;
    }
    
    /**
     * Load RTC configuration from properties file
     * 
     * @return Properties object containing RTC configuration
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
