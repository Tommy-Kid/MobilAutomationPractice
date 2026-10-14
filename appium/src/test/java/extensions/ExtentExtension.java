package extensions;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.junit.jupiter.api.extension.*;
import org.mobil.config.DriverManager;
import org.mobil.report.ExtentManager;
import org.mobil.utils.ScreenshotUtil;

import java.util.Optional;

/**
 * Minden tesztet beír az Extent riportba (PASS / FAIL / SKIP).
 * Screenshot csak hibánál készül, és még az @AfterEach (driver.quit) ELŐTT,
 * mert a TestWatcher.testFailed már csak a driver bezárása után futna le.
 */
public class ExtentExtension implements BeforeEachCallback, TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler, TestWatcher {

    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(ExtentExtension.class);
    private static final String SCREENSHOT = "screenshot";

    @Override
    public void beforeEach(ExtensionContext ctx) {
        ExtentManager.startTest(ctx.getDisplayName(),
                ctx.getRequiredTestClass().getSimpleName(),
                ctx.getTags().toArray(String[]::new));
    }

    /** A tesztmetódus dobott hibát: a driver még él, most fotózunk. */
    @Override
    public void handleTestExecutionException(ExtensionContext ctx, Throwable t) throws Throwable {
        takeScreenshot(ctx);
        throw t;   // továbbdobjuk, hogy a teszt tényleg elbukjon
    }

    /** A @BeforeEach-ben (pl. navigáció közben) dobott hiba esetén is legyen kép. */
    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext ctx, Throwable t) throws Throwable {
        takeScreenshot(ctx);
        throw t;
    }

    @Override
    public void testSuccessful(ExtensionContext ctx) {
        test(ctx).pass("Sikeres");
        finish();
    }

    @Override
    public void testFailed(ExtensionContext ctx, Throwable cause) {
        ExtentTest test = test(ctx);
        String base64 = ctx.getStore(NS).get(SCREENSHOT, String.class);
        if (base64 != null && !base64.isEmpty()) {
            test.fail(cause, MediaEntityBuilder.createScreenCaptureFromBase64String(base64, "Hiba pillanata").build());
        } else {
            test.fail(cause);
        }
        finish();
    }

    @Override
    public void testAborted(ExtensionContext ctx, Throwable cause) {
        test(ctx).skip(cause);
        finish();
    }

    @Override
    public void testDisabled(ExtensionContext ctx, Optional<String> reason) {
        ExtentManager.startTest(ctx.getDisplayName(), ctx.getRequiredTestClass().getSimpleName())
                .skip("Letiltva: " + reason.orElse("-"));
        finish();
    }

    /**
     * Soha nem dobhat kivételt: ha pl. a driver létrehozása bukott el (nincs driver),
     * akkor egyszerűen nincs kép - az EREDETI hiba marad a riportban, nem egy screenshot-hiba.
     */
    private void takeScreenshot(ExtensionContext ctx) {
        try {
            DriverManager.getDriver();   // van egyáltalán driver?
        } catch (Exception noDriver) {
            return;
        }
        try {
            ctx.getStore(NS).put(SCREENSHOT, ScreenshotUtil.captureScreenshotAsBase64());
            ScreenshotUtil.captureScreenshot(ctx.getRequiredTestMethod().getName()); // fájlba is, target/screenshots
        } catch (Exception ignored) {
            // screenshot hiba ne takarja el a teszt valódi hibáját
        }
    }

    /** Ha a beforeEach nem futott le (ritka), akkor is legyen bejegyzés. */
    private ExtentTest test(ExtensionContext ctx) {
        ExtentTest t = ExtentManager.test();
        return t != null ? t : ExtentManager.startTest(ctx.getDisplayName(), ctx.getRequiredTestClass().getSimpleName());
    }

    private void finish() {
        ExtentManager.endTest();
        ExtentManager.flush();
    }
}
