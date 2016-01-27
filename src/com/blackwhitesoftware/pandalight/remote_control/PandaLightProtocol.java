package com.blackwhitesoftware.pandalight.remote_control;

import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by sebastian on 29.10.15.
 */
public class PandaLightProtocol {
    //TODO Refactoring!!!

    public static final byte DATA_MAGIC = 0x65;
    public static final byte ACK_MAGIC = 0x66;
    public static final byte RESEND_MAGIC = 0x67;

    public static final int SYSINFO_SIZE = 12;
    public static final int SETTINGS_SIZE = 1024;
    public static final int BITFILE_SIZE = 342816;

    public static final long RESEND_TIMEOUT_MILLIS = 100;
    public static final int MAX_TIMEOUT_RESENDS = 10;

    public static final int MAX_PROTOCOL_ERRORS = 10;

    private final SerialConnection serialConnection;
    private final LinkedList<Byte> inDataBuffer = new LinkedList<>();
    private final byte[][] inPayloadBuffer = new byte[256][];
    private final byte[][] outPacketBuffer = new byte[256][];
    private int outPacketNumber = 0;
    private final Vector<Class<? extends PandaLightPacket>> expectedPackets = new Vector<>();
    private final Timer[] resendTimers = new Timer[256];
    private final List<ConnectionListener> connectionListeners = new Vector<>();

    private final PartialPacketJoiner sysinfoPacketJoiner = new PartialPacketJoiner(SYSINFO_SIZE);
    private final PartialPacketJoiner settingsPacketJoiner = new PartialPacketJoiner(SETTINGS_SIZE);
    private final PartialPacketJoiner bitfilePacketJoiner = new PartialPacketJoiner(BITFILE_SIZE);

    private int protocolErrorCount = 0;

    public PandaLightProtocol(SerialConnection connection) {
        serialConnection = connection;
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void connected() {
                inDataBuffer.clear();
                expectedPackets.clear();
                Arrays.fill(inPayloadBuffer, null);
                Arrays.fill(outPacketBuffer, null);
                Arrays.fill(resendTimers, null);
                outPacketNumber = 0;

                for (ConnectionListener l : connectionListeners)
                    l.connected();
            }

            @Override
            public void disconnected() {
                for (int i = 0; i < resendTimers.length; i++) {
                    Timer t = resendTimers[i];
                    if (t != null) {
                        t.cancel();
                        resendTimers[i] = null;
                    }
                }

                for (ConnectionListener l : connectionListeners)
                    l.disconnected();
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
                for (int i = offset; i < offset + length; i++)
                    inDataBuffer.add(data[i]);

                tryPopNextPacket();

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

    private synchronized boolean tryPopNextPacket() {
        if (inDataBuffer.size() < 3)
            // the packet was not yet read completely
            return false;

        byte magic;
        do {
            magic = inDataBuffer.getFirst();
        } while (
                magic != DATA_MAGIC &&
                        magic != ACK_MAGIC &&
                        magic != RESEND_MAGIC);

        int packetNumber = inDataBuffer.get(1);
        int checksum = (magic + packetNumber) % 256;

        switch (magic) {
            case ACK_MAGIC:
                removeFromInDataBuffer(2);

                if (!isChecksumValid(checksum))
                    return false;

                Logger.debug("got acknowledge for packet {}, cancelling resend timer", packetNumber);

                Timer timer = resendTimers[packetNumber];
                if (timer != null) {
                    timer.cancel();
                    resendTimers[packetNumber] = null;
                }
                return true;
            case RESEND_MAGIC:
                removeFromInDataBuffer(2);

                if (!isChecksumValid(checksum))
                    return false;

                Logger.debug("got resend request for packet {}", packetNumber);

                resendPacket(packetNumber);
                return true;
        }

        // it's a data packet

        byte length = (byte) (inDataBuffer.get(2) + 1);
        if (inDataBuffer.size() < length + 1) {
            // the packet was not yet read completely
            return false;
        }

        removeFromInDataBuffer(3);

        checksum = (checksum + length - 1) % 256;

        byte[] payload = new byte[length];
        for (int i = 0; i < length; i++) {
            byte b = inDataBuffer.pop();
            payload[i] = b;
            checksum = (checksum + b) % 256;
        }

        if (!isChecksumValid(checksum))
            return false;

        inPayloadBuffer[packetNumber] = payload;
        sendAcknowledge(packetNumber);
        tryCombinePayloads();

        return true;
    }

    private synchronized void removeFromInDataBuffer(int count) {
        for (int i = 0; i < count; i++)
            inDataBuffer.removeFirst();
    }

    private synchronized void tryCombinePayloads() {
        Class<? extends PandaLightPacket> nextExpectedPacket = expectedPackets.get(0);
        PandaLightPacket packet = null;

        try {
            if (nextExpectedPacket == PandaLightSysinfoPacket.class) {
                byte[] data = sysinfoPacketJoiner.tryCombinePayloads(inPayloadBuffer);
                packet = new PandaLightSysinfoPacket(data);
            } else if (nextExpectedPacket == PandaLightSettingsPacket.class) {
                byte[] data = settingsPacketJoiner.tryCombinePayloads(inPayloadBuffer);
                packet = new PandaLightSettingsPacket(data);
            } else if (nextExpectedPacket == PandaLightBitfilePacket.class) {
                byte[] data = bitfilePacketJoiner.tryCombinePayloads(inPayloadBuffer);
                packet = new PandaLightBitfilePacket(data);
            }
        } catch (PandaLightProtocolException e) {
            if (++protocolErrorCount == MAX_PROTOCOL_ERRORS)
                // TODO error message
                serialConnection.disconnect();

            // try again
            repeatCommand(nextExpectedPacket);
        }

        if (packet != null) {
            expectedPackets.remove(0);

            for (ConnectionListener l : connectionListeners)
                l.gotPacket(packet);
        }
    }

    private void repeatCommand(Class<? extends PandaLightPacket> expectedPacket) {
        try {
            if (expectedPacket == PandaLightSysinfoPacket.class) {
                sendCommand(PandaLightCommand.SYSINFO);
            } else if (expectedPacket == PandaLightSettingsPacket.class) {
                sendCommand(PandaLightCommand.WRITE_SETTINGS_TO_UART);
            } else if (expectedPacket == PandaLightBitfilePacket.class) {
                sendCommand(PandaLightCommand.WRITE_BITFILE_TO_UART);
            }
        }
        catch (IOException ignored) { }
    }

    private synchronized void sendAcknowledge(int packetNumber) {
        Logger.debug("sending acknowledge for packet {}", packetNumber);
        try {
            serialConnection.sendData(new byte[] {
                    ACK_MAGIC,
                    (byte) packetNumber,
                    (byte) ((ACK_MAGIC + packetNumber) % 256)
            });
        } catch (IOException ignored) { }
    }

    private synchronized boolean isChecksumValid(int checksum) {
        if (inDataBuffer.pop() == checksum) {
            Logger.debug("checksum matches");
            return true;
        }
        Logger.debug("checksum does NOT match");

        // bit error in packet, flush the buffer
        // to get a new packet beginning
        inDataBuffer.clear();
        return false;
    }

    private synchronized void resendPacket(int packetNumber) {
        Logger.debug("resending packet {}", packetNumber);
        byte[] data = outPacketBuffer[packetNumber];
        if (data == null)
            return;

        try {
            serialConnection.sendData(data);
        } catch (IOException ignored) { }
    }

    public void sendCommand(PandaLightCommand cmd) throws IOException {
        switch (cmd) {
            case SYSINFO:
                expectedPackets.add(PandaLightSysinfoPacket.class);
                break;
            case WRITE_SETTINGS_TO_UART:
                expectedPackets.add(PandaLightSettingsPacket.class);
                break;
            case WRITE_BITFILE_TO_UART:
                expectedPackets.add(PandaLightBitfilePacket.class);
                break;
        }

        for (ConnectionListener l : connectionListeners)
            l.sendingCommand(cmd);

        sendData(new byte[] {cmd.byteCommand()});
    }

    private synchronized void incrementOutPacketNumber() {
        outPacketNumber = (outPacketNumber + 1) % 256;
    }

    public void sendData(byte[] data) throws IOException {
        sendData(data, 0, data.length);
    }

    public synchronized void sendData(byte[] data, int offset, int length) throws IOException {
        int partialPacketCount = (length - 1) / 256 + 1; // 256 bytes per packet

        for (int packetI = 0; packetI < partialPacketCount; packetI++) {
            int partialPayloadLength = ((length - 1) % 256) + 1;
            int partialPacketLength = partialPayloadLength + 4;

            Logger.debug("sending partial packet {}/{} with size {}",
                    packetI + 1, partialPacketCount, partialPacketLength);

            byte[] wrappedData = new byte[partialPacketLength];

            wrappedData[0] = DATA_MAGIC;
            wrappedData[1] = (byte) outPacketNumber;
            wrappedData[2] = (byte) (partialPayloadLength - 1);
            int checksum = 0;

            System.arraycopy(
                    data, offset + packetI * 256, // from data
                    wrappedData, 3, // to wrappedData
                    partialPayloadLength
                    );

            for (byte b : wrappedData) {
                checksum += b;
            }
            checksum = checksum % 256;
            wrappedData[partialPayloadLength + 3] = (byte) checksum;

            outPacketBuffer[outPacketNumber] = wrappedData;
            serialConnection.sendData(wrappedData);

            scheduleResendTimer(outPacketNumber);

            length -= 256;
            incrementOutPacketNumber();
        }
    }

    private void scheduleResendTimer(final int packetNumber) {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            private int runCount = 0;

            @Override
            public void run() {
                Logger.debug("resend attempt {}/{} of packet {}",
                        runCount + 1, MAX_TIMEOUT_RESENDS, packetNumber);

                resendPacket(packetNumber);

                if (++runCount == MAX_TIMEOUT_RESENDS) {
                    Logger.debug("ending resend attempts of packet {}",
                            packetNumber);

                    cancel();
                    resendTimers[packetNumber] = null;
                }
            }
        }, RESEND_TIMEOUT_MILLIS, RESEND_TIMEOUT_MILLIS);
        resendTimers[outPacketNumber] = t;
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
