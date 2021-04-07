package io.mishustin.krypton.listeners;


import io.mishustin.krypton.TestInitialization;
import io.mishustin.krypton.TestReporter;
import io.mishustin.krypton.annotation.Xfail;
import io.mishustin.krypton.exception.ToolException;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.util.List;

public class TestNgListener implements ITestListener, IExecutionListener, IReporter, IClassListener, IMethodInterceptor {

    private static final Logger LOG = LogManager.getLogger(TestNgListener.class);

    @Override
    public void onTestStart(ITestResult iTestResult) {
        LOG.info("STARTED: {}", iTestResult.getMethod().getMethodName());
        String testCase = iTestResult.getTestClass().getName();
        String testName = iTestResult.getName();
        TestReporter.getReporter().createTest(testCase, testName);
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        LOG.info("PASSED: {}", iTestResult.getName());
        String testName = iTestResult.getName();
        TestReporter.getReporter().pass(testName);
    }

    private boolean isXfail(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getMethod().getDeclaredAnnotation(Xfail.class) != null;
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        String testName = iTestResult.getName();

        if (isXfail(iTestResult)) {
            LOG.info("XFAIL: {}", testName);
            TestReporter.getReporter().xfail(testName, iTestResult.getThrowable().getMessage());
            iTestResult.setStatus(ITestResult.SKIP);
            iTestResult.setThrowable(null);
            iTestResult.getTestContext().getFailedTests().removeResult(iTestResult);
            iTestResult.getTestContext().getSkippedTests().addResult(iTestResult, iTestResult.getMethod());
        } else {
            LOG.info("FAILED: {}", testName);
            TestReporter.getReporter().fail(testName, iTestResult.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        LOG.info("SKIPPED: {}", iTestResult.getMethod().getMethodName());
        String testName = iTestResult.getName();
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
        TestReporter.getReporter().cleanReporters();
        LOG.info("Test report: " + TestReporter.getReportPath());
    }

    @Override
    public void generateReport(List<XmlSuite> suits, List<ISuite> list1, String s) {
        LOG.info("Generate report time");
        TestReporter.getReporter().flush();
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> list, ITestContext iTestContext) {

        ClassPool pool = ClassPool.getDefault();


        list.sort(((obj1, obj2) -> {
            try {

                String obj1Class = obj1.getMethod().getTestClass().getName();
                String obj1Method = obj1.getMethod().getMethodName();

                String obj2Class = obj2.getMethod().getTestClass().getName();
                String obj2Method = obj2.getMethod().getMethodName();

                System.out.println(obj1Class);
                System.out.println(obj1Method);

                System.out.println(obj2Class);
                System.out.println(obj2Method);

                int obj1Position = pool.getMethod(obj1Class, obj1Method).getMethodInfo().getLineNumber(0);
                int obj2Position = pool.getMethod(obj2Class, obj2Method).getMethodInfo().getLineNumber(0);

                return obj1Position - obj2Position;
            } catch (NotFoundException e) {
                throw new ToolException(e);
            }
        }));
        return list;
    }
}