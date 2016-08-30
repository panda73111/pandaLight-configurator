package com.blackwhitesoftware.pandalight.spec;

import java.util.Observable;
import java.util.Observer;

/**
 * Configuration parameters for the image processing.
 */
public class ImageProcessConfig extends Observable {
    public DimensionalImageProcessConfig horizontal = new DimensionalImageProcessConfig();
    public DimensionalImageProcessConfig vertical = new DimensionalImageProcessConfig();

    // Blackborder detection
    public boolean mBlackBorderRemoval = true;
    public double mBlackBorderThreshold = 0.01;

    public boolean isBlackBorderRemoval() {
        return mBlackBorderRemoval;
    }

    public double getBlackborderThreshold() {
        return mBlackBorderThreshold;
    }

    public void setBlackBorderRemoval(boolean pBlackBorderRemoval) {
        if (mBlackBorderRemoval != pBlackBorderRemoval) {
            mBlackBorderRemoval = pBlackBorderRemoval;
            setChanged();
        }
    }

    public void setBlackborderThreshold(double pThreshold) {
        if (mBlackBorderThreshold != pThreshold) {
            mBlackBorderThreshold = pThreshold;
            setChanged();
        }
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
        horizontal.addObserver(observer);
        vertical.addObserver(observer);
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
        horizontal.deleteObserver(observer);
        vertical.deleteObserver(observer);
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
        horizontal.deleteObservers();
        vertical.deleteObservers();
    }

    @Override
    public synchronized int countObservers() {
        return super.countObservers() + horizontal.countObservers() + vertical.countObservers();
    }

    @Override
    public synchronized boolean hasChanged() {
        return super.hasChanged() || horizontal.hasChanged() || vertical.hasChanged();
    }
}
