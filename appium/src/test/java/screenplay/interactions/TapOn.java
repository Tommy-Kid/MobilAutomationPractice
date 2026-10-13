package screenplay.interactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Koppintás egy elemre. A Serenity Click-jét csomagolja, hogy a riportban és a kódban
 * mobilos nyelvezettel szerepeljen ("rákoppint").
 */
public class TapOn implements Interaction {

    private final Target target;

    public TapOn(Target target) {
        this.target = target;
    }

    public static TapOn the(Target target) {
        return instrumented(TapOn.class, target);
    }

    @Override
    @Step("{0} rákoppint: #target")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(target));
    }
}
