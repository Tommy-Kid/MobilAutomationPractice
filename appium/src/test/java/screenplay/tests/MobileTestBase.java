package screenplay.tests;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.actors.OnStage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import screenplay.actors.DemoAppCast;

/**
 * Közös alap: a Serenity a serenity.conf alapján elindítja az Appium sessiont,
 * és a szereplők ezt az eszközt kapják meg.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public abstract class MobileTestBase {

    @Managed(driver = "appium")
    protected WebDriver device;

    @BeforeEach
    void setTheStage() {
        OnStage.setTheStage(new DemoAppCast(device));
    }

    @AfterEach
    void drawTheCurtain() {
        OnStage.drawTheCurtain();
    }
}
