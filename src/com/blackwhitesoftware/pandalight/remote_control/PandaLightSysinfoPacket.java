package com.blackwhitesoftware.pandalight.remote_control;

/**
 * Created by sebastian on 30.10.15.
 */
public class PandaLightSysinfoPacket extends PandaLightPacket {
    private byte[] data;

    public PandaLightSysinfoPacket(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
