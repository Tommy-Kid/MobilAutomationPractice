package screenplay.screens;

import io.appium.java_client.AppiumBy;
import net.serenitybdd.screenplay.targets.Target;

/**
 * Home képernyő ("Screen Object"): csak lokátorok, logika nincs benne.
 */
public final class HomeScreen {

    private HomeScreen() {
    }

    /*
     * Az Apple és Android logó nem kép, hanem egy ikonfont (MaterialCommunityIcons) egy-egy karaktere
     * egy sima szövegmezőben. Ezért az Inspector egy kockát mutat "text"-ként.
     * A karakterkódok az ikonfont glyphmap-jéből:
     *   apple   = U+F0035
     *   android = U+F0032
     * Ezek a Unicode BMP-n kívül esnek, Javában ezért Character.toChars kell (két char lesz belőlük).
     */
    static final String APPLE_GLYPH = new String(Character.toChars(0xF0035));
    static final String ANDROID_GLYPH = new String(Character.toChars(0xF0032));

    /** A teljes képernyő konténere: testID / accessibilityLabel "Home-screen" mindkét platformon. */
    public static final Target SCREEN = Target.the("Home képernyő")
            .located(AppiumBy.accessibilityId("Home-screen"));

    public static final Target TITLE = byText("WEBDRIVER felirat", "WEBDRIVER");

    public static final Target SUBTITLE = byText("alcím", "Demo app for the appium-boilerplate");

    public static final Target SUPPORT_LABEL = byText("Support felirat", "Support");

    public static final Target APPLE_ICON = byText("Apple logó", APPLE_GLYPH);

    public static final Target ANDROID_ICON = byText("Android logó", ANDROID_GLYPH);

    /*
     * Alternatíva, ha a karakteres keresés valamiért nem működne (Android):
     * a "Support" felirat előtti testvér elemek. [1] = Android logó, [2] = Apple logó.
     *
     *   AppiumBy.xpath("//*[@content-desc='Home-screen']//android.widget.TextView[@text='Support']"
     *                + "/preceding-sibling::android.widget.TextView[2]")
     */

    /**
     * Szöveg alapú cél mindkét platformra.
     * Android: UiSelector text, iOS: predicate a label-re.
     * Az iOS lokátor nincs Macen ellenőrizve: az iOS buildet XCUITest driverrel, macOS-en kell kipróbálni.
     */
    private static Target byText(String description, String text) {
        return Target.the(description)
                .locatedForAndroid(AppiumBy.androidUIAutomator("new UiSelector().text(\"" + text + "\")"))
                .locatedForIOS(AppiumBy.iOSNsPredicateString("label == '" + text + "'"));
    }
}
