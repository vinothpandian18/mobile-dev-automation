package com.automation.mobile.util;

import io.appium.java_client.service.local.AppiumDriverLocalService;

import java.util.HashMap;
import java.util.Map;

public class GlobalVar {
    public static Map<String, Map<String, String>> DEVICE_LIST = new HashMap<String, Map<String, String>>();

    public static Map<AppiumDriverLocalService, String> APPIUM_SERVER_LIST = new HashMap<>();

    public static Map<String, String> GLOBAL_PARAMS = new HashMap<>();
}
