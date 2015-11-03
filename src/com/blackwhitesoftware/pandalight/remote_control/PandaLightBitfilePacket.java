package com.blackwhitesoftware.pandalight.remote_control;

/**
 * Created by sebastian on 30.10.15.
 */
public class PandaLightBitfilePacket extends PandaLightPacket {
    private byte[] data;

    public PandaLightBitfilePacket(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
