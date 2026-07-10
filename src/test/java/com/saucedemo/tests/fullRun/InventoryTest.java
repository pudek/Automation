package com.saucedemo.tests.fullRun;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.openqa.selenium.By;
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
import java.util.List;
import java.util.stream.Collectors;

public class InventoryTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-features=PasswordLeakDetection");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get("https://www.saucedemo.com/");

        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        driver.quit();
    }

    // --- Positive cases ---

    @DataProvider(name = "sortCases")
    public Object[][] sortCases() {
        return new Object[][] {
                {"az"},
                {"za"},
                {"lohi"},
                {"hilo"},
        };
    }

    @Test(dataProvider = "sortCases", groups = {"regression"})
    public void sortBy_producesCorrectOrder(String sortOption) {
        loginPage.login("standard_user", "secret_sauce");
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        inventoryPage.sortBy(sortOption);

        if (sortOption.equals("az") || sortOption.equals("za")) {
            List<String> names = inventoryPage.getItemNamesInOrder();
            List<String> sorted = names.stream().sorted().collect(Collectors.toList());
            if (sortOption.equals("za")) {
                sorted = sorted.stream()
                        .sorted(java.util.Comparator.reverseOrder())
                        .collect(Collectors.toList());
            }
            Assert.assertEquals(names, sorted);
        } else {
            List<Double> prices = inventoryPage.getItemPricesInOrder();
            List<Double> sorted = prices.stream().sorted().collect(Collectors.toList());
            if (sortOption.equals("hilo")) {
                sorted = sorted.stream()
                        .sorted(java.util.Comparator.reverseOrder())
                        .collect(Collectors.toList());
            }
            Assert.assertEquals(prices, sorted);
        }
    }

    @Test(groups = {"smoke", "regression"})
    public void addSingleItem_badgeShowsOne() {
        loginPage.login("standard_user", "secret_sauce");
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        inventoryPage.addItemToCart("Sauce Labs Backpack");

        Assert.assertEquals(inventoryPage.getCartBadgeCount(), 1);
        Assert.assertTrue(inventoryPage.isItemInCart("Sauce Labs Backpack"));
    }

    @Test(groups = {"regression"})
    public void addMultipleItems_badgeReflectsCorrectCount() {
        loginPage.login("standard_user", "secret_sauce");
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        inventoryPage.addItemToCart("Sauce Labs Backpack");
        inventoryPage.addItemToCart("Sauce Labs Bike Light");
        inventoryPage.addItemToCart("Sauce Labs Bolt T-Shirt");

        Assert.assertEquals(inventoryPage.getCartBadgeCount(), 3);
    }

    // --- Negative cases ---

    @Test(groups = {"regression"})
    public void removeItem_badgeDisappearsAtZero() {
        loginPage.login("standard_user", "secret_sauce");
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        inventoryPage.addItemToCart("Sauce Labs Backpack");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), 1);

        inventoryPage.removeItemFromCart("Sauce Labs Backpack");

        // Badge element should no longer exist once cart is empty
        Assert.assertTrue(driver.findElements(By.className("shopping_cart_badge")).isEmpty());
        Assert.assertFalse(inventoryPage.isItemInCart("Sauce Labs Backpack"));
    }

    @Test(groups = {"regression"})
    public void problemUser_imagesAreBroken() {
        loginPage.login("problem_user", "secret_sauce");
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        List<String> imageSrcs = driver.findElements(By.cssSelector(".inventory_item_img img"))
                .stream()
                .map(el -> el.getAttribute("src"))
                .distinct()
                .collect(Collectors.toList());

        Assert.assertTrue(imageSrcs.size() < 6,
                "Expected problem_user's known broken/duplicated images, but each product had a unique image — bug may be fixed");
    }

    @Test(groups = {"regression"})
    public void lockedOutUser_cannotReachInventory() {
        loginPage.login("locked_out_user", "secret_sauce");

        // Should never leave the login page
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory.html"));
        Assert.assertEquals(loginPage.getErrorText(),
                "Epic sadface: Sorry, this user has been locked out.");
    }
}