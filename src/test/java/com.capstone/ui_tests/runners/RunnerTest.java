package com.capstone.ui_tests.runners;

import org.junit.platform.suite.api.*;
import io.cucumber.junit.platform.engine.Constants;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = Constants.PLUGIN_PROPERTY_NAME,
        value = "pretty, summary, json:target/cucumber.json, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
@ConfigurationParameter(
        key = Constants.GLUE_PROPERTY_NAME,
        value = "com.capstone.ui_tests.stepdefinitions, com.capstone.ui_tests.hooks"
)
//@Tag("ui")
public class RunnerTest {}
