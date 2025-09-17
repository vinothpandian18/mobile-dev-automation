package com.automation.steps;

import com.automation.mobile.appium.AppiumDriverManager;
import com.automation.pages.swaglabsHomePage;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class swagLabHomeStepPage {
    AppiumDriver driver = AppiumDriverManager.getDriver();
    public swaglabsHomePage swaglabsHomePage = new swaglabsHomePage(driver);

    int cartCount;

    @Then("^tap on a product to add to a cart$")
    public void clickOnItem() {
        swaglabsHomePage.tapOnItem();
    }

    @Then("^tap on a product to remove that item from the cart$")
    public void clickOnItemToRemoveAnItemFromCart() {
        swaglabsHomePage.tapOnItem();
    }

    @When("^click on add to item button$")
    public void clickOnAddToItemButton() {
        swaglabsHomePage.clickOnAddToCartItem();
    }

    @Then("^click on cart icon$")
    public void clickOnCartIcon() {
        swaglabsHomePage.clickOnCartIcon();
    }

    @When("^click on remove button$")
    public void clickRemoveButton() {
        swaglabsHomePage.clickOnRemoveButton();
    }

    @When("^click on filter option$")
    public void clickOnFilterOption() {
        swaglabsHomePage.clickOnFilterOption();
    }

    @And("^select low to high price option$")
    public void selectLowToHighPriceOption() {
        swaglabsHomePage.selectLowToHighPriceFilter();
    }

    @And("^toggle the items view$")
    public void ToggleTheItemView() {
        swaglabsHomePage.toggleItem();
    }

    @And("^verify menu tab is displayed$")
    public void verifyMenuTabDisplayed() {
        Assert.assertTrue(swaglabsHomePage.verifyMenuTabDisplayed(), "menu tab is displayed");
    }

    @And("^get cart count$")
    public void getCartCount() {
       cartCount = swaglabsHomePage.getCartCount();
    }

    @And("^verify cart count increased$")
    public void verifyCartCountIncreased() {
        Assert.assertTrue(cartCount < swaglabsHomePage.getCartCount(),"cart count is not increased");
    }

    @And("^verify cart count decreased$")
    public void verifyCartCountDecreased() {
        Assert.assertTrue(cartCount > swaglabsHomePage.getCartCount(),"cart count is not decreased");
    }

    @And("^click on continue shopping$")
    public void clickOnContinueShopping() {
        swaglabsHomePage.clickOnContinueShopping();
    }

    @And("^click on add to cart button on home page$")
    public void clickOnAddToCartAtHomePage() {
        swaglabsHomePage.addItemInHomePage();
    }




}
