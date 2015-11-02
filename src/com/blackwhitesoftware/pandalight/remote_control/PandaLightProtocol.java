package com.blackwhitesoftware.pandalight.remote_control;

import com.blackwhitesoftware.pandalight.PandaLightCommand;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by sebastian on 29.10.15.
 */
public class PandaLightProtocol {
    private static final byte DATA_MAGIC = 0x66;
    private static final byte ACK_MAGIC = 0x67;
    private static final byte RESEND_MAGIC = 0x68;

    private SerialConnection serialConnection;
    private LinkedList<Byte> inDataBuffer = new LinkedList<>();
    private LinkedList<byte[]> inPayloadBuffer = new LinkedList<>();
    private LinkedList<byte[]> outPacketBuffer = new LinkedList<>();
    private int outPacketNumber = 0;
    private Vector<Class<? extends PandaLightPacket>> expectedPackets = new Vector<>();

    public PandaLightProtocol(SerialConnection connection) {
        serialConnection = connection;
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void connected() {
                inDataBuffer.clear();
                expectedPackets.clear();
                outPacketNumber = 0;
            }

            @Override
            public void disconnected() {
            }

            @Override
            public void sendingCommand(PandaLightCommand cmd) {
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
                for (int i = offset; i < offset + length; i++)
                    inDataBuffer.add(data[i]);

                tryPopNextPacket();
            }
        };
        serialConnection.addConnectionListener(listener);
    }

    private boolean tryPopNextPacket() {
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
                throw new NotImplementedException();
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

        inPayloadBuffer.add(packetNumber, payload);
        sendAcknowledge(packetNumber);

        return true;
    }

    private void sendAcknowledge(int packetNumber) {
        try {
            serialConnection.sendData(new byte[] {
                    ACK_MAGIC,
                    (byte) packetNumber,
                    (byte) ((ACK_MAGIC + packetNumber) % 256)
            });
        } catch (IOException ignored) { }
    }

    private boolean isChecksumValid(int checksum) {
        if (inDataBuffer.getFirst() == checksum)
            return true;

        // bit error in packet, flush the buffer
        // to get a new packet beginning
        inDataBuffer.clear();
        return false;
    }

    private void resendPacket(int packetNumber) {
        try {
            serialConnection.sendData(outPacketBuffer.get(packetNumber));
        } catch (IOException ignored) { }
    }

    public void sendCommand(PandaLightCommand cmd) throws IOException {
        sendData(new byte[] {cmd.byteCommand()});
    }

    private void incrementOutPacketNumber() {
        outPacketNumber = (outPacketNumber + 1) % 256;
    }

    public void sendData(byte[] data) throws IOException {
        sendData(data, 0, data.length);
    }

    public void sendData(byte[] data, int offset, int length) throws IOException {
        int partialPacketCount = (length - 1) / 256 + 1; // 256 bytes per packet

        for (int packetI = 0; packetI < partialPacketCount; packetI++) {
            int partialPacketLength = ((length - 1) % 256) + 1;
            byte[] wrappedData = new byte[partialPacketLength];

            wrappedData[0] = DATA_MAGIC;
            wrappedData[1] = (byte) outPacketNumber;
            wrappedData[2] = (byte) (partialPacketLength - 1);
            int checksum = (DATA_MAGIC + outPacketNumber + partialPacketLength - 1) % 256;

            for (int byteI = 0; byteI < partialPacketLength; byteI++) {
                // copy the data
                byte b = data[offset + byteI + packetI * 256];
                wrappedData[byteI + 3] = b;
                checksum = (checksum + b) % 256;
            }
            wrappedData[partialPacketLength + 3] = (byte) checksum;

            outPacketBuffer.add(outPacketNumber, wrappedData);
            serialConnection.sendData(wrappedData);

            length -= 256;
            incrementOutPacketNumber();
        }
    }
}
