package com.blackwhitesoftware.pandalight.gui;

import com.blackwhitesoftware.pandalight.Main;
import com.blackwhitesoftware.pandalight.remote_control.PandaLightSerialConnection;
import org.pmw.tinylog.*;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.Writer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by hudini on 20.08.2016.
 */
public class DebugDialog extends JFrame {
    private final PandaLightSerialConnection mSerialConnection;
    private final JPanel mContentPanel;
    private final JTabbedPane mTabs;
    private final HashMap<String, JTable> logBoxes;

    public DebugDialog(PandaLightSerialConnection serialConnection) {
        mSerialConnection = serialConnection;
        mContentPanel = new JPanel();
        mTabs = new JTabbedPane();
        logBoxes = new HashMap<>();
        initialize();
        setLogger();
    }

    private void initialize() {
        mContentPanel.setLayout(new BorderLayout());
        mContentPanel.add(mTabs);

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
        config.activate();
    }

    private class LogWriter implements Writer {
        private final DateFormat mDateFormat;

        public LogWriter() {
            mDateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        }

        @Override
        public Set<LogEntryValue> getRequiredLogEntryValues() {
            return EnumSet.of(
                    LogEntryValue.MESSAGE,
                    LogEntryValue.THREAD,
                    LogEntryValue.DATE);
        }

        @Override
        public void init(Configuration configuration) throws Exception {

        }

        @Override
        public synchronized void write(LogEntry logEntry) throws Exception {
            String threadName = logEntry.getThread().getName();
            JTable table;
            DefaultTableModel model;

            if (logBoxes.containsKey(threadName)) {
                table = logBoxes.get(threadName);
                model = (DefaultTableModel)table.getModel();
            }
            else
            {
                model = new DefaultTableModel(new Object[] {"Time", "Message"}, 0);
                table = new JTable(model);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.getColumnModel().getColumn(0).setMaxWidth(100);

                JScrollPane tab = new JScrollPane(table);
                mTabs.add(threadName, tab);

                logBoxes.put(threadName, table);
            }

            model.addRow(new Object[] {
                    mDateFormat.format(logEntry.getDate()),
                    logEntry.getMessage()
            });
        }

        @Override
        public void flush() throws Exception {

        }

        @Override
        public void close() throws Exception {

        }
    }
}
