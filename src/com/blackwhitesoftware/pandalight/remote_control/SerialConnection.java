package com.blackwhitesoftware.pandalight.remote_control;

import com.blackwhitesoftware.pandalight.PandaLightCommand;
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

    public void connect(String portName) throws PortInUseException, IOException, NoSuchPortException, UnsupportedCommOperationException {
        Logger.debug("connecting to serial port '{}'", portName);
        try {
            CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier(portName);
            CommPort port = identifier.open(this.getClass().getName(), TIMELIMIT);
            if (!(port instanceof SerialPort)) {
                //TODO error message
                return;
            }

            serialPort = (SerialPort) port;
            serialPort.setSerialPortParams(
                    BAUDRATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

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

    public void disconnect() {
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

    public void sendCommand(PandaLightCommand cmd) throws IOException {
        sendData(new byte[]{cmd.byteCommand()});
    }

    public void sendData(byte[] data) throws IOException {
        try {
            out.write(data);
        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        try {
            int readCount = in.read(buffer, offset, length);
            for (ConnectionListener listener : connectionListeners) {
                listener.gotData(buffer, offset, readCount);
            }
            return readCount;
        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
