package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void fillInfo(String firstName, String lastName, String zip) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("first-name")));
        driver.findElement(By.id("first-name")).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("postal-code")).sendKeys(zip);
        driver.findElement(By.id("continue")).click();
        // On success this lands on step-two (finish button appears);
        // on validation failure the error banner appears instead — wait for either.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.id("finish")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-test='error']"))
        ));
    }

    public void finishOrder() {
        driver.findElement(By.id("finish")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("complete-header")));
    }

    public String getConfirmationText() {
        return driver.findElement(By.className("complete-header")).getText();
    }

    public String getErrorText() {
        return driver.findElement(By.cssSelector("[data-test='error']")).getText();
    }

    public void cancelFromCheckoutOverview() {
        driver.findElement(By.id("cancel")).click();
        wait.until(ExpectedConditions.urlContains("inventory.html"));
    }
}