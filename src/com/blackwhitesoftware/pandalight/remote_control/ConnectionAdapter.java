package com.blackwhitesoftware.pandalight.remote_control;

public abstract class ConnectionAdapter implements ConnectionListener {

    @Override
    public void connected() {
    }

    @Override
    public void disconnected() {
    }

    @Override
    public void gotData(byte[] data) {
    }

}
