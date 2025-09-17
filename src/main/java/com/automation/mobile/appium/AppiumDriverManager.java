package com.automation.mobile.appium;

import com.automation.mobile.entities.MobileConfType;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppiumDriverManager {

    private static Logger logger = Logger.getLogger(AppiumDriverManager.class);
    private static ThreadLocal<AppiumDriver> appiumDriver = new ThreadLocal<>();
    private static List<AppiumDriver> listDrivers = new ArrayList<AppiumDriver>();


    public static AppiumDriver getDriver() {

        return appiumDriver.get();
    }

    public AppiumDriver initializeDriver(URL appiumServer, DesiredCapabilities ds) {
        AppiumDevice device = AppiumDeviceManager.getDevice();
        String platForm = device.getConfigureData(MobileConfType.PLATFORM_NAME);
        logger.info("Initializing driver");
        if (platForm.equalsIgnoreCase("android")) {
            logger.info("Initializing driver" + platForm);
            return new AndroidDriver(appiumServer, ds);
        } else {
            logger.info("Initializing driver" + platForm);
            return new IOSDriver(appiumServer, ds);
        }

    }

    public static void setDriver(AppiumDriver driver) {
        appiumDriver.set(driver);
    }

    public static void addDriver(AppiumDriver driver) {
        listDrivers.add(driver);
    }

    public static void closeAllDrivers() {

        for (AppiumDriver driver : listDrivers) {

            driver.quit();
        }
    }

    /**
     * This method is used to capture date in yyyy-MM-dd-HH-mm-ss format
     * @return : return the date in String format
     * @author : Mohammed Haseeb
     */
    public static String dateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
