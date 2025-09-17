package com.automation.mobile.listener;

import com.automation.mobile.appium.AppiumDevice;
import com.automation.mobile.appium.AppiumDeviceManager;
import com.automation.mobile.appium.AppiumDriverManager;
import com.automation.mobile.entities.MobileConfType;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoManager {

    private static Logger logger = Logger.getLogger(VideoManager.class);
    /**
     * This method is used for recording the scenario currently capturing recording for failed scenario
     * @author : Mohammed Haseeb
     */
    public void startRecording() {
        logger.info("starting video recording");
        ((CanRecordScreen) new AppiumDriverManager().getDriver()).startRecordingScreen();
    }

    /**
     * This method is used for stop the recording of scenario currently capturing recording for failed scenario
     * @param scenarioName : Passing the scenario name ex: login with invalid username
     * @throws IOException
     * @author : Mohammed Haseeb
     */
    public void stopRecording(String scenarioName) throws IOException {
        logger.info("stopping video recording");
        String media = ((CanRecordScreen) new AppiumDriverManager().getDriver()).stopRecordingScreen();

        AppiumDevice device = AppiumDeviceManager.getDevice();
        String deviceName = device.getConfigureData(MobileConfType.DEVICE_NAME);
        String platForm = device.getConfigureData(MobileConfType.PLATFORM_NAME);
        String platFormVersion = device.getConfigureData(MobileConfType.PLATFORM_VERSION);
        String dateTime = AppiumDriverManager.dateTime();

        String dirPath = "Failed_Scenario" + File.separator + platForm + "_" + platFormVersion + "_" + deviceName + "_" + dateTime +
                File.separator + "Videos";

        File videoDir = new File(dirPath);

        synchronized (videoDir) {
            if (!videoDir.exists()) {
                videoDir.mkdirs();
            }
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(videoDir + File.separator + scenarioName + ".mp4");
            stream.write(Base64.decodeBase64(media));
            stream.close();
           logger.info("video path: " + videoDir + File.separator + scenarioName + ".mp4");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error during video capture" + e.toString());
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }


}
