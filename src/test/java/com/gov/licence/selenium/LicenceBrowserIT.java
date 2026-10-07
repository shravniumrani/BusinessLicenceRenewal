package com.gov.licence.selenium;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

class LicenceBrowserIT {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String baseUrl = System.getProperty(
            "baseUrl",
            "http://localhost:8080/business-licence-renewal"
    );

    @RegisterExtension
    final TestExecutionExceptionHandler failureScreenshot =
            (context, failure) -> {
                if (driver != null) {
                    try {
                        Path folder = Path.of("target", "selenium-screenshots");
                        Files.createDirectories(folder);

                        byte[] screenshot =
                                ((org.openqa.selenium.TakesScreenshot) driver)
                                        .getScreenshotAs(OutputType.BYTES);

                        Files.write(
                                folder.resolve(
                                        context.getRequiredTestMethod().getName()
                                                + ".png"
                                ),
                                screenshot
                        );
                    } catch (Exception screenshotError) {
                        failure.addSuppressed(screenshotError);
                    }
                }
                throw failure;
            };

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1000");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username, String password) {
        driver.get(baseUrl + "/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("username")
        )).sendKeys(username);

        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
    }

    @Test
    void ownerCanLogin() {
        login("owner1", "OwnerDemo!2026");

        wait.until(ExpectedConditions.urlContains("/owner/dashboard"));

        assertEquals(
                "Role: BUSINESS_OWNER",
                driver.findElement(By.id("user-role")).getText()
        );

        assertTrue(driver.findElement(By.id("welcome-name")).isDisplayed());
        assertTrue(driver.findElement(By.id("logout-button")).isDisplayed());
    }

    @Test
    void invalidPasswordIsRejected() {
        login("owner1", "WrongPassword!123");

        String error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("login-error")
                )
        ).getText();

        assertFalse(error.isBlank());
        assertTrue(driver.getCurrentUrl().contains("/login"));
        assertTrue(driver.findElements(By.id("logout-button")).isEmpty());
    }

    @Test
    void logoutProtectsOwnerDashboard() {
        login("owner1", "OwnerDemo!2026");

        wait.until(ExpectedConditions.urlContains("/owner/dashboard"));
        driver.findElement(By.id("logout-button")).click();

        wait.until(ExpectedConditions.urlContains("/login"));

        driver.get(baseUrl + "/owner/dashboard");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("login-button")
        ));

        assertTrue(driver.getCurrentUrl().contains("/login"));
        assertTrue(driver.findElements(By.id("welcome-name")).isEmpty());
    }
}