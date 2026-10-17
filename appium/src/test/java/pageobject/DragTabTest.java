package pageobject;


import extensions.ExtentExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobil.config.DriverManager;
import org.mobil.factory.DriverFactory;
import org.mobil.pages.DragTabPage;
import org.mobil.pages.LoginPage;
import org.mobil.pages.NavBarPage;
import org.mobil.utils.AppiumServerManager;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Drag&Drop képernyő (classic Page Object)")
@ExtendWith(ExtentExtension.class)
public class DragTabTest {

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
    @Tag("Drag-drop-screen")
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("A Drag and Drop puzzle kirakható")
    void userCanLoginWithValidCredentials() {
        DragTabPage dragTabPagePage = navBar.openDrag();
        assertThat(dragTabPagePage.isDisplayed()).isTrue();

        dragTabPagePage.solvePuzzle();
        assertThat(dragTabPagePage.isSolved()).isTrue();
    }
}
