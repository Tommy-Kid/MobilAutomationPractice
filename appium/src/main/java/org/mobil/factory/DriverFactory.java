package org.mobil.factory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobil.config.Config;
import org.mobil.config.DevicePool;
import org.mobil.utils.AppiumServerManager;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

import static org.mobil.constants.FrameworkConstants.*;

public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {}

    public static AppiumDriver createDriver() {
        DevicePool.acquire();
        try {
            return createAndroidDriver();
        } catch (RuntimeException e) {
            // Sikertelen driverlétrehozás esetén az eszköz nem marad "foglaltként" örökre a poolban.
            DevicePool.release();
            throw e;
        }
    }

    private static AndroidDriver createAndroidDriver() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName(Config.CONFIG.platformName());
        options.setPlatformVersion(PLATFORM_VERSION);
        options.setAutomationName(Config.CONFIG.automationName());
        String deviceName = deviceName();
        options.setDeviceName(deviceName);
        options.setUdid(deviceUdid());
        options.setSystemPort(deviceSystemPort());
        options.setApp(resolveAppPath(Config.CONFIG.app()));
        options.setNoReset(Config.CONFIG.noReset());
        options.setAutoGrantPermissions(Config.CONFIG.autoGrantPermissions());
        options.setNewCommandTimeout(Duration.ofSeconds(Config.CONFIG.newCommandTimeout()));
        // Friss, most bootolt emulatoron (pl. CI-ban) az UiAutomator2 szerver elso telepitese/inditasa
        // lassabb lehet, mint az Appium 30s-os alapertelmezett timeout-ja - ezert megnoveljuk.
        options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(90));
        options.setUiautomator2ServerInstallTimeout(Duration.ofSeconds(90));

        log.info("Creating AndroidDriver for device: {} (thread: {})", deviceName, Thread.currentThread().getName());
        AndroidDriver driver = new AndroidDriver(AppiumServerManager.url(), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
        return driver;
    }

    /**
     * A classpath-relatív apk útvonalat (pl. "app/android.wdio.native.app.v2.2.0.apk") abszolút
     * fájlrendszerbeli útra oldja fel a classloaderen keresztül, így a tényleges elérési út
     * független attól, honnan (reactor gyökér vagy modul) indult a mvn/JVM.
     */
    private static String resolveAppPath(String classpathLocation) {
        URL resource = DriverFactory.class.getClassLoader().getResource(classpathLocation);
        if (resource == null) {
            throw new IllegalStateException("Nem található az apk a classpath-on: " + classpathLocation);
        }
        try {
            return Paths.get(resource.toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Érvénytelen apk resource URI: " + resource, e);
        }
    }
}
