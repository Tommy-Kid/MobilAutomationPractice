package screenplay.screens;

import io.appium.java_client.AppiumBy;
import net.serenitybdd.screenplay.targets.Target;

/**
 * Alsó navigációs sáv. Minden képernyőn látszik.
 *
 * Az app Androidon content-desc-et, iOS-en accessibilityLabel-t ad a füleknek,
 * és az Appium mindkettőt "accessibility id"-ként keresi. Ezért itt elég egy
 * lokátor, nem kell külön locatedForAndroid / locatedForIOS.
 */
public final class TabBar {

    private TabBar() {
    }

    public static final Target HOME = tab("Home");
    public static final Target WEBVIEW = tab("Webview");
    public static final Target LOGIN = tab("Login");
    public static final Target FORMS = tab("Forms");
    public static final Target SWIPE = tab("Swipe");
    public static final Target DRAG = tab("Drag");
    public static final Target MENU = tab("Menu");

    private static Target tab(String name) {
        return Target.the(name + " fül").located(AppiumBy.accessibilityId(name));
    }
}
