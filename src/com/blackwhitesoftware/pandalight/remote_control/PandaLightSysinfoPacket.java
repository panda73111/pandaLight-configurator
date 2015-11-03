package com.blackwhitesoftware.pandalight.remote_control;

import java.util.Arrays;

/**
 * Created by sebastian on 30.10.15.
 */
public class PandaLightSysinfoPacket extends PandaLightPacket {
    private byte[] data;
    private byte[] magic;
    private int majorVersion;
    private int minorVersion;

    public PandaLightSysinfoPacket(byte[] data) {
        this.data = data;
        magic = Arrays.copyOfRange(data, 0, 9);
        majorVersion = data[10];
        minorVersion = data[11];
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getMagic() {
        return magic;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }
}
