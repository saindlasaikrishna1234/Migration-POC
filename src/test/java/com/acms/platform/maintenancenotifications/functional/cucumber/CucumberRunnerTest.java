package com.acms.platform.maintenancenotifications.functional.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/",
        glue = {"com.acms.platform.maintenancenotifications.functional.cucumber.config",
                "com.acms.platform.maintenancenotifications.functional.cucumber"},
        plugin = {"pretty", "html:target/cucumber-reports"},
        monochrome = true)
public class CucumberRunnerTest {
}