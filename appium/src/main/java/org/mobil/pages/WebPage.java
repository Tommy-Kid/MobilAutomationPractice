package org.mobil.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.mobil.base.BasePage;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class WebPage extends BasePage {
    @AndroidFindBy(className = "android.view.View")
    @iOSXCUITFindBy(className = "android.view.View")
    private WebElement screen;

    private static final String LIVE_MODE_ID =
            "Live mode An interactive dashboard that opens while your tests run.";
;

    public boolean isDisplayed() {
        return isDisplayed(screen);
    }


    public void findAndOpenLiveMode(){
        scrollToAccessibilityId(LIVE_MODE_ID).click();
    }

    public boolean navigatedAway() {
        return isGoneWithin(AppiumBy.accessibilityId(LIVE_MODE_ID), Duration.ofSeconds(10));
    }



}
