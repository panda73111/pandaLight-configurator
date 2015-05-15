package com.blackwhitesoftware.pandalight.gui.Hardware_Tab.device;

import com.blackwhitesoftware.pandalight.spec.DeviceConfig;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PiBlasterPanel extends DeviceTypePanel {

    private JLabel mOutputLabel;
    private JTextField mOutputField;

    private JLabel mAssignmentLabel;
    private JTextField mAssignmentField;

    public PiBlasterPanel() {
        super();

        initialise();
    }

    @Override
    public void setDeviceConfig(DeviceConfig pDeviceConfig) {
        super.setDeviceConfig(pDeviceConfig);

        // Make sure that the device specific configuration (and only device specific) is set
        String output = getValue("output", "");
        String assignment = getValue("assignment", "");
        mDeviceConfig.mDeviceProperties.clear();
        mDeviceConfig.mDeviceProperties.put("output", output);
        mDeviceConfig.mDeviceProperties.put("assignment", assignment);

        mOutputField.setText(output);
        mAssignmentField.setText(assignment);
    }

    private void initialise() {
        mOutputLabel = new JLabel("Host: ");
        mOutputLabel.setMinimumSize(firstColMinDim);
        add(mOutputLabel);

        mOutputField = new JTextField();
        mOutputField.setMaximumSize(maxDim);
        mOutputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mOutputField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mOutputField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("output", mOutputField.getText());
            }
        });
        add(mOutputField);

        mAssignmentLabel = new JLabel("Baudrate: ");
        mAssignmentLabel.setMinimumSize(firstColMinDim);
        add(mAssignmentLabel);

        mAssignmentField = new JTextField();
        mAssignmentField.setMaximumSize(maxDim);
        mAssignmentField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("assignment", mAssignmentField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("assignment", mAssignmentField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mDeviceConfig.mDeviceProperties.put("assignment", mAssignmentField.getText());
            }
        });
        add(mAssignmentField);


        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mOutputLabel)
                                .addComponent(mAssignmentLabel))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mOutputField)
                                .addComponent(mAssignmentField))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mOutputLabel)
                                .addComponent(mOutputField))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mAssignmentLabel)
                                .addComponent(mAssignmentField))
        );
    }
}
