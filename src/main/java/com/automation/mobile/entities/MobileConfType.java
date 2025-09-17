package com.automation.mobile.entities;

public interface MobileConfType {

    /**
     * standard device configuration.
     */
    String UDID = "udid";
    String APPLICATION_NAME = "applicationName";
    String DEVICE_NAME = "devicename";
    String DEVICE = "device";
    String PLATFORM_VERSION = "platformversion";
    String PLATFORM_NAME = "platformname";
    String DEVICE_TYPE = "devicetype";
    String APPIUM_SERVER = "AppiumServer";

    /**
     * ios device configuration
     */
    String IOS_XCODE_ORGID = "xcodeOrgId";
    String IOS_XCODE_SIGNINGID = "xcodeSigningId";
    String IOS_DEVICE = "realdevice";

    /**
     * emulator configuration.
     */
    String AVD = "avd";


}
