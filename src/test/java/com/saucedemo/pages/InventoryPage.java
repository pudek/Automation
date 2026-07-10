package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryPage {
    private WebDriver driver;

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public void addItemToCart(String itemName) {
        String buttonId = "add-to-cart-" + itemName.toLowerCase().replace(" ", "-");
        driver.findElement(By.id(buttonId)).click();
    }

    public void goToCart() {
        driver.findElement(By.className("shopping_cart_link")).click();
    }

    public int getCartBadgeCount() {
        return Integer.parseInt(driver.findElement(By.className("shopping_cart_badge")).getText());
    }

    // --- Sorting ---

    public void sortBy(String option) {
        // option values: "az", "za", "lohi", "hilo"
        Select sortDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        sortDropdown.selectByValue(option);
    }

    public List<String> getItemNamesInOrder() {
        return driver.findElements(By.className("inventory_item_name"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getItemPricesInOrder() {
        return driver.findElements(By.className("inventory_item_price"))
                .stream()
                .map(el -> Double.parseDouble(el.getText().replace("$", "")))
                .collect(Collectors.toList());
    }
    public boolean isItemInCart(String itemName) {
        String buttonId = "remove-" + itemName.toLowerCase().replace(" ", "-");
        return !driver.findElements(By.id(buttonId)).isEmpty();
    }

    public void removeItemFromCart(String itemName) {
        String buttonId = "remove-" + itemName.toLowerCase().replace(" ", "-");
        driver.findElement(By.id(buttonId)).click();
    }
}