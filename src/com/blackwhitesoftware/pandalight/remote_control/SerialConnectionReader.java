package com.blackwhitesoftware.pandalight.remote_control;

import java.io.IOException;
import java.io.InputStream;

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

    private void readLines() {
        byte[] buffer = new byte[1024];
        int len;
        try {
            int lineStart = 0;
            while ((len = this.in.read(buffer, lineStart, buffer.length - lineStart)) > -1) {
                for (int i = lineStart; i < lineStart + len; i++)
                    if (buffer[i] == (byte) '\n') {
                        String line = new String(buffer, lineStart, i - lineStart - 1);
                        adapter.gotLine(line);
                        lineStart = i + 1;
                    }
                if (lineStart == len)
                    lineStart = 0;
            }
        } catch (IOException e) {
            adapter.disconnected();
        }
    }

    @Override
    public void run() {
        readLines();
    }
}
