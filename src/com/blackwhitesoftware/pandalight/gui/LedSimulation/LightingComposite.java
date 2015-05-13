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
    private ColorModel srcColorModel;
    private ColorModel dstColorModel;
    private final boolean useAlpha;

    public LightingComposite() {
        this(true);
    }

    public LightingComposite(boolean useAlpha) {
        super();
        this.useAlpha = useAlpha;
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        this.srcColorModel = srcColorModel;
        this.dstColorModel = dstColorModel;
        return new LightingCompositeContext(srcColorModel, dstColorModel);
    }

    private class LightingCompositeContext implements CompositeContext
    {
        public LightingCompositeContext(ColorModel srcColorModel, ColorModel dstColorModel)
        {
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

            src.getDataElements(src.getMinX(), src.getMinY(), w, h, srcPixels);
            dstIn.getDataElements(dstIn.getMinX(), dstIn.getMinY(), w, h, dstInPixels);

            int maxI = h * w;
            for (int i = 0; i < maxI; i++)
            {
                // additive lighting

                int srcRgba = srcPixels[i];
                int dstInRgba = dstInPixels[i];

                int srcRed = (srcRgba >> 16) & 0xFF;
                int srcGreen = (srcRgba >> 8) & 0xFF;
                int srcBlue = srcRgba & 0xFF;

                if (useAlpha && srcColorModel.hasAlpha())
                {
                    // regard the alpha value as brightness factor
                    int srcAlpha = (srcRgba >> 24) & 0xFF;
                    int srcInvAlpha = 255 - srcAlpha;
                    srcRed = Math.max(srcRed - srcInvAlpha, 0);
                    srcGreen = Math.max(srcGreen - srcInvAlpha, 0);
                    srcBlue = Math.max(srcBlue - srcInvAlpha, 0);
                }

                int dstInRed = (dstInRgba >> 16) & 0xFF;
                int dstInGreen = (dstInRgba >> 8) & 0xFF;
                int dstInBlue = dstInRgba & 0xFF;

                if (useAlpha && dstColorModel.hasAlpha())
                {
                    int dstInAlpha = (dstInRgba >> 24) & 0xFF;
                    int dstInInvAlpha = 255 - dstInAlpha;
                    dstInRed = Math.max(dstInRed - dstInInvAlpha, 0);
                    dstInGreen = Math.max(dstInGreen - dstInInvAlpha, 0);
                    dstInBlue = Math.max(dstInBlue - dstInInvAlpha, 0);
                }

                int dstOutRed = (int) Math.min(srcRed + dstInRed, 255.0f);
                int dstOutGreen = (int) Math.min(srcGreen + dstInGreen, 255.0f);
                int dstOutBlue = (int) Math.min(srcBlue + dstInBlue, 255.0f);

                int dstOutRgb = (dstOutRed << 16) | (dstOutGreen << 8) | dstOutBlue;

                dstOutPixels[i] = 0xFF000000 | dstOutRgb;
            }

            dstOut.setDataElements(dstOut.getMinX(), dstOut.getMinY(), w, h, dstOutPixels);
        }
    }
}
