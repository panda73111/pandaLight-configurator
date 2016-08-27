package com.blackwhitesoftware.pandalight.spec;

import com.blackwhitesoftware.pandalight.PandaLightConfigurationContainer;

/**
 * Created by Sebastian.Huether on 26.08.2016.
 */
public class PandaLightSettings {
    private final byte[] mData;

    public PandaLightSettings(PandaLightConfigurationContainer configuration) {
        mData = new byte[1024];
        mData[0x000] = (byte)configuration.mLedFrameConfig.getHorizontalLedCount();
    }

    public byte[] getData() {
        return null;
    }
}
