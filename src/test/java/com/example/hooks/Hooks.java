
package com.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import com.rtc.client.RtcConfig;

public class Hooks {

    private static WebDriver driver;
    private static boolean driverInitialized = false;
    
    static {
        // Add shutdown hook to ensure browser is closed on JVM exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (driver != null) {
                try {
                    driver.quit();
                    System.out.println("🔧 Browser closed on JVM shutdown");
                } catch (Exception e) {
                    System.err.println("❌ Error closing browser on shutdown: " + e.getMessage());
                }
            }
        }));
    }

    @Before
    public void setUp() {
        // Create WebDriver with automatic RTC healing based on configuration
        driver = RtcConfig.createWebDriver();
    }

    @After
    public void tearDown() {
        // Close the browser after each test scenario to prevent memory leaks
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("🔧 Browser closed after test scenario");
            } catch (Exception e) {
                System.err.println("❌ Error closing browser: " + e.getMessage());
            }
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }
}
