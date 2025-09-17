package com.automation.steps;


import com.automation.helpers.DeviceHelper;
import com.automation.mobile.appium.AppiumDevice;
import com.automation.mobile.appium.AppiumDeviceManager;
import com.automation.mobile.appium.AppiumDriverManager;
import com.automation.mobile.appium.DesiredCapabilityBuilder;
import com.automation.mobile.entities.MobileConfType;
import com.automation.mobile.listener.VideoManager;

import com.automation.mobile.util.CommonUtil;
import com.automation.mobile.util.GlobalVar;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static com.automation.mobile.appium.AppiumDriverManager.getDriver;

public class HookService {

    AppiumDriver  appiumDriver ;
    AppiumDevice device;
    String deviceType;
    AppiumDevice appiumDevice = AppiumDeviceManager.getDevice();
    private static Logger logger = Logger.getLogger(HookService.class);

    @Before
    public void before(Scenario scenario) throws IOException {
        logger.info("**************hooks*********************" + Thread.currentThread().getId());
        boolean isSessionCreated = false;
        if (appiumDevice == null) {
            synchronized (GlobalVar.DEVICE_LIST) {
                for (String mobile : GlobalVar.DEVICE_LIST.keySet()) {
                    Map<String, String> tempMap = GlobalVar.DEVICE_LIST.get(mobile);
                    if (tempMap.get("STATE").equalsIgnoreCase("available")) {
                        tempMap.put("STATE", "BUSY");
                        logger.info(GlobalVar.DEVICE_LIST);

                        //create appium device
                        appiumDevice = new AppiumDevice(tempMap);
                        AppiumDeviceManager.setDevice(appiumDevice);

                        //create desired capability
                        DesiredCapabilityBuilder.buildDesiredCapability(appiumDevice);
                        logger.info(appiumDevice);
                        logger.info(Thread.currentThread().getId());

                        AppiumDevice device = AppiumDeviceManager.getDevice();
                        String platForm = device.getConfigureData(MobileConfType.PLATFORM_NAME);
                        String deviceType = device.getConfigureData(MobileConfType.DEVICE_TYPE);
                        if (((platForm.equalsIgnoreCase("android")) || (platForm.equalsIgnoreCase("ios")))
                                && (!deviceType.equalsIgnoreCase("cloud"))) {
                            //create appium server
                            AppiumDriver appiumDriver = new AppiumDriverManager().

                                    initializeDriver(new URL(appiumDevice.getConfigureData(MobileConfType.APPIUM_SERVER)),
                                            DesiredCapabilityBuilder.getDesiredCapability());
                            AppiumDriverManager.setDriver(appiumDriver);
                            AppiumDriverManager.addDriver(appiumDriver);

                            //Below code will install the app again, this will ensure execution always starts on signin page
                            try {
                                logger.info("UnInstalling App");
                                new DeviceHelper(getDriver()).unInstallApp();
                                logger.info("Installing App");
                                new DeviceHelper(getDriver()).installApp();
                                getDriver().launchApp();
                            } catch (Exception e) {
                                logger.info("Exception caught while installing the app again, that's ok moving on..");
                            }
                            isSessionCreated = true;
                            break;
                        }else {
                            appiumDriver = new AppiumDriver(new URL(appiumDevice.getConfigureData(MobileConfType.APPIUM_SERVER)),
                                    DesiredCapabilityBuilder.getDesiredCapability());
                            AppiumDriverManager.setDriver(appiumDriver);
                            AppiumDriverManager.addDriver(appiumDriver);
                            isSessionCreated = true;
                            break;
                        }
                    }
                }
            }
        }
        try {
            if ((!isSessionCreated)) {
                getDriver().launchApp();
            }
        }catch (Exception ex ){
            logger.info(ex.getMessage() + "session is not created");
        }
        //Recording a scenario
        device = AppiumDeviceManager.getDevice();
        deviceType = device.getConfigureData(MobileConfType.DEVICE_TYPE);
        if(!deviceType.equalsIgnoreCase("cloud")) {
            new VideoManager().startRecording();
        }

    }

    @After
    public void afterStep(Scenario scenario) throws IOException {

        if (scenario.isFailed()) {
            AppiumDevice device = AppiumDeviceManager.getDevice();
            String deviceName = device.getConfigureData(MobileConfType.DEVICE_NAME);
            String platform = device.getConfigureData(MobileConfType.PLATFORM_NAME);
            String dateTime = AppiumDriverManager.dateTime();
            logger.info("******Capturing screen shot  ************"  + "_" + platform + "_" + deviceName + "_" + dateTime);
            scenario.attach(CommonUtil.captureScreenshot(getDriver()), "image/png",  platform + "_" + deviceName + "_" + dateTime);
            //capturing failed scenario via video
            if(!deviceType.equalsIgnoreCase("cloud")) {
                new VideoManager().stopRecording(scenario.getName());
            }

        }

    }

}

