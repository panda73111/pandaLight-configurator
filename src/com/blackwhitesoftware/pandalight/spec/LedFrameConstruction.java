package com.blackwhitesoftware.pandalight.spec;

import java.util.Observable;


/**
 * The LedFrame describes the construction of leds along the sides of the TV screen.
 */
public class LedFrameConstruction extends Observable {
    public Direction direction = Direction.clockwise;
    public int horizontalLedCount = 16;
    public int verticalLedCount = 9;
    public int firstLedOffset = 0;

    public int getTotalLedCount() {
        return 2 * horizontalLedCount + 2 * verticalLedCount;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    public enum Direction {
        clockwise,
        counterClockwise
    }
}
