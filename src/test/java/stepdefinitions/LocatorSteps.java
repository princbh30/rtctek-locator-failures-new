package stepdefinitions;

import com.example.hooks.Hooks;
import com.example.pages.HomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
        By locator = getLocator(type, value);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            System.out.println("✅ Successfully located: " + type + " = " + value);
        } catch (Exception e) {
            System.err.println("❌ Locator failed: " + type + " = " + value);
            throw e; // still fail the test
        }
    }

    @Then("the step should fail due to locator issue")
    public void the_step_should_fail_due_to_locator_issue() {
        Assert.assertTrue("This step is expected to be unreachable if the locator is wrong.", true);
    }

    @Then("the element should be found successfully")
    public void theElementShouldBeFoundSuccessfully() {
        // success if no exception thrown earlier
    }

    // 🔹 Helper method to build locator dynamically
    private By getLocator(String type, String value) {
        switch (type.toLowerCase()) {
            case "id": return By.id(value);
            case "name": return By.name(value);
            case "classname": return By.className(value);
            case "tagname": return By.tagName(value);
            case "linktext": return By.linkText(value);
            case "partiallinktext": return By.partialLinkText(value);
            case "css":
            case "cssselector": return By.cssSelector(value);
            case "xpath": return By.xpath(value);
            default: throw new IllegalArgumentException("Unsupported locator type: " + type);
        }
    }
}
