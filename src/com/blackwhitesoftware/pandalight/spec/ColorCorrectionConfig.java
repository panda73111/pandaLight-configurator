package com.blackwhitesoftware.pandalight.spec;

import java.awt.*;
import java.util.Observable;

/**
 * Created by Sebastian.Huether on 30.08.2016.
 */
public class ColorCorrectionConfig extends Observable {
    public boolean inExpertMode = false;
    public double gammaCorrection = 2.0;
    public Color minChannelValues = new Color(0, 0, 0);
    public Color maxChannelValues = new Color(255, 255, 255);
    public byte[][] channelLookupTables = new byte[3][256];

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

    public byte[][] getChannelLookupTables() {
        return channelLookupTables;
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

    public void setChannelLookupTables(byte[][] channelLookupTables) {
        this.channelLookupTables = channelLookupTables;
        setChanged();
    }

    public void setChannelLookupTable(int channel, byte[] channelLookupTable) {
        this.channelLookupTables[channel] = channelLookupTable;
        setChanged();
    }

    public void setChannelLookupTable(int channel, byte from, byte to) {
        this.channelLookupTables[channel][from & 0xFF] = to;
        setChanged();
    }
}
