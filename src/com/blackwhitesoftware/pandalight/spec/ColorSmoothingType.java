package com.blackwhitesoftware.pandalight.spec;

public enum ColorSmoothingType {
    /**
     * Linear smoothing of led data
     */
    linear("Linear smoothing");

    private final String mName;

    ColorSmoothingType(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }
}