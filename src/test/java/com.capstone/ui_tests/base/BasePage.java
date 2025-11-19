package com.capstone.ui_tests.base;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Selenide.*;

public abstract class BasePage {


    public void openPage(String url) {
        open(url);
    }


    protected void click(SelenideElement element) {
        element.shouldBe(Condition.visible).click();
    }

    protected void type(SelenideElement element, String text) {
        element.shouldBe(Condition.visible).setValue(text);
    }


    protected void clear(SelenideElement element) {
        element.shouldBe(Condition.visible).clear();
    }


    protected boolean isVisible(SelenideElement element) {
        return element.is(Condition.visible);
    }


    protected String getText(SelenideElement element) {
        return element.shouldBe(Condition.visible).getText();
    }

    protected void hover(SelenideElement element) {
        element.shouldBe(Condition.visible).hover();
    }


    protected void waitForText(SelenideElement element, String text) {
        element.shouldHave(Condition.text(text));
    }

    public String getCurrentUrl() {
        return WebDriverRunner.getWebDriver().getCurrentUrl();
    }
}
