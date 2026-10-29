package org.mobil.utils;


import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobil.config.DriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mobile gesture utilities: swipe, scroll, tap, long press, pinch, zoom.
 */
public final class GestureUtils {

    private static final Logger log = LogManager.getLogger(GestureUtils.class);
    private static final int SWIPE_DURATION_MS = 600;

    private GestureUtils() {}

    private static AppiumDriver driver() {
        return DriverManager.getDriver();
    }

    /**
     * Swipe from one point to another using W3C Actions.
     */
    public static void swipe(int startX, int startY, int endX, int endY) {
        log.debug("Swiping from ({},{}) to ({},{})", startX, startY, endX, endY);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeAction = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(SWIPE_DURATION_MS), PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver().perform(List.of(swipeAction));
    }

    /**
     * Swipe up (scroll down) by percentage of screen height.
     */
    public static void swipeUp(double percentage) {
        Dimension size = driver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * (0.8 - percentage));
        swipe(startX, startY, startX, endY);
        log.info("Swiped up by {}%", (int)(percentage * 100));
    }

    /**
     * Swipe down (scroll up) by percentage of screen height.
     */
    public static void swipeDown(double percentage) {
        Dimension size = driver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * (0.2 + percentage));
        swipe(startX, startY, startX, endY);
        log.info("Swiped down by {}%", (int)(percentage * 100));
    }

    /**
     * Swipe left (navigate forward/next).
     */
    public static void swipeLeft() {
        Dimension size = driver().manage().window().getSize();
        int startY = size.height / 2;
        int startX = (int) (size.width * 0.8);
        int endX = (int) (size.width * 0.2);
        swipe(startX, startY, endX, startY);
        log.info("Swiped left");
    }

    /**
     * Swipe right (navigate back).
     */
    public static void swipeRight() {
        Dimension size = driver().manage().window().getSize();
        int startY = size.height / 2;
        int startX = (int) (size.width * 0.2);
        int endX = (int) (size.width * 0.8);
        swipe(startX, startY, endX, startY);
        log.info("Swiped right");
    }

    /**
     * Scroll to an element using UiScrollable (Android) or mobile:scroll (iOS).
     */
    public static void scrollToElement(String text) {
        AppiumDriver drv = driver();
        if (drv instanceof AndroidDriver) {
            scrollToElementAndroid(text);
        } else {
            scrollToElementIOS(text);
        }
    }

    private static void scrollToElementAndroid(String text) {
        String uiScrollable = String.format(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"%s\"))", text);
        driver().findElement(AppiumBy.androidUIAutomator(uiScrollable));
    }

    private static void scrollToElementIOS(String text) {
        Map<String, Object> params = new HashMap<>();
        params.put("direction", "down");
        params.put("predicateString", "label == '" + text + "'");
        ((JavascriptExecutor) driver()).executeScript("mobile: scroll", params);
    }

    /**
     * Long press on an element.
     */
    public static void longPress(WebElement element, int durationMillis) {
        log.debug("Long pressing element for {}ms", durationMillis);
        int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
        int y = element.getLocation().getY() + element.getSize().getHeight() / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPress = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(durationMillis), PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver().perform(List.of(longPress));
    }

    /**
     * Double tap on an element.
     */
    public static void doubleTap(WebElement element) {
        log.debug("Double tapping element");
        int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
        int y = element.getLocation().getY() + element.getSize().getHeight() / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence doubleTap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver().perform(List.of(doubleTap));
    }

    /**
     * Pinch zoom on screen.
     */
    public static void pinchZoom(boolean zoomIn) {
        Dimension size = driver().manage().window().getSize();
        int centerX = size.width / 2;
        int centerY = size.height / 2;

        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        int startOffset = zoomIn ? 10 : 200;
        int endOffset = zoomIn ? 200 : 10;

        Sequence f1 = new Sequence(finger1, 1)
                .addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX - startOffset, centerY))
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger1.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), centerX - endOffset, centerY))
                .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence f2 = new Sequence(finger2, 1)
                .addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX + startOffset, centerY))
                .addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger2.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), centerX + endOffset, centerY))
                .addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver().perform(Arrays.asList(f1, f2));
        log.info("Performed {} gesture", zoomIn ? "zoom-in" : "pinch-out");
    }

    /**
     * Hide keyboard if visible.
     */
    public static void hideKeyboard() {
        try {
            AppiumDriver drv = driver();
            if (drv instanceof HidesKeyboard) {
                ((HidesKeyboard) drv).hideKeyboard();
                log.debug("Keyboard hidden");
            }
        } catch (Exception e) {
            log.debug("Keyboard was not visible or could not be hidden: {}", e.getMessage());
        }
    }
}
