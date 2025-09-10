
package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.rtc.client.RtcIntegration;
import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Initialize RTC integration with the WebDriver
        RtcIntegration.setDriver(driver);
    }

    public void open(String url) {
        driver.get(url);
    }

    /**
     * Find element with RTC healing support
     * This method will automatically attempt to heal locators if they fail
     */
    public WebElement by(String type, String value) {
        By locator;
        switch (type.toLowerCase()) {
            case "id": locator = By.id(value); break;
            case "name": locator = By.name(value); break;
            case "classname": locator = By.className(value); break;
            case "css": 
            case "cssselector": locator = By.cssSelector(value); break;
            case "xpath": locator = By.xpath(value); break;
            case "linktext": locator = By.linkText(value); break;
            case "partiallinktext": locator = By.partialLinkText(value); break;
            case "tagname": locator = By.tagName(value); break;
            default: throw new IllegalArgumentException("Unsupported locator type: " + type);
        }
        
        // Use RTC integration for automatic healing
        return RtcIntegration.findElement(locator);
    }

    /**
     * Find element with explicit wait and RTC healing
     */
    public WebElement findElementWithWait(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return driver.findElement(locator);
        } catch (NoSuchElementException e) {
            // If element not found, try RTC healing
            return RtcIntegration.findElement(locator);
        }
    }

    /**
     * Find elements with RTC healing support
     */
    public java.util.List<WebElement> findElements(By locator) {
        return RtcIntegration.findElements(locator);
    }
}
