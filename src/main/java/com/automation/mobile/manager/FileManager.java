package com.automation.mobile.manager;

import com.automation.mobile.util.KobitonUtils;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileManager {
    private FileInputStream inputStream;
    private static Logger logger = Logger.getLogger(FileManager.class);

    public FileManager(String filePath) throws IOException {
        inputStream = new FileInputStream(filePath);
    }

    public Map<String, String> getPropertyInMap() throws IOException{
        Map<String, String> propertyFileMap = new HashMap<>();
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.forEach((k, v) -> propertyFileMap.put(k.toString(), v.toString()));
        return propertyFileMap;
    }

}
