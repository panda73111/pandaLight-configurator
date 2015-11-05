package com.blackwhitesoftware.pandalight.remote_control;

public abstract class ConnectionAdapter implements ConnectionListener {
    @Override
    public void connected() {
    }

    @Override
    public void disconnected() {
    }

    @Override
    public void sendingData(byte[] data, int offset, int length) {
    }

    @Override
    public void sendingCommand(PandaLightCommand cmd) {
    }

    @Override
    public void gotData(byte[] data, int offset, int length) {
    }

    @Override
    public void gotPacket(PandaLightPacket packet) {
    }
}
