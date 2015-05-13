package com.blackwhitesoftware.pandalight.ssh;

import java.util.List;
import java.util.Vector;

public class SerialConnection {

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

	private final List<ConnectionListener> connectionListeners = new Vector<>();

	public void addConnectionListener(ConnectionListener listener) {
		connectionListeners.add(listener);
	}

	public void removeConnectionListener(ConnectionListener listener) {
		connectionListeners.remove(listener);
	}
}
