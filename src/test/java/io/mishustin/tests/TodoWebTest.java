package io.mishustin.tests;

import com.codeborne.selenide.Configuration;
import io.mishustin.krypton.ScreenshotListener;
import io.mishustin.krypton.TestConfiguration;
import io.mishustin.krypton.TestNgListener;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@Listeners({ScreenshotListener.class, TestNgListener.class})
public class TodoWebTest {

    @BeforeClass
    public void setHeadLess() {
        Configuration.headless = false;
    }

    @Test
    public void userShouldOpenPage() {
        open(TestConfiguration.webHost);
    }

    @Test
    public void userShouldCreateTasks() {
        $(By.className("new-todo")).setValue("sleep").pressEnter();
        $(By.className("new-todo")).setValue("eat").pressEnter();
        $(By.className("new-todo")).setValue("have fun").pressEnter();
    }

    @Test
    public void userShouldClickOnTask() {
        $(By.xpath("//div[//label[text()=\"have fun\"]]/input1")).click();
    }

}