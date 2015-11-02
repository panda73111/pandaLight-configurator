package com.blackwhitesoftware.pandalight.remote_control;

/**
 * Created by sebastian on 30.10.15.
 */
public class PandaLightSettingsPacket extends PandaLightPacket {
    private byte[] data;

    public PandaLightSettingsPacket(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
