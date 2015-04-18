package com.blackwhitesoftware.pandalight.spec;

import java.util.Observable;



/**
 * The LedFrame describes the construction of leds along the sides of the TV screen.
 */
public class LedFrameConstruction extends Observable {
	/**
	 * Enumeration of the led configuration direction 
	 */
	public enum Direction {
		/** Clockwise led configuration */
		clockwise,
		/** Counter Clockwise led configuration */
		counter_clockwise;
	}
	
	/** True if the leds are organised clockwise else false (counter clockwise) */
	public boolean clockwiseDirection = true;
	
	/** True if the top corners have a led else false */
	public boolean topCorners = true;
	/** True if the bottom corners have a led else false */
	public boolean bottomCorners = true;
	
	/** The number of leds between the top-left corner and the top-right corner of the screen 
		(excluding the corner leds) */
	public int topLedCnt = 16;
	/** The number of leds between the bottom-left corner and the bottom-right corner of the screen
		(excluding the corner leds) */
	public int bottomLedCnt = 16;
	
	/** The number of leds between the top-left corner and the bottom-left corner of the screen
		(excluding the corner leds) */
	public int leftLedCnt = 7;
	/** The number of leds between the top-right corner and the bottom-right corner of the screen
		(excluding the corner leds) */
	public int rightLedCnt = 7;
	
	/** The offset (in leds) of the starting led counted clockwise from the top-left corner */
	public int firstLedOffset = -16;
	
	/**
	 * Returns the total number of leds
	 * 
	 * @return The total number of leds
	 */
	public int getLedCount() {
		int cornerLedCnt = 0;
		if (topCorners)     cornerLedCnt+=2;
		if (bottomCorners)  cornerLedCnt+=2;
		
		return topLedCnt + bottomLedCnt + leftLedCnt + rightLedCnt + cornerLedCnt;
	}
	
	@Override
	public void setChanged() {
		super.setChanged();
	}
}
