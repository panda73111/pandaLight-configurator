package com.blackwhitesoftware.pandalight.remote_control;

import gnu.io.*;

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

    public SerialConnection() {

    }

    public void connect(String portName) {
        try {
            CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier(portName);
            CommPort port = identifier.open(this.getClass().getName(), TIMELIMIT);
            if (!(port instanceof SerialPort)) {
                //TODO error message
                return;
            }

            this.serialPort = (SerialPort) port;
            serialPort.setSerialPortParams(
                    BAUDRATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            InputStream in = serialPort.getInputStream();
            OutputStream out = serialPort.getOutputStream();

            (new Thread(new SerialConnectionReader(in))).start();
            (new Thread(new SerialConnectionWriter(out))).start();
        }
        catch (NoSuchPortException|PortInUseException|UnsupportedCommOperationException|IOException ex) {
            //TODO error message
        }
    }

    public boolean isConnected() {
        return this.serialPort != null;
    }

    public void disconnect() {
        serialPort.close();
        for (ConnectionListener cl : connectionListeners) {
            cl.disconnected();
        }
        this.serialPort = null;
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
