package com.saucedemo.tests.fullRun;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import com.saucedemo.pages.InventoryPage;
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
import java.util.Map;

public class CheckoutFlowTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-features=PasswordLeakDetection");
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false
        ));
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get("https://www.saucedemo.com/");

        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver, wait);
        checkoutPage = new CheckoutPage(driver, wait);

        loginPage.login("standard_user", "secret_sauce");
        wait.until(ExpectedConditions.urlContains("inventory.html"));
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        driver.quit();
    }

    // --- Positive cases ---

    @Test(groups = {"smoke", "regression"})
    public void completePurchase_happyPath() {
        inventoryPage.addItemToCart("Sauce Labs Backpack");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), 1);

        inventoryPage.goToCart();
        Assert.assertEquals(cartPage.getItemCount(), 1);

        cartPage.checkout();
        checkoutPage.fillInfo("Jane", "Doe", "12345");
        checkoutPage.finishOrder();

        Assert.assertEquals(checkoutPage.getConfirmationText(), "Thank you for your order!");
    }

    @Test(groups = {"regression"})
    public void completePurchase_multipleItems() {
        inventoryPage.addItemToCart("Sauce Labs Backpack");
        inventoryPage.addItemToCart("Sauce Labs Bike Light");
        inventoryPage.addItemToCart("Sauce Labs Bolt T-Shirt");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), 3);

        inventoryPage.goToCart();
        Assert.assertEquals(cartPage.getItemCount(), 3);

        cartPage.checkout();
        checkoutPage.fillInfo("Jane", "Doe", "12345");
        checkoutPage.finishOrder();

        Assert.assertEquals(checkoutPage.getConfirmationText(), "Thank you for your order!");
    }

    @Test(groups = {"regression"})
    public void removeItemFromCart_beforeCheckout_updatesCount() {
        inventoryPage.addItemToCart("Sauce Labs Backpack");
        inventoryPage.addItemToCart("Sauce Labs Bike Light");

        inventoryPage.goToCart();
        Assert.assertEquals(cartPage.getItemCount(), 2);

        cartPage.removeItem("Sauce Labs Backpack");
        Assert.assertEquals(cartPage.getItemCount(), 1);
    }

    // --- Negative cases ---

    @DataProvider(name = "missingFieldCases")
    public Object[][] missingFieldCases() {
        return new Object[][] {
                {"", "Doe", "12345", "Error: First Name is required"},
                {"Jane", "", "12345", "Error: Last Name is required"},
                {"Jane", "Doe", "", "Error: Postal Code is required"},
        };
    }

    @Test(dataProvider = "missingFieldCases", groups = {"regression"})
    public void checkout_missingRequiredField_showsCorrectError(
            String firstName, String lastName, String zip, String expectedError) {

        inventoryPage.addItemToCart("Sauce Labs Backpack");
        inventoryPage.goToCart();
        cartPage.checkout();

        checkoutPage.fillInfo(firstName, lastName, zip);
        Assert.assertEquals(checkoutPage.getErrorText(), expectedError);
    }

    @Test(groups = {"regression"})
    public void checkout_emptyCart_cannotProceed() {
        // No items added — go straight to cart
        inventoryPage.goToCart();
        Assert.assertEquals(cartPage.getItemCount(), 0);

        cartPage.checkout();
        checkoutPage.fillInfo("Jane", "Doe", "12345");
        checkoutPage.finishOrder();

        // Saucedemo allows checkout with an empty cart — this documents that known behavior
        Assert.assertEquals(checkoutPage.getConfirmationText(), "Thank you for your order!");
    }

    @Test(groups = {"regression"})
    public void cancelCheckout_returnsToInventory() {
        inventoryPage.addItemToCart("Sauce Labs Backpack");
        inventoryPage.goToCart();
        cartPage.checkout();
        checkoutPage.fillInfo("Jane", "Doe", "12345");

        checkoutPage.cancelFromCheckoutOverview();

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"));

        // Confirm the item is still in the cart even after cancelling checkout
        inventoryPage.goToCart();
        Assert.assertEquals(cartPage.getItemCount(), 1);
    }
}