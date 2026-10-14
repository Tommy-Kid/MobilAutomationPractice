package org.mobil.base;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.mobil.config.DriverManager;
import org.mobil.utils.WaitUtil;
import org.openqa.selenium.By;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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

    /** Mint a scrollToAccessibilityId, de elég, ha a content-desc TARTALMAZZA a szöveget (eszközfüggetlenebb). */
    protected WebElement scrollToDescriptionContains(String text) {
        // WebView-ban a UiScrollable gyakran "átrohan" az elemen: a Chrome csak késve frissíti az
        // akkadálymentességi fát görgetés után, így a keresés lefut, mielőtt az elem megjelenne.
        // Ezért kis lépésekben görgetünk, és minden lépés után várunk egy kicsit, mielőtt keresünk.
        By target = AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + text + "\")");
        By scrollable = AppiumBy.androidUIAutomator("new UiSelector().scrollable(true)");
        AppiumDriver driver = getDriver();
        Duration implicit = driver.manage().timeouts().getImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        try {
            for (int i = 0; i < 40; i++) {
                java.util.List<WebElement> found = driver.findElements(target);
                if (!found.isEmpty()) {
                    return found.get(0);
                }
                WebElement area = driver.findElement(scrollable);
                Object canScrollMore = driver.executeScript("mobile: scrollGesture", java.util.Map.of(
                        "elementId", ((org.openqa.selenium.remote.RemoteWebElement) area).getId(),
                        "direction", "down",
                        "percent", 0.5));
                sleep(700);
                if (Boolean.FALSE.equals(canScrollMore)) {
                    // az oldal alján vagyunk: még egy utolsó keresés a frissült fán
                    found = driver.findElements(target);
                    if (!found.isEmpty()) {
                        return found.get(0);
                    }
                    break;
                }
            }
        } finally {
            driver.manage().timeouts().implicitlyWait(implicit);
        }
        throw new org.openqa.selenium.NoSuchElementException(
                "Nem található görgetés után sem: content-desc tartalmazza \"" + text + "\"");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
}
