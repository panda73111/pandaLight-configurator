package com.blackwhitesoftware.pandalight.gui;

import com.blackwhitesoftware.pandalight.Main;
import com.blackwhitesoftware.pandalight.remote_control.PandaLightSerialConnection;
import org.pmw.tinylog.*;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.Writer;

import javax.swing.*;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by hudini on 20.08.2016.
 */
public class DebugDialog extends JFrame {
    private final PandaLightSerialConnection mSerialConnection;
    private final JTextArea mTestTextArea;
    private final JPanel mContentPanel;

    public DebugDialog(PandaLightSerialConnection serialConnection) {
        mSerialConnection = serialConnection;
        mContentPanel = new JPanel();
        mTestTextArea = new JTextArea();
        initialize();
        setLogger();
    }

    private void initialize() {
        mContentPanel.add(mTestTextArea);

        GroupLayout layout = new GroupLayout(mContentPanel);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(mTestTextArea));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(mTestTextArea));

        mContentPanel.setLayout(layout);

        setContentPane(mContentPanel);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 800);
        setTitle("Debug");
        setIconImage(new ImageIcon(Main.class.getResource("icon64.png")).getImage());
    }

    private void setLogger() {
        Writer writer = new LogWriter();
        Configurator config = Logger.getConfiguration();
        config.addWriter(writer, Level.TRACE);
    }

    private class LogWriter implements Writer {
        public LogWriter() {
        }

        @Override
        public Set<LogEntryValue> getRequiredLogEntryValues() {
            return EnumSet.of(LogEntryValue.MESSAGE);
        }

        @Override
        public void init(Configuration configuration) throws Exception {

        }

        @Override
        public synchronized void write(LogEntry logEntry) throws Exception {
            mTestTextArea.setText(mTestTextArea.getText() + "\n" + logEntry.getMessage());
        }

        @Override
        public void flush() throws Exception {

        }

        @Override
        public void close() throws Exception {

        }
    }
}
