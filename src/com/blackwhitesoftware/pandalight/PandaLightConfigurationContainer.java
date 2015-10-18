package com.blackwhitesoftware.pandalight;

import com.blackwhitesoftware.pandalight.spec.*;

import java.util.Vector;

/**
 * The full configuration of Hyperion with sub-items for device, color, grabber-v4l2 and miscelanuous items.
 */
public class PandaLightConfigurationContainer {
    /**
     * The configuration of the output device
     */
    public final DeviceConfig mDeviceConfig = new DeviceConfig();

    /**
     * THe configuration of the 'physical' led frame
     */
    public final LedFrameConstruction mLedFrameConfig = new LedFrameConstruction();

    /**
     * The configuration of the image processing
     */
    public final ImageProcessConfig mProcessConfig = new ImageProcessConfig();

    /**
     * The color adjustment configuration
     */
    public final ColorConfig mColorConfig = new ColorConfig();

    /**
     * The miscellaneous configuration (bootsequence, blackborder detector, etc)
     */
    public final MiscConfig mMiscConfig = new MiscConfig();

    /**
     * The translation of the led frame construction and image processing to individual led configuration
     */
    public Vector<Led> leds;

}
