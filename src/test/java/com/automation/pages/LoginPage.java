package com.automation.pages;

import com.automation.mobile.appium.AppiumDevice;
import com.automation.mobile.appium.AppiumDeviceManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage {

    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(id = "test-Username")
    public MobileElement usernameTxtFld;

    @AndroidFindBy (accessibility = "test-Password")
    @iOSXCUITFindBy (id = "test-Password")
    public MobileElement passwordTxtFld;

    @AndroidFindBy (accessibility = "test-LOGIN")
    @iOSXCUITFindBy (id = "test-LOGIN")
    public MobileElement loginBtn;

    @AndroidFindBy (xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView")
    @iOSXCUITFindBy (xpath = "//XCUIElementTypeOther[@name=\"test-Error message\"]/child::XCUIElementTypeStaticText")
    public MobileElement errTxt;


   // private LoginPageElements loginPageElements = new LoginPageElements();

    AppiumDevice device = AppiumDeviceManager.getDevice();

    public LoginPage(AppiumDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }


    public void enterUserName(String username) {
        clear(usernameTxtFld);
        sendKeys(usernameTxtFld, username, "login with " + username);
    }

    public void enterPassword(String password) {
        clear(passwordTxtFld);
        sendKeys(passwordTxtFld, password, "password is " + password);
    }

    public void pressLoginBtn() {
        click(loginBtn, "press login button");
    }

    public String getErrTxt() {
        String err = getText(errTxt, "error text is - ");
        return err;
    }

}
