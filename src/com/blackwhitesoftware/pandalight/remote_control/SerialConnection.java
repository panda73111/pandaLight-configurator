package com.blackwhitesoftware.pandalight.remote_control;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
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

    public static String[] getSerialPorts() {
        ArrayList<String> ports = new ArrayList<>();

        Enumeration enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            CommPortIdentifier serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                ports.add(serialPortId.getName());
            }
        }

        return ports.toArray(new String[ports.size()]);
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
