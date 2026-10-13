package org.mobil.constants;

import org.mobil.config.Config;
import org.mobil.config.DevicePool;

import java.nio.file.Path;

public class FrameworkConstants {

    public static final String HOST = Config.CONFIG.serverHost();
    public static final int PORT = Config.CONFIG.serverPort();
    public static final String PLATFORM_VERSION = Config.CONFIG.platformVersion();

    public static final Path TARGET = Path.of("target");
    public static final Path SCREENSHOTS_DIR = TARGET.resolve("screenshots");

    public static final int EXPLICIT_WAIT = Config.CONFIG.explicitTimeoutSeconds();
    public static final int IMPLICIT_WAIT = 10;
    public static final int PAGE_LOAD_TIMEOUT = 60;

    private FrameworkConstants() {}

    /** Az aktuális szálhoz kiosztott eszköz neve. Nem static final, mert szálanként eltérhet. */
    public static String deviceName() {
        return Config.CONFIG.deviceName(DevicePool.current());
    }

    public static String deviceUdid() {
        return Config.CONFIG.deviceUdid(DevicePool.current());
    }

    public static int deviceSystemPort() {
        return Config.CONFIG.deviceSystemPort(DevicePool.current());
    }
}
