package com.blackwhitesoftware.pandalight.spec;

import java.util.Observable;

/**
 * Created by Sebastian.Huether on 30.08.2016.
 */
public class DimensionalImageProcessConfig extends Observable {
    // Screen percentages
    public double mLedWidth = 0;
    public double mLedHeight = 0;
    public double mLedStep = 0;
    public double mLedPadding = 0;
    public double mLedOffset = 0;

    public double getLedWidth() {
        return mLedWidth;
    }

    public double getLedHeight() {
        return mLedHeight;
    }

    public double getLedStep() {
        return mLedStep;
    }

    public double getLedPadding() {
        return mLedPadding;
    }

    public double getLedOffset() {
        return mLedOffset;
    }

    public void setLedWidth(double horizontalLedWidth) {
        if (mLedWidth != horizontalLedWidth) {
            mLedWidth = horizontalLedWidth;
            setChanged();
            notifyObservers(this);
        }
    }

    public void setLedHeight(double horizontalLedHeight) {
        if (mLedHeight != horizontalLedHeight) {
            mLedHeight = horizontalLedHeight;
            setChanged();
            notifyObservers(this);
        }
    }

    public void setLedStep(double horizontalLedStep) {
        if (mLedStep != horizontalLedStep) {
            mLedStep = horizontalLedStep;
            setChanged();
            notifyObservers(this);
        }
    }

    public void setLedPadding(double horizontalLedPadding) {
        if (mLedPadding != horizontalLedPadding) {
            mLedPadding = horizontalLedPadding;
            setChanged();
            notifyObservers(this);
        }
    }

    public void setLedOffset(double horizontalLedOffset) {
        if (mLedOffset != horizontalLedOffset) {
            mLedOffset = horizontalLedOffset;
            setChanged();
            notifyObservers(this);
        }
    }
}
