package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage {
    private WebDriver driver;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public void fillInfo(String firstName, String lastName, String zip) {
        driver.findElement(By.id("first-name")).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("postal-code")).sendKeys(zip);
        driver.findElement(By.id("continue")).click();
    }

    public void finishOrder() {
        driver.findElement(By.id("finish")).click();
    }

    public String getConfirmationText() {
        return driver.findElement(By.className("complete-header")).getText();
    }

    public String getErrorText() {
        return driver.findElement(By.cssSelector("[data-test='error']")).getText();
    }
    public void goToCartFromCheckoutOverview() {
        driver.findElement(By.id("cancel")).click();
    }

    public String getSubtotal() {
        return driver.findElement(By.className("summary_subtotal_label")).getText();
    }
}