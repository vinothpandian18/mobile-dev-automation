package com.automation.steps;

import com.automation.mobile.appium.AppiumDriverManager;
import com.automation.pages.AmwayLoginPage;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AmwayLoginSteps {

    AppiumDriver driver = AppiumDriverManager.getDriver();
    public AmwayLoginPage amwayLogin = new AmwayLoginPage(driver);


    @When("^amway enter user name \"([^\"]*)\"$")
    public void enterUserName(String username) {
        amwayLogin.enterUserName(username);
    }

    @When("^amway enter the password as \"([^\"]*)\"$")
    public void enterThePasswordAs(String password) {
        amwayLogin.enterPassword(password);
    }

    @Then("^amway login$")
    public void login() {
        amwayLogin.pressLoginBtn();
    }

}
