package com.blackwhitesoftware.pandalight.connect;

public abstract class ConnectionListener {

    public void connected() {}

    public void disconnected() {}

    public void pause() {}

    public void unpause() {}

    public void sendingData(byte[] data, int offset, int length) {}

    public void sendingCommand(PandaLightCommand cmd) {}

    public void gotData(byte[] data, int offset, int length) {}

    public void gotPacket(PandaLightPacket packet) {}
}
