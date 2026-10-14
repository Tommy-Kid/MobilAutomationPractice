package org.mobil.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.mobil.base.BasePage;
import org.openqa.selenium.WebElement;

/**
 * Alsó navigációs sáv (minden képernyőn látszik). Accessibility id minden fülre
 * ugyanaz Androidon (content-desc) és iOS-en (accessibilityLabel) is.
 */
public class NavBarPage extends BasePage {

    @AndroidFindBy(accessibility = "Home")
    @iOSXCUITFindBy(accessibility = "Home")
    private WebElement homeTab;

    @AndroidFindBy(accessibility = "Webview")
    @iOSXCUITFindBy(accessibility = "Webview")
    private WebElement webviewTab;

    @AndroidFindBy(accessibility = "Login")
    @iOSXCUITFindBy(accessibility = "Login")
    private WebElement loginTab;

    @AndroidFindBy(accessibility = "Forms")
    @iOSXCUITFindBy(accessibility = "Forms")
    private WebElement formsTab;

    @AndroidFindBy(accessibility = "Swipe")
    @iOSXCUITFindBy(accessibility = "Swipe")
    private WebElement swipeTab;

    @AndroidFindBy(accessibility = "Drag")
    @iOSXCUITFindBy(accessibility = "Drag")
    private WebElement dragTab;

    @AndroidFindBy(accessibility = "Menu")
    @iOSXCUITFindBy(accessibility = "Menu")
    private WebElement menuTab;

    public void openHome() {
        click(homeTab);
    }

    /**
     * Rákattint a Web fülre, és meg is győződik róla, hogy átváltott: ha 15 mp alatt nem jelenik meg
     * a WebView, itt azonnal, érthető üzenettel bukik el - nem később, egy félrevezető hibával.
     */
    public WebPage openWebview() {
        click(webviewTab);
        WebPage webPage = new WebPage();
        if (!webPage.isOpened()) {
            throw new IllegalStateException("A Web fülre kattintás után nem jelent meg a WebView");
        }
        log.info("Web fül megnyitva (selected={})", isSelected(webviewTab));
        return webPage;
    }

    /** A fül ki van-e jelölve (Android "selected" attribútum). */
    public boolean isWebviewTabSelected() {
        return isSelected(webviewTab);
    }

    private boolean isSelected(WebElement tab) {
        try {
            return "true".equals(tab.getAttribute("selected"));
        } catch (Exception e) {
            return false;
        }
    }

    public LoginPage openLogin() {
        click(loginTab);
        return new LoginPage();
    }

    public void openForms() {
        click(formsTab);
    }

    public void openSwipe() {
        click(swipeTab);
    }

    public void openDrag() {
        click(dragTab);
    }

    public void openMenu() {
        click(menuTab);
    }
}
