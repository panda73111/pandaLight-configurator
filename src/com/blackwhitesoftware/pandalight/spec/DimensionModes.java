package com.blackwhitesoftware.pandalight.spec;

/**
 * Created by Fabian on 22.02.2015.
 */
public enum DimensionModes {
    TwoD("2D"),
    ThreeDSBS("3DSBS"),
    ThreeDTAB("3DTAB");

    private final String text;

    DimensionModes(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
