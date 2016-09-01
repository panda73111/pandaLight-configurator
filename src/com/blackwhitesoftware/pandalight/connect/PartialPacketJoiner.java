package com.blackwhitesoftware.pandalight.connect;

/**
 * Created by sebastian on 03.11.15.
 */
public class PartialPacketJoiner {
    private byte[] packetBuffer;
    private int prevPacketNumber = -1;
    private int bytesGotten = 0;

    public PartialPacketJoiner(int packetSize) {
        packetBuffer = new byte[packetSize];
    }

    public byte[] tryCombinePayloads(byte[][] payloadBuffer) throws PandaLightProtocolException {
        // search for consecutive payloads
        // and copy them to the respective buffer
        for (int i = 0; i < 256; i++) {
            int packetNumber = (prevPacketNumber + 1) % 256;

            byte[] paylaod = payloadBuffer[packetNumber];

            if (paylaod == null)
                return null;

            if (bytesGotten + paylaod.length > packetBuffer.length) {
                reset();
                throw new PandaLightProtocolException();
            }

            System.arraycopy(
                    paylaod, 0,
                    packetBuffer, bytesGotten,
                    paylaod.length);

            prevPacketNumber = packetNumber;
            bytesGotten += paylaod.length;

            if (bytesGotten != packetBuffer.length)
                continue;

            // packet completed
            reset();
            return packetBuffer.clone();
        }

        return null;
    }

    private void reset() {
        prevPacketNumber = -1;
        bytesGotten = 0;
    }
}
