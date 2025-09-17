package com.automation.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.support.PageFactory;

public class AmwayLoginPage extends BasePage {

    @AndroidFindBy(xpath = "Pending")
    @iOSXCUITFindBy(iOSNsPredicate = "type =='XCUIElementTypeTextField' AND value == '請輸入直銷商/會員編號或手機號碼'")
    private MobileElement amwayUserField;

    @AndroidFindBy(xpath = "Pending")
    @iOSXCUITFindBy(iOSNsPredicate = "type == 'XCUIElementTypeSecureTextField' AND value == '請輸入密碼'")
    private MobileElement amwayPasswordField;

    @AndroidFindBy(xpath = "Pending")
    @iOSXCUITFindBy(iOSNsPredicate = "type == 'XCUIElementTypeButton' AND name == '登入'")
    private MobileElement amwayLoginBtn;

    public AmwayLoginPage(AppiumDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public void enterUserName(String username) {
        clear(amwayUserField);
        sendKeys(amwayUserField, username, "login with " + username);
    }

    public void enterPassword(String password) {
        clear(amwayPasswordField);
        sendKeys(amwayPasswordField, password, "password is " + password);
    }

    public void pressLoginBtn() {
        click(amwayLoginBtn, "press login button");
    }

}