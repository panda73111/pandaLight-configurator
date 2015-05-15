package com.blackwhitesoftware.pandalight.ssh;

import java.util.Vector;

public class ConnectionMessageCollector extends ConnectionAdapter {

    public final Vector<String> mLines = new Vector<>();

    public final Vector<String> mErrors = new Vector<>();

    @Override
    public void addLine(String pLine) {
        mLines.add(pLine);
    }

    @Override
    public void addError(String pLine) {
        mErrors.add(pLine);
    }
}
