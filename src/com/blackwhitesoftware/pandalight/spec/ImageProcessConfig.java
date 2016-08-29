package com.blackwhitesoftware.pandalight.spec;

import java.util.Observable;

/**
 * Configuration parameters for the image processing.
 */
public class ImageProcessConfig extends Observable {

    // Screen percentages
    public double mHorizontalLedWidth = 0.1;
    public double mHorizontalLedHeight = 0.1;
    public double mHorizontalLedStep = 0.1;
    public double mHorizontalLedPadding = 0.1;
    public double mHorizontalLedOffset = 0.1;
    public double mVerticalLedWidth = 0.1;
    public double mVerticalLedHeight = 0.1;
    public double mVerticalLedStep = 0.1;
    public double mVerticalLedPadding = 0.1;
    public double mVerticalLedOffset = 0.1;

    // Blackborder detection
    public boolean mBlackBorderRemoval = true;
    public int mBlackBorderThreshold = 10;

    public double getHorizontalLedWidth() {
        return mHorizontalLedWidth;
    }

    public double getHorizontalLedHeight() {
        return mHorizontalLedHeight;
    }

    public double getVerticalLedWidth() {
        return mVerticalLedWidth;
    }

    public double getVerticalLedHeight() {
        return mVerticalLedHeight;
    }

    public double getHorizontalLedStep() {
        return mHorizontalLedStep;
    }

    public double getHorizontalLedPadding() {
        return mHorizontalLedPadding;
    }

    public double getHorizontalLedOffset() {
        return mHorizontalLedOffset;
    }

    public double getVerticalLedStep() {
        return mVerticalLedStep;
    }

    public double getVerticalLedPadding() {
        return mVerticalLedPadding;
    }

    public double getVerticalLedOffset() {
        return mVerticalLedOffset;
    }

    public boolean isBlackBorderRemoval() {
        return mBlackBorderRemoval;
    }

    public int getBlackborderThreshold() {
        return mBlackBorderThreshold;
    }

    public void setHorizontalLedWidth(double horizontalLedWidth) {
        if (mHorizontalLedWidth != horizontalLedWidth) {
            mHorizontalLedWidth = horizontalLedWidth;
            setChanged();
        }
    }

    public void setHorizontalLedHeight(double horizontalLedHeight) {
        if (mHorizontalLedHeight != horizontalLedHeight) {
            mHorizontalLedHeight = horizontalLedHeight;
            setChanged();
        }
    }

    public void setVerticalLedWidth(double verticalLedWidth) {
        if (mVerticalLedWidth != verticalLedWidth) {
            mVerticalLedWidth = verticalLedWidth;
            setChanged();
        }
    }

    public void setVerticalLedHeight(double verticalLedHeight) {
        if (mVerticalLedHeight != verticalLedHeight) {
            mVerticalLedHeight = verticalLedHeight;
            setChanged();
        }
    }

    public void setHorizontalLedStep(double horizontalLedStep) {
        if (mHorizontalLedStep != horizontalLedStep) {
            mHorizontalLedStep = horizontalLedStep;
            setChanged();
        }
    }

    public void setHorizontalLedPadding(double horizontalLedPadding) {
        if (mHorizontalLedPadding != horizontalLedPadding) {
            mHorizontalLedPadding = horizontalLedPadding;
            setChanged();
        }
    }

    public void setHorizontalLedOffset(double horizontalLedOffset) {
        if (mHorizontalLedOffset != horizontalLedOffset) {
            mHorizontalLedOffset = horizontalLedOffset;
            setChanged();
        }
    }

    public void setVerticalLedStep(double verticalLedStep) {
        if (mVerticalLedStep != verticalLedStep) {
            mVerticalLedStep = verticalLedStep;
            setChanged();
        }
    }

    public void setVerticalLedPadding(double verticalLedPadding) {
        if (mVerticalLedPadding != verticalLedPadding) {
            mVerticalLedPadding = verticalLedPadding;
            setChanged();
        }
    }

    public void setVerticalLedOffset(double verticalLedOffset) {
        if (mVerticalLedOffset != verticalLedOffset) {
            mVerticalLedOffset = verticalLedOffset;
            setChanged();
        }
    }

    public void setBlackBorderRemoval(boolean pBlackBorderRemoval) {
        if (mBlackBorderRemoval != pBlackBorderRemoval) {
            mBlackBorderRemoval = pBlackBorderRemoval;
            setChanged();
        }
    }

    public void setBlackborderThreshold(int pThreshold) {
        if (mBlackBorderThreshold != pThreshold) {
            mBlackBorderThreshold = pThreshold;
            setChanged();
        }
    }

}
