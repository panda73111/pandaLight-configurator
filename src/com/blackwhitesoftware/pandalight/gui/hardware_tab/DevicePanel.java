package com.blackwhitesoftware.pandalight.gui.hardware_tab;

import com.blackwhitesoftware.pandalight.spec.ColorByteOrder;
import com.blackwhitesoftware.pandalight.spec.DeviceConfig;
import com.blackwhitesoftware.pandalight.spec.DeviceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Transient;

public class DevicePanel extends JPanel {

    private final DeviceConfig mDeviceConfig;

    private JLabel mTypeLabel;
    private JComboBox<DeviceType> mTypeCombo;

    private JLabel mRgbLabel;
    private JComboBox<ColorByteOrder> mRgbCombo;
    private final ActionListener mActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mDeviceConfig.mType = (DeviceType) mTypeCombo.getSelectedItem();
            mDeviceConfig.mColorByteOrder = (ColorByteOrder) mRgbCombo.getSelectedItem();
        }
    };

    public DevicePanel(DeviceConfig pDeviceConfig) {
        super();
        mDeviceConfig = pDeviceConfig;
        initialise();
    }

    @Override
    @Transient
    public Dimension getMaximumSize() {
        Dimension maxSize = super.getMaximumSize();
        Dimension prefSize = super.getPreferredSize();
        return new Dimension(maxSize.width, prefSize.height);
    }

    private void initialise() {
        setBorder(BorderFactory.createTitledBorder("Device"));

        mTypeLabel = new JLabel("Type: ");
        mTypeLabel.setMinimumSize(new Dimension(80, 10));
        add(mTypeLabel);

        mTypeCombo = new JComboBox<>(DeviceType.values());
        mTypeCombo.setSelectedItem(mDeviceConfig.mType);
        mTypeCombo.addActionListener(mActionListener);
        add(mTypeCombo);

        mRgbLabel = new JLabel("RGB Byte Order: ");
        mRgbLabel.setMinimumSize(new Dimension(80, 10));
        add(mRgbLabel);

        mRgbCombo = new JComboBox<>(ColorByteOrder.values());
        mRgbCombo.setSelectedItem(mDeviceConfig.mColorByteOrder);
        mRgbCombo.addActionListener(mActionListener);
        add(mRgbCombo);

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(mTypeLabel)
                        .addComponent(mTypeCombo))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(mRgbLabel)
                        .addComponent(mRgbCombo)));
        layout.setVerticalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(mTypeLabel)
                        .addComponent(mRgbLabel))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(mTypeCombo)
                        .addComponent(mRgbCombo)));
    }
}
