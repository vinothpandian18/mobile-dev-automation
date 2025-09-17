package com.automation.mobile.appium;

import com.automation.mobile.entities.AppConfType;
import com.automation.mobile.entities.MobileConfType;
import com.automation.mobile.util.GlobalVar;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;

public class AppiumServerManager {
    private static Logger logger = Logger.getLogger(AppiumServerManager.class);

    private AppiumDriverLocalService createAppiumLocalServer() {
        System.out.println("Starting Appium Server on Localhost");
        AppiumDriverLocalService appiumDriverLocalService;
        HashMap<String, String> environment = new HashMap<>();
        environment.put("PATH", "/usr/local/bin:" + System.getenv("PATH"));
        AppiumServiceBuilder builder =
                new AppiumServiceBuilder()
                        //.withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
                        .withArgument(GeneralServerFlag.RELAXED_SECURITY)
                        .usingAnyFreePort()
                        .withEnvironment(environment).withLogFile(new File("build/logs/AppiumServer.log"));
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "warn");
        appiumDriverLocalService = AppiumDriverLocalService.buildService(builder);
        appiumDriverLocalService.start();
        // Appium server logs does not log in the console during execution
        appiumDriverLocalService.clearOutPutStreams();
        logger.info("Appium Server Started at......"
                + appiumDriverLocalService.getUrl());
        return appiumDriverLocalService;
    }

    public void startAppiumServer() {
        logger.info("Start Appium server");
        for (String mobile : GlobalVar.DEVICE_LIST.keySet()) {
            String deviceType = GlobalVar.DEVICE_LIST.get(mobile).get(MobileConfType.DEVICE_TYPE);
            if (deviceType.equalsIgnoreCase("local")) {
                AppiumDriverLocalService server = createAppiumLocalServer();
                GlobalVar.APPIUM_SERVER_LIST.put(server, "AVAILABLE");
                GlobalVar.DEVICE_LIST.get(mobile).put("AppiumServer", server.getUrl().toString());
            } else {
                GlobalVar.DEVICE_LIST.get(mobile).put("AppiumServer",
                        GlobalVar.DEVICE_LIST.get(mobile).get(AppConfType.CLOUD_SERVER));
            }
        }
    }

    public void stopAppiumServer() {
        logger.info("stop appium server");
        for (AppiumDriverLocalService server : GlobalVar.APPIUM_SERVER_LIST.keySet()) {
            server.stop();
        }
    }
}
