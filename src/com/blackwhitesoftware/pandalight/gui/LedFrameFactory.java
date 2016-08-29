package com.blackwhitesoftware.pandalight.gui;

import com.blackwhitesoftware.pandalight.PandaLightConfigurationContainer;
import com.blackwhitesoftware.pandalight.spec.BorderSide;
import com.blackwhitesoftware.pandalight.spec.ImageProcessConfig;
import com.blackwhitesoftware.pandalight.spec.Led;
import com.blackwhitesoftware.pandalight.spec.LedFrameConstruction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * The LedFrameFactory translates user specifications (number of leds, etc) to actual led
 * specifications (location of led, depth and width of integration, etc)
 */
public class LedFrameFactory {

    /**
     * Convenience method for increasing the led counter (it might actually decrease if the frame is
     * counter clockwise)
     *
     * @param frameSpec   The specification of the led-frame
     * @param pLedCounter The current led counter
     * @return The counter/index of the next led
     */
    private static int increase(LedFrameConstruction frameSpec, int pLedCounter) {
        if (frameSpec.direction == LedFrameConstruction.Direction.clockwise) {
            return (pLedCounter + 1) % frameSpec.getTotalLedCount();
        } else {
            if (pLedCounter == 0) {
                return frameSpec.getTotalLedCount() - 1;
            }
            return pLedCounter - 1;
        }

    }

    /**
     * Translate a 'frame' and picture integration specification to per-led specification
     *
     * @param config     The configuration
     * @return The per-led specification
     */
    public static Vector<Led> construct(PandaLightConfigurationContainer config) {
        Vector<Led> mLeds = new Vector<>();
        LedFrameConstruction frameSpec = config.mLedFrameConfig;
        ImageProcessConfig processConfig = config.mProcessConfig;

        int totalLedCount = config.mLedFrameConfig.getTotalLedCount();
        if (totalLedCount <= 0) {
            return mLeds;
        }

        // Determine the led-number of the top-left led
        int iLed = (totalLedCount - frameSpec.firstLedOffset) % totalLedCount;
        if (iLed < 0) {
            iLed += totalLedCount;
        }

        if (frameSpec.horizontalLedCount > 0) {
            int ledCnt = frameSpec.horizontalLedCount;

            // Construct all leds along the top of the screen (if any)

            for (int iTop = 0; iTop < ledCnt; ++iTop) {
                // Construct and add the single led specification to the list of leds
                mLeds.add(createLed(iLed, iTop, BorderSide.top, config));
                iLed = increase(frameSpec, iLed);
            }

            // Construct all leds along the bottom of the screen (if any)

            for (int iBottom = (ledCnt - 1); iBottom >= 0; --iBottom) {
                // Construct and add the single led specification to the list of leds
                mLeds.add(createLed(iLed, iBottom, BorderSide.bottom, config));
                iLed = increase(frameSpec, iLed);
            }
        }

        if (frameSpec.verticalLedCount > 0) {
            int ledCnt = frameSpec.verticalLedCount;

            // Construct all leds along the right of the screen (if any)

            for (int iRight = 0; iRight < ledCnt; ++iRight) {
                // Construct and add the single led specification to the list of leds
                mLeds.add(createLed(iLed, iRight, BorderSide.right, config));
                iLed = increase(frameSpec, iLed);
            }

            // Construct all leds along the left of the screen (if any)

            for (int iLeft = (ledCnt - 1); iLeft >= 0; --iLeft) {
                // Construct and add the single led specification to the list of leds
                mLeds.add(createLed(iLed, iLeft, BorderSide.left, config));
                iLed = increase(frameSpec, iLed);
            }
        }

        Collections.sort(mLeds, new Comparator<Led>() {
            @Override
            public int compare(Led o1, Led o2) {
                return Integer.compare(o1.mTotalLedIndex, o2.mTotalLedIndex);
            }
        });
        return mLeds;
    }

    /**
     * Constructs the specification of a single led
     *
     * @param totalLedIndex  The total index of the LED
     * @param borderLedIndex The index within one side of the frame
     * @param borderSide     The side on which the led is located
     * @param config         The configuration
     * @return The image integration specifications of the single led
     */
    private static Led createLed(
            int totalLedIndex, int borderLedIndex,
            BorderSide borderSide, PandaLightConfigurationContainer config) {
        Led led = new Led();

        double scale = 255.0 / (256.0 * 8.0);

        int sideLedCount;
        double width, height, offset, padding, step;
        double x, y;

        if (borderSide.isHorizontal()) {
            width = config.mProcessConfig.getHorizontalLedWidth() * scale / 255.0;
            height = config.mProcessConfig.getHorizontalLedHeight() * scale / 255.0;
            offset = config.mProcessConfig.getHorizontalLedOffset() * scale / 255.0;
            padding = config.mProcessConfig.getHorizontalLedPadding() * scale/ 255.0;
            step = config.mProcessConfig.getHorizontalLedStep() * scale / 255.0;
            x = offset + borderLedIndex * step;
            y = padding;

            if (borderSide == BorderSide.bottom)
                y = 1.0 - height - padding;
        } else {
            width = config.mProcessConfig.getVerticalLedWidth() * scale / 255.0;
            height = config.mProcessConfig.getVerticalLedHeight() * scale / 255.0;
            offset = config.mProcessConfig.getVerticalLedOffset() * scale / 255.0;
            padding = config.mProcessConfig.getVerticalLedPadding() * scale / 255.0;
            step = config.mProcessConfig.getVerticalLedStep() * scale / 255.0;
            x = padding;
            y = offset + borderLedIndex * step;

            if (borderSide == BorderSide.right)
                x = 1.0 - width - padding;
        }

        led.mTotalLedIndex = totalLedIndex;
        led.mLocation = new Point2D.Double(x, y);
        led.mSide = borderSide;

        led.mImageRectangle = new Rectangle2D.Double(x, y, width, height);
        
        return led;
    }

}
