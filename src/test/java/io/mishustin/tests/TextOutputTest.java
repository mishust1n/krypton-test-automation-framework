package io.mishustin.tests;

import io.mishustin.krypton.TestNgListener;
import io.mishustin.krypton.TestReporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestNgListener.class)
public class TextOutputTest {


    @Test
    public void shouldPrintCurrentEpoch() {
        TestReporter.getReporter().log("" + System.currentTimeMillis());
    }

    @Test
    public void shouldPrintHelloWorld() {
        TestReporter.getReporter().log("" + System.currentTimeMillis());
    }

}
