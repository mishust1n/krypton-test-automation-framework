package io.mishustin.tests.dummy;

import io.mishustin.krypton.listeners.TestNgListener;
import io.mishustin.krypton.TestReporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Random;

@Listeners(TestNgListener.class)
public class RandomWaitTest {

    @Test(dataProvider = "random")
    public void dataDrivenWaitTest(int time) throws InterruptedException {
        TestReporter.getReporter().log("Wait " + time);
        Thread.sleep(time);
    }

    @DataProvider(name = "random", parallel = true)
    public Object[][] randomDataProvider() {
        Random r = new Random();
        int size = 400;
        Object[][] data = new Object[size][1];

        for (int i = 0; i < size; i++) {
            data[i][0] = r.nextInt(1000) * r.nextInt(4);
        }

        return data;
    }
}
