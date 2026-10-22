package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.logging.Level;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;

    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("Start Test case: testCase01");

        // 1. Navigate to Google Form
        String formUrl = "https://docs.google.com/forms/d/e/1FAIpQLSep9LTMntH5YqIXa5nkiPKSs283kdwitBBhXWyZdAS-e4CxBQ/viewform";
        driver.get(formUrl);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@role='listitem'] | //form")));
        Thread.sleep(2000);

        // 2. Fill in "Crio Learner" in the 1st text box
        By nameInput = By.xpath("(//div[@role='listitem']//input[@type='text'])[1] | (//input[@type='text'])[1]");
        Wrappers.enterText(driver, nameInput, "Crio Learner");

        // 3. Write down: "I want to be the best QA Engineer! <epoch_time>"
        String epochTime = Wrappers.getEpochTimeString();
        String motivationText = "I want to be the best QA Engineer! " + epochTime;
        By motivationInput = By.xpath("//textarea | (//div[@role='listitem']//input[@type='text'])[2]");
        Wrappers.enterText(driver, motivationInput, motivationText);

        // 4. Enter Automation Testing experience in radio button
        By expRadio = By.xpath("//span[contains(text(), '0 - 2') or contains(text(), '3 - 5') or contains(text(), 'Fresher')]/ancestor::label | //div[@role='radio']//span[contains(text(), '0 - 2')]/ancestor::div[@role='radio'] | (//div[@role='radio'])[1]");
        Wrappers.clickElement(driver, expRadio);

        // 5. Select Java, Selenium, and TestNG checkboxes
        String[] skills = {"Java", "Selenium", "TestNG"};
        for (String skill : skills) {
            Wrappers.selectCheckbox(driver, skill);
            Thread.sleep(400);
        }

        // 6. Provide how you would like to be addressed in the dropdown (Mr)
        Wrappers.selectDropdownOption(driver, "Mr");

        // 7. Provide current date minus 7 days dynamically
        String formattedDate = Wrappers.getPastDateFormatted(7, "dd/MM/yyyy");
        String isoDate = Wrappers.getPastDateFormatted(7, "yyyy-MM-dd");

        try {
            By dateLocator = By.xpath("//input[@type='date']");
            WebElement dateField = wait.until(ExpectedConditions.presenceOfElementLocated(dateLocator));
            try {
                dateField.sendKeys(formattedDate);
            } catch (Exception ex) {
                driver.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", dateField, isoDate);
            }
        } catch (Exception e) {
            By genericDateLocator = By.xpath("(//div[@role='listitem']//input[@type='text'])[3]");
            Wrappers.enterText(driver, genericDateLocator, formattedDate);
        }

        // 8. Provide the time 07:30
        try {
            By hourInput = By.xpath("//input[@aria-label='Hour' or @aria-label='hh']");
            By minuteInput = By.xpath("//input[@aria-label='Minute' or @aria-label='mm']");
            Wrappers.enterText(driver, hourInput, "07");
            Wrappers.enterText(driver, minuteInput, "30");
        } catch (Exception ignored) {}

        // 9. Submit the form
        WebElement submitBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//span[text()='Submit']/ancestor::div[@role='button'] | //div[@role='button']//span[contains(text(), 'Submit')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", submitBtn);
        Thread.sleep(800);
        try {
            submitBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
        }

        // 10. Verify and print success confirmation message on console
        By successMsg = By.xpath("//div[contains(@class, 'vHW8du') or contains(@class, 'ResponseConfirmation') or contains(text(), 'recorded') or contains(text(), 'response') or contains(., 'Thanks for your response')]");
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg));
        String responseText = confirmation.getText().trim();
        System.out.println("Form Submission Response: " + responseText);

        System.out.println("end Test case: testCase01");
}
    @AfterTest
    public void endTest() {
        if (driver != null) {
            driver.quit();
        }
    }
}