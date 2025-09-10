package com.example.pages;

import org.openqa.selenium.WebDriver;

/**
 * Base page class - simplified for RTC integration
 * All element finding is now handled by RtcConfig
 */
public class BasePage {
    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String url) {
        driver.get(url);
    }
}