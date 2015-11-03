package com.blackwhitesoftware.pandalight.remote_control;

import com.blackwhitesoftware.pandalight.PandaLightCommand;

import java.io.IOException;
import java.util.*;

/**
 * Created by sebastian on 29.10.15.
 */
public class PandaLightProtocol {
    //TODO Refactoring!!!

    private static final byte DATA_MAGIC = 0x66;
    private static final byte ACK_MAGIC = 0x67;
    private static final byte RESEND_MAGIC = 0x68;

    private static final int SYSINFO_SIZE = 12;
    private static final int SETTINGS_SIZE = 1024;
    private static final int BITFILE_SIZE = 342816;

    private static final long RESEND_TIMEOUT_MILLIS = 100;
    private static final int MAX_TIMEOUT_RESENDS = 10;

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

    public PandaLightProtocol(SerialConnection connection) {
        serialConnection = connection;
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void connected() {
                for (ConnectionListener l : connectionListeners)
                    l.connected();

                inDataBuffer.clear();
                expectedPackets.clear();
                Arrays.fill(inPayloadBuffer, null);
                Arrays.fill(outPacketBuffer, null);
                Arrays.fill(resendTimers, null);
                outPacketNumber = 0;
            }

            @Override
            public void disconnected() {
                for (ConnectionListener l : connectionListeners)
                    l.disconnected();

                for (Timer t : resendTimers)
                    if (t != null)
                        t.cancel();
            }

            @Override
            public void sendingCommand(PandaLightCommand cmd) {
                for (ConnectionListener l : connectionListeners)
                    l.sendingCommand(cmd);

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
            }

            @Override
            public void gotData(byte[] data, int offset, int length) {
                for (ConnectionListener l : connectionListeners)
                    l.gotData(data, offset, length);

                for (int i = offset; i < offset + length; i++)
                    inDataBuffer.add(data[i]);

                tryPopNextPacket();
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

        int packetNumber = inDataBuffer.getFirst();
        int checksum = (magic + packetNumber) % 256;

        switch (magic) {
            case ACK_MAGIC:
                if (!isChecksumValid(checksum))
                    return false;

                Timer timer = resendTimers[packetNumber];
                if (timer != null) {
                    timer.cancel();
                    resendTimers[packetNumber] = null;
                }
                return true;
            case RESEND_MAGIC:
                if (!isChecksumValid(checksum))
                    return false;

                resendPacket(packetNumber);
                return true;
        }

        // it's a data packet

        byte length = (byte) (inDataBuffer.getFirst() + 1);
        if (inDataBuffer.size() < length + 1) {
            // the packet was not yet read completely
            inDataBuffer.addFirst(length);
            inDataBuffer.addFirst((byte) packetNumber);
            inDataBuffer.addFirst(magic);
            return false;
        }

        checksum = (checksum + length) % 256;

        byte[] payload = new byte[length];
        for (int i = 0; i < length; i++) {
            byte b = inDataBuffer.getFirst();
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

    private void tryCombinePayloads() {
        Class<? extends PandaLightPacket> nextExpectedPacket = expectedPackets.get(0);
        PandaLightPacket packet = null;

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

        if (packet != null) {
            expectedPackets.remove(0);

            for (ConnectionListener l : connectionListeners)
                l.gotPacket(packet);
        }
    }

    private synchronized void sendAcknowledge(int packetNumber) {
        try {
            serialConnection.sendData(new byte[] {
                    ACK_MAGIC,
                    (byte) packetNumber,
                    (byte) ((ACK_MAGIC + packetNumber) % 256)
            });
        } catch (IOException ignored) { }
    }

    private synchronized boolean isChecksumValid(int checksum) {
        if (inDataBuffer.getFirst() == checksum)
            return true;

        // bit error in packet, flush the buffer
        // to get a new packet beginning
        inDataBuffer.clear();
        return false;
    }

    private synchronized void resendPacket(int packetNumber) {
        byte[] data = outPacketBuffer[packetNumber];
        if (data == null)
            return;

        try {
            serialConnection.sendData(data);
        } catch (IOException ignored) { }
    }

    public void sendCommand(PandaLightCommand cmd) throws IOException {
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
            int partialPacketLength = ((length - 1) % 256) + 1;
            byte[] wrappedData = new byte[partialPacketLength];

            wrappedData[0] = DATA_MAGIC;
            wrappedData[1] = (byte) outPacketNumber;
            wrappedData[2] = (byte) (partialPacketLength - 1);
            int checksum = 0;

            System.arraycopy(
                    data, offset + packetI * 256, // from data
                    wrappedData, 3, // to wrappedData
                    partialPacketLength
                    );

            for (byte b : wrappedData) {
                checksum += b;
            }
            checksum = checksum % 256;
            wrappedData[partialPacketLength + 3] = (byte) checksum;

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
                resendPacket(packetNumber);

                if (++runCount == MAX_TIMEOUT_RESENDS)
                    cancel();
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
