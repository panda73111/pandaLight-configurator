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
    private LinkedList<Byte> buffer = new LinkedList<>();
    private Vector<Class<? extends PandaLightPacket>> expectedPackets = new Vector<>();

    public PandaLightProtocol(SerialConnection connection) {
        serialConnection = connection;
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void connected() {
                buffer.clear();
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
                    buffer.add(data[i]);

                tryPopNextPacket();
            }
        };
        serialConnection.addConnectionListener(listener);
    }

    private boolean tryPopNextPacket() {
        //TODO CHECK THIS SIGNED BYTE JAVA FUCK FOR CORRECTNESS

        if (buffer.size() < 3)
            // the packet was not yet read completely
            return false;

        byte magic;
        do {
            magic = buffer.getFirst();
        } while (
                magic != DATA_MAGIC &&
                        magic != ACK_MAGIC &&
                        magic != RESEND_MAGIC);

        byte packetNumber = buffer.getFirst();
        //TODO use packet number for sorting packets

        byte checksum = (byte) (magic + packetNumber);

        switch (magic) {
            case ACK_MAGIC:
                throw new NotImplementedException();
            case RESEND_MAGIC:
                throw new NotImplementedException();
        }

        // it's a data packet

        byte length = (byte) (buffer.getFirst() + 1);
        if (buffer.size() < length + 1) {
            // the packet was not yet read completely
            buffer.addFirst(length);
            buffer.addFirst(packetNumber);
            buffer.addFirst(magic);
            return false;
        }

        checksum += length;

        byte[] payload = new byte[length];
        for (int i = 0; i < length; i++) {
            byte b = buffer.getFirst();
            payload[i] = b;
            checksum += b;
        }

        if (buffer.getFirst() != checksum) {
            // bit error in packet, purge it
            return false;
        }

        //TODO store partial data packet

        return true;
    }

    public void sendCommand(PandaLightCommand cmd) throws IOException {
        serialConnection.sendData(new byte[]{cmd.byteCommand()});
    }
}
