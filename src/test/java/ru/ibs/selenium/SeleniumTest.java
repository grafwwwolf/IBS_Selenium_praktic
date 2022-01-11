package ru.ibs.selenium;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.ibs.selenium.base.BaseTests;

import static org.hamcrest.CoreMatchers.equalTo;


public class SeleniumTest extends BaseTests {

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
//        Assert.assertEquals("страница ДМС не загрузилась", "Добровольное медицинское страхование для компаний и юридических лиц в Росгосстрахе", driver.getTitle());
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
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), "Иванов Иван Иванович");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), " (779) 998-8877");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), "qwertyqwerty");
        WebElement agreementToProcessing = driver.findElement(By.xpath("//div[@class=\"checkbox-body form__checkbox\"]/input"));
        scrollToElementJs(agreementToProcessing);


        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].click;", agreementToProcessing);

        String addresFieldXPath = "//div[@class=\"vue-dadata__search\"]/input";
        WebElement addresField = driver.findElement(By.xpath(addresFieldXPath));
        fillInputField(addresField, "г Пенза");
        addresField.submit();

        String contactMeXPath = "//button[@class=\"form__button-submit btn--basic\" and contains(text(), \"Свяжитесь со мной\")]";
        WebElement contactMeButton = driver.findElement(By.xpath(contactMeXPath));
        waitUtilElementToBeClickable(contactMeButton);
        javascriptExecutor.executeScript("arguments[0].click;", contactMeButton);

        // Проверка на ошибку в поле email:
        WebElement email = driver.findElement(By.xpath(String.format(fieldXPath, "userEmail")));
        checkCorrect(email);


        sleep(5000);
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
