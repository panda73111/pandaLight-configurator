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

    public ImageProcessConfig() {
        horizontal.addObserver(this);
        vertical.addObserver(this);

        horizontal.mLedWidth = 4;
        horizontal.mLedHeight = 7;
        horizontal.mLedStep = 6.267;
        horizontal.mLedPadding = 2;
        horizontal.mLedOffset = 1;
        vertical.mLedWidth = 4;
        vertical.mLedHeight = 7;
        vertical.mLedStep = 11.125;
        vertical.mLedPadding = 1;
        vertical.mLedOffset = 2;
    }

    public boolean isBlackBorderRemoval() {
        return mBlackBorderRemoval;
    }

    public double getBlackborderThreshold() {
        return mBlackBorderThreshold;
    }

    public void setBlackBorderRemoval(boolean pBlackBorderRemoval) {
        mBlackBorderRemoval = pBlackBorderRemoval;
    }

    public void setBlackborderThreshold(double pThreshold) {
        mBlackBorderThreshold = pThreshold;
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
