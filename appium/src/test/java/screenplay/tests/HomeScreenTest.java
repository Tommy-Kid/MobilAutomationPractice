package screenplay.tests;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.ensure.Ensure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import screenplay.actors.Personas;
import screenplay.interactions.Scroll;
import screenplay.questions.TheHomeScreen;
import screenplay.screens.HomeScreen;
import screenplay.tasks.NavigateTo;

@DisplayName("Home képernyő")
class HomeScreenTest extends MobileTestBase {

    private Actor gabi;

    @BeforeEach
    void guestOpensTheApp() {
        gabi = OnStage.theActorCalled(Personas.GUEST);
        gabi.attemptsTo(NavigateTo.theHomeScreen());
    }

    @Test
    @DisplayName("Megjelenik a WEBDRIVER felirat és az alcím")
    void showsTheBranding() {
        gabi.attemptsTo(
                Ensure.that(HomeScreen.SCREEN).isDisplayed(),
                Ensure.that(HomeScreen.TITLE).text().isEqualTo("WEBDRIVER"),
                Ensure.that(HomeScreen.SUBTITLE).isDisplayed()
        );
    }

    @Test
    @DisplayName("Az Apple és az Android logó is látszik a Support felirat felett")
    void showsBothSupportedPlatforms() {
        gabi.attemptsTo(
                Ensure.that(HomeScreen.APPLE_ICON).isDisplayed(),
                Ensure.that(HomeScreen.ANDROID_ICON).isDisplayed(),
                Ensure.that(HomeScreen.SUPPORT_LABEL).isDisplayed(),
                Ensure.that(TheHomeScreen.supportedPlatforms()).containsExactly("iOS", "Android")
        );
    }

    @Test
    @DisplayName("Le- és visszagörgetés után a cím újra látszik")
    void canScrollDownAndBackUp() {
        gabi.attemptsTo(
                Scroll.down(),
                Scroll.up(),
                Ensure.that(HomeScreen.TITLE).isDisplayed()
        );
    }
}
