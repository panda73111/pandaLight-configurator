package com.blackwhitesoftware.pandalight.test;

import com.blackwhitesoftware.pandalight.ConfigurationFile;
import com.blackwhitesoftware.pandalight.Main;
import com.blackwhitesoftware.pandalight.spec.*;

public class TesConfigWriter {

    public static void main(String[] pArgs) {
        DeviceConfig deviceConfig = new DeviceConfig();
        LedFrameConstruction frameConfig = new LedFrameConstruction();
        ColorConfig colorConfig = new ColorConfig();
        ImageProcessConfig imageConfig = new ImageProcessConfig();
        MiscConfig miscConfig = new MiscConfig();

        deviceConfig.mName = "DAG";
        deviceConfig.mType = DeviceType.lpd6803;
        deviceConfig.mDeviceProperties.put("output", "/dev/null");
        deviceConfig.mDeviceProperties.put("baudrate", 4800);
        deviceConfig.mColorByteOrder = ColorByteOrder.BGR;


        ConfigurationFile configFile = new ConfigurationFile();
        configFile.store(deviceConfig);
        configFile.store(frameConfig);
        configFile.store(colorConfig);
        configFile.store(imageConfig);
        configFile.store(miscConfig);
        configFile.save(Main.configFilename);

        ConfigurationFile configFile2 = new ConfigurationFile();
        configFile2.load(Main.configFilename);
        configFile2.restore(deviceConfig);
        configFile2.restore(frameConfig);
        configFile2.restore(colorConfig);
        configFile2.restore(imageConfig);
        configFile2.restore(miscConfig);

        System.out.println(configFile2);
    }
}
