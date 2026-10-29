package pageobject;

import extensions.ExtentExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobil.config.DriverManager;
import org.mobil.factory.DriverFactory;
import org.mobil.pages.NavBarPage;
import org.mobil.pages.SwipePage;
import org.mobil.utils.AppiumServerManager;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Swipe a képernyőn a,íg a cél kártyát meg nem találjuk")
@ExtendWith(ExtentExtension.class)
public class SwipeTest {


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
    @Tag("swipe-screen")
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("A swipe funció tesztelése és cél kártya megtalálása")
    void userCanLoginWithValidCredentials() {
        SwipePage swipePage = navBar.openSwipe();
        swipePage.swipeRightToExtendableCard();
        assertThat(swipePage.isExtendableCardDisplayed()).isTrue();
    }
}


