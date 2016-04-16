package com.blackwhitesoftware.pandalight.remote_control;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hudini on 06.11.2015.
 */
public class SerialEventListener implements SerialPortEventListener {
    private final InputStream in;
    private final List<ConnectionListener> connectionListeners;

    public SerialEventListener(InputStream in, List<ConnectionListener> connectionListeners) {
        this.in = in;
        this.connectionListeners = connectionListeners;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.CTS) {
            Logger.debug("Serial port CTS Event: " + serialPortEvent.getNewValue());
            return;
        }

        try {
            byte[] buffer = new byte[in.available()];
            int readCount = in.read(buffer);
            for (ConnectionListener l : connectionListeners)
                l.gotData(buffer, 0, readCount);
        } catch (IOException e) {
            Logger.trace(e, "error during serial port event");
        }
    }
}
