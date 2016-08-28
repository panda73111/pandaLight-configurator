package com.blackwhitesoftware.pandalight.spec;

import java.util.Observable;

/**
 * Configuration parameters for the image processing.
 */
public class ImageProcessConfig extends Observable {

    // Screen percentages
    public int mHorizontalLedWidth = 96;
    public int mHorizontalLedHeight = 226;
    public int mHorizontalLedStep = 128;
    public int mHorizontalLedPadding = 15;
    public int mHorizontalLedOffset = 16;
    public int mVerticalLedWidth = 128;
    public int mVerticalLedHeight = 169;
    public int mVerticalLedStep = 226;
    public int mVerticalLedPadding = 8;
    public int mVerticalLedOffset = 29;

    // Blackborder detection
    public boolean mBlackBorderRemoval = true;
    public int mBlackBorderThreshold = 10;

    public int getHorizontalLedWidth() {
        return mHorizontalLedWidth;
    }

    public int getHorizontalLedHeight() {
        return mHorizontalLedHeight;
    }

    public int getVerticalLedWidth() {
        return mVerticalLedWidth;
    }

    public int getVerticalLedHeight() {
        return mVerticalLedHeight;
    }

    public int getHorizontalLedStep() {
        return mHorizontalLedStep;
    }

    public int getHorizontalLedPadding() {
        return mHorizontalLedPadding;
    }

    public int getHorizontalLedOffset() {
        return mHorizontalLedOffset;
    }

    public int getVerticalLedStep() {
        return mVerticalLedStep;
    }

    public int getVerticalLedPadding() {
        return mVerticalLedPadding;
    }

    public int getVerticalLedOffset() {
        return mVerticalLedOffset;
    }

    public boolean isBlackBorderRemoval() {
        return mBlackBorderRemoval;
    }

    public int getBlackborderThreshold() {
        return mBlackBorderThreshold;
    }

    public void setHorizontalLedWidth(int horizontalLedWidth) {
        if (mHorizontalLedWidth != horizontalLedWidth) {
            mHorizontalLedWidth = horizontalLedWidth;
            setChanged();
        }
    }

    public void setHorizontalLedHeight(int horizontalLedHeight) {
        if (mHorizontalLedHeight != horizontalLedHeight) {
            mHorizontalLedHeight = horizontalLedHeight;
            setChanged();
        }
    }

    public void setVerticalLedWidth(int verticalLedWidth) {
        if (mVerticalLedWidth != verticalLedWidth) {
            mVerticalLedWidth = verticalLedWidth;
            setChanged();
        }
    }

    public void setVerticalLedHeight(int verticalLedHeight) {
        if (mVerticalLedHeight != verticalLedHeight) {
            mVerticalLedHeight = verticalLedHeight;
            setChanged();
        }
    }

    public void setHorizontalLedStep(int horizontalLedStep) {
        if (mHorizontalLedStep != horizontalLedStep) {
            mHorizontalLedStep = horizontalLedStep;
            setChanged();
        }
    }

    public void setHorizontalLedPadding(int horizontalLedPadding) {
        if (mHorizontalLedPadding != horizontalLedPadding) {
            mHorizontalLedPadding = horizontalLedPadding;
            setChanged();
        }
    }

    public void setHorizontalLedOffset(int horizontalLedOffset) {
        if (mHorizontalLedOffset != horizontalLedOffset) {
            mHorizontalLedOffset = horizontalLedOffset;
            setChanged();
        }
    }

    public void setVerticalLedStep(int verticalLedStep) {
        if (mVerticalLedStep != verticalLedStep) {
            mVerticalLedStep = verticalLedStep;
            setChanged();
        }
    }

    public void setVerticalLedPadding(int verticalLedPadding) {
        if (mVerticalLedPadding != verticalLedPadding) {
            mVerticalLedPadding = verticalLedPadding;
            setChanged();
        }
    }

    public void setVerticalLedOffset(int verticalLedOffset) {
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
