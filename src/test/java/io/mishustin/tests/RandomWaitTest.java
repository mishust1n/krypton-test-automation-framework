package io.mishustin.tests;

import io.mishustin.krypton.TestNgListener;
import io.mishustin.krypton.TestReporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Random;

@Listeners(TestNgListener.class)
public class RandomWaitTest {

    @Test(dataProvider = "random")
    public void dataDrivenHalfWait(int time) throws InterruptedException {
        time = time / 2;
        TestReporter.getReporter().log("Wait " + time);
        Thread.sleep(time);
    }

    @Test(dataProvider = "random")
    public void dataDriven5eWait(int time) throws InterruptedException {
        time = time * 5;
        TestReporter.getReporter().log("Wait " + time);
        Thread.sleep(time);
    }

    @Test(dataProvider = "random")
    public void dataDriven4eWait(int time) throws InterruptedException {
        time = time * 4;
        TestReporter.getReporter().log("Wait " + time);
        Thread.sleep(time);
    }

    @Test(dataProvider = "random")
    public void dataDrivenTripleWait(int time) throws InterruptedException {
        time = time * 3;
        TestReporter.getReporter().log("Wait " + time);
        Thread.sleep(time);
    }

    @Test(dataProvider = "random")
    public void dataDrivenDoubleWait(int time) throws InterruptedException {
        time = time * 2;
        TestReporter.getReporter().log("Wait " + time);
        Thread.sleep(time);
    }

    @Test(dataProvider = "random")
    public void dataDrivenWait(int time) throws InterruptedException {
        TestReporter.getReporter().log("Wait " + time);
        Thread.sleep(time);
    }

    @DataProvider(name = "random", parallel = true)
    public Object[][] randomDataProvider() {
        Random r = new Random();
        int size = 250;
        Object[][] data = new Object[size][1];

        for (int i = 0; i < size; i++) {
            data[i][0] = r.nextInt(1000);
        }

        return data;
    }


}
