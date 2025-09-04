
package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void openHome() {
        open("https://rtctek.com/");
    }

    public WebElement find(String type, String value) {
        return by(type, value);
    }
}
