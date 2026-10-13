package pageobject;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mobil.config.DriverManager;
import org.mobil.factory.DriverFactory;
import org.mobil.pages.LoginPage;
import org.mobil.pages.NavBarPage;
import org.mobil.utils.AppiumServerManager;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Login képernyő (classic Page Object)")
class LoginTest {

    private NavBarPage navBar;

    @BeforeAll
    static void startAppiumServer() {
        AppiumServerManager.startServer();
    }

    @AfterAll
    static void stopAppiumServer() {
        AppiumServerManager.stop();
    }

    @BeforeEach
    void startSession() {
        DriverManager.setDriver(DriverFactory.createDriver());
        navBar = new NavBarPage();
    }

    @AfterEach
    void endSession() {
        DriverManager.removeDriver();
    }

    @Test
    @Tag("login")
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Helyes adatokkal be lehet jelentkezni")
    void userCanLoginWithValidCredentials() {
        LoginPage loginPage = navBar.openLogin();
        assertThat(loginPage.isDisplayed()).isTrue();

        loginPage.login("test@webdriver.io", "Test1234!");

        assertThat(loginPage.getAlertMessage()).isEqualTo("You are logged in!");
        loginPage.clickOnAlert();
    }

    @Test
    @Tag("login")
    @Tag("regression")
    @DisplayName("Helytelen adatokkal hibaüzenet jelenik meg")
    void userCannotLoginWithInvalidCredentials() {
        LoginPage loginPage = navBar.openLogin();

        loginPage.login("invalid@webdriver.io", "wrongPassword");

        assertThat(loginPage.getAlertMessage()).isNotBlank();
        loginPage.clickOnAlert();
    }

    @ParameterizedTest(name = "email=\"{0}\", password=\"{1}\" -> \"{2}\"")
    @MethodSource("invalidFieldData")
    @Tag("login")
    @Tag("regression")
    @DisplayName("Hibás mezőkitöltésnél a megfelelő validációs üzenet jelenik meg")
    void userSeesFieldValidationMessage(String email, String password, String expectedMessage) {
        LoginPage loginPage = navBar.openLogin();

        loginPage.enterUsername(email);
        loginPage.enterPassword(password);
        loginPage.tapLogin();

        assertThat(loginPage.isValidationMessageDisplayed(expectedMessage)).isTrue();
    }


    static Stream<Arguments> invalidFieldData() {
        return Stream.of(
                Arguments.of("", "Password123", "Please enter a valid email address"),
                Arguments.of("notanemail", "Password123", "Please enter a valid email address"),
                Arguments.of("valid@webdriver.io", "", "Please enter at least 8 characters"),
                Arguments.of("valid@webdriver.io", "short", "Please enter at least 8 characters")
        );
    }
}
