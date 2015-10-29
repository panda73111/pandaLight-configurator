package com.blackwhitesoftware.pandalight.gui.hardware_tab.device;

import com.blackwhitesoftware.pandalight.spec.DeviceConfig;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LightPackPanel extends DeviceTypePanel {

    private JLabel mSerialNoLabel;
    private JTextField mSerialNoField;

    public LightPackPanel() {
        super();

        initialise();
    }

    @Override
    public void setDeviceConfig(DeviceConfig pDeviceConfig) {
        super.setDeviceConfig(pDeviceConfig);

        // Make sure that the device specific configuration (and only device specific) is set
        String output = getValue("output", "");
        mDeviceConfig.mDeviceProperties.clear();
        mDeviceConfig.mDeviceProperties.put("output", output);

        mSerialNoField.setText(output);
    }

    private void initialise() {
        mSerialNoLabel = new JLabel("Serial #: ");
        mSerialNoLabel.setMinimumSize(firstColMinDim);
        add(mSerialNoLabel);

        mSerialNoField = new JTextField();
        mSerialNoField.setMaximumSize(maxDim);
        mSerialNoField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mSerialNoField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mSerialNoField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mSerialNoField.getText());
            }
        });
        add(mSerialNoField);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(mSerialNoLabel)
                .addComponent(mSerialNoField));
        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(mSerialNoLabel)
                .addComponent(mSerialNoField));
    }

}
