package com.blackwhitesoftware.pandalight.spec;

/**
 * Enumeration of known device types
 */
public enum DeviceType {
    /**
     * WS2801 Led String device with one continuous shift-register (1 byte per color-channel)
     */
    ws2801("WS2801");

    /**
     * The 'pretty' name of the device type
     */
    private final String mName;

    /**
     * Constructs the DeviceType
     *
     * @param name         The 'pretty' name of the device type
     */
    DeviceType(final String name) {
        mName = name;
    }

    public static String listTypes() {
        StringBuilder sb = new StringBuilder();
        for (DeviceType type : DeviceType.values()) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(type.toString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return mName;
    }
}
