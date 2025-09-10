
package com.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.rtc.client.RtcWebDriver;

public class Hooks {

    private static WebDriver driver;
    private static boolean driverInitialized = false;

    @Before
    public void setUp() {
        // Only initialize driver once for the entire test suite
        if (!driverInitialized) {
            ChromeOptions options = new ChromeOptions();
            String headless = System.getenv().getOrDefault("HEADLESS", "true");
            if (headless.equalsIgnoreCase("true")) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--window-size=1366,768");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            // Create ChromeDriver and wrap it with RtcWebDriver for automatic healing
            WebDriver chromeDriver = new ChromeDriver(options);
            driver = new RtcWebDriver(chromeDriver);
            driverInitialized = true;
        }
    }

    @After
    public void tearDown() {
        // Don't quit driver after each scenario, keep it for the entire test suite
        // The driver will be closed when the JVM shuts down
    }

    public static WebDriver getDriver() {
        return driver;
    }
}
