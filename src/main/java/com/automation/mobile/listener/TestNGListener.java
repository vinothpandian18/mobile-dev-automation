package com.automation.mobile.listener;

import com.automation.mobile.appium.AppiumServerManager;
import org.apache.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;


public class TestNGListener implements ISuiteListener {

    private static Logger logger = Logger.getLogger(TestNGListener.class);

    public void onStart(ISuite iSuite) {
        new AppiumServerManager().startAppiumServer();

    }

    @Override
    public void onFinish(ISuite iSuite) {
        new AppiumServerManager().stopAppiumServer();
    }
}
