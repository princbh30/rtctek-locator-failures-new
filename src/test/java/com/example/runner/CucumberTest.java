
package com.example.runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.example.steps", "com.example.hooks"},
    plugin = {"pretty", "html:target/cucumber-report.html"},
    tags = "@rtctek",
    monochrome = true
)
public class CucumberTest { }
