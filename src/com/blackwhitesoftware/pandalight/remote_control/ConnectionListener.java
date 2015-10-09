package com.blackwhitesoftware.pandalight.remote_control;

public interface ConnectionListener {

    void connected();

    void disconnected();

    void gotLine(String pLine);
}
