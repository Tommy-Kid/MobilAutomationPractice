package screenplay.abilities;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;

/**
 * Képesség: a szereplőnek van fiókja, amivel be tud lépni.
 * A Login task majd innen veszi az adatokat, így a tesztben nem kell jelszót írni.
 */
public class UseAnAccount implements Ability {

    private final String email;
    private final String password;

    private UseAnAccount(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UseAnAccount with(String email, String password) {
        return new UseAnAccount(email, password);
    }

    public static UseAnAccount as(Actor actor) {
        return actor.abilityTo(UseAnAccount.class);
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }

    @Override
    public String toString() {
        return "use an account (" + email + ")";
    }
}
