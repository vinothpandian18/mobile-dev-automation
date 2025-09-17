package com.automation.mobile;

import com.automation.mobile.entities.CommandArgument;
import com.automation.mobile.manager.ConfigFileManager;
import com.automation.mobile.util.GlobalVar;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;


import java.io.IOException;
import java.util.*;

public class MobileRunner {
    private static Logger logger = Logger.getLogger(MobileRunner.class);

    @Test
    public void executeRunner() throws Exception {

        String testType = System.getenv("TEST_TYPE");
//		String testType = "parallel";
        logger.info(testType);

        Map<String, List<String>> parsedArgument = parseCommandArgument();
        GlobalVar.DEVICE_LIST = generateDeviceList(parsedArgument);
        logger.info(GlobalVar.DEVICE_LIST);

        int threadCount = GlobalVar.DEVICE_LIST.size();

        TestNGGenerator testng = new TestNGGenerator(testType, threadCount);
        try {
            testng.runTest();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private Map<String, List<String>> parseCommandArgument() {

        Map<String, List<String>> testParameters = new HashMap<String, List<String>>();
        if (null != System.getProperty(CommandArgument.MOBILE_DEVICES) && !System.getProperty(CommandArgument.MOBILE_DEVICES).isEmpty()) {
            if (System.getProperty(CommandArgument.MOBILE_DEVICES).contains(",")) {
                testParameters.put(CommandArgument.MOBILE_DEVICES,
                        Arrays.asList(System.getProperty(CommandArgument.MOBILE_DEVICES).split(",")));
            } else {
                testParameters.put(CommandArgument.MOBILE_DEVICES,
                        Arrays.asList(System.getProperty(CommandArgument.MOBILE_DEVICES)));
            }

            testParameters.put("environment", Arrays.asList(System.getProperty(CommandArgument.MOBILE_ENVIRONMENT)));
        } else {
            if (System.getenv(CommandArgument.DEVICES).contains(",")) {
                testParameters.put(CommandArgument.MOBILE_DEVICES,
                        Arrays.asList(System.getenv(CommandArgument.DEVICES).split(",")));
            } else {
                testParameters.put(CommandArgument.MOBILE_DEVICES,
                        Arrays.asList(System.getenv(CommandArgument.DEVICES)));
            }

            testParameters.put("environment", Arrays.asList(System.getenv(CommandArgument.ENVIRONMENT)));
        }
        logger.info(testParameters);
        return testParameters;
    }

    public Map<String, Map<String, String>> generateDeviceList(Map<String, List<String>> inputParameters) throws IOException {
        Map<String, Map<String, String>> totalParameters = new HashMap<String, Map<String, String>>();
        List<String> mobileDevices = inputParameters.get("mobileDevice");
        String env = inputParameters.get("environment").get(0);

        for (int i = 0; i < mobileDevices.size(); i++) {
            String mobile = mobileDevices.get(i);
            Map<String, String> deviceParam = new HashMap<>();
            logger.info(mobile);
            deviceParam.putAll(new ConfigFileManager().getMobilePropertyMap(mobile));
            deviceParam.putAll(new ConfigFileManager().getAppPropertyMap(env));
            deviceParam.put("env", env);
            deviceParam.put("STATE", "AVAILABLE");
            deviceParam.put("REGISTERUSER", "");
            totalParameters.put(mobile, deviceParam);
        }
        return totalParameters;
    }
}


