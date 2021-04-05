package io.mishustin.krypton;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.OutputType;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        String img = Selenide.screenshot(OutputType.BASE64);
        if (img != null && !img.isEmpty()) {
            TestReporter.getReporter().logImage(img);
        }
    }
}
