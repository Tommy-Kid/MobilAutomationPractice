package org.mobil.utils;



import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobil.config.DriverManager;
import org.mobil.constants.FrameworkConstants;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Utility for capturing and managing screenshots.
 */
public final class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {}

    /**
     * Captures screenshot and saves to screenshots directory.
     * @return absolute file path of the saved screenshot
     */
    public static String captureScreenshot(String testName) {

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String fileName = testName + "_" + timestamp + ".png";

        try {
            Files.createDirectories(FrameworkConstants.SCREENSHOTS_DIR);
            String filePath = FrameworkConstants.SCREENSHOTS_DIR + File.separator + fileName;
            File srcFile = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            File destFile = new File(filePath);
            FileUtils.copyFile(srcFile, destFile);
            log.info("Screenshot captured: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Captures screenshot as Base64 string (for Extent Reports embedding).
     */
    public static String captureScreenshotAsBase64() {
        try {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture base64 screenshot: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Captures screenshot as byte array (for Allure attachment).
     */
    public static byte[] captureScreenshotAsBytes() {
        try {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to capture screenshot bytes: {}", e.getMessage());
            return new byte[0];
        }
    }
}
