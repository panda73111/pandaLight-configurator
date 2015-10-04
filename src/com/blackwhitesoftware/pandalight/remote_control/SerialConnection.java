package com.blackwhitesoftware.pandalight.remote_control;

import java.util.List;
import java.util.Vector;

public class SerialConnection {

    private final List<ConnectionListener> connectionListeners = new Vector<>();

    public SerialConnection() {

    }

    public void connect(String portName) {
        //TODO implement
    }

    public boolean isConnected() {
        //TODO implement
        return false;
    }

    public void disconnect() {
        //TODO implement
        for (ConnectionListener cl : connectionListeners) {
            cl.disconnected();
        }
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
