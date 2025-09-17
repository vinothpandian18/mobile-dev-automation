package com.automation.mobile.appium;

import com.automation.mobile.entities.AppConfType;
import com.automation.mobile.entities.ConfigType;
import com.automation.mobile.entities.MobileConfType;

import java.util.HashMap;
import java.util.Map;

public class AppiumDevice {

    private Map<String, String> appiumDeviceData = new HashMap<>();

    public AppiumDevice(Map<String, String> deviceData) {
        deviceData.forEach((k, v) -> appiumDeviceData.put(k, v));
    }

    public String getConfigureData(String configureName) {
        return appiumDeviceData.get(configureName);
    }

    public String getMobileName() {
        return appiumDeviceData.get(ConfigType.MOBILE_DEVICE_NAME);
    }

    public String getEnv() {
        return appiumDeviceData.get(ConfigType.APP_ENVIRONMENT);
    }

    public String getUdid() {
        return appiumDeviceData.get(MobileConfType.UDID);
    }

    public String getDeviceName() {
        return appiumDeviceData.get(MobileConfType.DEVICE_NAME);
    }

    public String getDeviceNameActual() {
        return appiumDeviceData.get(MobileConfType.DEVICE);
    }

    public String getPlatform() {
        return appiumDeviceData.get(MobileConfType.PLATFORM_NAME);
    }

    public String getCloudUserName() {
        return appiumDeviceData.get(AppConfType.CLOUD_USER);
    }

    public String getCouldPassword() {
        return appiumDeviceData.get(AppConfType.CLOUD_PASSWORD);
    }

    public String getCloudUrl() {
        return appiumDeviceData.get(AppConfType.CLOUD_SERVER);
    }

    public String getCouldToken() {
        return appiumDeviceData.get(AppConfType.CLOUD_TOKEN);
    }

    public String getCouldApiKey() {

        return appiumDeviceData.get(AppConfType.API_KEY);
    }

    public String getDeviceType() {
        return appiumDeviceData.get(MobileConfType.DEVICE_TYPE);
    }

    public String getApplicationName() {
        return appiumDeviceData.get(MobileConfType.DEVICE_TYPE);
    }

    public String getPlatformVersion() {
        return appiumDeviceData.get(MobileConfType.PLATFORM_VERSION);
    }

    public String getXcodeOrgId() {
        return appiumDeviceData.get(MobileConfType.IOS_XCODE_ORGID);
    }

    public String getXcodeSignId() {
        return appiumDeviceData.get(MobileConfType.IOS_XCODE_SIGNINGID);
    }

    public String getActivityName() {
        return appiumDeviceData.get(AppConfType.ACTIVITY_NAME);
    }

    public String getPackageName() {
        return appiumDeviceData.get(AppConfType.PACKAGE_NAME);
    }

    public String getBundleId() {
        return appiumDeviceData.get(AppConfType.BUNDLE_ID);
    }

    public void setFeatureName(String featureName){
        appiumDeviceData.put("FEATURE", featureName);
    }

    public String getFeatureName(){
        return appiumDeviceData.get("FEATURE");
    }
}
