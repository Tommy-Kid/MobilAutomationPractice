package org.mobil.factory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobil.config.Config;
import org.mobil.utils.AppiumServerManager;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

import static org.mobil.constants.FrameworkConstants.IMPLICIT_WAIT;

/**
 * A driver létrehozása az Appium device-farm plugin eszközfoglalására épül: itt nem választunk
 * eszközt, csak platformot (és opcionálisan -Dudid-et) kérünk, a farm kiad egy szabadot, a
 * driver.quit() pedig felszabadítja. Párhuzamos futásnál a farm gondoskodik róla, hogy ne
 * kapjon két session ugyanazt az eszközt (és a portokat - systemPort, wdaLocalPort - is ő osztja).
 */
public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {}

    public static AppiumDriver createDriver() {
        String platform = System.getProperty("platform", Config.CONFIG.platformName());
        String udid = System.getProperty("udid");
        // -Dplatform=any: szálanként kiegyensúlyozva osztja az Android / iOS között (lásd PlatformAllocator)
        boolean balanced = platform.equalsIgnoreCase("any");
        if (balanced) {
            platform = PlatformAllocator.acquire();
        }
        AppiumDriver driver;
        try {
            driver = switch (platform.toLowerCase()) {
                case "android" -> createAndroidDriver(udid);
                case "ios" -> createIosDriver(udid);
                default -> throw new IllegalArgumentException("Nem támogatott platform: " + platform);
            };
        } catch (RuntimeException e) {
            if (balanced) {
                PlatformAllocator.release();
            }
            throw e;
        }
        log.info("Farm allocated device: udid={}, name={}, platformVersion={} (platform: {}, thread: {})",
                capability(driver, "udid"), capability(driver, "deviceName"),
                capability(driver, "platformVersion"), platform, Thread.currentThread().getName());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
        return driver;
    }

    /** A szerver válaszában a capability "appium:" előtaggal vagy anélkül is érkezhet. */
    private static Object capability(AppiumDriver driver, String name) {
        Object value = driver.getCapabilities().getCapability("appium:" + name);
        return value != null ? value : driver.getCapabilities().getCapability(name);
    }

    private static AndroidDriver createAndroidDriver(String udid) {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setAutomationName(Config.CONFIG.automationName());
        options.setApp(resolveAppPath(Config.CONFIG.app()));
        options.setNoReset(Config.CONFIG.noReset());
        options.setAutoGrantPermissions(Config.CONFIG.autoGrantPermissions());
        options.setNewCommandTimeout(Duration.ofSeconds(Config.CONFIG.newCommandTimeout()));
        // Friss, most bootolt emulatoron az UiAutomator2 szerver első telepítése/indítása lassabb lehet.
        options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(90));
        options.setUiautomator2ServerInstallTimeout(Duration.ofSeconds(90));
        if (udid != null) {
            options.setUdid(udid);
        }

        log.info("Requesting an Android device from the farm (udid: {}, thread: {})",
                udid == null ? "any" : udid, Thread.currentThread().getName());
        return new AndroidDriver(AppiumServerManager.url(), options);
    }

    private static IOSDriver createIosDriver(String udid) {
        XCUITestOptions options = new XCUITestOptions();
        options.setNoReset(Config.CONFIG.noReset());
        options.setNewCommandTimeout(Duration.ofSeconds(Config.CONFIG.newCommandTimeout()));
        if (!Config.CONFIG.iosApp().isBlank()) {
            options.setApp(resolveAppPath(Config.CONFIG.iosApp()));
        }
        if (udid != null) {
            options.setUdid(udid);
        }

        log.info("Requesting an iOS device from the farm (udid: {}, thread: {})",
                udid == null ? "any" : udid, Thread.currentThread().getName());
        return new IOSDriver(AppiumServerManager.url(), options);
    }

    /**
     * A classpath-relatív apk útvonalat (pl. "app/android.wdio.native.app.v2.2.0.apk") abszolút
     * fájlrendszerbeli útra oldja fel a classloaderen keresztül, így a tényleges elérési út
     * független attól, honnan (reactor gyökér vagy modul) indult a mvn/JVM.
     */
    private static String resolveAppPath(String classpathLocation) {
        URL resource = DriverFactory.class.getClassLoader().getResource(classpathLocation);
        if (resource == null) {
            throw new IllegalStateException("Nem található az app a classpath-on: " + classpathLocation);
        }
        try {
            return Paths.get(resource.toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Érvénytelen app resource URI: " + resource, e);
        }
    }
}
