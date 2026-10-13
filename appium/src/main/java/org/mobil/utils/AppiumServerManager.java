package org.mobil.utils;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobil.config.Config;

import java.io.File;
import java.net.*;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mobil.constants.FrameworkConstants.HOST;
import static org.mobil.constants.FrameworkConstants.PORT;

/**
 * Egyetlen Appium szervert tart életben a teljes futás alatt, akkor is, ha több teszt osztály
 * (párhuzamosan, több szálon) indítja/állítja el egymástól függetlenül a saját @BeforeAll/@AfterAll-jában.
 * Referenciaszámlálóval: a szerver csak akkor áll le valóban, amikor az utolsó használó is jelezte, hogy végzett.
 */
public class AppiumServerManager {

    private static final Logger log = LogManager.getLogger(AppiumServerManager.class);
    private static AppiumDriverLocalService service;
    private static final AtomicInteger users = new AtomicInteger(0);

    private AppiumServerManager() {}

    public static synchronized void startServer() {
        users.incrementAndGet();

        if (service != null && service.isRunning()) {
            log.info("Appium server is already running at: {}", service.getUrl());
            return;
        }

        if (isHealthyAppiumServer()) {
            log.info("Healthy Appium server already running on {}:{}, reusing it", HOST, PORT);
            return;
        }

        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress(HOST)
                .usingPort(PORT)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "info")
                .withLogFile(new File("target/appium-server.log"))
                .withTimeout(Duration.ofSeconds(60));

        service = AppiumDriverLocalService.buildService(builder);
        service.start();
        log.info("Appium server started at {}", service.getUrl());

    }

    public static synchronized void stop(){
        if (users.decrementAndGet() > 0) {
            log.info("Appium server still used by other test classes, not stopping yet");
            return;
        }

        if (service != null) {
            service.stop();
            log.info("Appium server stopped");
            service = null;
        }
    }

    public static URL url() {
        if (service != null) {
            return service.getUrl();
        }
        try {
            return new URI("http", null, HOST, PORT, "/", null, null).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException("Invalid Appium server URL", e);
        }
    }

    /**
     * Nem elég, ha valami hallgatózik a porton (az lehet egy korábbi futásból visszamaradt,
     * elfajult Appium-folyamat is) - ténylegesen lekérdezi a /status végpontot, mielőtt
     * újrahasznosítaná. Ha nem egészséges, inkább friss szervert indítunk (vagy az indítás
     * azonnal, egyértelműen elhasal, nem pedig egy később, zavaróan a session-létrehozásnál).
     */
    private static boolean isHealthyAppiumServer() {
        try {
            URL statusUrl = new URI("http", null, HOST, PORT, "/status", null, null).toURL();
            HttpURLConnection conn = (HttpURLConnection) statusUrl.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
