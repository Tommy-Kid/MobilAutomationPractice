package org.mobil.config;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriverManager {

    /**Thread-local driver manager for parallel execution support. */
    private static final ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();
    private static final Logger log = LogManager.getLogger(DriverManager.class);

    public static void setDriver(AppiumDriver driver) {
        driverThreadLocal.set(driver);
        log.debug("Driver set for thread: {}", Thread.currentThread().getName());
    }

    public static AppiumDriver getDriver(){
        AppiumDriver driver = driverThreadLocal.get();
        if( driver == null){ throw new IllegalStateException("Driver not initialized for thread: " + Thread.currentThread().getName());}
        return  driver;
    }

    public static void removeDriver(){
        AppiumDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver quit successfully for thread: {}", Thread.currentThread().getName());
            } catch (Exception e) {
                log.warn("Error while quitting driver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
                DevicePool.release();
            }
        }
    }
}
