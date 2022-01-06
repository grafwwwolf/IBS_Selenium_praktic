package ru.ibs.selenium;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class TestSelenium {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
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

    @Test
    public void test() {

        String forCompaniesXPATH = "//a[@class=\"text--second\" and contains(text(), \"Компаниям\")]";
        WebElement forCompaniesButton = driver.findElement(By.xpath(forCompaniesXPATH));
        waitUtilElementToBeClickable(forCompaniesButton);
        forCompaniesButton.click();

        wait.until(ExpectedConditions.attributeContains(forCompaniesButton, "Class", "nuxt-link-exact-active"));

        String healthXPATH = "//span[@class=\"padding\" and contains(text(), \"Здоровье\")]";
        WebElement healthButton = driver.findElement(By.xpath(healthXPATH));
        waitUtilElementToBeClickable(healthButton);
        healthButton.click();

        WebElement parentHealth = healthButton.findElement(By.xpath("./.."));
        wait.until(ExpectedConditions.attributeContains(parentHealth, "Class", "active"));

        String DmsXPath = "//a[contains(text(), \"Добровольное медицинское страхование\")]";
        WebElement DmsButton = driver.findElement(By.xpath(DmsXPath));
        waitUtilElementToBeClickable(DmsButton);
        DmsButton.click();

        sleep();
        Assert.assertEquals("страница ДМС не загрузилась", "Добровольное медицинское страхование для компаний и юридических лиц в Росгосстрахе", driver.getTitle());

        WebElement h1Dms = driver.findElement(By.xpath("//h1[contains(text(), \"Добровольное медицинское страхование\")]"));
        Assert.assertTrue("Заголовок отсутствует/не соответствует требуемому", h1Dms.getText().equals("Добровольное медицинское страхование"));

        String requestXPath = "//button/span[text()=\"Отправить заявку\"]";
        WebElement requestButton = driver.findElement(By.xpath(requestXPath));
        waitUtilElementToBeClickable(requestButton);
        requestButton.click();

        String h2RecallTitleXPath = "//h2[text()=\"Оперативно перезвоним\"]";
        WebElement h2RecallTitle = driver.findElement(By.xpath(h2RecallTitleXPath));
        String resultH2Title = h2RecallTitle.getText().replace("\n", " ");
        Assert.assertTrue("страница с заголовком Оперативно Перезвоним для оформления полиса не загрузилась", resultH2Title.equals("Оперативно перезвоним для оформления полиса"));

        String fieldXPath = "//input[@name=\"%s\"]";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), "Иванов Иван Иванович");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), " (779) 998-8877");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), "qwertyqwerty");
        WebElement agreementToProcessing = driver.findElement(By.xpath("//div[@class=\"checkbox-body form__checkbox\"]/input"));
        scrollToElementJs(agreementToProcessing);

        sleep();

        Actions actions = new Actions(driver);
        actions.moveToElement(agreementToProcessing).click(agreementToProcessing).build().perform();

        String addresFieldXPath = "//div[@class=\"vue-dadata__search\"]/input";
        WebElement addresField = driver.findElement(By.xpath(addresFieldXPath));
        fillInputField(addresField, "г Пенза");

        String contactMeXPath = "//button[@class=\"form__button-submit btn--basic\" and contains(text(), \"Свяжитесь со мной\")]";
        WebElement contactMeButton = driver.findElement(By.xpath(contactMeXPath));
        waitUtilElementToBeClickable(contactMeButton);
        contactMeButton.click();

        // Проверка на ошибку в поле email:
        WebElement email = driver.findElement(By.xpath(String.format(fieldXPath, "userEmail")));
        checkCorrect(email);


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        driver.quit();
    }

    private void waitUtilElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void fillInputField(WebElement element, String value) {
        scrollToElementJs(element);
        waitUtilElementToBeClickable(element);
        element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);

    }

    private void checkCorrect(WebElement element) {
        WebElement errorInfo = element.findElement(By.xpath("./../../span[@class=\"input__error text--small\"]"));
//        wait.until(ExpectedConditions.visibilityOf(errorInfo));
        wait.until(ExpectedConditions.textToBePresentInElement(errorInfo, "Введите корректный адрес электронной почты"));
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
