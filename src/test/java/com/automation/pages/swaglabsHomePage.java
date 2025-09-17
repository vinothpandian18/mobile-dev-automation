package com.automation.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class swaglabsHomePage extends BasePage{

    @AndroidFindBy(xpath = "//*[contains(@content-desc,'test-Item')]")
    @iOSXCUITFindBy(iOSNsPredicate = "type == 'XCUIElementTypeStaticText' AND label == 'Sauce Labs Backpack'")
    private MobileElement itemImage;

    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='test-ADD TO CART']")
    @iOSXCUITFindBy(accessibility = "test-ADD TO CART")
    private MobileElement addToCart;

    @AndroidFindBy(xpath = "//*[contains(@content-desc,'test-Toggle')]")
    @iOSXCUITFindBy(accessibility = "test-Toggle")
    private MobileElement toggleButton;

    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc='test-REMOVE']")
    @iOSXCUITFindBy(accessibility = "test-REMOVE")
    private MobileElement removeButton;

    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Cart\"]/android.view.ViewGroup/android.widget.TextView")
    @iOSXCUITFindBy(iOSNsPredicate = "name  == 'test-Cart' or label == '1'")
    private MobileElement cart;

    @AndroidFindBy(xpath= "//*[contains(@content-desc,'test-Menu')]")
    @iOSXCUITFindBy(iOSNsPredicate = "name  == 'test-Menu'")
    private MobileElement menu;

    @AndroidFindBy(xpath = "//*[contains(@content-desc,'test-Modal Selector Button')]")
    @iOSXCUITFindBy(iOSNsPredicate = "name  == 'test-Modal Selector Button'")
    private MobileElement filterOption;

    @AndroidFindBy(xpath = "//android.widget.ScrollView[@content-desc=\"Selector container\"]/android.view.ViewGroup/android.view.ViewGroup[5]/android.view.ViewGroup")
    @iOSXCUITFindBy(accessibility = "Price (low to high)")
    private MobileElement lowToHighOption;

    @AndroidFindBy(accessibility = "test-BACK TO PRODUCTS")
    @iOSXCUITFindBy(accessibility = "test-BACK TO PRODUCTS")
    private MobileElement continueShopping;

    @AndroidFindBy(xpath = "//*[contains(@content-desc,'test-ADD TO CART')]")
    @iOSXCUITFindBy(accessibility = "test-ADD TO CART")
    private MobileElement addToCartAtHomePage;

    @AndroidFindBy(xpath = "//*[contains(@content-desc,'test-Price')]")
    @iOSXCUITFindBy(iOSNsPredicate = "type == 'XCUIElementTypeStaticText' AND name == 'test-Price'")
    private List<MobileElement> itemPrice;


    public swaglabsHomePage(AppiumDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);

    }

    public void tapOnItem() {
        clickElement(itemImage);
    }

    public void clickOnAddToCartItem() {
        waitForLoaderToDisappear();
        scrollDownForElement(addToCart);
        clickElement(addToCart);
    }

    public void clickOnCartIcon() {
        clickElement(cart);
    }

    public void clickOnRemoveButton() {
        scrollDownForElement(removeButton);
        clickElement(removeButton);
    }

    public void toggleItem() {
        clickElement(toggleButton);
    }

    public void clickOnFilterOption() {
        clickElement(filterOption);
    }

    public void selectLowToHighPriceFilter() {
        clickElement(lowToHighOption);
    }

    public boolean verifyMenuTabDisplayed() {
       return isElementDisplayed(menu);
    }

    public int getCartCount() {
        return Integer.parseInt(cart.getText());
    }

    public void clickOnContinueShopping() {
        clickElement(continueShopping);
    }

    public void addItemInHomePage() {
        clickElement(addToCartAtHomePage);
    }
}
