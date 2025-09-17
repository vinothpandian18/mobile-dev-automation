package com.automation.steps;

import com.automation.mobile.appium.AppiumDriverManager;
import com.automation.pages.LoginPage;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class LoginPageSteps {
    AppiumDriver driver = AppiumDriverManager.getDriver();
    public LoginPage loginPage = new LoginPage(driver);
    

    @When("^enter user name \"([^\"]*)\"$")
    public void enterUserName(String username){
        loginPage.enterUserName(username);


    }
    @When("^enter the password as \"([^\"]*)\"$")
    public void enterThePasswordAs(String password) {
        loginPage.enterPassword(password);

    }
    @When("login")
    public void login() {
        loginPage.pressLoginBtn();
    }


    @Then("^should get error message \"([^\"]*)\"$")
    public void shouldGetErrorMessage(String err) {
        Assert.assertEquals(loginPage.getErrTxt(), err);

    }

   /* @Then("^should see Products page with title \"([^\"]*)\"$")
    public void shouldSeeProductsPageWithTitle(String title) {
        Assert.assertEquals(new ProductsPage().getTitle(), title);

    }*/

}
