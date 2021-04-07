package io.mishustin.tests.cucumber;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import io.mishustin.krypton.listeners.TestNgListener;
import org.testng.ITest;
import org.testng.annotations.*;

import java.lang.reflect.Method;

@CucumberOptions(
        plugin = {"junit:target/cucumber.xml", "pretty"},
        features = "src/test/java/io/mishustin/tests/cucumber",
        glue = {"io.mishustin.tests.cucumber"}
)
@Listeners(TestNgListener.class)
public class CucumberTestRunner implements ITest {

    private final ThreadLocal<String> testName = new ThreadLocal<>();
    private final TestNGCucumberRunner testNGCucumberRunner;

    public CucumberTestRunner() {
        this.testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @Test(
            groups = {"cucumber"},
            description = "Runs Cucumber Scenarios",
            dataProvider = "scenarios"
    )
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        this.testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    @DataProvider
    public Object[][] scenarios() {
        return this.testNGCucumberRunner == null ? new Object[0][0] : this.testNGCucumberRunner.provideScenarios();
    }

    @AfterClass(
            alwaysRun = true
    )
    public void tearDownClass() {
        if (this.testNGCucumberRunner != null) {
            this.testNGCucumberRunner.finish();
        }
    }

    @BeforeMethod
    public void beforeMethod(Method method, Object[] testData) {
        testName.set(testData[0].toString());
    }

    @Override
    public String getTestName() {
        return testName.get();
    }
}
