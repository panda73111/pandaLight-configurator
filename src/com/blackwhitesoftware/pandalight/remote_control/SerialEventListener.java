package com.blackwhitesoftware.pandalight.remote_control;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.pmw.tinylog.Logger;

import java.util.List;

/**
 * Created by hudini on 06.11.2015.
 */
public class SerialEventListener implements SerialPortEventListener {
    private final List<ConnectionListener> connectionListeners;
    private final SerialPort serialPort;

    public SerialEventListener(SerialPort serialPort, List<ConnectionListener> connectionListeners) {
        this.serialPort = serialPort;
        this.connectionListeners = connectionListeners;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isCTS()) {
            Logger.debug("Serial port CTS Event: " + serialPortEvent.getEventValue());
            return;
        }

        if (serialPortEvent.isDSR()) {
            Logger.debug("Serial port DSR Event: " + serialPortEvent.getEventValue());
            return;
        }

        if (!serialPortEvent.isRXCHAR())
            return;

        try {
            byte[] buffer = serialPort.readBytes();
            int readCount = buffer.length;
            for (ConnectionListener l : connectionListeners)
                l.gotData(buffer, 0, readCount);
        } catch (SerialPortException e) {
            Logger.trace(e, "error during serial port event");
        }
    }
}
