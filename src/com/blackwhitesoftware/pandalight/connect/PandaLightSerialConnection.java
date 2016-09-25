package com.blackwhitesoftware.pandalight.connect;

import com.blackwhitesoftware.pandalight.Bitfile;
import com.blackwhitesoftware.pandalight.Helpers;
import com.blackwhitesoftware.pandalight.spec.PandaLightSettings;
import jssc.SerialPortException;
import org.pmw.tinylog.Logger;

import java.awt.*;
import java.util.Observable;

/**
 * @author Sebastian Hüther
 *         A high level connection between the GUI and the pandaLight board
 */
public class PandaLightSerialConnection extends Observable {

    private final SerialConnection serialConnection;
    private final PandaLightProtocol protocol;

    public PandaLightSerialConnection() {
        serialConnection = new SerialConnection();
        protocol = new PandaLightProtocol(serialConnection);
        protocol.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected() {
                Logger.debug("serial port connected");
                setChanged();
                notifyObservers();
            }

            @Override
            public void disconnected() {
                Logger.debug("serial port disconnected");
                setChanged();
                notifyObservers();
            }

            @Override
            public void pause() {
                Logger.debug("pausing serial port");
            }

            @Override
            public void unpause() {
                Logger.debug("unpausing serial port");
            }

            @Override
            public void sendingData(byte[] data, int offset, int length) {
                Logger.debug("sending {} bytes of serial data: {}",
                        length, Helpers.bytesToHex(data, offset, length));
            }

            @Override
            public void sendingCommand(PandaLightCommand cmd) {
                Logger.debug("sending serial command: {}",
                        Helpers.bytesToHex(new byte[]{cmd.getByteCommand()}));
            }

            @Override
            public void gotData(byte[] data, int offset, int length) {
                Logger.debug("got {} bytes of serial data: {}",
                        length, Helpers.bytesToHex(data, offset, length));
            }

            @Override
            public void gotPacket(PandaLightPacket packet) {
                Logger.debug("got packet: {}", packet);
            }
        });
    }

    /**
     * Tries to establish a connection
     *
     * @param portName
     * @return true if the connection is established
     */
    public boolean connect(String portName) throws SerialPortException {
        serialConnection.connect(portName);
        return isConnected();
    }

    /**
     * Closes the connection and removes the connectionlistener
     */
    public void disconnect() {
        serialConnection.disconnect();
    }

    public void sendCommand(PandaLightCommand cmd) {
        protocol.sendCommand(cmd);
    }

    public void sendLedColors(Color[] leds) {
        protocol.sendLedColors(leds);
    }

    public void sendBitfile(byte bitfileIndex, Bitfile bitfile) {
        protocol.sendBitfile(bitfileIndex, bitfile);
    }

    public void sendSettings(PandaLightSettings settings) {
        protocol.sendSettings(settings);
    }

    /**
     * Get connection status
     *
     * @return
     */
    public boolean isConnected() {
        return serialConnection.isConnected();
    }

    public void addConnectionListener(ConnectionListener listener) {
        protocol.addConnectionListener(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        protocol.removeConnectionListener(listener);
    }
}
