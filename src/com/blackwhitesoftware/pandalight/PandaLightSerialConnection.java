package com.blackwhitesoftware.pandalight;

import com.blackwhitesoftware.pandalight.remote_control.ConnectionAdapter;
import com.blackwhitesoftware.pandalight.remote_control.ConnectionListener;
import com.blackwhitesoftware.pandalight.remote_control.SerialConnection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Observable;

/**
 * @author Sebastian Hüther
 *         A high level connection between the GUI and the pandaLight board
 */
public class PandaLightSerialConnection extends Observable {

    private static boolean printTraffic = true;

    final private SerialConnection serialConnection;
    private final ConnectionListener connectionConsoleListener = new ConnectionAdapter() {
        @Override
        public void connected() {
            if (printTraffic) {
                System.out.println("serial port connected");
            }
            super.connected();
        }

        @Override
        public void disconnected() {
            if (printTraffic) {
                System.out.println("serial port disconnected");
            }
            super.disconnected();
        }
    };
    private boolean wasConnected;

    public PandaLightSerialConnection() {
        serialConnection = new SerialConnection();
        serialConnection.addConnectionListener(connectionConsoleListener);
    }

    /**
     * Convert an int to a hex value as String. Eg. 15 -> 0F , 16 -> 1F
     *
     * @param i
     * @return
     */
    private static String intToTwoValueHex(int i) {
        StringBuffer hex = new StringBuffer(Integer.toHexString(i));
        if (hex.length() == 1) {
            hex.insert(0, "0");
        }
        return hex.toString();
    }

    /**
     * Tries to establish a connection
     *
     * @param portName
     * @return true if the connection is established
     */
    public boolean connect(String portName) {
        serialConnection.connect(portName);

        if (isConnected()) {
            setChanged();
            notifyObservers();
            return true;
        }
        return false;
    }

    /**
     * Closes the connection and removes the connectionlistener
     */
    public void disconnect() {
        if (!isConnected()) {
            return;
        }
        serialConnection.disconnect();
        serialConnection.removeConnectionListener(connectionConsoleListener);
        setChanged();
        notifyObservers();
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
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new IllegalArgumentException();
        }

        return sendLedColor(intToTwoValueHex(red) + intToTwoValueHex(green) + intToTwoValueHex(blue));
    }

    /**
     * Sends the command to set the led color
     *
     * @param hexValues RRGGBB as Hexvalues, eg. FF0000 for 255 0 0
     * @return false if there is no connection, true after the command was sent
     * @throws IllegalArgumentException when the parameters don't fit
     */
    public boolean sendLedColor(String hexValues) throws IllegalArgumentException {

        if (hexValues.length() != 6) {
            throw new IllegalArgumentException();
        }

        if (!isConnected()) {
            return false;
        }

        throw new NotImplementedException();
    }

    /**
     * Get connection status
     *
     * @return
     */
    public boolean isConnected() {
        if (wasConnected != serialConnection.isConnected()) {
            wasConnected = serialConnection.isConnected();
            setChanged();
            notifyObservers();
        }
        return serialConnection.isConnected();
    }

    public void addConnectionListener(ConnectionListener listener) {
        serialConnection.addConnectionListener(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        serialConnection.removeConnectionListener(listener);
    }


}
