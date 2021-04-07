package io.mishustin.krypton;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import io.mishustin.krypton.exception.ToolException;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.aventstack.extentreports.AnalysisStrategy.CLASS;

public class TestReporter {

    private static String reportPath;
    private static final Logger LOG = LogManager.getLogger(TestReporter.class);
    private static final ThreadLocal<TestReporter> reporters = ThreadLocal.withInitial(() -> {
        LOG.debug("GET new test reporter instance from thread local");
        return new TestReporter();
    });

    private static ExtentReports extent;
    private ExtentTest currentTest;

    private static Map<String, ExtentTest> testNodes;

    private TestReporter() {
        LOG.info("Create test reporter instance");
    }

    public static synchronized TestReporter getReporter() {
        LOG.info("Get test reporter instance");
        return reporters.get();
    }

    public synchronized void createTestNode(String className) {
        LOG.info("Create test node: " + className);

        if (!testNodes.containsKey(className)) {
            testNodes.put(className, extent.createTest(className));
        }
    }

    public synchronized void flush() {
        extent.flush();
    }

    public synchronized void logPassBddResult(String msg) throws ClassNotFoundException {
        currentTest.createNode(new GherkinKeyword("given"), msg).pass("pass");
    }

    public synchronized void logFailBddResult(String msg) throws ClassNotFoundException {
        currentTest.createNode(new GherkinKeyword("given"), msg).fail("pass");
    }

    public static synchronized String getReportPath() {
        return reportPath;
    }

    public static synchronized void createReportFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String reportName = "report-" + LocalDateTime.now().format(formatter) + ".html";

        LOG.info("Create test report file " + reportName);

        File reportsFolder = new File("reports");
        if (!reportsFolder.exists()) {
            if (!new File("reports").mkdir()) {
                throw new ToolException("Unable to create \"reports\" folder");
            }
        }

        ExtentSparkReporter spark = new ExtentSparkReporter(Paths.get("reports", reportName).toFile())
                .viewConfigurer()
                .viewOrder()
                .as(new ViewName[]{ViewName.TEST, ViewName.DASHBOARD, ViewName.EXCEPTION}).apply();

        reportPath = Paths.get("reports", reportName).toAbsolutePath().toString();

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setAnalysisStrategy(CLASS);

        testNodes = new HashMap<>();
    }

    public synchronized void createTest(String testCase, String testName) {
        LOG.debug("Create test: " + testCase + " - " + testName);
        currentTest = testNodes.get(testCase).createNode(testName);
    }

    public synchronized void pass(String testName) {
        LOG.debug("Log passed test: " + testName);
        currentTest.pass("PASSED: " + testName);
    }

    public synchronized void xfail(String testName, String reason) {
        currentTest.assignCategory("XFAIL");

        skip(testName, reason);
    }

    public synchronized void skip(String testName, String reason) {
        currentTest.skip("SKIPPED: " + testName);
        currentTest.skip(reason);
    }

    public synchronized void fail(String testName, String reason) {
        if (currentTest != null) {
            currentTest.fail("FAILED: " + testName);
            currentTest.fail(reason);
        }
    }

    public synchronized void fail(String testName, Throwable throwable) {
        currentTest.fail("FAILED: " + testName);
        currentTest.fail(throwable);
    }

    public synchronized void logImage(String base64Image) {
        String message = "<img src=\"data:image/png;base64, " + base64Image + "\" width=\"100%\" />";
        currentTest.log(Status.INFO, message);
    }

    public synchronized void cleanReporters() {
        reporters.remove();
    }

    public synchronized void log(String message) {
        currentTest.log(Status.INFO, message);
    }
}
