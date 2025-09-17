package com.automation.mobile.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.File;

public class KobitonIntegrate {

    private static Logger logger = Logger.getLogger(KobitonIntegrate.class);
    public static int versionId;


    public static int uploadAppKobiton(String appPath, String appName) {

        logger.info("Step 1: Generate Upload URL");
        File file = new File(appPath);
        appPath = file.getAbsolutePath();

        String jsonData = KobitonUtils.generateUploadURL(appName);
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonData);
        String appPathCloud = jsonObject.getAsJsonPrimitive("appPath").getAsString();
        String appURL = jsonObject.getAsJsonPrimitive("url").getAsString();

        logger.info("Step 2: Upload file to S3");
        KobitonUtils.uploadFileToS3(appPath, appURL);

        logger.info("Step 3: Create an app or app version");
        String appResult = KobitonUtils.createAnAppOrVersion(appName, appPathCloud);
        jsonObject = (JsonObject) JsonParser.parseString(appResult);
        versionId = jsonObject.getAsJsonPrimitive("versionId").getAsInt();
        logger.info("Wait for few seconds to sync data");
        KobitonUtils.sleep(3000);

        logger.info("Step 4: Get App Info");
        KobitonUtils.getAppVersion(versionId);
        /* Does not require information of apps in order to execute scripts. commenting the method*/
        //Common.getApps();
        return versionId;
    }
}