package com.blackwhitesoftware.pandalight.spec;

public enum ColorByteOrder {
    RGB(0),
    RBG(1),
    GRB(2),
    GBR(3),
    BRG(4),
    BGR(5);

    /**
     * The number representation of this configuration
     */
    private final int mIndex;

    ColorByteOrder(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }
}
