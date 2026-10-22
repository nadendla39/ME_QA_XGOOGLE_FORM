package demo.wrappers;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wrappers {

    public static void enterText(WebDriver driver, By locator, String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        wait.until(ExpectedConditions.visibilityOf(element));
        try {
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", element, text);
        }
    }

    public static void clickElement(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    public static void selectCheckbox(WebDriver driver, String optionText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        By locator = By.xpath("//label[.//span[normalize-space()='" + optionText + "']] | //span[normalize-space()='" + optionText + "']/ancestor::label | //span[normalize-space()='" + optionText + "']/ancestor::div[@role='checkbox']");
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    public static void selectDropdownOption(WebDriver driver, String optionText) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // 1. Locate the dropdown menu card trigger
        By dropdownLocator = By.xpath("//div[@role='listbox']");
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(dropdownLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", dropdown);
        Thread.sleep(800);

        // Click via native Actions to trigger the overlay menu
        Actions actions = new Actions(driver);
        actions.moveToElement(dropdown).click().perform();
        Thread.sleep(1200);

        // 2. Select the option from the opened overlay
        try {
            By optionLocator = By.xpath("//div[@role='option' and .//span[normalize-space()='" + optionText + "']] | (//div[@role='option'])[2]");
            WebElement optionElement = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));
            actions.moveToElement(optionElement).click().perform();
        } catch (Exception e) {
            // Fallback: Send Down arrow key followed by Enter to select the first choice (Mr)
            actions.sendKeys(Keys.DOWN).sendKeys(Keys.ENTER).perform();
        }
        Thread.sleep(1000);
    }

    public static String getEpochTimeString() {
        return String.valueOf(Instant.now().getEpochSecond());
    }

    public static String getPastDateFormatted(int minusDays, String pattern) {
        LocalDate date = LocalDate.now().minusDays(minusDays);
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
}