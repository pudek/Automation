package com.saucedemo.tests.fullRun;

import com.saucedemo.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;

public class Login {
    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-features=PasswordLeakDetection");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get("https://www.saucedemo.com/");

        loginPage = new LoginPage(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        driver.quit();
    }

    @DataProvider(name = "positiveLoginCases")
    public Object[][] positiveLoginCases() {
        return new Object[][] {
                {"standard_user", "secret_sauce"},
        };
    }

    @DataProvider(name = "negativeLoginCases")
    public Object[][] negativeLoginCases() {
        return new Object[][] {
                {"locked_out_user", "secret_sauce",
                        "Epic sadface: Sorry, this user has been locked out."},
                {"standard_user", "wrong_password",
                        "Epic sadface: Username and password do not match any user in this service"},
                {"", "secret_sauce",
                        "Epic sadface: Username is required"},
        };
    }

    @Test(dataProvider = "positiveLoginCases", groups = {"smoke", "regression"})
    public void positiveLogin(String username, String password) {
        loginPage.login(username, password);

        wait.until(ExpectedConditions.urlContains("inventory.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"));
    }

    @Test(dataProvider = "negativeLoginCases", groups = {"regression"})
    public void negativeLogin(String username, String password, String expectedError) {
        loginPage.login(username, password);

        String actualError = loginPage.getErrorText();
        Assert.assertEquals(actualError, expectedError);

        Assert.assertTrue(driver.getCurrentUrl().equals("https://www.saucedemo.com/"));
    }
}