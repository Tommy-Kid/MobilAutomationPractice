package screenplay.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import screenplay.interactions.TapOn;
import screenplay.screens.TabBar;

/**
 * Navigáció az alsó fülsávon keresztül. Üzleti szintű lépés: a teszt nem tudja,
 * hogy ez egy koppintás egy accessibility id-ra.
 */
public final class NavigateTo {

    private NavigateTo() {
    }

    public static Performable theHomeScreen() {
        return Task.where("{0} megnyitja a Home képernyőt",
                TapOn.the(TabBar.HOME));
    }

    public static Performable theLoginScreen() {
        return Task.where("{0} megnyitja a Login képernyőt",
                TapOn.the(TabBar.LOGIN));
    }

    public static Performable theFormsScreen() {
        return Task.where("{0} megnyitja a Forms képernyőt",
                TapOn.the(TabBar.FORMS));
    }
}
