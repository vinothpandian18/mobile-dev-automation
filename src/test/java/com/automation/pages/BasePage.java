package com.automation.pages;

import com.automation.mobile.appium.AppiumDevice;
import com.automation.mobile.appium.AppiumDeviceManager;
import com.automation.mobile.entities.CommandArgument;
import com.automation.mobile.helpers.AppRelaunchHelper;
import com.automation.mobile.manager.ConfigFileManager;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class BasePage {
    public AppiumDriver driver;
    public WebDriverWait waitVar;
    public String platformName;
    private final int APP_BACKGROUND_TIME = 5;
    private final int WAIT_FOR_ELEMENT_TIMEOUT = 10;
    private AppRelaunchHelper appRelaunchHelper;
    private static Logger logger = Logger.getLogger(BasePage.class);


    public BasePage(AppiumDriver driver) {
        this.driver = driver;
        this.waitVar = new WebDriverWait(driver, 20);
        this.platformName = driver.getPlatformName().toLowerCase();
        appRelaunchHelper = new AppRelaunchHelper(driver);
    }

    public Map<String, String> getBannerPropertiesMap() throws IOException {
        logger.info("store current environment " + getCurrentEnv());
        String env = getCurrentEnv();
        Map<String, String> bannerProperties = ConfigFileManager.getAppPropertyMap(env);
        logger.info("getting current environment " + getCurrentEnv());
        return bannerProperties;
    }

    public void navigateBack() {
        logger.info("navigating back ");
        driver.navigate().back();
    }

    public boolean textDisplayed(String text) {
        logger.info("inspecting platform name as " + driver.getPlatformName());
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            logger.info("waiting for the presence of element " + text + " when platform is iOS");
            waitVar.until(ExpectedConditions.presenceOfElementLocated(MobileBy.name(text)));
            logger.info("text is displayed ");
            return true;
        } else if (driver.getPlatformName().equalsIgnoreCase("android")) {
            String textNew = text.substring(0, 1).toUpperCase() + text.substring(1);
            String selector = "new UiSelector().textContains(" + "\"" + textNew + "\"" + ")";
            logger.info("waiting for the presence of element " + text + " when platform is android");
            waitVar.until(ExpectedConditions.presenceOfElementLocated(MobileBy.AndroidUIAutomator(selector)));
            logger.info("text " +text+ " is displayed");
            return true;
        }
        logger.info("text " +text+ " is not displayed");
        return false;
    }

    public boolean containTextDisplayed(String text) {
        waitForLoadingComplete();
        WebElement element;
        logger.info("inspecting platform name " + driver.getPlatformName());
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            logger.info("get ios element");
            String iosText = "type == 'XCUIElementTypeStaticText' AND name CONTAINS "
                    + "'"
                    + text
                    + "'";
            element = waitVar.until(ExpectedConditions.presenceOfElementLocated(MobileBy.iOSNsPredicateString(iosText)));
            logger.info("text " + text + " is not displayed");
            return false;
        } else {
            logger.info("get android element ");
            String selector = "new UiSelector().textContains(" + "\"" + text + "\"" + ")";
            element = waitVar.until(ExpectedConditions.presenceOfElementLocated(MobileBy.AndroidUIAutomator(selector)));
            logger.info("text " + text + " is not displayed");
            return false;
        }
    }

    public void enterKey(MobileElement element, String keyValue) {
        element.click();
        logger.info("inspecting platform name " + driver.getPlatformName());
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {

            try {
                if (AppiumDeviceManager.getDevice().getDeviceType().equalsIgnoreCase("cloud")) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(1500);
                }
                logger.info("performing tap action for element "+element+"and enters key value as "+keyValue);
                TouchAction action = new TouchAction(driver);
                action.tap(TapOptions.tapOptions().withElement(ElementOption.element(element)).withTapsCount(2)).perform();
                element.sendKeys(Keys.DELETE);
                if (AppiumDeviceManager.getDevice().getDeviceType().equalsIgnoreCase("cloud")) {
                    Thread.sleep(500);
                } else {
                    Thread.sleep(1000);
                }
                element.sendKeys(keyValue + "\n");
            } catch (InterruptedException e) {
                // do nothing
                logger.error(e.getMessage());
            }
        } else {
            element.clear();
            element.sendKeys(keyValue);
            if (AppiumDeviceManager.getDevice().getDeviceType().equalsIgnoreCase("local")) {
                Actions action = new Actions(driver);
                action.sendKeys(Keys.ENTER).perform();
            } else {
                Map<String, Object> EnterKeyEvent = new HashMap<>();
                EnterKeyEvent.put("key", "66");
                driver.executeScript("mobile:key:event", EnterKeyEvent);
            }
        }
    }

    public void pressEnterAndroid() {
        if (AppiumDeviceManager.getDevice().getDeviceType().equalsIgnoreCase("local")) {
            Actions action1 = new Actions(driver);
            logger.info("sending keys ENTER");
            action1.sendKeys(Keys.ENTER).perform();
        } else {
            Map<String, Object> EnterKeyEvent = new HashMap<>();
            logger.info("passing key value as 66");
            EnterKeyEvent.put("key", "66");
            logger.info("performing ENTER action");
            driver.executeScript("mobile:key:event", EnterKeyEvent);
        }
    }

    // Method to tap on search or enter(next) on keyboard for android
    public void tapSearchEnterAndroid() {
        logger.info("get height");
        int height = driver.findElement(By.id("drawer_layout")).getSize().getHeight();
        logger.info("get width");
        int width = driver.findElement(By.id("drawer_layout")).getSize().getWidth();
        logger.info("get the layout of search or next button on keyboard for android");
        int searchX = driver.findElement(By.id("drawer_layout")).getLocation().getX() + width - 10;
        int searchY = driver.findElement(By.id("drawer_layout")).getLocation().getY() + height - 20;

        TouchAction touchAction = new TouchAction(driver);
        logger.info("performing tap action");
        touchAction.tap(PointOption.point(searchX, searchY)).perform();
    }

    public String getTextFromTextField(MobileElement element) {
        String val = "";
        logger.info("inspecting platform name as " + driver.getPlatformName());
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            TouchAction action = new TouchAction(driver);
            action.tap(TapOptions.tapOptions().withElement(ElementOption.element(element)).withTapsCount(2)).perform();
            driver.findElement(By.name("Copy")).click();
            logger.info("storing the value of clipboard text");
            val = ((IOSDriver) driver).getClipboardText();
            //val= ((HasClipboard) driver).getClipboardText();


        }
        logger.info("returning the text from the text field " + element);
        return val;
    }

    public void enterTextNoPressEnter(MobileElement element, String keyValue) {
        logger.info("clicking element " + element);
        element.click();
        logger.info("inspecting platform name as " + driver.getPlatformName());
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            try {
                if (AppiumDeviceManager.getDevice().getDeviceType().equalsIgnoreCase("cloud")) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(1500);
                }
                TouchAction action = new TouchAction(driver);
                logger.info("performing tap action for element " + element);
                action.tap(TapOptions.tapOptions().withElement(ElementOption.element(element)).withTapsCount(2)).perform();
                logger.info("passing DELETE keys ");
                element.sendKeys(Keys.DELETE);
                if (AppiumDeviceManager.getDevice().getDeviceType().equalsIgnoreCase("cloud")) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(1500);
                }
                logger.info("passing the value that needs to be entered in text field " + keyValue);
                element.sendKeys(keyValue);
            } catch (InterruptedException e) {
                // do nothing
            }
        } else {
            logger.info("passing the value that needs to be entered in text field " + keyValue);
            element.sendKeys(keyValue);
        }
    }

    public void waitForDisplayed(MobileElement element) {
        logger.info("waiting for visibility of element " + element);
        waitVar.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForDisplayed(MobileElement element, int waitTime) {
        logger.info("waiting " + waitTime + " seconds for element " + element);
        new FluentWait<>(driver).withTimeout(Duration.ofSeconds(waitTime)).pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class).until(ExpectedConditions.visibilityOf(element));
    }

    public void scrollDown() {
        logger.info("inspecting platform name as " + driver.getPlatformName());
        if (driver.getPlatformName().equalsIgnoreCase("android")) {
            int pressX = driver.manage().window().getSize().width / 3;
            int bottomY = (driver.manage().window().getSize().height * 3) / 4;
            int topY = driver.manage().window().getSize().height / 4;
            logger.info("scrolling down");
            scroll(pressX, bottomY, pressX, topY);
        } else {
            int pressX = driver.manage().window().getSize().width / 3;
            int bottomY = (driver.manage().window().getSize().height * 3) / 4;
            int topY = driver.manage().window().getSize().height / 2;
            logger.info("scrolling down");
            scroll(pressX, bottomY, pressX, topY);
        }
    }

    public void scrollUp() {
        Dimension size = driver.manage().window().getSize();
        int starty = size.height / 3;
        int endy = (size.height * 5) / 6;
        int startx = size.width / 2;

        // Swipe from Bottom to Top
        TouchAction touchAction = new TouchAction(driver);
        logger.info("scrolling up");
        touchAction.longPress(PointOption.point(startx, starty)).moveTo(PointOption.point(startx, endy)).release().perform();
    }

    public void scrollDownForDeliverySlot() {
        logger.info("inspecting platform name as " + driver.getPlatformName());
        if (driver.getPlatformName().equalsIgnoreCase("android")) {
            int pressX = driver.manage().window().getSize().width / 3;
            int bottomY = (int) (driver.manage().window().getSize().height / 1.5);
            int topY = driver.manage().window().getSize().height / 3;
            logger.info("scrolling down for delivery slot");
            scroll(pressX, bottomY, pressX, topY);
        } else {
            int pressX = driver.manage().window().getSize().width / 2;
            int bottomY = driver.manage().window().getSize().height * 2 / 5;
            int topY = driver.manage().window().getSize().height / 8;
            logger.info("scrolling down for delivery slot");
            scroll(pressX, bottomY, pressX, topY);
        }
    }

    public void scrollDown(int count) {
        for (int i = 0; i < count; i++) {
            int pressX = driver.manage().window().getSize().width / 2;
            int bottomY = driver.manage().window().getSize().height * 2 / 5;
            int topY = driver.manage().window().getSize().height / 8;
            logger.info("scrolling down");
            scroll(pressX, bottomY, pressX, topY);
        }
    }

    public void swipeLeft() {
        int fromX = driver.manage().window().getSize().width * 2 / 4;
        int toX = driver.manage().window().getSize().width / 10;
        int fromY = driver.manage().window().getSize().height / 2;
        logger.info("swiping left");
        scroll(fromX, fromY, toX, fromY);
    }

    public void scrollTop() {
        int pressX = driver.manage().window().getSize().width / 2;
        int bottomY = driver.manage().window().getSize().height * 4 / 5;
        int topY = driver.manage().window().getSize().height / 8;
        logger.info("scrolling top");
        scroll(pressX, topY, pressX, bottomY);
    }

    public void swipeLeftOnElement(MobileElement element) {
        logger.info("swipe left on element" +element);
        scroll(element.getCenter().getX(), element.getCenter().getY(), 0, element.getCenter().getY());
    }

    public boolean scrollDownForElement(WebElement element) {
        for (int i = 0; i < 20; i++) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
            }
            logger.info("scrolling down till you find" +element);
            scrollDown();
        }
        logger.info("element " +element + " not found");
        return false;
    }

    public boolean scrollDownForElement(WebElement element, int numberOfScrolls) {
        for (int i = 0; i < numberOfScrolls; i++) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
            }
            logger.info("scrolling down " + numberOfScrolls + " number of times to find element " + element);
            scrollDown();
        }
        logger.info("element " +element+ " not found after scrolling " + numberOfScrolls + " number of times");
        return false;
    }

    public boolean scrollUpForElement(WebElement element, int numberOfScrolls) {
        for (int i = 0; i < numberOfScrolls; i++) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
            }
            logger.info("scrolling up till you find " +element);
            scrollUp();
        }
        return false;
    }

    public boolean scrollDownForElement(String xpath) {
        for (int i = 0; i < 20; i++) {
            try {
                WebElement ele = driver.findElement(By.xpath(xpath));
                if (ele.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
            }
            logger.info("scrolling down till you find " +xpath);
            scrollDown();
        }
        return false;
    }

    public boolean swipeLeftForElement(String xpath) {
        for (int i = 0; i < 10; i++) {
            try {
                WebElement ele = driver.findElement(By.xpath(xpath));
                if (ele.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
            }
            logger.info("swiping left till you find " + xpath);
            swipeLeft();
        }
        return false;
    }

    public boolean scrollDownForElement(String xpath, int scrollCount) {
        for (int i = 0; i < scrollCount; i++) {
            try {
                WebElement ele = driver.findElement(By.xpath(xpath));
                if (ele.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
            }
            logger.info("scrolling down " + scrollCount + " number of times to find xpath " + xpath);
            scrollDown();
        }
        logger.info("xpath" +xpath+ " not found after scrolling " + scrollCount + " number of times");
        return false;
    }

    public boolean scrollUpForElement(By by, int scrollCount) {
        for (int i = 0; i < scrollCount; i++) {
            try {
                WebElement ele = driver.findElement(by);
                if (ele.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
            }
            logger.info("scrolling up " + scrollCount + " number of times to find element using BY " + by);
            scrollUp();
        }
        logger.info("element not found after scrolling " + scrollCount + " number of times using BY ");
        return false;
    }

    public boolean scrollUpForElement(WebElement element) {
        for (int i = 0; i < 20; i++) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
            }
            logger.info("scroll up until you find element " + element);
            scrollUp();
        }
        return false;
    }

    public void clickElement(WebElement ele) {
        int count = 0;
        while (count < 3) {
            try {
                logger.info("waiting for element " + ele);
                WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_ELEMENT_TIMEOUT);
                MobileElement element = (MobileElement) wait.until(ExpectedConditions.visibilityOf(ele));
                if (element.isDisplayed()) {
                    logger.info("Clicking element " + ele);
                    element.click();
                    break;
                }
            } catch (StaleElementReferenceException e) {
                //StaleElementReferenceException caught, trying again..
                logger.error(e.getMessage());
            }
            count++;
        }
    }

    public MobileElement findElementByName(String name) {
        logger.info("getting device name");
        AppiumDevice device = AppiumDeviceManager.getDevice();
        logger.info("inspecting platform name as " + driver.getPlatformName());
        if (device.getPlatform().equalsIgnoreCase("ios")) {
            logger.info("finding element by name " +name);
            return (MobileElement) driver.findElementByName(name);
        } else {
            ArrayList<MobileElement> elementList = (ArrayList<MobileElement>) driver.findElementsByClassName("android.widget.TextView");
            for (MobileElement element : elementList) {
                if (element.getText().equalsIgnoreCase(name)) {
                    logger.info("finding element by name " +name);
                    return element;
                }
            }
        }
        logger.info("no element found using name" + name + "returning" + null );
        return null;
    }

    public MobileElement findElementByLabel(String label) {
        String xpath = "";
        AppiumDevice device = AppiumDeviceManager.getDevice();
        logger.info("inspecting platform name as " + driver.getPlatformName());
        if (device.getPlatform().equalsIgnoreCase("ios")) {

            logger.info("finding element using label " + label);
            new FluentWait<>(driver).withTimeout(Duration.ofSeconds(5)).pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class).until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.iOSNsPredicateString(String.format("label BEGINSWITH '%s' and visible==1", label)))));
            try {
                return (MobileElement) driver.findElement(MobileBy.iOSNsPredicateString(String.format("label BEGINSWITH '%s' and visible==1", label)));
            } catch (Exception e) {
                return (MobileElement) driver.findElement(MobileBy.iOSNsPredicateString(String.format("label=='%s'", label)));

            }
        } else {
            // label==text here
            ArrayList<MobileElement> elementList = (ArrayList<MobileElement>) driver.findElementsByClassName("android.widget.TextView");
            for (MobileElement element : elementList) {
                if (element.getText().equalsIgnoreCase(label.trim())) {
                    logger.info("finding element using label " + label);
                    return element;
                }
            }
        }


        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            scrollDownForElement(driver.findElement(MobileBy.iOSNsPredicateString(String.format("label BEGINSWITH '%s'", label))));
            logger.info("finding element using label " + label);
            return (MobileElement) driver.findElement(MobileBy.iOSNsPredicateString(String.format("label BEGINSWITH '%s'", label)));
        } else {
            xpath = String.format("//*[@text='%s']", label);
            scrollDownForElement(driver.findElement(MobileBy.xpath(xpath)));
            logger.info("finding element using label " + label);
            return (MobileElement) driver.findElement(MobileBy.xpath(xpath));
        }
    }

    public void scroll(int fromX, int fromY, int toX, int toY) {
        logger.info("scroll from point ("+fromX+","+fromX+") to point ("+toX+","+toY+")");
        TouchAction touchAction = new TouchAction(driver);
        touchAction.longPress(PointOption.point(fromX, fromY)).moveTo(PointOption.point(toX, toY)).release().perform();
    }

    public void scrollDownBySmallAmount() {
        int pressX = driver.manage().window().getSize().width / 2;
        int bottomY = driver.manage().window().getSize().height * 2 / 5;
        int topY = driver.manage().window().getSize().height / 4;
        logger.info("scrolling down by small amount");
        scroll(pressX, bottomY, pressX, topY);
    }


    public void enterFromKeyboard(MobileElement element, String keyValue) {
        logger.info("clicking element" +element);
        element.click();
        logger.info("enter key value "+keyValue+" to element "+element);
        driver.getKeyboard().pressKey(keyValue);
        driver.getKeyboard().pressKey(Keys.ENTER);
    }

    public void doubleClick(MobileElement element) {
        logger.info("clicking on element "+element+ "once");
        clickElement(element);
        logger.info("clicking on element "+element+ "twice");
        clickElement(element);
    }

    public void deleteCurrentTextInEditText(MobileElement element) {
        logger.info("clicking element" +element);
        clickElement(element);
        String temp = "";
        int stringLength = element.getText().length();
        for (int i = 0; i < stringLength; i++) {
            temp = temp + "\b";
        }
        logger.info("deleting current text in edit text field element "+element);
        element.sendKeys(temp);
    }

    /* public void tapIOSCoordinate() {
         IOSTouchAction touch = new IOSTouchAction (driver);
         touch.tap (TapOptions.tapOptions ()
                 .withElement (ElementOption.element (e)))
                 .perform ();
     }*/
    public void tapCoordinate(int x, int y) {
        logger.info("taping co-ordinates ("+x+","+y+")");
        TouchAction touch = new TouchAction(driver);
        touch.tap(new PointOption().withCoordinates(x, y)).perform();
    }

    public void tapElementCoordinate(MobileElement element) {
        TouchAction touch = new TouchAction(driver);
        Point point = element.getCenter();
        logger.info("getting " +element+ "Coordinate and performing tap action");
        touch.tap(new PointOption().withCoordinates(point)).perform();
    }

    public void tapSearchAndroidKeyboard() {
        Map<String, Object> params = new HashMap<>();
        params.put("keySequence", "KEYBOARD_SEARCH");
        logger.info("tapping search button in android keyboard");
        driver.executeScript("mobile:presskey", params);
//        Dimension dimension = driver.manage().window().getSize();
//        int height = (int) (dimension.getHeight() * .95);
//        int width = (int) (dimension.getWidth() * .92);
//        tapCoordinate(width, height);
    }

    public void tapEnterAndroidKeyboard() {
        Dimension dimension = driver.manage().window().getSize();
        int height = (int) (dimension.getHeight() * .97);
        int width = (int) (dimension.getWidth() * .90);
        logger.info("tapping enter button in android keyboard");
        tapCoordinate(width, height);
    }

    public void tapElement(MobileElement element) {
        logger.info("tapping element "+element);
        TouchAction touch = new TouchAction(driver);
        touch.tap(
                new TapOptions().withElement(
                        ElementOption.element(
                                element)))
                .perform();

    }

    public void waitForLoadingComplete() {
        logger.info("waiting for loading to complete");
        waitVar.until(ExpectedConditions.invisibilityOfElementLocated((By.xpath("//AndroidLoading | //iOSLoading"))));
    }

    public void androidKeyboardClickDone() {
        logger.info("performing done action in android keyboard");
        driver.executeScript("mobile: performEditorAction", ImmutableMap.of("action", "Done"));
    }

    public String getmfaexistingAccountUser() {
        logger.info("getting mfa existing account user");
        return driver.getCapabilities().getCapability("USERNAME_MFA").toString();
    }


    public String getCurrentEnv() {
        logger.info("getting current environment");
        return driver.getCapabilities().getCapability("env").toString();
    }

    public String getPlatformName() {
        logger.info("getting current platform anme");
        return driver.getPlatformName();
    }

    public void closeAndRelaunch() throws InterruptedException {
        logger.info("closing and relaunching app");
        appRelaunchHelper.closeAndRelaunchApp();

    }

    public void relaunch() throws InterruptedException {
        logger.info("relaunching app");
        driver.launchApp();
    }

    public void closeApp() {
        logger.info("closing app");
        driver.closeApp();
    }

    public void waitForClickableAndClick(MobileElement element) {
        logger.info("waiting for "+element+ "for clickable and after clicking");
        waitVar.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    public void waitForTextPresentInElement(MobileElement element, String text) {
        logger.info("wait for text "+text+ "to be present in element "+element);
        waitVar.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public void hideKeyBoard() {
        logger.info("hiding keyboard");
        driver.hideKeyboard();
    }

    public void showKeyBoard() {
        logger.info("showing keyboard");
        driver.getKeyboard();
    }

    public void androidNavigateBack() {
        logger.info("navigating back for android");
        if (driver.getPlatformName().equalsIgnoreCase("android")) {
            navigateBack();
        }
    }

    public void clickTextOnScreen(String arg0) {
        if (driver.getPlatformName().equalsIgnoreCase("android")) {
            String text = "new UiSelector().text(\"" + arg0 + "\")";
            scrollDownForElement(driver.findElement(MobileBy.AndroidUIAutomator(text)));
            logger.info("clicking string "+arg0+ "on screen");
            clickElement(driver.findElement(MobileBy.AndroidUIAutomator(text)));
        } else {
            scrollDownForElement(driver.findElementByName(arg0));
            logger.info("clicking string "+arg0+ "on screen");
            clickElement(driver.findElementByName(arg0));
        }
    }

    public void clickButtonText(String button) {
        MobileElement element;
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            element = (MobileElement) driver.findElementByName(button);
        } else {
            String selector = "new UiSelector().textContains(" + "\"" + button + "\"" + ")";
            element = (MobileElement) driver.findElement(MobileBy.AndroidUIAutomator(selector));
        }
        logger.info("clicking button "+button+ "text on screen");
        element.click();
    }

    public boolean isElementDisplayed(MobileElement ele) {
        boolean found = false;
        try {
            WebDriverWait wait = new WebDriverWait(driver, WAIT_FOR_ELEMENT_TIMEOUT);
            MobileElement element = (MobileElement) wait.until(ExpectedConditions.visibilityOf(ele));
            if (element.isDisplayed()) {
                found = true;
            }
        } catch (Exception e) {
            found = false;
        }
        logger.info("element "+ele+ "is displayed");
        return found;
    }

    public boolean isElementDisplayed(MobileElement ele, int waitTime) {
        boolean found = false;
        logger.info("waiting for element "+ele+ "for "+waitTime+"seconds");
        try {
            WebDriverWait wait = new WebDriverWait(driver, waitTime);
            MobileElement element = (MobileElement) wait.until(ExpectedConditions.visibilityOf(ele));
            if (element.isDisplayed()) {
                found = true;
            }
        } catch (Exception e) {
            found = false;
        }
        logger.info("element "+ele+ "is displayed");
        return found;
    }

    public void waitForElementToDisappear(MobileElement ele, int waitTime) {
        logger.info("waiting "+waitTime+"seconds for element "+ele+ "to disappear");
        for (int i = 0; i < waitTime; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, 1);
                wait.until(ExpectedConditions.visibilityOf(ele));

            } catch (TimeoutException | StaleElementReferenceException | NoSuchElementException e) {
                logger.error(e.getMessage());
                return;
            }
        }
    }

    public void waitForElementToDisappear(String xpath, int waitTime) {
        logger.info("waiting "+waitTime+"seconds for xpath "+xpath+ "to disappear");
        for (int i = 0; i < waitTime; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, 1);
                wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpath))));
            } catch (Exception e) {
                logger.error(e.getMessage());
                return;
            }
        }
    }


    public void moveAppToBackground() {
        logger.info("moving app to background");
        driver.runAppInBackground(Duration.ofSeconds(APP_BACKGROUND_TIME));
        if (driver.getPlatformName().equalsIgnoreCase("android")) {
            if (driver.getOrientation().toString().equalsIgnoreCase("landscape")) {
                driver.rotate(ScreenOrientation.PORTRAIT);
            }
        }
    }


    public void enterValue(MobileElement element, String keyValue) {
        logger.info("clicking element "+element);
        element.click();
        logger.info("sending key value "+keyValue+"to element "+ element);
        element.sendKeys(keyValue);
    }

    public void waitforUMAhomeScreen(MobileElement element) {
        logger.info("waiting for home screen using element " +element);
        waitVar.until(ExpectedConditions.invisibilityOf(element));
    }

    public String getText(MobileElement targetElement) {
        logger.info("wait for "+targetElement);
        waitForDisplayed(targetElement);
        logger.info("get text from element "+targetElement);
        return targetElement.getText();
    }


    public void pleaseWaitForTheLoadingToComplete() {
        logger.info("waiting for the loading time to complete");
        try {
            new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15)).pollingEvery(Duration.ofSeconds(3)).ignoring(NoSuchElementException.class).until((Function<WebDriver, WebElement>) driver -> driver.findElement(By.xpath("//*[contains(@text,'Please')]")));
        } catch (TimeoutException e) {
            logger.error(e.getMessage());
            //Element is not visible
        }
    }

    public void refreshScreen() {
        logger.info("refreshing the screen");
        int deviceWidth = driver.manage().window().getSize().getWidth();
        int deviceHeight = driver.manage().window().getSize().getHeight();
        int midX = (deviceWidth / 2);
        int midY = (deviceHeight / 2);
        int bottomEdge = (deviceHeight * 95) / 100;
        new TouchAction(driver)
                .press(point(midX, midY))
                .waitAction(waitOptions(ofMillis(2000)))
                .moveTo(point(midX, bottomEdge))
                .release().perform();
    }

    public void waitForLoaderToDisappear() {
        logger.info("waiting for loader to disappear");
        try {
            if (driver.getPlatformName().equalsIgnoreCase("android")) {
                waitForElementToDisappear((MobileElement) driver.findElement(By.className("android.widget.ProgressBar")), 60);
            } else {
                waitForElementToDisappear((MobileElement) driver.findElement(By.id("In progress")), 60);
            }
        } catch (NoSuchElementException e) {
            logger.error("An element could not be located on the page using the given search parameters.");
            return;
        }
    }

    public void waitForElementToBeClickable(MobileElement element, int secondsToWait) {
        logger.info("waiting for element "+element+ "for "+secondsToWait+"seconds");
        try {
            new FluentWait<>(driver).withTimeout(Duration.ofSeconds(secondsToWait)).pollingEvery(Duration.ofMillis(250)).ignoring(NoSuchElementException.class).until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException te) {
            // Do nothing
        }
    }

    public boolean isMFAEnabled() {
        logger.info("inspecting MFA is enabled");
        if (System.getProperty(CommandArgument.MFA_ENABLED, "true").equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public String generateMFAUserForAhem() {
        logger.info("generating mfa user by using current date and time");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime ldt = LocalDateTime.now();
        logger.info("returning mfa user");
        return String.format("test.%s@hostux.ninja", dtf.format(ldt));
    }

    public boolean scrollDownForElementWithSmallScroll(WebElement element, int numberOfScrolls) {
        for (int i = 0; i < numberOfScrolls; i++) {
            try {
                if (element.isDisplayed()) {
                    logger.info("scrolling down "+numberOfScrolls+"number of time to find an element "+element);
                    return true;
                }
            } catch (Exception e) {
            }
            scrollDownBySmallAmount();
        }
        logger.info("element "+element+ "not found");
        return false;
    }

    public MobileElement waitForElement(By by, int durationInSeconds) {
        logger.info("waiting for "+durationInSeconds+"seconds using By");
        FluentWait<AppiumDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(durationInSeconds)).pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(TimeoutException.class);

        WebElement element = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(by);
            }
        });
        logger.info("returning element "+element+"using By");
        return (MobileElement) element;
    }


    /*************************************************/

    public void waitForVisibility(MobileElement e) {
        logger.info("waiting for visibility of MobileElement " + e );
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOf(e));
    }

    public void clear(MobileElement e) {
        waitForVisibility(e);
        e.clear();
    }

    public void click(MobileElement e) {
        waitForVisibility(e);
        e.click();
        logger.info("Clicked mobileElement " + e);
    }

    public void click(MobileElement e, String msg) {
        waitForVisibility(e);
        logger.info("clicking  - " + msg);
        e.click();
    }


    public void sendKeys(MobileElement e, String txt) {
        waitForVisibility(e);
        logger.info("entering" +txt);
        e.sendKeys(txt);
    }

    public void sendKeys(MobileElement e, String txt, String msg) {
        waitForVisibility(e);
        logger.info(msg);
        e.sendKeys(txt);
    }

    public String getAttribute(MobileElement e, String attribute) {
        waitForVisibility(e);
        return e.getAttribute(attribute);
    }

    public String getText(MobileElement e, String msg) {
        String txt;
        switch(platformName){
            case "android":
                txt = getAttribute(e, "text");
                break;
            case "ios":
                txt = getAttribute(e, "label");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + platformName);
        }
        logger.info(msg + txt);
        return txt;
    }


}
