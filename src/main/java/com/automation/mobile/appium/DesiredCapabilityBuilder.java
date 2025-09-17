package com.automation.mobile.appium;

import com.automation.mobile.entities.AppConfType;
import com.automation.mobile.entities.ConfigType;
import com.automation.mobile.entities.FileLocations;
import com.automation.mobile.entities.MobileConfType;
import com.automation.mobile.util.KobitonIntegrate;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;


public class DesiredCapabilityBuilder {

    private static Logger logger = Logger.getLogger(DesiredCapabilityBuilder.class);
    private static ThreadLocal<DesiredCapabilities> desiredCapabilitiesThreadLocal = new ThreadLocal<>();

    public static DesiredCapabilities getDesiredCapability() {
        return desiredCapabilitiesThreadLocal.get();
    }

    private static String getAppPath(AppiumDevice appiumDevice) throws IOException {

        String platform = appiumDevice.getConfigureData(MobileConfType.PLATFORM_NAME);
        String environment = appiumDevice.getConfigureData(ConfigType.APP_ENVIRONMENT);
        String deviceType = appiumDevice.getConfigureData(MobileConfType.DEVICE_TYPE);
        String iosdevice = appiumDevice.getConfigureData(MobileConfType.IOS_DEVICE);
        String appName = appiumDevice.getConfigureData(AppConfType.APPNAME_BUILD);
        String appPath = FileLocations.MOBILE_APP_LOCATION + platform + "/" + environment + "/" + appName;

        if (deviceType.equalsIgnoreCase("cloud")) {
            logger.info("Cloud device");

            if (platform.equalsIgnoreCase("ios")) {

                appPath += ".ipa";

            } else {

                appPath += ".apk";
            }

        } else {
            if ((platform.equalsIgnoreCase("ios"))&&(iosdevice.equalsIgnoreCase("true"))) {

                appPath += ".ipa";
               }

            else if (platform.equalsIgnoreCase("ios")) {

                appPath += ".app";

            } else {

                appPath += ".apk";
            }
        }
        return appPath;
    }

    private static String getAppName(AppiumDevice appiumDevice) {

        String deviceType = appiumDevice.getConfigureData(MobileConfType.DEVICE_TYPE);
        String platform = appiumDevice.getConfigureData(MobileConfType.PLATFORM_NAME);
        String appName = appiumDevice.getConfigureData(AppConfType.APPNAME_BUILD);
        String iosdevice = appiumDevice.getConfigureData(MobileConfType.IOS_DEVICE);

        if (deviceType.equalsIgnoreCase("cloud")) {

            if (platform.equalsIgnoreCase("ios")) {

                appName += ".ipa";

            } else {

                appName += ".apk";
            }
        } else {
            if ((platform.equalsIgnoreCase("ios"))&&(iosdevice.equalsIgnoreCase("true"))) {

                appName += ".ipa";
            }

            else if (platform.equalsIgnoreCase("ios")) {

                appName += ".app";

            } else {

                appName += ".apk";
            }
        }
        return appName;
    }

    public static void buildDesiredCapability(AppiumDevice appiumDevice) throws IOException {
        AppiumDevice ad = appiumDevice;
        String platform = ad.getConfigureData(MobileConfType.PLATFORM_NAME);
        String env = ad.getConfigureData(ConfigType.APP_ENVIRONMENT);
        String deviceType = ad.getConfigureData(MobileConfType.DEVICE_TYPE);
        String appPath = getAppPath(ad);
        String appName = getAppName(ad);
        DesiredCapabilities dc = new DesiredCapabilities();

        logger.info("setting general capabilities");
//		dc.setCapability(MobileCapabilityType.UDID, ad.getConfigureData(MobileConfType.UDID));
//		dc.setCapability(MobileCapabilityType.APPLICATION_NAME, ad.getConfigureData(MobileConfType.APPLICATION_NAME));
        dc.setCapability(MobileCapabilityType.DEVICE_NAME, ad.getConfigureData(MobileConfType.DEVICE_NAME));
        dc.setCapability(MobileCapabilityType.PLATFORM_NAME, ad.getConfigureData(MobileConfType.PLATFORM_NAME));
        dc.setCapability(MobileCapabilityType.PLATFORM_VERSION, ad.getConfigureData(MobileConfType.PLATFORM_VERSION));
        dc.setCapability("enableAppiumBehavior", true);
        dc.setCapability("env", env);
        dc.setCapability(MobileCapabilityType.FULL_RESET, false);
        dc.setCapability(MobileCapabilityType.NO_RESET, true);


        if (platform.equalsIgnoreCase("android")) {
            logger.info(" setting android capabilities");
            dc.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, ad.getConfigureData(AppConfType.PACKAGE_NAME));
            dc.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ad.getConfigureData(AppConfType.ACTIVITY_NAME));
            if (ad.getConfigureData(MobileConfType.AVD) != null) {
                dc.setCapability(AndroidMobileCapabilityType.AVD, ad.getConfigureData(MobileConfType.AVD));
            }
        } else if (platform.equalsIgnoreCase("ios")) {
            dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, ad.getConfigureData(AppConfType.BUNDLE_ID));
        }
        //ios sign capability
        if (platform.equalsIgnoreCase("ios")) {
            logger.info(" setting ios capabilities");
            //dc.setCapability(IOSMobileCapabilityType.XCODE_ORG_ID, ad.getConfigureData(MobileConfType.IOS_XCODE_ORGID));
            //dc.setCapability(IOSMobileCapabilityType.XCODE_SIGNING_ID,ad.getConfigureData(MobileConfType.IOS_XCODE_SIGNINGID));
            dc.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
            dc.setCapability("iOSResign", true);
            dc.setCapability("simpleIsVisibleCheck", true);
            dc.setCapability("wdaLocalPort", wdaLocalPort());
            dc.setCapability("webkitDebugProxyPort", webkitDebugProxyPort());
        }
        if (deviceType.equalsIgnoreCase("cloud")) {
            logger.info(" setting cloud capabilities");
            String user = ad.getConfigureData(AppConfType.CLOUD_USER);
            String password = ad.getConfigureData(AppConfType.CLOUD_PASSWORD);
            dc.setCapability("sessionName", "Automation test session");
            dc.setCapability("sessionDescription", "");
            dc.setCapability("deviceOrientation", "portrait");
            dc.setCapability("captureScreenshots", true);
            dc.setCapability("user", user);
            dc.setCapability("password", password);
            dc.setCapability("securityToken", appiumDevice.getCouldToken());
            dc.setCapability("groupId", 1466); // Group: Team-QA
            dc.setCapability("deviceGroup", "KOBITON");
            int versionId = KobitonIntegrate.uploadAppKobiton(appPath, appName);
            dc.setCapability("app", "kobiton-store:v"+versionId+"");
        } else {
            //local device install app
            dc.setCapability(MobileCapabilityType.UDID, ad.getConfigureData(MobileConfType.UDID));
            dc.setCapability(MobileCapabilityType.APPLICATION_NAME, ad.getConfigureData(MobileConfType.APPLICATION_NAME));
            dc.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120);
            dc.setCapability(MobileCapabilityType.APP, appPath);
        }
        desiredCapabilitiesThreadLocal.set(dc);
        logger.info(desiredCapabilitiesThreadLocal.get().toString());
    }

    /**
     * Generating a unique wdalocalport using randomStringUtils class
     * @return : 4 digit wda port 8100
     */
    public static int wdaLocalPort() {
        String wdaLocal = RandomStringUtils.randomNumeric(4);
        return Integer.parseInt(wdaLocal);
    }

    /**
     * Generating a unique wdalocalport using randomStringUtils class
     * @return : 4 digit webkitDebugProxyPort ex: 1104
     */
    public static int webkitDebugProxyPort() {
        String webkitDebugProxy = RandomStringUtils.randomNumeric(4);
        return Integer.parseInt(webkitDebugProxy);
    }
}
