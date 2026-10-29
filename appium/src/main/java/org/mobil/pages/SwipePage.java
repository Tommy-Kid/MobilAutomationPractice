package org.mobil.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import lombok.Getter;
import org.mobil.base.BasePage;
import org.mobil.utils.GestureUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class SwipePage extends BasePage {

    @AndroidFindBy(accessibility = "Swipe-screen")
    private WebElement screen;

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"Carousel\")")
    private WebElement carousel;


    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"__CAROUSEL_ITEM_4__\")")
    private WebElement extendAbleCard;

    private static final int MAX_SWIPES = 10;


    public boolean isDisplayed(){
        return isDisplayed(screen);
    }

    public void swipeRightToExtendableCard() {
        swipeRightToTargetCard(extendAbleCard);
    }

    public boolean isExtendableCardDisplayed() {
        return isDisplayed(extendAbleCard);
    }

    private void swipeRightToTargetCard(WebElement targetCard) {
        for (int i = 0; i < MAX_SWIPES && !isDisplayed(targetCard); i++) {
            GestureUtils.swipeLeft();
        }
    }

}
