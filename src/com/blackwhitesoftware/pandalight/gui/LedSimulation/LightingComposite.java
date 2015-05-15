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
    private final boolean useAlpha;
    private ColorModel srcColorModel;
    private ColorModel dstColorModel;

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

    private class LightingCompositeContext implements CompositeContext {
        public LightingCompositeContext(ColorModel srcColorModel, ColorModel dstColorModel) {
        }

        @Override
        public void dispose() {

        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            if (src.getSampleModel().getDataType() != DataBuffer.TYPE_INT ||
                    dstIn.getSampleModel().getDataType() != DataBuffer.TYPE_INT ||
                    dstOut.getSampleModel().getDataType() != DataBuffer.TYPE_INT) {
                throw new IllegalArgumentException("Source and destination must store pixels as integers");
            }

            final int w = dstIn.getWidth();
            final int h = dstIn.getHeight();

            final int[] srcPixels = new int[w * h];
            final int[] dstInPixels = new int[w * h];
            final int[] dstOutPixels = new int[w * h];

            src.getDataElements(src.getMinX(), src.getMinY(), w, h, srcPixels);
            dstIn.getDataElements(dstIn.getMinX(), dstIn.getMinY(), w, h, dstInPixels);

            int maxI = h * w;
            for (int i = 0; i < maxI; i++) {
                // additive lighting

                int srcRgba = srcPixels[i];
                int dstInRgba = dstInPixels[i];

                int srcRed = (srcRgba >> 16) & 0xFF;
                int srcGreen = (srcRgba >> 8) & 0xFF;
                int srcBlue = srcRgba & 0xFF;

                if (useAlpha && srcColorModel.hasAlpha()) {
                    // regard the alpha value as brightness factor
                    double srcAlpha = ((srcRgba >> 24) & 0xFF) / 255.0;
                    srcRed = (int) (srcRed * srcAlpha);
                    srcGreen = (int) (srcGreen * srcAlpha);
                    srcBlue = (int) (srcBlue * srcAlpha);
                }

                int dstInRed = (dstInRgba >> 16) & 0xFF;
                int dstInGreen = (dstInRgba >> 8) & 0xFF;
                int dstInBlue = dstInRgba & 0xFF;

                if (useAlpha && dstColorModel.hasAlpha()) {
                    double dstInAlpha = ((dstInRgba >> 24) & 0xFF) / 255.0;
                    dstInRed = (int) (dstInRed * dstInAlpha);
                    dstInGreen = (int) (dstInGreen * dstInAlpha);
                    dstInBlue = (int) (dstInBlue * dstInAlpha);
                }

                int dstOutRed = Math.max(srcRed, dstInRed);
                int dstOutGreen = Math.max(srcGreen, dstInGreen);
                int dstOutBlue = Math.max(srcBlue, dstInBlue);

                int dstOutRgb = (dstOutRed << 16) | (dstOutGreen << 8) | dstOutBlue;

                dstOutPixels[i] = 0xFF000000 | dstOutRgb;
            }

            dstOut.setDataElements(dstOut.getMinX(), dstOut.getMinY(), w, h, dstOutPixels);
        }
    }
}
