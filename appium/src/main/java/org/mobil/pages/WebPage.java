package org.mobil.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.mobil.base.BasePage;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class WebPage extends BasePage {

    /** A Web fül főcíme - ha ez látszik, a WebView betöltötte az oldalt. */
    private static final String HEADLINE = "One framework to test every platform your users are on";
    /** Az Inspector szerint: android.widget.TextView, text="Live mode". */
    private static final String LIVE_MODE = "Live mode";

    @AndroidFindBy(className = "android.webkit.WebView")
    @iOSXCUITFindBy(className = "XCUIElementTypeWebView")
    private WebElement screen;

    public boolean isDisplayed() {
        return isDisplayed(screen);
    }

    /** Az oldal akkor van betöltve, ha a főcím megjelent. */
    public boolean isLoaded() {
        return isTextVisibleWithin(HEADLINE, Duration.ofSeconds(20));
    }

    public void findAndOpenLiveMode() {
        scrollToText(LIVE_MODE).click();
    }

    /** Továbbnavigáltunk, ha a "Live mode" cím már nem látszik. */
    public boolean navigatedAway() {
        return isGoneWithin(
                AppiumBy.androidUIAutomator("new UiSelector().text(\"" + LIVE_MODE + "\")"),
                Duration.ofSeconds(10));
    }
}
