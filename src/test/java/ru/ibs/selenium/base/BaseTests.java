package ru.ibs.selenium.base;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class BaseTests {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeClass()
    public static void beforClass() {
        System.out.println("@BeforeClass -> beforeAll()\n");
    }

    @Before()
    public void before() {
        System.setProperty("webdriver.chrome.driver", "src/test/recources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 10, 1000);

        String baseUrl = "https://www.rgs.ru";
        driver.get(baseUrl);
    }

    @After()
    public void after() {
        driver.quit();
    }

    @AfterClass()
    public static void afterClass() {
        System.out.println("@AfterClass -> afterAll()\n");
    }
}
