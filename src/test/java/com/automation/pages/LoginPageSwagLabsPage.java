package com.automation.pages;
import com.automation.mobile.manager.ConfigFileManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.apache.log4j.Logger;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;
import java.util.Map;

public class LoginPageSwagLabsPage extends BasePage{

    private static Logger logger = Logger.getLogger(LoginPageSwagLabsPage.class);

    @AndroidFindBy(xpath= "//*[contains(@text,'Username')]")
    @iOSXCUITFindBy(accessibility = "test-Username")
    private MobileElement usernameTextField;

    @AndroidFindBy(xpath= "//*[contains(@text,'Password')]")
    @iOSXCUITFindBy(accessibility= "test-Password")
    private MobileElement passwordTextField;

    @AndroidFindBy(xpath= "//*[contains(@text,'LOGIN')]")
    @iOSXCUITFindBy(accessibility= "test-LOGIN")
    private MobileElement loginButton;

    @AndroidFindBy(xpath= "//*[contains(@text,'PRODUCTS')]")
    @iOSXCUITFindBy(iOSNsPredicate= "label CONTAINS 'PRODUCTS'")
    private MobileElement homePage;
    

    public LoginPageSwagLabsPage(AppiumDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    /**
     * This method is used to login with valid credentials and credentials are fetched from properties file
     * @throws IOException
     * @author : Mohammed Haseeb
     */
    public void loginWithValidCredentials() throws IOException {
        String username =  getBannerPropertiesMap().get("USERNAME");
        logger.info("Entering username " + username);
        sendKeys(usernameTextField, username);
        String password =  getBannerPropertiesMap().get("PASSWORD");
        logger.info("Entering password " + "*************");
        sendKeys(passwordTextField,password);
        logger.info("click on login ");
        click(loginButton);
    }
    /**
     * Method to enter email
     *
     * @throws IOException
     */
    public void enterEmail(String arg) throws IOException {
        String env = getCurrentEnv();
        Map<String, String> bannerProperty = ConfigFileManager.getAppPropertyMap(env);
        String username = bannerProperty.get(arg);
        logger.info("Entering username " + username);
        enterKey(usernameTextField, username);
    }

    /**
     * Method to enter password
     *
     * @throws IOException
     */
    public void enterPassword(String arg) throws IOException {
        String env = getCurrentEnv();
        Map<String, String> bannerProperty = ConfigFileManager.getAppPropertyMap(env);
        String password = bannerProperty.get(arg);
        logger.info("Entering username " + "************");
        enterKey(passwordTextField, password);
    }

    public void enterCredentials(String user, String password) throws IOException {
        enterEmail(user);
        enterPassword(password);
        clickElement(loginButton);
        waitForLoaderToDisappear();
    }

    /**
     * Method to sign in to the application
     *
     * @throws IOException
     */
    public void signIn() throws IOException {
        enterCredentials("USERNAME", "PASSWORD");
    }

    /**
     * Method to sign in to the application with wrong username
     *
     * @throws IOException
     */
    public void signInWithWrongUserName() throws IOException {
        enterCredentials("INVALIDUSERNAME", "PASSWORD");
    }

    /**
     * Method to sign in to the application with wrong username
     *
     * @throws IOException
     */
    public void signInWithWrongPassword() throws IOException {
        enterCredentials("USERNAME", "INVALIDPASSWORD");
    }

    /**
     * Method to sign in to the application with amway username and password
     *
     * @throws IOException
     */
    public void signInAmway() throws IOException {
        enterCredentials("AMWAYUSERNAME", "AMWAYPASSWORD");
    }

    public boolean isHomepageDisplayed() {
        return isElementDisplayed(homePage);
    }
}
