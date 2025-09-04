
package com.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Hooks {

    private static WebDriver driver;

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        String headless = System.getenv().getOrDefault("HEADLESS", "true");
        if (headless.equalsIgnoreCase("true")) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1366,768");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options); // Selenium Manager will resolve the driver
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
