package screenplay.interactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Görgetés W3C touch gesztussal. Ugyanúgy működik Androidon és iOS-en,
 * mert nem platformfüggő "mobile:" parancsot használ.
 *
 * Scroll.down(): a tartalom lefelé halad (az ujj alulról felfelé húz).
 * Scroll.up():   vissza a tetejére (az ujj felülről lefelé húz).
 */
public class Scroll implements Interaction {

    public enum Direction { UP, DOWN }

    private static final double TOP = 0.25;
    private static final double BOTTOM = 0.70;
    private static final Duration SWIPE_DURATION = Duration.ofMillis(600);

    private final Direction direction;

    public Scroll(Direction direction) {
        this.direction = direction;
    }

    public static Scroll down() {
        return instrumented(Scroll.class, Direction.DOWN);
    }

    public static Scroll up() {
        return instrumented(Scroll.class, Direction.UP);
    }

    @Override
    @Step("{0} görget: #direction")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        Dimension screen = driver.manage().window().getSize();

        int x = screen.getWidth() / 2;
        int top = (int) (screen.getHeight() * TOP);
        int bottom = (int) (screen.getHeight() * BOTTOM);
        int startY = direction == Direction.DOWN ? bottom : top;
        int endY = direction == Direction.DOWN ? top : bottom;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(SWIPE_DURATION, PointerInput.Origin.viewport(), x, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((Interactive) driver).perform(List.of(swipe));
    }
}
