package org.mobil.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.mobil.base.BasePage;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class WebPage extends BasePage {

    private static final String HEADLINE = "One framework to test every platform your users are on";
    private static final String LIVE_MODE = "Live mode";

    @AndroidFindBy(className = "android.webkit.WebView")
    @iOSXCUITFindBy(className = "XCUIElementTypeWebView")
    private WebElement screen;

    public boolean isDisplayed() {
        return isDisplayed(screen);
    }

    public boolean isLoaded() {
        return isTextVisibleWithin(HEADLINE, Duration.ofSeconds(20));
    }

    public void findAndOpenLiveMode() {
        scrollToText(LIVE_MODE).click();
    }

    public boolean navigatedAway() {
        return isGoneWithin(
                AppiumBy.androidUIAutomator("new UiSelector().text(\"" + LIVE_MODE + "\")"),
                Duration.ofSeconds(10));
    }
}
