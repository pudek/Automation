package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public CartPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void checkout() {
        driver.findElement(By.id("checkout")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("first-name")));
    }

    public void continueShopping() {
        driver.findElement(By.id("continue-shopping")).click();
        wait.until(ExpectedConditions.urlContains("inventory.html"));
    }

    public void removeItem(String itemName) {
        String buttonId = "remove-" + itemName.toLowerCase().replace(" ", "-");
        driver.findElement(By.id(buttonId)).click();
    }

    public int getItemCount() {
        return driver.findElements(By.className("cart_item")).size();
    }
}