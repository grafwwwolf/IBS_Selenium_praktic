package ru.ibs.selenium.parametrized;

import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class ParametrizedSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Parameterized.Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] {
            {"Иванов Иван Иванович", " (779) 998-8877", "qwertyqwerty", "г Пенза"},
            {"Петров Петр Петрович", " (937) 123-9922", "qwertyqwerty", "г Москва"},
            {"Денисов Денис Сергеевич", " (987) 234-8877", "qwertyqwerty", "г Саранск"}
        });
    }

    @Parameterized.Parameter(0)
    public String name;

    @Parameterized.Parameter(1)
    public String telephone;

    @Parameterized.Parameter(2)
    public String email;

    @Parameterized.Parameter(3)
    public String address;

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

    @Test
    public void test() {

        String forCompaniesXPATH = "//a[@class=\"text--second\" and contains(text(), \"Компаниям\")]";
        WebElement forCompaniesButton = driver.findElement(By.xpath(forCompaniesXPATH));
        waitUtilElementToBeClickable(forCompaniesButton).click();

        wait.until(ExpectedConditions.attributeContains(forCompaniesButton, "Class", "nuxt-link-exact-active"));

        String healthXPATH = "//span[@class=\"padding\" and contains(text(), \"Здоровье\")]";
        WebElement healthButton = driver.findElement(By.xpath(healthXPATH));
        waitUtilElementToBeClickable(healthButton).click();

        WebElement parentHealth = healthButton.findElement(By.xpath("./.."));
        wait.until(ExpectedConditions.attributeContains(parentHealth, "Class", "active"));

        String DmsXPath = "//a[contains(text(), \"Добровольное медицинское страхование\")]";
        WebElement DmsButton = driver.findElement(By.xpath(DmsXPath));
        waitUtilElementToBeClickable(DmsButton).click();

        WebElement checkPageUniqueElement = driver.findElement(By.xpath("//p[@class=\"text text--basic-second word-breaking\" and contains(text(), \"ДМС сегодня\")]"));
        wait.until(ExpectedConditions.visibilityOf(checkPageUniqueElement));
        MatcherAssert.assertThat("страница ДМС не загрузилась", driver.getTitle(), equalTo("Добровольное медицинское страхование для компаний и юридических лиц в Росгосстрахе"));

        WebElement h1Dms = driver.findElement(By.xpath("//h1[@class=\"title word-breaking title--h2\"]"));
        Assert.assertTrue("Заголовок отсутствует/не соответствует требуемому", h1Dms.getText().equals("Добровольное медицинское страхование"));

        String requestXPath = "//button/span[text()=\"Отправить заявку\"]";
        WebElement requestButton = driver.findElement(By.xpath(requestXPath));
        waitUtilElementToBeClickable(requestButton).click();

        String h2RecallTitleXPath = "//h2[text()=\"Оперативно перезвоним\"]";
        WebElement h2RecallTitle = driver.findElement(By.xpath(h2RecallTitleXPath));
        String resultH2Title = h2RecallTitle.getText().replace("\n", " ");
        Assert.assertTrue("страница с заголовком Оперативно Перезвоним для оформления полиса не загрузилась", resultH2Title.equals("Оперативно перезвоним для оформления полиса"));

        String fieldXPath = "//input[@name=\"%s\"]";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), name);
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), telephone);
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), email);
        WebElement agreementToProcessing = driver.findElement(By.xpath("//div[@class=\"checkbox-body form__checkbox\"]/input"));
        scrollToElementJs(agreementToProcessing);

        sleep(2000);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].click();", agreementToProcessing);

        String addresFieldXPath = "//div[@class=\"vue-dadata__search\"]/input";
        WebElement addresField = driver.findElement(By.xpath(addresFieldXPath));
        fillInputField(addresField, address);

        String contactMeXPath = "//button[@class=\"form__button-submit btn--basic\" and contains(text(), \"Свяжитесь со мной\")]";
        WebElement contactMeButton = driver.findElement(By.xpath(contactMeXPath));
        waitUtilElementToBeClickable(contactMeButton);
        contactMeButton.click();

        // Проверка на ошибку в поле email:
        WebElement email = driver.findElement(By.xpath(String.format(fieldXPath, "userEmail")));
        checkCorrect(email);


        sleep(5000);
    }

    @After()
    public void after() {
        driver.quit();
    }

    private WebElement waitUtilElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
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
        wait.until(ExpectedConditions.textToBePresentInElement(errorInfo, "Введите корректный адрес электронной почты"));
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
