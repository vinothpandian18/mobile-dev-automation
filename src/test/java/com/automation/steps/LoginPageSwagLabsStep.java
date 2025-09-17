package com.automation.steps;
import com.automation.mobile.appium.AppiumDriverManager;
import com.automation.pages.BasePage;
import com.automation.pages.LoginPageSwagLabsPage;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.io.IOException;

public class LoginPageSwagLabsStep {
    public AppiumDriver driver = AppiumDriverManager.getDriver();
    public LoginPageSwagLabsPage loginPageSwagLabsPage = new LoginPageSwagLabsPage(driver);

    public BasePage basePage = new BasePage(driver);

    @Then("^sign in to the application with valid username and password$")
    public void signInToTheApp() throws Exception {
            loginPageSwagLabsPage.signIn();
        }

    @Then("^sign in to the application using invalid username$")
    public void signInToTheAppUsingInvalidUserName() throws Exception {
            loginPageSwagLabsPage.signInWithWrongUserName();
        }


    @Then("^sign in to the application using invalid password$")
    public void signInToTheAppUsingInvalidPassword() throws Exception {
            loginPageSwagLabsPage.signInWithWrongPassword();
        }

    @Then("^verify home page displayed$")
    public void isHomePageDisplayed() {
        Assert.assertTrue(loginPageSwagLabsPage.isHomepageDisplayed(), "login failed");
    }


    @When("^login via valid credentials$")
    public void login_via_valid_credentials(){
        try {
            loginPageSwagLabsPage.loginWithValidCredentials();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("login failed");
        }
    }
}
