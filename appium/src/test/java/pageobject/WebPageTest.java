package pageobject;

import extensions.ExtentExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobil.config.DriverManager;
import org.mobil.factory.DriverFactory;
import org.mobil.pages.LoginPage;
import org.mobil.pages.NavBarPage;
import org.mobil.pages.WebPage;
import org.mobil.utils.AppiumServerManager;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ExtentExtension.class)
public class WebPageTest {

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
    @Tag("Webpage")
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Megkeresni a Live Mode részt és tovább navigálni")
    void userCanOpenLiveMode() {
        WebPage webPage = navBar.openWebview();
        assertThat(webPage.isDisplayed()).isTrue();

        webPage.findAndOpenLiveMode();

        assertThat(webPage.navigatedAway())
                .as("A Live mode-ra kattintás után el kellett volna navigálni")
                .isTrue();
    }
}
