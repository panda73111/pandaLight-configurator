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
    private LinkedList<byte[]> inPacketBuffer = new LinkedList<>();
    private LinkedList<byte[]> outPacketBuffer = new LinkedList<>();
    private short inPacketNumber = 0;
    private short outPacketNumber = 0;
    private Vector<Class<? extends PandaLightPacket>> expectedPackets = new Vector<>();

    public PandaLightProtocol(SerialConnection connection) {
        serialConnection = connection;
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void connected() {
                inDataBuffer.clear();
                expectedPackets.clear();
                inPacketNumber = 0;
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
        //TODO CHECK THIS SIGNED BYTE JAVA FUCK FOR CORRECTNESS

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

        byte packetNumber = inDataBuffer.getFirst();
        //TODO use packet number for sorting packets

        int checksum = magic + packetNumber;

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
            inDataBuffer.addFirst(packetNumber);
            inDataBuffer.addFirst(magic);
            return false;
        }

        checksum += length;

        byte[] payload = new byte[length];
        for (int i = 0; i < length; i++) {
            byte b = inDataBuffer.getFirst();
            payload[i] = b;
            checksum += b;
        }

        if (!isChecksumValid(checksum))
            return false;

        //TODO store partial data packet

        return true;
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
        byte[] data = new byte[] {
                DATA_MAGIC,
                (byte) outPacketNumber,
                cmd.byteCommand(),
                0, // [payload length]-1
                (byte) (DATA_MAGIC + outPacketNumber + cmd.byteCommand()) // checksum
        };
        outPacketBuffer.add(outPacketNumber, data);
        serialConnection.sendData(data);
        outPacketNumber++;
    }
}
