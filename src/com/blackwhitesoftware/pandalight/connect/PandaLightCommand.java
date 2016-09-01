package com.blackwhitesoftware.pandalight.connect;

/**
 * Created by hudini on 25.10.2015.
 */
public enum PandaLightCommand {
    SYSINFO((byte) 0x00),
    REBOOT((byte) 0x01),
    LOAD_SETTINGS_FROM_FLASH((byte) 0x20),
    WRITE_SETTINGS_TO_FLASH((byte) 0x21),
    LOAD_SETTINGS_FROM_UART((byte) 0x22),
    WRITE_SETTINGS_TO_UART((byte) 0x23),
    LOAD_BITFILE_FROM_UART((byte) 0x40);

    private final byte byteCommand;

    PandaLightCommand(byte byteCommand) {
        this.byteCommand = byteCommand;
    }

    public byte getByteCommand() {
        return byteCommand;
    }
}
