package com.blackwhitesoftware.pandalight;

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
     * @param frameSpec     The specification of the led frame
     * @param processConfig The picture integration specification
     * @return The per-led specification
     */
    public static Vector<Led> construct(LedFrameConstruction frameSpec, ImageProcessConfig processConfig) {
        Vector<Led> mLeds = new Vector<>();

        int totalLedCount = frameSpec.getTotalLedCount();
        if (totalLedCount <= 0) {
            return mLeds;
        }

        // Determine the led-number of the top-left led
        int iLed = (totalLedCount - frameSpec.firstLedOffset) % totalLedCount;
        if (iLed < 0) {
            iLed += totalLedCount;
        }

        // Construct all leds along the top of the screen (if any)
        if (frameSpec.horizontalLedCount > 0) {
            // Determine the led-spacing
            int ledCnt = frameSpec.horizontalLedCount;
            double ledSpacing = 1.0 / (ledCnt);

            for (int iTop = 0; iTop < ledCnt; ++iTop) {
                // Compute the location of this led
                double led_x = ledSpacing / 2.0 + iTop * ledSpacing;
                double led_y = 0;

                // Construct and add the single led specification to the list of leds
                mLeds.add(createLed(frameSpec, processConfig, iLed, led_x, led_y, BorderSide.top));
                iLed = increase(frameSpec, iLed);
            }
        }

        // Construct all leds along the right of the screen (if any)
        if (frameSpec.verticalLedCount > 0) {
            // Determine the led-spacing
            int ledCnt = frameSpec.verticalLedCount;
            double ledSpacing = 1.0 / ledCnt;

            for (int iRight = 0; iRight < ledCnt; ++iRight) {
                // Compute the location of this led
                double led_x = 1.0;
                double led_y = ledSpacing / 2.0 + iRight * ledSpacing;

                // Construct and add the single led specification to the list of leds
                mLeds.add(createLed(frameSpec, processConfig, iLed, led_x, led_y, BorderSide.right));
                iLed = increase(frameSpec, iLed);
            }
        }

        // Construct all leds along the bottom of the screen (if any)
        if (frameSpec.horizontalLedCount > 0) {
            // Determine the led-spacing (based on top-leds [=bottom leds + gap size])
            int ledCnt = frameSpec.horizontalLedCount;
            double ledSpacing = 1.0 / ledCnt;

            for (int iBottom = (ledCnt - 1); iBottom >= 0; --iBottom) {
                // Compute the location of this led
                double led_x = ledSpacing / 2.0 + iBottom * ledSpacing;
                double led_y = 1.0;

                // Construct and add the single led specification to the list of leds
                mLeds.add(createLed(frameSpec, processConfig, iLed, led_x, led_y, BorderSide.bottom));
                iLed = increase(frameSpec, iLed);
            }
        }

        // Construct all leds along the left of the screen (if any)
        if (frameSpec.verticalLedCount > 0) {
            // Determine the led-spacing
            int ledCnt = frameSpec.verticalLedCount;
            double ledSpacing = 1.0 / ledCnt;

            for (int iRight = (ledCnt - 1); iRight >= 0; --iRight) {
                // Compute the location of this led
                double led_x = 0.0;
                double led_y = ledSpacing / 2.0 + iRight * ledSpacing;

                // Construct and add the single led specification to the list of leds
                mLeds.add(createLed(frameSpec, processConfig, iLed, led_x, led_y, BorderSide.left));
                iLed = increase(frameSpec, iLed);
            }
        }

        Collections.sort(mLeds, new Comparator<Led>() {
            @Override
            public int compare(Led o1, Led o2) {
                return Integer.compare(o1.mLedSeqNr, o2.mLedSeqNr);
            }
        });
        return mLeds;
    }

    /**
     * Constructs the specification of a single led
     *
     * @param pFrameSpec   The overall led-frame specification
     * @param pProcessSpec The overall image-processing specification
     * @param seqNr        The number of the led
     * @param x_frac       The x location of the led in fractional range [0.0; 1.0]
     * @param y_frac       The y location of the led in fractional range [0.0; 1.0]
     * @param pBorderSide  The side on which the led is located
     * @return The image integration specifications of the single led
     */
    private static Led createLed(
            LedFrameConstruction pFrameSpec, ImageProcessConfig pProcessSpec,
            int seqNr, double x_frac, double y_frac, BorderSide pBorderSide) {
        Led led = new Led();
        led.mLedSeqNr = seqNr;
        led.mLocation = new Point2D.Double(x_frac, y_frac);
        led.mSide = pBorderSide;
        led.mImageRectangle = new Rectangle2D.Double(0, 0, 1, 1);
        
        return led;
    }

}
