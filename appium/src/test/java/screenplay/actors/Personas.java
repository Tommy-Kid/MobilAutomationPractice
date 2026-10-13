package screenplay.actors;

/**
 * A tesztek szereplői és a szerepük.
 * A név jelenik meg a Serenity riportban ("Gabi rákoppint: Home fül").
 */
public final class Personas {

    private Personas() {
    }

    /** Vendég: nincs fiókja, csak böngészi az appot (Home, Swipe, Drag, Webview). */
    public static final String GUEST = "Gabi";

    /** Regisztrált felhasználó: van fiókja, be tud lépni (Login, Sign up utáni lépések). */
    public static final String REGISTERED_USER = "Rita";
}
