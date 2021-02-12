package io.mishustin.krypton;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class TestNgListener implements ITestListener, IExecutionListener, IReporter, IClassListener {

    private static final Logger LOG = LogManager.getLogger(TestNgListener.class);

    @Override
    public void onTestStart(ITestResult iTestResult) {
        LOG.info("STARTED: {}", iTestResult.getMethod().getMethodName());
        String testCase = iTestResult.getTestClass().getName();
        String testName = iTestResult.getMethod().getMethodName();
        TestReporter.getReporter().createTest(testCase, testName);
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        LOG.info("PASSED: {}", iTestResult.getMethod().getMethodName());
        String testName = iTestResult.getMethod().getMethodName();
        TestReporter.getReporter().pass(testName);
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        LOG.info("FAILED: {}", iTestResult.getMethod().getMethodName());
        iTestResult.getThrowable().printStackTrace();
        LOG.error(iTestResult.getThrowable());
        String testName = iTestResult.getMethod().getMethodName();
        TestReporter.getReporter().fail(testName, iTestResult.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        LOG.info("SKIPPED: {}", iTestResult.getMethod().getMethodName());
        String testName = iTestResult.getMethod().getMethodName();
        TestReporter.getReporter().skip(testName, iTestResult.getThrowable().getMessage());
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
        TestReporter.getReporter().createTestNode(testClass.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {
        LOG.info("Test execution started");
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        LOG.info("Test execution finished");
    }

    @Override
    public void onExecutionStart() {
        LOG.info("On test execution started");
        try {
            TestInitialization.init();
        } catch (IOException e) {
            LOG.error("Error during test initialization", e);
        }
    }

    @Override
    public void onExecutionFinish() {

    }

    @Override
    public void generateReport(List<XmlSuite> suits, List<ISuite> list1, String s) {
        LOG.info("Generate report time");
        TestReporter.getReporter().flush();
        for (ISuite iSuite : list1) {
            Collection<ISuiteResult> values = iSuite.getResults().values();

            for (ISuiteResult value : values) {
                value.getTestContext().getSuite().getResults();
            }
        }

    }
}
