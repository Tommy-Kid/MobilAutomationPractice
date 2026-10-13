package screenplay.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.questions.Visibility;
import screenplay.screens.HomeScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Kérdések a Home képernyőről. A Question a Screenplay "ellenőrző" rétege:
 * nem assertál, csak visszaadja, amit lát. Az ellenőrzés a tesztben történik (Ensure).
 */
public final class TheHomeScreen {

    private TheHomeScreen() {
    }

    public static Question<Boolean> isDisplayed() {
        return Visibility.of(HomeScreen.SCREEN).describedAs("a Home képernyő látható");
    }

    public static Question<String> title() {
        return Text.of(HomeScreen.TITLE).describedAs("a Home képernyő címe");
    }

    /** Melyik platform-logók látszanak a "Support" felirat felett. */
    public static Question<Collection<String>> supportedPlatforms() {
        return Question.about("a támogatott platformok").answeredBy(actor -> {
            List<String> platforms = new ArrayList<>();
            if (HomeScreen.APPLE_ICON.resolveFor(actor).isCurrentlyVisible()) {
                platforms.add("iOS");
            }
            if (HomeScreen.ANDROID_ICON.resolveFor(actor).isCurrentlyVisible()) {
                platforms.add("Android");
            }
            return platforms;
        });
    }
}
