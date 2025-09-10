
package com.example.pages;

import org.openqa.selenium.WebDriver;

/**
 * Home page class - simplified for RTC integration
 * All element finding is now handled by RtcConfig
 */
public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void openHome() {
        open("https://rtctek.com/");
    }
}
