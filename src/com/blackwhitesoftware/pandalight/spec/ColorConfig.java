package com.blackwhitesoftware.pandalight.spec;

import java.util.Vector;

/**
 * The color tuning parameters of the different color channels (both in RGB space as in HSV space)
 */
public class ColorConfig {

    /**
     * List with color transformations
     */
    public Vector<TransformConfig> mTransforms = new Vector<>();
    public boolean mSmoothingEnabled = false;
    /**
     * The type of smoothing algorithm
     */
    public ColorSmoothingType mSmoothingType = ColorSmoothingType.linear;
    /**
     * The time constant for smoothing algorithm in milliseconds
     */
    public int mSmoothingTime_ms = 200;
    /**
     * The update frequency of the leds in Hz
     */
    public double mSmoothingUpdateFrequency_Hz = 20.0;
    /**
     * The number of periods (1/mSmoothingUpdateFrequency_Hz) to delay the update of the leds
     */
    public int mUpdateDelay = 0;

    {
        mTransforms.add(new TransformConfig());
    }

}
