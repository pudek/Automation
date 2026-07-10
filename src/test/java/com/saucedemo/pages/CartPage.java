package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage {
    private WebDriver driver;

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public void checkout() {
        driver.findElement(By.id("checkout")).click();
    }

    public int getItemCount() {
        return driver.findElements(By.className("cart_item")).size();
    }
    public void continueShopping() {
        driver.findElement(By.id("continue-shopping")).click();
    }

    public void removeItem(String itemName) {
        String buttonId = "remove-" + itemName.toLowerCase().replace(" ", "-");
        driver.findElement(By.id(buttonId)).click();
    }
}