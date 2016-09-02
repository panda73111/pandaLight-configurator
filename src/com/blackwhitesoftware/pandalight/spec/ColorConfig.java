package com.blackwhitesoftware.pandalight.spec;

import java.awt.*;
import java.util.Observable;

/**
 * Created by Sebastian.Huether on 30.08.2016.
 */
public class ColorConfig extends Observable {
    public boolean inExpertMode = false;
    public double gammaCorrection = 2.0;
    public Color minChannelValues = new Color(0, 0, 0);
    public Color maxChannelValues = new Color(255, 255, 255);
    public byte[] redLookupTable = new byte[256];
    public byte[] greenLookupTable = new byte[256];
    public byte[] blueLookupTable = new byte[256];

    public ColorConfig() {
        for (int i = 0; i < 256; i++) {
            byte value = (byte) (Math.pow(i / 255.0, gammaCorrection) * 255);
            redLookupTable[i] = value;
            greenLookupTable[i] = value;
            blueLookupTable[i] = value;
        }
    }

    public boolean isInExpertMode() {
        return inExpertMode;
    }

    public double getGammaCorrection() {
        return gammaCorrection;
    }

    public Color getMinChannelValues() {
        return minChannelValues;
    }

    public Color getMaxChannelValues() {
        return maxChannelValues;
    }

    public byte[] getRedLookupTable() {
        return redLookupTable;
    }

    public byte[] getGreenLookupTable() {
        return greenLookupTable;
    }

    public byte[] getBlueLookupTable() {
        return blueLookupTable;
    }

    public void setGammaCorrection(double gammaCorrection) {
        if (this.gammaCorrection != gammaCorrection) {
            this.gammaCorrection = gammaCorrection;
            setChanged();
        }
    }

    public void setMinChannelValues(Color minChannelValues) {
        if (this.minChannelValues != minChannelValues) {
            this.minChannelValues = minChannelValues;
            setChanged();
        }
    }

    public void setMaxChannelValues(Color maxChannelValues) {
        if (this.maxChannelValues != maxChannelValues) {
            this.maxChannelValues = maxChannelValues;
            setChanged();
        }
    }

    public void setRedLookupTable(byte[] redLookupTable) {
        this.redLookupTable = redLookupTable;
        setChanged();
    }

    public void setRedLookupTable(int from, int to) {
        redLookupTable[from] = (byte) to;
        setChanged();
    }

    public void setGreenLookupTable(byte[] greenLookupTable) {
        this.greenLookupTable = greenLookupTable;
        setChanged();
    }

    public void setGreenLookupTable(int from, int to) {
        greenLookupTable[from] = (byte) to;
        setChanged();
    }

    public void setBlueLookupTable(byte[] blueLookupTable) {
        this.blueLookupTable = blueLookupTable;
        setChanged();
    }

    public void setBlueLookupTable(int from, int to) {
        blueLookupTable[from] = (byte) to;
        setChanged();
    }
}
