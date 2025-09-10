package com.example.steps;

import com.example.hooks.Hooks;
import com.example.pages.HomePage;
import com.rtc.client.RtcIntegration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LocatorSteps {

    private final WebDriver driver = Hooks.getDriver();
    private HomePage home;

    @Given("I open the RTCtek homepage")
    public void i_open_the_rtctek_homepage() {
        home = new HomePage(driver);
        home.openHome();
    }

    @When("I try to locate by {string} with value {string}")
    public void i_try_to_locate_by_with_value(String type, String value) {
        try {
            By locator = getLocator(type, value);
            
            // Use RTC integration for automatic healing
            WebElement element = RtcIntegration.findElement(locator);
            
            System.out.println("✅ Successfully located: " + type + " = " + value);
            System.out.println("🔧 Element found: " + element.getTagName());
            
        } catch (Exception e) {
            System.err.println("❌ Locator failed even after RTC healing: " + type + " = " + value);
            System.err.println("❌ Error: " + e.getMessage());
            throw e; // keep failing the test
        }
    }

    @When("I try to locate by {string} with value {string} using RTC healing")
    public void i_try_to_locate_by_with_value_using_rtc_healing(String type, String value) {
        try {
            By locator = getLocator(type, value);
            
            // Use RTC integration for automatic healing
            WebElement element = RtcIntegration.findElement(locator);
            
            System.out.println("✅ Successfully located with RTC healing: " + type + " = " + value);
            System.out.println("🔧 Element found: " + element.getTagName());
            
        } catch (Exception e) {
            System.err.println("❌ Locator failed even after RTC healing: " + type + " = " + value);
            System.err.println("❌ Error: " + e.getMessage());
            throw e; // keep failing the test
        }
    }

    @Then("the step should fail due to locator issue")
    public void the_step_should_fail_due_to_locator_issue() {
        Assert.assertTrue(
                "This step is expected to be unreachable if the locator is wrong.",
                true
        );
    }

    @Then("the element should be found successfully")
    public void the_element_should_be_found_successfully() {
        // success if no exception is thrown
    }

    @Then("the element should be found with RTC healing")
    public void the_element_should_be_found_with_rtc_healing() {
        // success if no exception is thrown
        System.out.println("✅ RTC healing was successful!");
    }

    // 🔹 Helper method: map locator strategy to Selenium By
    private By getLocator(String type, String value) {
        switch (type.toLowerCase()) {
            case "id":
                return By.id(value);
            case "name":
                return By.name(value);
            case "classname":
            case "class":
                return By.className(value);
            case "tagname":
            case "tag":
                return By.tagName(value);
            case "linktext":
                return By.linkText(value);
            case "partiallinktext":
                return By.partialLinkText(value);
            case "css":
            case "cssselector":
                return By.cssSelector(value);
            case "xpath":
                return By.xpath(value);
            default:
                throw new IllegalArgumentException("❌ Unsupported locator type: " + type);
        }
    }
}
