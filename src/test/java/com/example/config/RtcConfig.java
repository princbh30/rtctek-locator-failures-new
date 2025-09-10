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
 * 
 * Usage:
 * 1. Call RtcConfig.initialize(driver) in your test setup
 * 2. Replace driver.findElement() calls with RtcConfig.findElement()
 */
public class RtcConfig {
    
    private static RtcIntegration rtcIntegration;
    private static boolean initialized = false;
    private static Properties rtcProperties;
    
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
     * Reset RTC configuration (useful for testing)
     */
    public static void reset() {
        rtcIntegration = null;
        initialized = false;
        rtcProperties = null;
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
