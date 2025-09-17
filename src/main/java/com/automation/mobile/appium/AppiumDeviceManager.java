package com.automation.mobile.appium;


public class AppiumDeviceManager {
    private static ThreadLocal<AppiumDevice> appiumDeviceThreadLocal = new ThreadLocal<>();
    public static AppiumDevice getDevice(){
        return appiumDeviceThreadLocal.get();
    }
    public static void setDevice(AppiumDevice device){
            appiumDeviceThreadLocal.set(device);
    }
}
