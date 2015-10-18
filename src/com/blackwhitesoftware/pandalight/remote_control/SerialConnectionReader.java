package com.blackwhitesoftware.pandalight.remote_control;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by sebastian on 09.10.15.
 */
public class SerialConnectionReader implements Runnable {
    InputStream in;
    ConnectionAdapter adapter;

    public SerialConnectionReader(InputStream in, ConnectionAdapter adapter) {
        this.in = in;
        this.adapter = adapter;
    }

    private void readAll() {
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = this.in.read(buffer, 0, buffer.length)) > -1) {
                adapter.gotData(Arrays.copyOfRange(buffer, 0, len));
            }
        } catch (IOException e) {
            adapter.disconnected();
        }
    }

    @Override
    public void run() {
        readAll();
    }
}
