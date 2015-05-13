package com.blackwhitesoftware.pandalight.spec;

/**
 * @author Fabian Hertwig
 *
 */
public class SerialAndColorPickerConfig {
	
	public String portName;
	public boolean colorPickerInExpertmode;
	public boolean colorPickerShowColorWheel;
	
	
	
	/**Constructor
	 * 
	 */
	public SerialAndColorPickerConfig() {
		portName = "COM1";
		colorPickerInExpertmode = false;
		colorPickerShowColorWheel = true;
	}

}
