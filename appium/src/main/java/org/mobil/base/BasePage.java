package org.mobil.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.mobil.config.DriverManager;
import org.mobil.utils.WaitUtil;
import org.openqa.selenium.By;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

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
