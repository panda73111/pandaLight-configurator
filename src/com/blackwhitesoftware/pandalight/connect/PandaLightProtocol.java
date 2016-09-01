package com.blackwhitesoftware.pandalight.connect;

import com.blackwhitesoftware.pandalight.Bitfile;
import com.blackwhitesoftware.pandalight.Helpers;
import com.blackwhitesoftware.pandalight.spec.PandaLightSettings;
import jssc.SerialPortException;
import org.pmw.tinylog.Logger;

import java.util.*;

/**
 * Created by sebastian on 29.10.15.
 */
public class PandaLightProtocol {
    //TODO: _REFACTORING_
    private static final byte DATA_MAGIC = 0x65;
    private static final byte ACK_MAGIC = 0x66;

    private static final int SYSINFO_SIZE = 12;
    private static final int SETTINGS_SIZE = 1024;

    private static final int BUFFERED_PACKETS = 8;
    private static final long RESEND_TIMEOUT_MILLIS = 500;
    private static final int MAX_TIMEOUT_RESENDS = 10;

    private static final int MAX_PROTOCOL_ERRORS = 10;

    private final SerialConnection serialConnection;
    private final LinkedList<Byte> inDataBuffer = new LinkedList<>();
    private final byte[][] inPayloadBuffer = new byte[256][];
    private final byte[][] outPacketBuffer = new byte[256][];
    private int outPacketNumber = 0;
    private final Vector<Class<? extends PandaLightPacket>> expectedPackets = new Vector<>();
    private final Timer[] resendTimers = new Timer[256];
    private final List<ConnectionListener> connectionListeners = new Vector<>();

    private volatile boolean newDataReceived = false;
    private volatile boolean paused = false;
    private volatile boolean serialPaused = false;
    private final Object receiveLock = new Object();
    private final Object sendLock = new Object();
    private Thread receiveThread;
    private Thread sendThread;
    private final LinkedList<Packet> sendQueue = new LinkedList<>();

    private final PartialPacketJoiner sysinfoPacketJoiner = new PartialPacketJoiner(SYSINFO_SIZE);
    private final PartialPacketJoiner settingsPacketJoiner = new PartialPacketJoiner(SETTINGS_SIZE);

    private int protocolErrorCount = 0;
    private volatile int minAcknowledgedPacketNumber = 0;

    public PandaLightProtocol(SerialConnection connection) {
        serialConnection = connection;
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void connected() {
                synchronized (receiveLock) {
                    inDataBuffer.clear();
                }
                synchronized (sendLock) {
                    sendQueue.clear();
                }
                expectedPackets.clear();
                Arrays.fill(inPayloadBuffer, null);
                Arrays.fill(outPacketBuffer, null);
                Arrays.fill(resendTimers, null);
                outPacketNumber = 0;

                startThreads();

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
            } catch (InterruptedException ignored) { }

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

    private class Packet {
        final int number;
        final byte[] data;
        final boolean prioritized;
        final boolean scheduleResends;
        final int partialPacketIndex;
        final int maxPartialPacketIndex;

        Packet(int number, byte[] data, boolean prioritized) {
            this(number, data, prioritized, false, 0, 0);
        }

        Packet(
                int number, byte[] data, boolean prioritized, boolean scheduleResends,
                int partialPacketIndex, int maxPartialPacketIndex) {
            this.number = number;
            this.data = data;
            this.prioritized = prioritized;
            this.scheduleResends = scheduleResends;
            this.partialPacketIndex = partialPacketIndex;
            this.maxPartialPacketIndex = maxPartialPacketIndex;
        }
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
                        while (tryPopNextPacket()) { }
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

            int packetsToSendTillPausing = BUFFERED_PACKETS;

            while (true) {
                try {
                    Packet packet;

                    synchronized (sendLock) {
                        while (sendQueue.size() == 0 || paused || serialPaused)
                            sendLock.wait();

                        boolean foundPrioritizedPacket = false;

                        for (int i = 0; i < sendQueue.size(); i++) {
                            packet = sendQueue.get(i);

                            if (packet.prioritized) {
                                if (packet.scheduleResends)
                                    scheduleResendTimer(packet);

                                Logger.debug(
                                        "sending prioritized packet #{}, partial packet #{}",
                                        packet.number, packet.partialPacketIndex);

                                serialConnection.sendData(packet.data);
                                sendQueue.remove(i);

                                foundPrioritizedPacket = true;
                                break;
                            }
                        }

                        if (foundPrioritizedPacket)
                            continue;

                        packet = sendQueue.pop();
                        if (packet.scheduleResends)
                            scheduleResendTimer(packet);

                        Logger.debug(
                                "sending unprioritized packet #{}, partial packet #{}",
                                packet.number, packet.partialPacketIndex);

                        serialConnection.sendData(packet.data);
                    }

                    synchronized (receiveLock) {
                        if (packetsToSendTillPausing > 0)
                            packetsToSendTillPausing--;
                        else
                            minAcknowledgedPacketNumber = (minAcknowledgedPacketNumber + 1) % 256;

                        if (packet.partialPacketIndex == packet.maxPartialPacketIndex) {
                            Logger.debug("finished sending partial packets");
                            packetsToSendTillPausing = BUFFERED_PACKETS;
                        } else if (packetsToSendTillPausing == 0 && resendTimers[minAcknowledgedPacketNumber] != null) {
                            Logger.debug("pausing until packet #{} is acknowledged", minAcknowledgedPacketNumber);
                            pauseSending();
                        }
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

    private boolean tryPopNextPacket() {
        if (inDataBuffer.size() < 3)
            // the packet was not yet read completely
            return false;

        byte magic;
        do {
            magic = inDataBuffer.getFirst();
        } while (magic != DATA_MAGIC && magic != ACK_MAGIC);

        int packetNumber = inDataBuffer.get(1) & 0xFF;
        int checksum = (magic + packetNumber) % 256;

        if (magic == ACK_MAGIC) {
            removeFromInDataBuffer(2);

            if (!isChecksumValid(checksum))
                return false;

            Logger.debug("got acknowledge for packet #{}, cancelling resend timer", packetNumber);

            Timer timer = resendTimers[packetNumber];
            if (timer != null) {
                timer.cancel();
                resendTimers[packetNumber] = null;
            }

            if (packetNumber == minAcknowledgedPacketNumber)
                unpauseSending();

            return true;
        }

        // it's a data packet

        int length = (inDataBuffer.get(2) & 0xFF) + 1;
        if (inDataBuffer.size() < length + 1) {
            // the packet was not yet read completely (+1 for checksum)
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

        Logger.debug("got data packet: {}", Helpers.bytesToHex(payload));

        if (!isChecksumValid(checksum))
            return false;

        inPayloadBuffer[packetNumber] = payload;

        try {
            sendAcknowledge(packetNumber);
            tryCombinePayloads();
        } catch (PandaLightProtocolException e) {
            if (++protocolErrorCount == MAX_PROTOCOL_ERRORS) {
                Logger.error("Too many protocol errors, disconnecting");
                serialConnection.disconnect();
            }

            repeatCommand(expectedPackets.get(0));
            return false;
        }

        return true;
    }

    private void removeFromInDataBuffer(int count) {
        for (int i = 0; i < count; i++)
            inDataBuffer.removeFirst();
    }

    private void tryCombinePayloads() throws PandaLightProtocolException {
        if (expectedPackets.size() == 0)
        {
            Logger.error("not expecting a packet, can't combine payloads!");
            return;
        }

        Class<? extends PandaLightPacket> nextExpectedPacket = expectedPackets.get(0);
        PandaLightPacket packet = null;

        if (nextExpectedPacket == PandaLightSysinfoPacket.class) {
            byte[] data = sysinfoPacketJoiner.tryCombinePayloads(inPayloadBuffer);
            packet = new PandaLightSysinfoPacket(data);
        } else if (nextExpectedPacket == PandaLightSettingsPacket.class) {
            byte[] data = settingsPacketJoiner.tryCombinePayloads(inPayloadBuffer);
            packet = new PandaLightSettingsPacket(data);
        }

        if (packet != null) {
            expectedPackets.remove(0);

            for (ConnectionListener l : connectionListeners)
                l.gotPacket(packet);
        }
    }

    private void repeatCommand(Class<? extends PandaLightPacket> expectedPacket) {
        if (expectedPacket == PandaLightSysinfoPacket.class) {
            sendCommand(PandaLightCommand.SYSINFO, true);
        } else if (expectedPacket == PandaLightSettingsPacket.class) {
            sendCommand(PandaLightCommand.WRITE_SETTINGS_TO_UART, true);
        }
    }

    private void sendAcknowledge(int packetNumber) {
        Logger.debug("sending acknowledge for packet #{}", packetNumber);

        byte[] data = new byte[] {
                ACK_MAGIC,
                (byte) packetNumber,
                (byte) ((ACK_MAGIC + packetNumber) % 256)
        };

        synchronized (sendLock) {
            sendQueue.add(new Packet(packetNumber, data, true));
            sendLock.notify();
        }
    }

    private boolean isChecksumValid(int checksum) {
        if ((inDataBuffer.pop() & 0xFF) == checksum) {
            Logger.debug("checksum matches");
            return true;
        }
        Logger.debug("checksum does NOT match");

        // bit error in packet, flush the buffer
        // to get a new packet beginning
        inDataBuffer.clear();
        return false;
    }

    private void resendPacket(Packet packet) {
        int packetNumber = packet.number;

        Logger.debug(
                "resending packet #{}, partial packet #{}",
                packetNumber, packet.partialPacketIndex);

        byte[] data = outPacketBuffer[packetNumber];
        if (data == null)
            return;

        synchronized (sendLock) {
            sendQueue.add(new Packet(packetNumber, data, true));
            sendLock.notify();
        }
    }

    public void sendCommand(PandaLightCommand cmd) {
        sendCommand(cmd, false);
    }

    private void sendCommand(PandaLightCommand cmd, boolean prioritized) {
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

        sendData(new byte[] {cmd.getByteCommand()}, prioritized);
    }

    public void sendBitfile(byte bitfileIndex, Bitfile bitfile) {
        sendCommand(PandaLightCommand.LOAD_BITFILE_FROM_UART);

        int length = bitfile.getLength();
        sendData(new byte[] {
                bitfileIndex,
                (byte) ((length & 0xFF0000) >> 16),
                (byte) ((length & 0xFF00) >> 8),
                (byte) (length & 0xFF)});

        sendData(bitfile.getData());
    }

    public void sendSettings(PandaLightSettings settings) throws SerialPortException {
        sendCommand(PandaLightCommand.LOAD_SETTINGS_FROM_UART);
        sendData(settings.getData());
    }

    public void sendData(byte[] data) {
        sendData(data, false);
    }

    private void sendData(byte[] data, boolean prioritized) {
        sendData(data, 0, data.length, prioritized);
    }

    public void sendData(byte[] data, int offset, int length) {
        sendData(data, offset, length, false);
    }

    private synchronized void sendData(byte[] data, int offset, int length, boolean prioritized) {
        int partialPacketCount = (length - 1) / 256 + 1; // 256 bytes per packet

        for (int packetI = 0; packetI < partialPacketCount; packetI++) {
            int partialPayloadLength = Math.min(length, 256);
            int partialPacketLength = partialPayloadLength + 4;

            Logger.debug("queueing partial packet {}/{} #{} with size {}",
                    packetI + 1, partialPacketCount,
                    outPacketNumber, partialPacketLength);

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

            Packet packet = new Packet(
                    outPacketNumber, wrappedData,
                    prioritized, true, packetI,
                    partialPacketCount - 1);

            synchronized (sendLock) {
                sendQueue.add(packet);
                sendLock.notify();
            }

            length -= 256;
            outPacketNumber = (outPacketNumber + 1) % 256;
        }
    }

    private void scheduleResendTimer(final Packet packet) {
        final int packetNumber = packet.number;

        TimerTask task = new TimerTask() {
            private int runCount = 0;

            @Override
            public void run() {
                Logger.debug("resend attempt {}/{} of packet #{}, partial packet #{}",
                        runCount + 1, MAX_TIMEOUT_RESENDS,
                        packetNumber, packet.partialPacketIndex);

                resendPacket(packet);

                if (++runCount == MAX_TIMEOUT_RESENDS) {
                    Logger.debug("ending resend attempts of packet #{}",
                            packetNumber);

                    cancel();
                    resendTimers[packetNumber] = null;
                }
            }
        };

        Timer t = new Timer("resend timer " + packetNumber);
        t.scheduleAtFixedRate(task, RESEND_TIMEOUT_MILLIS, RESEND_TIMEOUT_MILLIS);
        resendTimers[packetNumber] = t;
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
