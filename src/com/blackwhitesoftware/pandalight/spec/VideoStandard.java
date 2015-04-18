package com.blackwhitesoftware.pandalight.spec;

/**
 * Created by Fabian on 22.02.2015.
 */
public enum VideoStandard{
    noChange("no-change"),
    PAL("PAL"),
    NTSC("NTSC");

    private final String text;

    private VideoStandard(final String text){
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }
}