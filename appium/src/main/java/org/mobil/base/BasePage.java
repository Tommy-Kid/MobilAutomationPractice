package org.mobil.base;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.mobil.config.DriverManager;
import org.mobil.utils.WaitUtil;
import org.openqa.selenium.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected BasePage(){
        PageFactory.initElements(
                new AppiumFieldDecorator(DriverManager.getDriver(), Duration.ofSeconds(10)),
                this
        );
    }

    protected AppiumDriver getDriver(){return DriverManager.getDriver(); }

    protected void click(WebElement element) {
        WaitUtil.waitForClickability(element).click();
        log.debug("Clicked on element");
    }

    protected void openTab(WebElement tab, Supplier<Boolean> screenOpened, String screenName) {
        new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(WebDriverException.class, TimeoutException.class)
                .withMessage("A(z) " + screenName + " nem jelent meg a fülre kattintás után")
                .until(d -> {
                    new WebDriverWait(d, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(tab)).click();
                    log.debug("Clicked on tab, waiting for {}", screenName);
                    return screenOpened.get();
                });
    }

    protected void sendKey(WebElement element, String text) {
        WaitUtil.waitForVisibility(element);
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }

    protected String getText(WebElement element) {
        WaitUtil.waitForVisibility(element);
        return element.getText();
    }

    protected String getAttribute(WebElement element, String attribute) {
        WaitUtil.waitForPresent(element);
        return element.getAttribute(attribute);
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected WebElement scrollToAccessibilityId(String id) {
        return getDriver().findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().description(\"" + id + "\"))"));
    }


    protected WebElement scrollToText(String text) {
        return scrollIntoView("new UiSelector().text(\"" + text + "\")");
    }

    /**
     * Általános elemre görgetés tetszőleges UiSelectorral. A UiScrollable.scrollIntoView először az
     * elejére görget (scrollToBeginning), utána lefelé keres - így akkor is megtalálja az elemet,
     * ha az oldal egy korábbi futásból alul maradt.
     */
    protected WebElement scrollIntoView(String uiSelector) {
        return getDriver().findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(" + uiSelector + ")"));
    }

    /** Megvárja, hogy a lokátorral megadott elem látható legyen; true, ha a határidőn belül megjelent. */
    protected boolean isVisibleWithin(By locator, Duration timeout) {
        try {
            new WebDriverWait(getDriver(), timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Megvárja, hogy egy adott szövegű elem megjelenjen (pl. egy oldal címe = az oldal betöltött). */
    protected boolean isTextVisibleWithin(String text, Duration timeout) {
        By locator = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + text + "\")");
        try {
            new WebDriverWait(getDriver(), timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isGoneWithin(By locator, Duration timeout) {
        try {
            return new WebDriverWait(getDriver(), timeout)
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Szöveg alapján keres egy elemet (pl. natív/RN validációs üzenetek, amiknek nincs
     * resource-id-jük vagy accessibility id-jük, csak a kiírt szövegük). Szándékosan nem
     * WebElement mező, mert a konkrét szöveg tesztenként változik.
     */
    protected boolean isTextDisplayed(String text) {
        By locator = By.xpath(String.format("//*[@text=%s]", quoteForXPath(text)));
        try {
            WaitUtil.waitForVisibility(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String quoteForXPath(String text) {
        if (!text.contains("'")) {
            return "'" + text + "'";
        }
        return "concat('" + text.replace("'", "', \"'\", '") + "')";
    }

    protected void dragAndDrop(WebElement from, WebElement to) {
        Point a = center(from.getRect());
        Point b = center(to.getRect());

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence drag = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), a.x, a.y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(800)))
                .addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), b.x, b.y))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(List.of(drag));
        getDriver().resetInputState();
    }

    private Point center(Rectangle r) {
        return new Point(r.x + r.width / 2, r.y + r.height / 2);
    }
}
