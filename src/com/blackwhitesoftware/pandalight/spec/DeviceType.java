package com.blackwhitesoftware.pandalight.spec;

/**
 * Enumeration of known device types
 */
public enum DeviceType {
    /**
     * WS2801 Led String device with one continuous shift-register (1 byte per color-channel)
     */
    ws2801("WS2801", 0),
    ws2811_fast("WS2811 fast mode", 1),
    ws2811_slow("WS2811 slow mode", 2);

    /**
     * The 'pretty' name of the device type
     */
    private final String mName;

    /**
     * The number representation of this configuration
     */
    private final int mIndex;

    /**
     * Constructs the DeviceType
     *
     * @param name         The 'pretty' name of the device type
     */
    DeviceType(String name, int index) {
        mName = name;
        mIndex = index;
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

    public int getIndex() {
        return mIndex;
    }

    @Override
    public String toString() {
        return mName;
    }
}
