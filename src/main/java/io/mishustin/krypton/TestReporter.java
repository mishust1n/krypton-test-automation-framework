package io.mishustin.krypton;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

public class TestReporter {

    private static final Logger LOG = LogManager.getLogger(TestReporter.class);
    private static ThreadLocal<TestReporter> reporters = ThreadLocal.withInitial(new Supplier<TestReporter>() {
        @Override
        public TestReporter get() {
            LOG.debug("GET new test reporter instance from thread local");
            return new TestReporter();
        }
    });

    private static ExtentReports extent;
    private ExtentTest currentTest;

    private static Map<String, ExtentTest> testNodes;

    public static TestReporter getReporter() {
        LOG.info("Get test reporter instance");
        return reporters.get();
    }

    private TestReporter() {
        LOG.info("Create test reporter instance");
    }

    public void createTestNode(String className) {
        LOG.info("Create test node: " + className);

        if (!testNodes.containsKey(className)) {
            testNodes.put(className, extent.createTest(className));
        }
    }

    public static void createReportFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String fileName = "raport-" + LocalDateTime.now().format(formatter) + ".html";

        LOG.info("Create test report file " + fileName);

        new File("reports").mkdir();

        ExtentSparkReporter spark = new ExtentSparkReporter(Paths.get("reports", fileName).toFile())
                .viewConfigurer()
                .viewOrder()
                .as(new ViewName[]{ViewName.TEST, ViewName.DASHBOARD, ViewName.EXCEPTION}).apply();

        extent = new ExtentReports();
        extent.attachReporter(spark);
        testNodes = new HashMap<>();
    }

    public void createTest(String testCase, String testName) {
        LOG.debug("Create test: " + testCase + " - " + testName);


        currentTest = testNodes.get(testCase).createNode(testName);
        extent.flush();
    }

    public void pass(String testName) {
        LOG.debug("Log passed test: " + testName);
        currentTest.pass("PASSED: " + testName);
        extent.flush();
    }

    public void xfail(String testName, String reason) {
        currentTest.assignCategory("XFAIL");
        skip(testName, reason);
    }

    public void skip(String testName, String reason) {
        currentTest.skip("SKIPPED: " + testName);
        currentTest.skip(reason);
        extent.flush();
    }

    public void fail(String testName, String reason) {
        if (currentTest != null) {
            currentTest.fail("FAILED: " + testName);
            currentTest.fail(reason);
            extent.flush();
        }
    }

    public void fail(String testName, Throwable throwable) {
        currentTest.fail("FAILED: " + testName);
        currentTest.fail(throwable.toString());
        extent.flush();
    }

    public void logImage(String base64Image) {
        String message = "<img src=\"data:image/png;base64, " + base64Image + "\" width=\"100%\" />";
        currentTest.log(Status.INFO, message);
    }

    public void log(String message) {
        System.out.println("Current test not equals null: " + currentTest != null);
        System.out.println(message);
        try {
            currentTest.log(Status.INFO, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        extent.flush();
    }
}
