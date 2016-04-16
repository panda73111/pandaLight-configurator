package com.blackwhitesoftware.pandalight.remote_control;

import com.blackwhitesoftware.pandalight.Bitfile;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import org.pmw.tinylog.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.Formatter;
import java.util.Observable;
import java.util.TooManyListenersException;

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
        protocol.addConnectionListener(new ConnectionAdapter() {
            @Override
            public void connected() {
                Logger.debug("serial port connected");
                super.connected();
                setChanged();
                notifyObservers();
            }

            @Override
            public void disconnected() {
                Logger.debug("serial port disconnected");
                super.disconnected();
                setChanged();
                notifyObservers();
            }

            @Override
            public void sendingData(byte[] data, int offset, int length) {
                Logger.debug("sending {} bytes of serial data: {}",
                        length, bytesToHex(data, offset, length));

                super.sendingData(data, offset, length);
            }

            @Override
            public void sendingCommand(PandaLightCommand cmd) {
                Logger.debug("sending serial command: {}",
                        bytesToHex(new byte[]{cmd.getByteCommand()}));

                super.sendingCommand(cmd);
            }

            @Override
            public void gotData(byte[] data, int offset, int length) {
                Logger.debug("got {} bytes of serial data: {}",
                        length, bytesToHex(data, offset, length));

                super.gotData(data, offset, length);
            }

            @Override
            public void gotPacket(PandaLightPacket packet) {
                Logger.debug("got packet: {}", packet);

                super.gotPacket(packet);
            }
        });
    }

    private static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, 0, bytes.length);
    }

    private static String bytesToHex(byte[] bytes, int offset, int length) {
        Formatter formatter = new Formatter();
        for (int i = offset; i < length; i++) {
            formatter.format("%02x", bytes[i]);
        }
        return formatter.toString();
    }

    /**
     * Tries to establish a connection
     *
     * @param portName
     * @return true if the connection is established
     */
    public boolean connect(String portName) throws
            PortInUseException, UnsupportedCommOperationException,
            NoSuchPortException, IOException, TooManyListenersException {
        serialConnection.connect(portName);
        return isConnected();
    }

    /**
     * Closes the connection and removes the connectionlistener
     */
    public void disconnect() {
        serialConnection.disconnect();
    }

    public void sendCommand(PandaLightCommand cmd) throws IOException {
        protocol.sendCommand(cmd);
    }

    /**
     * Sends the command to set the led color
     *
     * @param red   value between 0 and 255
     * @param green value between 0 and 255
     * @param blue  value between 0 and 255
     * @return false if there is no connection, true after the command was sent
     * @throws IllegalArgumentException when the parameters don't fit
     */
    public boolean sendLedColor(int red, int green, int blue) throws IllegalArgumentException {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255)
            throw new IllegalArgumentException();

        throw new NotImplementedException();
    }

    public void sendBitfile(byte bitfileIndex, Bitfile bitfile) throws IOException {
        protocol.sendBitfile(bitfileIndex, bitfile);
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
