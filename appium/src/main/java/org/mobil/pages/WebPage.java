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

    // Csak a cím eleje: a teljes content-desc szövege eszközönként (pl. a kihajtott Fold széles
    // nézetében) eltérhet, a "Live mode" rész viszont mindenhol benne van.
    private static final String LIVE_MODE_TEXT = "Live mode";

    public boolean isDisplayed() {
        return isDisplayed(screen);
    }


    public void findAndOpenLiveMode(){
        scrollToDescriptionContains(LIVE_MODE_TEXT).click();
    }

    public boolean navigatedAway() {
        return isGoneWithin(AppiumBy.androidUIAutomator(
                "new UiSelector().descriptionContains(\"" + LIVE_MODE_TEXT + "\")"), Duration.ofSeconds(10));
    }



}
