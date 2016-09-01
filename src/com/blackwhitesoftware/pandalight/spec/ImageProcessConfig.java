package com.blackwhitesoftware.pandalight.spec;

import java.util.Observable;
import java.util.Observer;

/**
 * Configuration parameters for the image processing.
 */
public class ImageProcessConfig extends Observable implements Observer {
    public DimensionalImageProcessConfig horizontal = new DimensionalImageProcessConfig();
    public DimensionalImageProcessConfig vertical = new DimensionalImageProcessConfig();

    // Blackborder detection
    public boolean mBlackBorderRemoval = true;
    public double mBlackBorderThreshold = 0.01;

    /**
     * The number of frames the LED output is delayed by
     */
    public int mFrameDelay = 0;

    public ImageProcessConfig() {
        horizontal.addObserver(this);
        vertical.addObserver(this);

        horizontal.mLedWidth = 60.0 / 1280;
        horizontal.mLedHeight = 80.0 / 720;
        horizontal.mLedStep = 80.0 / 1280;
        horizontal.mLedPadding = 5.0 / 720;
        horizontal.mLedOffset = 10.0 / 1280;
        vertical.mLedWidth = 80.0 / 1280;
        vertical.mLedHeight = 60.0 / 720;
        vertical.mLedStep = 80.0 / 720;
        vertical.mLedPadding = 5.0 / 1280;
        vertical.mLedOffset = 10.0 / 720;
    }

    public boolean isBlackBorderRemoval() {
        return mBlackBorderRemoval;
    }

    public double getBlackborderThreshold() {
        return mBlackBorderThreshold;
    }

    public int getFrameDelay() {
        return mFrameDelay;
    }

    public void setBlackBorderRemoval(boolean pBlackBorderRemoval) {
        mBlackBorderRemoval = pBlackBorderRemoval;
    }

    public void setBlackborderThreshold(double pThreshold) {
        mBlackBorderThreshold = pThreshold;
    }

    public void setFrameDelay(int frameDelay) {
        this.mFrameDelay = frameDelay;
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        horizontal.addObserver(observer);
        vertical.addObserver(observer);
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        horizontal.deleteObserver(observer);
        vertical.deleteObserver(observer);
    }

    @Override
    public synchronized void deleteObservers() {
        horizontal.deleteObservers();
        vertical.deleteObservers();
    }

    @Override
    public synchronized boolean hasChanged() {
        return horizontal.hasChanged() || vertical.hasChanged();
    }

    @Override
    public void update(Observable observable, Object o) {
        setChanged();
        notifyObservers(o);
    }
}
