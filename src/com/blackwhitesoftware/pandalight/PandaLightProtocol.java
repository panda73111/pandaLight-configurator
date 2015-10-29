package com.blackwhitesoftware.pandalight;

import com.blackwhitesoftware.pandalight.remote_control.SerialConnection;

import java.io.IOException;

/**
 * Created by sebastian on 29.10.15.
 */
public class PandaLightProtocol {
    private SerialConnection serialConnection;

    public PandaLightProtocol(SerialConnection connection) {
        serialConnection = connection;
    }

    public void sendCommand(PandaLightCommand cmd) throws IOException {
        serialConnection.sendData(new byte[]{cmd.byteCommand()});
    }
}
