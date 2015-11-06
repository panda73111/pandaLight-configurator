package com.blackwhitesoftware.pandalight.remote_control;

import gnu.io.*;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class SerialConnection {

    private final static int BAUDRATE = 115200;
    private final static int TIMELIMIT = 2000;
    private final List<ConnectionListener> connectionListeners = new Vector<>();

    private SerialPort serialPort = null;
    private InputStream in = null;
    private OutputStream out = null;

    public SerialConnection() {
    }

    public static String[] getSerialPorts() {
        ArrayList<String> ports = new ArrayList<>();

        Enumeration enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            CommPortIdentifier serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                ports.add(serialPortId.getName());
            }
        }

        return ports.toArray(new String[ports.size()]);
    }

    public synchronized void connect(String portName) throws PortInUseException, IOException, NoSuchPortException, UnsupportedCommOperationException {
        if (isConnected())
            return;

        Logger.debug("connecting serial port '{}'", portName);
        try {
            CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier(portName);
            CommPort port = identifier.open(this.getClass().getName(), TIMELIMIT);
            if (!(port instanceof SerialPort)) {
                Logger.error("the target device is no serial port");
                throw new IOException("Not a serial port");
            }

            serialPort = (SerialPort) port;
            serialPort.setSerialPortParams(
                    BAUDRATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(
                    SerialPort.FLOWCONTROL_RTSCTS_IN |
                            SerialPort.FLOWCONTROL_RTSCTS_OUT
            );

            in = serialPort.getInputStream();
            out = serialPort.getOutputStream();

            for (ConnectionListener listener : connectionListeners) {
                listener.connected();
            }
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | IOException e) {
            Logger.error("error while opening serial port: {}", e.getClass().getSimpleName());
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
        serialPort.close();
        serialPort = null;
        try {
            in.close();
        } catch (IOException ignored) {
        }
        try {
            out.close();
        } catch (IOException ignored) {
        }
        in = null;
        out = null;
        for (ConnectionListener listener : connectionListeners) {
            listener.disconnected();
        }
    }

    public void sendData(byte[] data) throws IOException {
        sendData(data, 0, data.length);
    }

    public synchronized void sendData(byte[] data, int offset, int length) throws IOException {
        if (!isConnected())
            return;

        for (ConnectionListener listener : connectionListeners) {
            listener.sendingData(data, offset, length);
        }

        try {
            out.write(data, offset, length);
        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    public synchronized int read(byte[] buffer, int offset, int length) throws IOException {
        if (!isConnected())
            return -1;

        int readCount;
        try {
            readCount = in.read(buffer, offset, length);
        } catch (IOException e) {
            disconnect();
            throw e;
        }

        for (ConnectionListener listener : connectionListeners) {
            listener.gotData(buffer, offset, readCount);
        }
        return readCount;
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
