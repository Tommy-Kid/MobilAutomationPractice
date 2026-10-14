package org.mobil.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.mobil.base.BasePage;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class WebPage extends BasePage {

    // A főcím: "One framework to test every platform your users are on"
    private static final String HEADLINE_START = "One framework to test";
    private static final String LIVE_MODE = "Live mode";

    @AndroidFindBy(className = "android.webkit.WebView")
    @iOSXCUITFindBy(className = "XCUIElementTypeWebView")
    private WebElement screen;

    public boolean isDisplayed() {
        return isDisplayed(screen);
    }

    /** A Web fül megnyílt, ha a WebView megjelent (csak ezen a fülön van WebView). */
    public boolean isOpened() {
        return isVisibleWithin(AppiumBy.className("android.webkit.WebView"), Duration.ofSeconds(15));
    }

    /**
     * Betöltött, ha a főcím megtalálható. Elemre görgetve keressük, mert az app megőrzi a WebView
     * görgetési helyzetét (a Jenkinsben a láblécnél nyílt meg), a scrollIntoView pedig előbb az elejére
     * görget. textContains: ha a cím több részre van tördelve, akkor is egyezzen.
     */
    public boolean isLoaded() {
        if (!isDisplayed(screen)) {
            return false;
        }
        try {
            scrollIntoView("new UiSelector().textContains(\"" + HEADLINE_START + "\")");
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
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
