package com.blackwhitesoftware.pandalight.spec;

/**
 * The device specific configuration
 */
public class DeviceConfig {
    /**
     * The type specification of the device
     */

    public DeviceType mType = DeviceType.ws2801;
    /**
     * The order of the color bytes
     */
    public ColorByteOrder mColorByteOrder = ColorByteOrder.RGB;
}
