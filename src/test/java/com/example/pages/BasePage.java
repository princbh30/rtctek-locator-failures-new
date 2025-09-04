
package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String url) {
        driver.get(url);
    }

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
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return driver.findElement(locator);
    }
}
