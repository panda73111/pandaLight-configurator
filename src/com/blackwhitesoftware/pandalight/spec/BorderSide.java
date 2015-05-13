package com.blackwhitesoftware.pandalight.spec;

/**
 * Enumeration of possible led-locations (aka border-sides). This also contains the specification of
 * the angle at which the led is placed along a specific border (0.0rad = pointing right).
 */
public enum BorderSide {
	top          (0.00 * Math.PI),
	top_left     (0.25 * Math.PI),
	left         (0.50 * Math.PI),
	bottom_left  (0.75 * Math.PI),
	bottom       (1.00 * Math.PI),
	bottom_right (1.25 * Math.PI),
	right        (1.50 * Math.PI),
	top_right    (1.75 * Math.PI);
	
	/** The angle of the led [rad] */
	private final double mAngle_rad;
	
	/**
	 * Constructs the BorderSide with the given led angle
	 * 
	 * @param pAngle_rad  The angle of the led [rad]
	 */
	BorderSide(double pAngle_rad) {
		mAngle_rad = pAngle_rad;
	}
	
	/**
	 * Returns the angle of the led placement
	 * 
	 * @return The angle of the led [rad]
	 */
	public double getAngle_rad() {
		return mAngle_rad;
	}
}
