package com.blackwhitesoftware.pandalight.ssh;

public abstract class ConnectionAdapter implements ConnectionListener {

    @Override
    public void connected() {
    }

    @Override
    public void disconnected() {
    }

    @Override
    public void addLine(String pLine) {
    }

    @Override
    public void addError(String pLine) {
    }

}
