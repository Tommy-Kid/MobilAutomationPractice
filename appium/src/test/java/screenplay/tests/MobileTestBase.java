package screenplay.tests;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.actors.OnStage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobil.config.DriverManager;
import org.mobil.utils.AppiumServerManager;
import org.mobil.factory.DriverFactory;
import org.openqa.selenium.WebDriver;
import screenplay.actors.DemoAppCast;

/**
 * Közös alap: az eszközt a device-farm foglalja le (DriverFactory), a szereplők ezt kapják meg.
 * Platform és eszköz: -Dplatform=android|ios, -Dudid=...
 */
@ExtendWith(SerenityJUnit5Extension.class)
public abstract class MobileTestBase {

    protected WebDriver device;

    @BeforeAll
    static void startAppiumServer() {
        AppiumServerManager.startServer();
    }

    @AfterAll
    static void stopAppiumServer() {
        AppiumServerManager.stop();
    }

    @BeforeEach
    void setTheStage() {
        DriverManager.setDriver(DriverFactory.createDriver());
        device = DriverManager.getDriver();
        OnStage.setTheStage(new DemoAppCast(device));
    }

    @AfterEach
    void drawTheCurtain() {
        OnStage.drawTheCurtain();
        DriverManager.removeDriver();
    }
}
