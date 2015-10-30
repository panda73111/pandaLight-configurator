package com.blackwhitesoftware.pandalight.remote_control;

import com.blackwhitesoftware.pandalight.PandaLightCommand;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Created by sebastian on 29.10.15.
 */
public class PandaLightProtocol {
    private static final byte DATA_MAGIC = 0x66;
    private static final byte ACK_MAGIC = 0x67;
    private static final byte RESEND_MAGIC = 0x68;

    private SerialConnection serialConnection;
    private ByteBuffer buffer;
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
                if (!parseTransportLayer(data, offset, length))
                    return;

                buffer.put(data, offset, length);
            }
        };
        serialConnection.addConnectionListener(listener);
    }

    public void sendCommand(PandaLightCommand cmd) throws IOException {
        serialConnection.sendData(new byte[]{cmd.byteCommand()});
    }

    private boolean parseTransportLayer(byte[] data, int offset, int length) {
        int checksum = 0;
        for (int i = offset; i < offset+length-1; i++) {
            checksum += data[i];
        }
        if (checksum != data[offset+length-1])
            // bit error in packet
            return false;

        switch (data[offset]) {
            case DATA_MAGIC:
                break;
            case ACK_MAGIC:
                break;
            case RESEND_MAGIC:
                break;
        }

        return true;
    }
}
