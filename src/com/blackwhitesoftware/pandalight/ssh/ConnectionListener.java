package com.blackwhitesoftware.pandalight.ssh;

public interface ConnectionListener {

	void connected();
	
	void disconnected();

	void addLine(String pLine);
	
	void addError(String pLine);
}
