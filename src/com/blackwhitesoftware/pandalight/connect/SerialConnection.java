package com.blackwhitesoftware.pandalight.connect;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.pmw.tinylog.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class SerialConnection {

    private final static int BAUDRATE = SerialPort.BAUDRATE_115200;
    private final List<ConnectionListener> connectionListeners = new Vector<>();

    private SerialPort serialPort = null;

    public SerialConnection() {
    }

    public static String[] getSerialPorts() {
        return SerialPortList.getPortNames();
    }

    public synchronized void connect(String portName) throws SerialPortException {
        if (isConnected())
            return;

        Logger.debug("connecting serial port '{}'", portName);
        try {
            serialPort = new SerialPort(portName);
            serialPort.openPort();

            serialPort.setParams(
                    BAUDRATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.setFlowControlMode(
                    SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

            serialPort.setEventsMask(SerialPortEvent.RXCHAR | SerialPortEvent.CTS);
            serialPort.addEventListener(new SerialEventListener(serialPort, connectionListeners));

            for (ConnectionListener listener : connectionListeners) {
                listener.connected();
            }
        } catch (SerialPortException e) {
            Logger.trace(e, "error while opening serial port");
            throw e;
        }
    }

    public boolean isConnected() {
        return serialPort != null;
    }

    public synchronized void disconnect() {
        if (!isConnected())
            return;

        Logger.debug("disconnecting serial port");
        try {
            serialPort.closePort();
        } catch (SerialPortException ignored) {
        }

        serialPort = null;

        for (ConnectionListener listener : connectionListeners) {
            listener.disconnected();
        }
    }

    public void sendData(byte[] data) throws SerialPortException {
        sendData(data, 0, data.length);
    }

    public synchronized void sendData(byte[] data, int offset, int length) throws SerialPortException {
        if (!isConnected())
            return;

        for (ConnectionListener listener : connectionListeners) {
            listener.sendingData(data, offset, length);
        }

        data = Arrays.copyOfRange(data, offset, length);

        try {
            serialPort.writeBytes(data);
        } catch (SerialPortException e) {
            disconnect();
            throw e;
        }
    }

    public int read(byte[] buffer) throws SerialPortException {
        return read(buffer, 0, buffer.length);
    }

    public synchronized int read(byte[] buffer, int offset, int length) throws SerialPortException {
        if (!isConnected())
            return -1;

        byte[] read;

        try {
            read = serialPort.readBytes(length);
        } catch (SerialPortException e) {
            disconnect();
            throw e;
        }

        length = Math.min(length, read.length);
        System.arraycopy(read, 0, buffer, offset, length);

        for (ConnectionListener listener : connectionListeners) {
            listener.gotData(buffer, offset, length);
        }
        return length;
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
