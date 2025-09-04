
# rtctek-locator-failures (Java + Selenium + Cucumber + POM)

This sample project demonstrates **10 failing UI tests** against `https://rtctek.com` caused by **broken/incorrect locators** using a **Page Object Model** structure.

> Intentional design: All scenarios fail because the element locators are wrong (non‑existent or overly brittle). This is useful for practicing locator debugging and stabilization strategies.

## Stack
- Java 17
- Maven
- Selenium 4 (uses Selenium Manager for drivers)
- Cucumber JVM 7 + JUnit 4
- Page Object Model (POM)

## Run
```bash
mvn -q test
```
The runner is `com.example.runner.CucumberTest` and will execute all scenarios tagged `@rtctek`.

### Headless mode
The tests default to **headless** Chrome for CI-friendliness. To see the browser, set env var `HEADLESS=false` before running:
```bash
HEADLESS=false mvn -q test
```

## Structure
```
src/test/java/com/example
  ├── hooks/Hooks.java           -> WebDriver lifecycle & options
  ├── pages/BasePage.java
  ├── pages/HomePage.java        -> Example POM for rtctek.com
  ├── steps/LocatorSteps.java    -> Generic step to try any locator type
  └── runner/CucumberTest.java   -> JUnit runner

src/test/resources/features/rtctek_locators.feature  -> 10 scenarios (all failing)
```

## Why these fail
Each example uses a different locator strategy (`id`, `name`, `className`, `cssSelector`, `xpath`, `linkText`, `partialLinkText`, `tagName`, etc.) paired with values that are **intentionally wrong** (e.g., `definitely-not-an-id`). Selenium will throw `NoSuchElementException` or similar, causing scenario failure.

## Make them pass
Update the locators in `rtctek_locators.feature` or inside the Page Object methods to **correct, stable** selectors (data-test IDs recommended) and rerun.

---

**Note**: This project does not assert any business logic — only locator resolution. It is safe for demo and training.
