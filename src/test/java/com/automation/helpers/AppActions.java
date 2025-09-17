package com.automation.helpers;

import com.automation.mobile.appium.AppiumDriverManager;
import com.automation.pages.BasePage;
import org.openqa.selenium.ScreenOrientation;

import java.io.IOException;
import java.time.Duration;

public class AppActions extends BasePage {
    private final int APP_BACKGROUND_TIME = 5;

    public AppActions() {
        super(AppiumDriverManager.getDriver());
    }

    public void terminateApp() throws IOException {
        switch (driver.getPlatformName().toUpperCase()) {
            case "IOS":
                driver.terminateApp(getBannerPropertiesMap().get("bundleId"));
                break;
            case "ANDROID":
                driver.terminateApp(getBannerPropertiesMap().get("packageName"));
                break;
        }
    }

    public void relaunchApp() throws IOException {
        switch (driver.getPlatformName().toUpperCase()) {
            case "IOS":
                driver.activateApp(getBannerPropertiesMap().get("bundleId"));
                break;
            case "ANDROID":
                driver.activateApp(getBannerPropertiesMap().get("packageName"));
                break;
        }
    }

    public void moveAppToBackground() {
        driver.runAppInBackground(Duration.ofSeconds(APP_BACKGROUND_TIME));
        if (driver.getPlatformName().equalsIgnoreCase("android")) {
            if (driver.getOrientation().toString().equalsIgnoreCase("landscape")) {
                driver.rotate(ScreenOrientation.PORTRAIT);
            }
        }
    }

    public void moveAppToBackground(int timeInSeconds) {
        driver.runAppInBackground(Duration.ofSeconds(timeInSeconds));
        if (driver.getPlatformName().equalsIgnoreCase("android")) {
            if (driver.getOrientation().toString().equalsIgnoreCase("landscape")) {
                driver.rotate(ScreenOrientation.PORTRAIT);
            }
        }
    }
}
