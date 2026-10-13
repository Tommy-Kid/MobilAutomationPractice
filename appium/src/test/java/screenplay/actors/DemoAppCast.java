package screenplay.actors;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.Cast;
import org.openqa.selenium.WebDriver;
import screenplay.abilities.UseAnAccount;

/**
 * Szereposztás: mindenki tudja kezelni az appot (BrowseTheWeb),
 * a szerepe szerint pedig további képességeket kap.
 */
public class DemoAppCast extends Cast {

    public DemoAppCast(WebDriver device) {
        super(new Ability[]{BrowseTheWeb.with(device)});
    }

    @Override
    protected void assignGeneralAbilitiesTo(Actor actor) {
        super.assignGeneralAbilitiesTo(actor);

        if (Personas.REGISTERED_USER.equals(actor.getName())) {
            // A demo app bármilyen érvényes email + min. 8 karakteres jelszót elfogad
            actor.whoCan(UseAnAccount.with("rita@example.com", "Jelszo123!"));
        }
    }
}
