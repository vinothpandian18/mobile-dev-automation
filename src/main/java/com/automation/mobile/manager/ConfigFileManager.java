package com.automation.mobile.manager;

import com.automation.mobile.entities.FileLocations;

import java.io.IOException;
import java.util.Map;

public class ConfigFileManager {

    public static Map<String, String> getAppPropertyMap(String environment) throws IOException {
        String bannerFilePath = new FileLocations().getAppConfigPath(environment);
        return new FileManager(bannerFilePath).getPropertyInMap();
    }

    public static Map<String, String> getMobilePropertyMap(String mobileDevice) throws IOException {
        String mobileFilePath = new FileLocations().getMobileConfigPath(mobileDevice);
        return new FileManager(mobileFilePath).getPropertyInMap();
    }
}
