package com.blackwhitesoftware.pandalight.spec;

import com.blackwhitesoftware.pandalight.ConfigurationContainer;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Created by Sebastian.Huether on 26.08.2016.
 */
public class PandaLightSettings {
    private final byte[] mData;

    public PandaLightSettings(ConfigurationContainer configuration) {
        final ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put((byte)configuration.mLedFrameConfig.horizontalLedCount);
        putFraction(buffer, configuration.mProcessConfig.horizontal.getLedWidth());
        putFraction(buffer, configuration.mProcessConfig.horizontal.getLedHeight());
        putFraction(buffer, configuration.mProcessConfig.horizontal.getLedStep());
        putFraction(buffer, configuration.mProcessConfig.horizontal.getLedPadding());
        putFraction(buffer, configuration.mProcessConfig.horizontal.getLedOffset());

        buffer.put((byte)configuration.mLedFrameConfig.verticalLedCount);
        putFraction(buffer, configuration.mProcessConfig.vertical.getLedWidth());
        putFraction(buffer, configuration.mProcessConfig.vertical.getLedHeight());
        putFraction(buffer, configuration.mProcessConfig.vertical.getLedStep());
        putFraction(buffer, configuration.mProcessConfig.vertical.getLedPadding());
        putFraction(buffer, configuration.mProcessConfig.vertical.getLedOffset());

        buffer.position(0x40);

        buffer.put((byte)configuration.mLedFrameConfig.firstLedOffset);
        buffer.put((byte)configuration.mProcessConfig.getFrameDelay());
        buffer.put((byte)configuration.mDeviceConfig.mColorByteOrder.getIndex());
        buffer.put((byte)configuration.mDeviceConfig.mType.getIndex());

        // 4 Bit + 12 Bit fixed point
        double gamma = configuration.mColorConfig.gammaCorrection;
        int integerPart = (int)Math.floor(gamma) & 0x000F;
        int fractionPart = (int)(gamma * 0x1000) & 0x0FFF;
        short fixedPoint = (short)((integerPart << 12) | fractionPart);
        buffer.putShort(fixedPoint);

        Color minValues = configuration.mColorConfig.getMinChannelValues();
        Color maxValues = configuration.mColorConfig.getMaxChannelValues();
        buffer.put((byte)minValues.getRed());
        buffer.put((byte)maxValues.getRed());
        buffer.put((byte)minValues.getGreen());
        buffer.put((byte)maxValues.getGreen());
        buffer.put((byte)minValues.getBlue());
        buffer.put((byte)maxValues.getBlue());

        buffer.position(0x100);

        buffer.put(configuration.mColorConfig.redLookupTable);
        buffer.put(configuration.mColorConfig.greenLookupTable);
        buffer.put(configuration.mColorConfig.blueLookupTable);

        mData = buffer.array();
    }

    private void putFraction(ByteBuffer buffer, double value) {
        buffer.putShort((short) (value * 0xFFFF));
    }

    public byte[] getData() {
        return mData;
    }
}
