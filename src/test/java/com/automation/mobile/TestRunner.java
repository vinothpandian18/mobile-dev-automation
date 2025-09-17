package com.automation.mobile;

import com.automation.helpers.ThreadLocalHelper;
import com.automation.mobile.appium.AppiumDevice;
import com.automation.mobile.appium.AppiumDeviceManager;
import com.automation.mobile.appium.AppiumDriverManager;
import com.automation.mobile.entities.MobileConfType;
import com.automation.mobile.util.KobitonUtils;
import com.automation.mobile.util.KobitonIntegrate;
import io.cucumber.testng.*;
import org.apache.log4j.Logger;
import org.testng.annotations.*;

@CucumberOptions(
        features = "src/test/resources/Mobile/",
        glue = {"com.automation.steps"},
        plugin = {"pretty",
                "summary",
                "com.automation.mobile.listener.CucumberExtentReportListener:",
                "json:target/cucumber-reports/cucumber.json"},
        monochrome = true
)

public class TestRunner {
    private static Logger logger = Logger.getLogger(TestRunner.class);
    private TestNGCucumberRunner testNGCucumberRunner;

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        this.testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());

    }

    @Test(groups = "cucumber scenarios", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        ThreadLocalHelper.feature.set(pickleWrapper.getPickle());
        this.testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    @DataProvider
    public Object[][] scenarios() {
        return this.testNGCucumberRunner == null ? new Object[0][0] : this.testNGCucumberRunner.provideScenarios();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        AppiumDevice device;
        device = AppiumDeviceManager.getDevice();
        String deviceType = device.getConfigureData(MobileConfType.DEVICE_TYPE);
        if (deviceType.equalsIgnoreCase("cloud")) {
            int version = KobitonIntegrate.versionId;
            KobitonUtils.deleteAppVersion(version);
        }
        try {
            if (AppiumDriverManager.getDriver() != null) {
                AppiumDriverManager.getDriver().quit();
                logger.info("*************stop driver*************");
                AppiumDriverManager.setDriver(null);
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
        if (this.testNGCucumberRunner != null) {
            this.testNGCucumberRunner.finish();
        }
    }
}