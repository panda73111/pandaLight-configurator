package com.blackwhitesoftware.pandalight;

import java.util.Formatter;

/**
 * Created by hudini on 30.04.2016.
 */
public abstract class Helpers {
    public static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, 0, bytes.length);
    }

    public static String bytesToHex(byte[] bytes, int offset, int length) {
        Formatter formatter = new Formatter();
        for (int i = offset; i < length; i++) {
            formatter.format("%02x", bytes[i]);
        }
        return formatter.toString();
    }
}
