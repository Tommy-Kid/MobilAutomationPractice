package org.mobil.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.mobil.base.BasePage;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DragTabPage extends BasePage {

    @AndroidFindBy(accessibility = "Drag-drop-screen")
    @iOSXCUITFindBy(accessibility = "")
    WebElement screen;

    @AndroidFindBy(accessibility = "renew")
    @iOSXCUITFindBy(accessibility = "renew")
    WebElement renewButton;

    private static final List<String> POSITIONS =
            List.of("l1", "c1", "r1", "l2", "c2", "r2", "l3", "c3", "r3");


    public boolean isDisplayed() {
        return isDisplayed(screen);
    }


    public void resetPuzzle() {
        click(renewButton);
    }

    public void placePiece(String pos) {
        WebElement piece = getDriver().findElement(AppiumBy.accessibilityId("drag-" + pos));
        WebElement zone  = getDriver().findElement(AppiumBy.accessibilityId("drop-" + pos));
        dragAndDrop(piece, zone);
    }

    public void solvePuzzle() {
        resetPuzzle();
        POSITIONS.forEach(this::placePiece);
    }

    public boolean isSolved() {
        return isTextDisplayed("You made it, click retry if you want to try it again.");
    }
}
