package com.blackwhitesoftware.pandalight.connect;

import com.blackwhitesoftware.pandalight.Bitfile;
import com.blackwhitesoftware.pandalight.spec.PandaLightSettings;
import jssc.SerialPortException;
import org.pmw.tinylog.Logger;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Created by sebastian on 29.10.15.
 */
public class PandaLightProtocol {
    //TODO: _REFACTORING_
    private static final int SYSINFO_SIZE = 12;
    private static final int SETTINGS_SIZE = 1024;

    private final SerialConnection serialConnection;
    private final LinkedList<Byte> inDataBuffer = new LinkedList<>();
    private final LinkedList<Byte> outDataBuffer = new LinkedList<>();
    private final List<Class<? extends PandaLightPacket>> expectedPackets = new Vector<>();
    private final List<ConnectionListener> connectionListeners = new Vector<>();
    private final Object receiveLock = new Object();
    private final Object sendLock = new Object();
    private volatile boolean newDataReceived = false;
    private volatile boolean paused = false;
    private volatile boolean serialPaused = false;
    private Thread receiveThread;
    private Thread sendThread;

    public PandaLightProtocol(SerialConnection connection) {
        serialConnection = connection;
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void connected() {
                synchronized (receiveLock) {
                    inDataBuffer.clear();
                }
                synchronized (sendLock) {
                    outDataBuffer.clear();
                }
                expectedPackets.clear();

                startThreads();

                for (ConnectionListener l : connectionListeners)
                    l.connected();
            }

            @Override
            public void disconnected() {
                stopThreads();

                for (ConnectionListener l : connectionListeners)
                    l.disconnected();
            }

            @Override
            public void pause() {
                serialPaused = true;
                pauseSending();

                for (ConnectionListener l : connectionListeners)
                    l.pause();
            }

            @Override
            public void unpause() {
                serialPaused = false;
                unpauseSending();

                for (ConnectionListener l : connectionListeners)
                    l.unpause();
            }

            @Override
            public void sendingData(byte[] data, int offset, int length) {
                for (ConnectionListener l : connectionListeners)
                    l.sendingData(data, offset, length);
            }

            @Override
            public void sendingCommand(PandaLightCommand cmd) {
                for (ConnectionListener l : connectionListeners)
                    l.sendingCommand(cmd);
            }

            @Override
            public void gotData(byte[] data, int offset, int length) {
                synchronized (receiveLock) {
                    for (int i = offset; i < offset + length; i++)
                        inDataBuffer.add(data[i]);

                    newDataReceived = true;
                    receiveLock.notify();
                }

                for (ConnectionListener l : connectionListeners)
                    l.gotData(data, offset, length);
            }

            @Override
            public void gotPacket(PandaLightPacket packet) {
                for (ConnectionListener l : connectionListeners)
                    l.gotPacket(packet);
            }
        };
        serialConnection.addConnectionListener(listener);
    }

    private void startThreads() {
        receiveThread = new Thread(new ReceiveThread(), "receive thread");
        sendThread = new Thread(new SendThread(), "send thread");
        receiveThread.start();
        sendThread.start();
    }

    private void stopThreads() {
        if (receiveThread != null) {
            receiveThread.interrupt();

            try {
                receiveThread.join();
            } catch (InterruptedException ignored) {
            }

            receiveThread = null;
        }

        if (sendThread != null) {
            sendThread.interrupt();

            try {
                sendThread.join();
            } catch (InterruptedException ignored) {
            }

            sendThread = null;
        }
    }

    private void pauseSending() {
        Logger.debug("pausing sending unprioritized data");
        synchronized (sendLock) {
            paused = true;
        }
    }

    private void unpauseSending() {
        Logger.debug("unpausing sending unprioritized data");
        synchronized (sendLock) {
            paused = false;
            sendLock.notify();
        }
    }

    private boolean tryCombinePayloads() {
        if (expectedPackets.size() == 0) {
            return false;
        }

        Class<? extends PandaLightPacket> nextExpectedPacket = expectedPackets.get(0);
        PandaLightPacket packet = null;

        if (nextExpectedPacket == PandaLightSysinfoPacket.class) {
            packet = tryReadSysinfoPacket();
        } else if (nextExpectedPacket == PandaLightSettingsPacket.class) {
            packet = tryReadSettingsPacket();
        }

        if (packet != null) {
            expectedPackets.remove(0);

            for (ConnectionListener l : connectionListeners)
                l.gotPacket(packet);

            return true;
        }
        return false;
    }

    private PandaLightPacket tryReadSysinfoPacket() {
        if (inDataBuffer.size() < SYSINFO_SIZE)
            return null;

        byte[] data = new byte[SYSINFO_SIZE];
        for (int i = 0; i < SYSINFO_SIZE; i++) {
            data[i] = inDataBuffer.getFirst();
            inDataBuffer.removeFirst();
        }
        return new PandaLightSysinfoPacket(data);
    }

    private PandaLightPacket tryReadSettingsPacket() {
        if (inDataBuffer.size() < SETTINGS_SIZE)
            return null;

        byte[] data = new byte[SETTINGS_SIZE];
        for (int i = 0; i < SETTINGS_SIZE; i++) {
            data[i] = inDataBuffer.getFirst();
            inDataBuffer.removeFirst();
        }
        return new PandaLightSettingsPacket(data);
    }

    public void sendCommand(PandaLightCommand cmd) {
        switch (cmd) {
            case SYSINFO:
                expectedPackets.add(PandaLightSysinfoPacket.class);
                break;
            case WRITE_SETTINGS_TO_UART:
                expectedPackets.add(PandaLightSettingsPacket.class);
                break;
        }

        for (ConnectionListener l : connectionListeners)
            l.sendingCommand(cmd);

        sendData(new byte[]{cmd.getByteCommand()});
    }

    public void sendBitfile(byte bitfileIndex, Bitfile bitfile) {
        sendCommand(PandaLightCommand.LOAD_BITFILE_FROM_UART);

        int length = bitfile.getLength();
        sendData(new byte[]{
                bitfileIndex,
                (byte) ((length & 0xFF0000) >> 16),
                (byte) ((length & 0xFF00) >> 8),
                (byte) (length & 0xFF)});

        sendData(bitfile.getData());
    }

    public void sendSettings(PandaLightSettings settings) {
        sendCommand(PandaLightCommand.LOAD_SETTINGS_FROM_UART);
        sendData(settings.getData());
    }

    public void sendLedColors(Color[] leds) {
        sendCommand(PandaLightCommand.RECEIVE_LED_COLORS_FROM_UART);
        sendData(new byte[]{(byte) leds.length});
        for (Color color : leds) {
            byte[] rgb = new byte[]{
                    (byte) color.getRed(),
                    (byte) color.getGreen(),
                    (byte) color.getBlue()
            };
            sendData(rgb);
        }
    }

    private void sendData(byte[] data) {
        sendData(data, 0, data.length);
    }

    private synchronized void sendData(byte[] data, int offset, int length) {
        synchronized (sendLock) {
            for (int i = offset; i < offset + length; i++)
                outDataBuffer.add(data[i]);
            sendLock.notify();
        }
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    private class ReceiveThread implements Runnable {
        @Override
        public void run() {
            Logger.debug("receive thread started");
            while (true) {
                try {
                    synchronized (receiveLock) {
                        while (!newDataReceived)
                            receiveLock.wait();
                        while (tryCombinePayloads()) {
                        }
                        newDataReceived = false;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
            Logger.debug("receive thread ended");
        }
    }

    private class SendThread implements Runnable {
        @Override
        public void run() {
            Logger.debug("send thread started");

            while (true) {
                try {
                    synchronized (sendLock) {
                        int length;

                        while ((length = outDataBuffer.size()) == 0 || paused || serialPaused)
                            sendLock.wait();

                        byte[] data = new byte[length];
                        for (int i = 0; i < length; i++) {
                            data[i] = outDataBuffer.getFirst();
                            outDataBuffer.removeFirst();
                        }

                        serialConnection.sendData(data);
                    }
                } catch (SerialPortException e) {
                    Logger.error("Sending data failed: {}", e.getLocalizedMessage());
                } catch (InterruptedException e) {
                    break;
                }
            }
            Logger.debug("send thread ended");
        }
    }
}
