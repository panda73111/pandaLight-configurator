package com.blackwhitesoftware.pandalight.test;

import com.blackwhitesoftware.pandalight.ConfigurationFile;
import com.blackwhitesoftware.pandalight.Main;
import com.blackwhitesoftware.pandalight.spec.*;

public class TestConfigWriter {

    public static void main(String[] pArgs) {
        DeviceConfig deviceConfig = new DeviceConfig();
        LedFrameConstruction frameConfig = new LedFrameConstruction();
        ImageProcessConfig imageConfig = new ImageProcessConfig();
        MiscConfig miscConfig = new MiscConfig();

        deviceConfig.mType = DeviceType.ws2801;
        deviceConfig.mColorByteOrder = ColorByteOrder.BGR;


        ConfigurationFile configFile = new ConfigurationFile();
        configFile.store(deviceConfig);
        configFile.store(frameConfig);
        configFile.store(imageConfig);
        configFile.store(miscConfig);
        configFile.save(Main.configFilename);

        ConfigurationFile configFile2 = new ConfigurationFile();
        configFile2.load(Main.configFilename);
        configFile2.restore(deviceConfig);
        configFile2.restore(frameConfig);
        configFile2.restore(imageConfig);
        configFile2.restore(miscConfig);

        System.out.println(configFile2);
    }
}
