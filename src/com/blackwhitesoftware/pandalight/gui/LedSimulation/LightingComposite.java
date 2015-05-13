package com.blackwhitesoftware.pandalight.gui.LedSimulation;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Created by Sebastian Hüther on 13.05.15.
 */
public class LightingComposite implements Composite {
    public LightingComposite() {
        super();
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new LightingCompositeContext(srcColorModel, dstColorModel);
    }

    private class LightingCompositeContext implements CompositeContext
    {
        public LightingCompositeContext(ColorModel srcColorModel, ColorModel dstColorModel)
        {
            if (srcColorModel.getNumComponents() != 4 || dstColorModel.getNumComponents() != 4)
                throw new IllegalArgumentException("Unsupported color components");
        }

        @Override
        public void dispose() {

        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            if (src.getSampleModel().getDataType() != DataBuffer.TYPE_INT ||
                    dstIn.getSampleModel().getDataType() != DataBuffer.TYPE_INT ||
                    dstOut.getSampleModel().getDataType() != DataBuffer.TYPE_INT)
            {
                throw new IllegalArgumentException("Source and destination must store pixels as INT.");
            }

            final int w = dstIn.getWidth();
            final int h = dstIn.getHeight();

            final int[] srcPixels = new int[w * h];
            final int[] dstInPixels = new int[w * h];
            final int[] dstOutPixels = new int[w * h];

            int srcRgba, dstInRgba;

            src.getDataElements(src.getMinX(), src.getMinY(), w, h, srcPixels);
            dstIn.getDataElements(dstIn.getMinX(), dstIn.getMinY(), w, h, dstInPixels);

            int maxI = h * w;
            for (int i = 0; i < maxI; i++)
            {
                srcRgba = srcPixels[i];
                dstInRgba = dstInPixels[i];

                int srcRed = (srcRgba >> 16) & 0xFF;
                int srcGreen = (srcRgba >> 8) & 0xFF;
                int srcBlue = srcRgba & 0xFF;

                int dstInRed = (dstInRgba >> 16) & 0xFF;
                int dstInGreen = (dstInRgba >> 8) & 0xFF;
                int dstInBlue = dstInRgba & 0xFF;

                float srcBrightness = (srcRed + srcGreen + srcBlue) / 3.0f;
                float dstInBrightness = (dstInRed + dstInGreen + dstInBlue) / 3.0f;

                int dstOutRed = (srcRed + dstInRed) / 2;
                int dstOutGreen = (srcGreen + dstInGreen) / 2;
                int dstOutBlue = (srcBlue + dstInBlue) / 2;

                float dstOutActualBrightness = (dstOutRed + dstOutGreen + dstOutBlue) / 3.0f;
                float dstOutTargetBrightness = Math.max(srcBrightness, dstInBrightness);
                int dstOutBrightnessDelta = (int)(dstOutTargetBrightness - dstOutActualBrightness);

                dstOutRed = Math.max(Math.min(dstOutRed + dstOutBrightnessDelta, 255), 0);
                dstOutGreen = Math.max(Math.min(dstOutGreen + dstOutBrightnessDelta, 255), 0);
                dstOutBlue = Math.max(Math.min(dstOutBlue + dstOutBrightnessDelta, 255), 0);

                int dstOutRgb = (dstOutRed << 16) | (dstOutGreen << 8) | dstOutBlue;

                dstOutPixels[i] = 0xFF000000 | dstOutRgb;
            }

            dstOut.setDataElements(dstOut.getMinX(), dstOut.getMinY(), w, h, dstOutPixels);
        }
    }
}
