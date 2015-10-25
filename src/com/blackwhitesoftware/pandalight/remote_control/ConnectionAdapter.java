package com.blackwhitesoftware.pandalight.remote_control;

import com.blackwhitesoftware.pandalight.PandaLightCommand;

public abstract class ConnectionAdapter implements ConnectionListener {

    @Override
    public void connected() {
    }

    @Override
    public void disconnected() {
    }

    @Override
    public void sendingCommand(PandaLightCommand cmd) {
    }

    @Override
    public void gotData(byte[] data, int offset, int length) {
    }

}
