package org.mobil.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.mobil.constants.FrameworkConstants;

/**
 * Egyetlen közös Extent riport az egész futásra (target/reports/ExtentReport.html).
 * Az aktuális szál tesztjét ThreadLocal-ban tartja, így párhuzamos futásnál sem keverednek a lépések.
 */
public final class ExtentManager {

    private static final ExtentReports EXTENT = create();
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentManager() {}

    private static ExtentReports create() {
        String path = FrameworkConstants.REPORTS_DIR.resolve("ExtentReport.html").toString();
        ExtentSparkReporter spark = new ExtentSparkReporter(path);
        spark.config().setDocumentTitle("Appium tesztriport");
        spark.config().setReportName("WebdriverIO Native Demo App");
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setEncoding("UTF-8");

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Device", System.getProperty("device", "default"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java", System.getProperty("java.version"));
        return extent;
    }

    public static synchronized ExtentTest startTest(String name, String className, String... tags) {
        ExtentTest test = EXTENT.createTest(name).assignCategory(className);
        for (String tag : tags) test.assignCategory(tag);
        CURRENT.set(test);
        return test;
    }

    /** Teszten belül lépés logolására: ExtentManager.test().info("Rákattintottam a Live mode-ra"); */
    public static ExtentTest test() {
        return CURRENT.get();
    }

    public static void endTest() {
        CURRENT.remove();
    }

    /** Minden teszt után kiírjuk, így a riport akkor is meglesz, ha a futás félbeszakad. */
    public static synchronized void flush() {
        EXTENT.flush();
    }
}
