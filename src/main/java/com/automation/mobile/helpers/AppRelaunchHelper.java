package com.automation.mobile.helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class AppRelaunchHelper {
    public AppiumDriver driver;
    private String iOSNextElement = "(//*[@label='Next'])";
    private String androidNextElement = "(//*[@text='Next'])";


    public AppRelaunchHelper(AppiumDriver driver) {
        this.driver = driver;
    }

    public void closeAndRelaunchApp() throws InterruptedException {
        driver.closeApp();
        Thread.sleep(10000);
        driver.launchApp();
        if (driver instanceof AndroidDriver) {
            for (int i = 0; i < 1; i++) {
                if (driver.findElement(By.xpath(androidNextElement)).isDisplayed()) {
                    driver.findElement(By.xpath(androidNextElement)).click();
                    Thread.sleep(1000);
                } else
                    break;
            }
        } else {
            for (int i = 0; i < 1; i++) {
                if (driver.findElement(By.xpath(iOSNextElement)).isDisplayed()) {
                    driver.findElement(By.xpath(iOSNextElement)).click();
                    Thread.sleep(1000);
                } else
                    break;
            }
        }
    }
}

