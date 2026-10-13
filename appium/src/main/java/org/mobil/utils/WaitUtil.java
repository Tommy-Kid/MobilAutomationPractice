package org.mobil.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobil.config.DriverManager;
import org.mobil.constants.FrameworkConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtil {
    private static final Logger log = LogManager.getLogger(WaitUtil.class);

    private WaitUtil() {}

    private static WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    private static WebDriverWait getWait() {
        return getWait(FrameworkConstants.EXPLICIT_WAIT);
    }

    public static WebElement waitForVisibility(WebElement element) {
        log.debug("Waiting for element visibility");
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForVisibility(By locator) {
        log.debug("Waiting for element visibility by locator: {}", locator);
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickability(WebElement element) {
        log.debug("Waiting for element clickability");
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForClickability(By locator) {
        log.debug("Waiting for element clickability by locator: {}", locator);
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresent(By locator){
        log.debug("Waiting for element presence by locator: {}", locator);
        return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static WebElement waitForPresent(WebElement element) {
        log.debug("Waiting for element presence");
        return getWait().until(driver -> {
            try {
                element.isEnabled();
                return element;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }
}
