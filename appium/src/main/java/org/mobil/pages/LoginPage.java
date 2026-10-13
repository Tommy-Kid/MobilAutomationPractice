package org.mobil.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.mobil.base.BasePage;
import org.openqa.selenium.WebElement;

/**
 * Login képernyő. Accessibility id-k a demo app (wdio appium-boilerplate)
 * LoginScreen screenobject-je alapján.
 */
public class LoginPage extends BasePage {

    @AndroidFindBy(accessibility = "Login-screen")
    @iOSXCUITFindBy(accessibility = "Login-screen")
    private WebElement screen;

    @AndroidFindBy(accessibility = "input-email")
    @iOSXCUITFindBy(accessibility = "input-email")
    private WebElement usernameInput;

    @AndroidFindBy(accessibility = "input-password")
    @iOSXCUITFindBy(accessibility = "input-password")
    private WebElement passwordInput;

    @AndroidFindBy(accessibility = "button-LOGIN")
    @iOSXCUITFindBy(accessibility = "button-LOGIN")
    private WebElement loginButton;

    @AndroidFindBy(id = "android:id/message")
    private WebElement nativeAlertMessage;

    @AndroidFindBy(id = "android:id/button1")
    private WebElement nativeAlertOkButton;



    public boolean isDisplayed() {
        return isDisplayed(screen);
    }

    public String getAlertMessage() {
        return getText(nativeAlertMessage);
    }

    public void enterUsername(String username) {
        sendKey(usernameInput, username);
    }

    public void enterPassword(String password) {
        sendKey(passwordInput, password);
    }

    public void tapLogin() {
        click(loginButton);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        tapLogin();
    }

    public void clickOnAlert(){
        click(nativeAlertOkButton);
    }


    public boolean isValidationMessageDisplayed(String message) {
        return isTextDisplayed(message);
    }
}
