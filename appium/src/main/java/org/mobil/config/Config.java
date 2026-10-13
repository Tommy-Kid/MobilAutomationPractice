package org.mobil.config;

import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Config.*;

@Sources({"classpath:config.properties"})
public interface Config extends org.aeonbits.owner.Config {

    Config CONFIG = ConfigFactory.create(Config.class);

    // ---------- Appium szerver ----------
    @Key("server.host")
    String serverHost();

    @Key("server.port")
    int serverPort();

    // ---------- Alap capabilityk ----------
    @Key("appium.platformName")
    String platformName();

    @Key("appium.platform.version")
    String platformVersion();

    @Key("appium.automationName")
    String automationName();

    @Key("appium.app")
    String app();

    @Key("appium.noReset")
    boolean noReset();

    @Key("appium.newCommandTimeout")
    int newCommandTimeout();

    @Key("appium.autoGrantPermissions")
    boolean autoGrantPermissions();

    // ---------- Eszközök ----------
    @Key("device.pool")
    String devicePool();

    @Key("device.name.%s")
    String deviceName(String device);

    @Key("device.%s.udid")
    String deviceUdid(String device);

    @Key("device.%s.systemPort")
    int deviceSystemPort(String device);

    // ---------- Keretrendszer beállítások ----------
    @Key("timeout.explicit.seconds")
    int explicitTimeoutSeconds();

    @Key("screenshot.onFailure")
    boolean screenshotOnFailure();
}
