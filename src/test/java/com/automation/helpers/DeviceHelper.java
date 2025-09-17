package com.automation.helpers;

import com.automation.mobile.appium.AppiumDeviceManager;
import com.automation.pages.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.Map;

public class DeviceHelper {
    Logger logger = LogManager.getLogger(this.getClass());
    public static By airplaneBtnIOS = MobileBy.xpath("//XCUIElementTypeSwitch[@name='Airplane Mode']");
    public static By networkAndInternetSettingAndroid = MobileBy.AndroidUIAutomator("new UiSelector().text(\"Network & internet\")");
    public static By wifiStatusAndroid = MobileBy.xpath("//*[@text='Wi‑Fi']//following-sibling::*");
    public static By wifiToggleAndroid = MobileBy.xpath("//android.widget.Switch[@content-desc='Wi‑Fi']");

    public static String settingsBundleIdIOS = "com.apple.Preferences";
    public static String settingsPackageAndroid = "com.android.settings";

    private AppiumDriver driver;
    BasePage basePage;

    public DeviceHelper(AppiumDriver driver) {
        this.driver = driver;
        basePage = new BasePage(driver);
    }

    public void installApp() {
        // Uninstall app if it is already installed before installing again
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            if (driver.isAppInstalled(driver.getCapabilities().getCapability("bundleId").toString())) {
                driver.removeApp(driver.getCapabilities().getCapability("bundleId").toString());
            }
        } else {
            if (driver.isAppInstalled(driver.getCapabilities().getCapability("appPackage").toString())) {
                driver.removeApp(driver.getCapabilities().getCapability("appPackage").toString());
            }
        }

        Map<String, Object> params = new HashMap<>();
        String appName = "";
        if (AppiumDeviceManager.getDevice().getDeviceType().equalsIgnoreCase("local")) {
            appName = driver.getCapabilities().getCapability("app").toString();
        } else {
            appName = driver.getCapabilities().getCapability("cloudAppPath").toString();
        }

        params.put("instrument", "noinstrument");
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            params.put("resign", "true");
        }

        if (driver.getPlatformName().equalsIgnoreCase("android")) {
            driver.installApp(appName);
        } else {
            if (AppiumDeviceManager.getDevice().getDeviceType().equalsIgnoreCase("local")) {
                params.put("app", appName);
                driver.executeScript("mobile:installApp", params);
            } else {
                params.put("file", appName);
                driver.executeScript("mobile:application:install", params);
            }
        }
    }

    public void unInstallApp() {
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            driver.removeApp(driver.getCapabilities().getCapability("bundleId").toString());
        } else {
            driver.removeApp(driver.getCapabilities().getCapability("appPackage").toString());
        }
    }

    public boolean turnOnOffAirplaneModeIOS(String desiredStatus) {
        boolean isDesiredStatusSet = false;
        launchSettingsByPlatform();
        basePage.scrollUpForElement(airplaneBtnIOS, 3);
        basePage.waitForElement(airplaneBtnIOS, 5);
        int expectedAirplaneModeStatus = Integer.parseInt(driver.findElement(airplaneBtnIOS).getAttribute("value"));
        switch (desiredStatus.toUpperCase()) {
            case "ON":
                if (expectedAirplaneModeStatus == 0) {
                    driver.findElement(airplaneBtnIOS).click();
                    logger.info("Airplane mode is turned On");
                    isDesiredStatusSet = true;
                } else {
                    logger.info("Airplane mode is already turned On");
                    isDesiredStatusSet = true;
                }
                break;
            case "OFF":
                if (expectedAirplaneModeStatus == 1) {
                    driver.findElement(airplaneBtnIOS).click();
                    //new GG_DashboardPage(driver).clickOkIfDisplayed();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        // Do Nothing
                    }
                    logger.info("Airplane mode is turned Off");
                    isDesiredStatusSet = true;
                } else {
                    logger.info("Airplane mode is already turned Off");
                    isDesiredStatusSet = true;
                }
                break;
        }

        return isDesiredStatusSet;
    }

    public boolean turnOnOffWifiAndroid(String desiredStatus) {
        boolean isDesiredStatusSet = false;
        launchSettingsByPlatform();
        basePage.scrollUpForElement(networkAndInternetSettingAndroid, 3);
        MobileElement networkEle = basePage.waitForElement(networkAndInternetSettingAndroid, 10);
        networkEle.click();
        basePage.waitForElement(wifiStatusAndroid, 10);
        String currentWifiStatus = driver.findElement(wifiStatusAndroid).getText();
        switch (desiredStatus.toUpperCase()) {
            case "ON":
                if (currentWifiStatus.contains("Off")) {
                    driver.findElement(wifiToggleAndroid).click();
                    logger.info("Wifi is turned On");
                    isDesiredStatusSet = true;
                } else {
                    logger.info("Wifi is already On");
                    isDesiredStatusSet = true;
                }
                break;
            case "OFF":
                if (!currentWifiStatus.contains("Off")) {
                    driver.findElement(wifiToggleAndroid).click();
                    logger.info("Wifi is turned Off");
                    isDesiredStatusSet = true;
                } else {
                    logger.info("Wifi is already Off");
                    isDesiredStatusSet = true;
                }
                break;
        }

        return isDesiredStatusSet;
    }

    public void launchSettingsByPlatform() {
        switch (driver.getPlatformName().toUpperCase()) {
            case "IOS":
                HashMap<String, Object> value = new HashMap<>();
                value.put("bundleId", settingsBundleIdIOS);
                driver.executeScript("mobile: launchApp", value);
                break;
            case "ANDROID":
                driver.activateApp(settingsPackageAndroid);
                break;
        }
    }

    public boolean turnOnInternetByPlatform() {
        boolean isInternetTurnedOn = false;
        switch (driver.getPlatformName().toUpperCase()) {
            case "IOS":
                isInternetTurnedOn = turnOnOffAirplaneModeIOS("OFF");
                break;
            case "ANDROID":
                isInternetTurnedOn = turnOnOffWifiAndroid("ON");
                break;
        }
        return isInternetTurnedOn;
    }

    public boolean turnOffInternetByPlatform() {
        boolean isInternetTurnedOff = false;
        switch (driver.getPlatformName().toUpperCase()) {
            case "IOS":
                isInternetTurnedOff = turnOnOffAirplaneModeIOS("ON");
                break;
            case "ANDROID":
                isInternetTurnedOff = turnOnOffWifiAndroid("OFF");
                break;
        }
        return isInternetTurnedOff;
    }
}
