package com.blackwhitesoftware.pandalight.spec;

import java.util.Hashtable;

/**
 * The device specific configuration
 */
public class DeviceConfig {

    /**
     * Device (specific) properties
     */
    public final Hashtable<String, Object> mDeviceProperties = new Hashtable<String, Object>();
    /**
     * The name of the device
     */
    public String mName = "MyPi";
    /**
     * The type specification of the device
     */
    public DeviceType mType = DeviceType.ws2801;
    /**
     * The order of the color bytes
     */
    public ColorByteOrder mColorByteOrder = ColorByteOrder.RGB;

}
