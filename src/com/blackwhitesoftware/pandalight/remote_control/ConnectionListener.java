package com.blackwhitesoftware.pandalight.remote_control;

public interface ConnectionListener {

    void connected();

    void disconnected();

    void sendingCommand(PandaLightCommand cmd);

    void gotData(byte[] data, int offset, int length);

    void gotPacket(PandaLightPacket packet);
}
