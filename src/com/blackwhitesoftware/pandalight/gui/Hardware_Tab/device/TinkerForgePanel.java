package com.blackwhitesoftware.pandalight.gui.Hardware_Tab.device;

import com.blackwhitesoftware.pandalight.spec.DeviceConfig;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TinkerForgePanel extends DeviceTypePanel {

    private JLabel mHostLabel;
    private JTextField mHostField;

    private JLabel mPortLabel;
    private JSpinner mPortSpinner;

    private JLabel mUidLabel;
    private JTextField mUidField;

    private JLabel mRateLabel;
    private JSpinner mRateSpinner;
    private ChangeListener mChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() == mRateSpinner) {
                mDeviceConfig.mDeviceProperties.put("rate", mRateSpinner.getValue());
            } else if (e.getSource() == mPortSpinner) {
                mDeviceConfig.mDeviceProperties.put("port", mPortSpinner.getValue());
            }
        }
    };

    public TinkerForgePanel() {
        super();

        initialise();
    }

    @Override
    public void setDeviceConfig(DeviceConfig pDeviceConfig) {
        super.setDeviceConfig(pDeviceConfig);

        // Make sure that the device specific configuration (and only device specific) is set
        String host = getValue("output", "127.0.0.1");
        int port = getValue("port", 4223);
        String uid = getValue("uid", "");
        int rate = getValue("rate", 100000);
        mDeviceConfig.mDeviceProperties.clear();
        mDeviceConfig.mDeviceProperties.put("output", host);
        mDeviceConfig.mDeviceProperties.put("port", port);
        mDeviceConfig.mDeviceProperties.put("uid", uid);
        mDeviceConfig.mDeviceProperties.put("rate", rate);

        mHostField.setText(host);
        mPortSpinner.getModel().setValue(port);
        mUidField.setText(uid);
        mRateSpinner.getModel().setValue(rate);
    }

    private void initialise() {
        mHostLabel = new JLabel("Host: ");
        mHostLabel.setMinimumSize(firstColMinDim);
        add(mHostLabel);

        mHostField = new JTextField();
        mHostField.setMaximumSize(maxDim);
        mHostField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mHostField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mHostField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mHostField.getText());
            }
        });
        add(mHostField);

        mPortLabel = new JLabel("Baudrate: ");
        mPortLabel.setMinimumSize(firstColMinDim);
        add(mPortLabel);

        mPortSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 128));
        mPortSpinner.setMaximumSize(maxDim);
        mPortSpinner.addChangeListener(mChangeListener);
        add(mPortSpinner);

        mUidLabel = new JLabel("UID: ");
        mUidLabel.setMinimumSize(firstColMinDim);
        add(mUidLabel);

        mUidField = new JTextField();
        mUidField.setMaximumSize(maxDim);
        mUidField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("uid", mUidField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("uid", mUidField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("uid", mUidField.getText());
            }
        });
        add(mUidField);

        mRateLabel = new JLabel("Rate: ");
        mRateLabel.setMinimumSize(firstColMinDim);
        add(mRateLabel);

        mRateSpinner = new JSpinner(new SpinnerNumberModel(100000, 4800, 1000000, 1000));
        mRateSpinner.setMaximumSize(maxDim);
        mRateSpinner.addChangeListener(mChangeListener);
        add(mRateSpinner);


        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mHostLabel)
                                .addComponent(mPortLabel)
                                .addComponent(mUidLabel)
                                .addComponent(mRateLabel))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mHostField)
                                .addComponent(mPortSpinner)
                                .addComponent(mUidField)
                                .addComponent(mRateSpinner))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mHostLabel)
                                .addComponent(mHostField))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mPortLabel)
                                .addComponent(mPortSpinner))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mUidLabel)
                                .addComponent(mUidField))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mRateLabel)
                                .addComponent(mRateSpinner))
        );
    }
}
