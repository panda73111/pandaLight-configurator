package com.blackwhitesoftware.pandalight.gui.hardware_tab;

import com.blackwhitesoftware.pandalight.spec.ImageProcessConfig;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Transient;

public class ImageProcessPanel extends JPanel {

    private final ImageProcessConfig mProcessConfig;

    private JLabel mHorizontalLedWidthLabel;
    private JSpinner mHorizontalLedWidthSpinner;

    private JLabel mHorizontalLedHeightLabel;
    private JSpinner mHorizontalLedHeightSpinner;

    private JLabel mVerticalLedWidthLabel;
    private JSpinner mVerticalLedWidthSpinner;

    private JLabel mVerticalLedHeightLabel;
    private JSpinner mVerticalLedHeightSpinner;

    private JLabel mHorizontalLedStepLabel;
    private JSpinner mHorizontalLedStepSpinner;

    private JLabel mHorizontalLedPaddingLabel;
    private JSpinner mHorizontalLedPaddingSpinner;

    private JLabel mHorizontalLedOffsetLabel;
    private JSpinner mHorizontalLedOffsetSpinner;

    private JLabel mVerticalLedStepLabel;
    private JSpinner mVerticalLedStepSpinner;

    private JLabel mVerticalLedPaddingLabel;
    private JSpinner mVerticalLedPaddingSpinner;

    private JLabel mVerticalLedOffsetLabel;
    private JSpinner mVerticalLedOffsetSpinner;

    private JLabel mBlackborderDetectorLabel;
    private JComboBox<String> mBlackborderDetectorCombo;

    private JLabel mBlackborderThresholdLabel;
    private JSpinner mBlackborderThresholdSpinner;
    private final ActionListener mActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Update the processing configuration
            mProcessConfig.setBlackBorderRemoval((mBlackborderDetectorCombo.getSelectedItem() == "On"));

            // set gui state of spinner
            mBlackborderThresholdLabel.setEnabled(mProcessConfig.isBlackBorderRemoval());
            mBlackborderThresholdSpinner.setEnabled(mProcessConfig.isBlackBorderRemoval());

            // Notify observers
            mProcessConfig.notifyObservers(this);
        }
    };
    private final ChangeListener mChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            // Update the processing configuration
            mProcessConfig.setHorizontalLedWidth(((Double) mHorizontalLedWidthSpinner.getValue()) / 100.0);
            mProcessConfig.setHorizontalLedHeight(((Double) mHorizontalLedHeightSpinner.getValue()) / 100.0);
            mProcessConfig.setVerticalLedWidth(((Double) mVerticalLedWidthSpinner.getValue()) / 100.0);
            mProcessConfig.setVerticalLedHeight(((Double) mVerticalLedHeightSpinner.getValue()) / 100.0);
            mProcessConfig.setHorizontalLedStep(((Double) mHorizontalLedStepSpinner.getValue()) / 100.0);
            mProcessConfig.setHorizontalLedPadding(((Double) mHorizontalLedPaddingSpinner.getValue()) / 100.0);
            mProcessConfig.setHorizontalLedOffset(((Double) mHorizontalLedOffsetSpinner.getValue()) / 100.0);
            mProcessConfig.setVerticalLedStep(((Double) mVerticalLedStepSpinner.getValue()) / 100.0);
            mProcessConfig.setVerticalLedPadding(((Double) mVerticalLedPaddingSpinner.getValue()) / 100.0);
            mProcessConfig.setVerticalLedOffset(((Double) mVerticalLedOffsetSpinner.getValue()) / 100.0);
            mProcessConfig.setBlackborderThreshold(((Double) mBlackborderThresholdSpinner.getValue()) / 100.0);

            // Notify observers
            mProcessConfig.notifyObservers(this);
        }
    };

    public ImageProcessPanel(ImageProcessConfig pProcessConfig) {
        super();

        mProcessConfig = pProcessConfig;

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
        setBorder(BorderFactory.createTitledBorder("Image Process"));

        int maximumScreenWidth = (int) Math.pow(2, 11) - 1;
        double maximumPercentage = 255.0 * 100.0 / maximumScreenWidth;
        double percentageStep = 0.5; // maximumPercentage / 255.0;

        mHorizontalLedWidthLabel = new JLabel("Horizontal width [%]:");
        add(mHorizontalLedWidthLabel);

        mHorizontalLedWidthSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedWidth() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mHorizontalLedWidthSpinner.addChangeListener(mChangeListener);
        add(mHorizontalLedWidthSpinner);

        mHorizontalLedHeightLabel = new JLabel("Horizontal height [%]:");
        add(mHorizontalLedHeightLabel);

        mHorizontalLedHeightSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedHeight() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mHorizontalLedHeightSpinner.addChangeListener(mChangeListener);
        add(mHorizontalLedHeightSpinner);

        mVerticalLedWidthLabel = new JLabel("Vertical width [%]:");
        add(mVerticalLedWidthLabel);

        mVerticalLedWidthSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getVerticalLedWidth() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mVerticalLedWidthSpinner.addChangeListener(mChangeListener);
        add(mVerticalLedWidthSpinner);

        mVerticalLedHeightLabel = new JLabel("Vertical height [%]:");
        add(mVerticalLedHeightLabel);

        mVerticalLedHeightSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getVerticalLedHeight() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mVerticalLedHeightSpinner.addChangeListener(mChangeListener);
        add(mVerticalLedHeightSpinner);

        mHorizontalLedStepLabel = new JLabel("Horizontal step [%]:");
        add(mHorizontalLedStepLabel);

        mHorizontalLedStepSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedStep() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mHorizontalLedStepSpinner.addChangeListener(mChangeListener);
        add(mHorizontalLedStepSpinner);

        mHorizontalLedPaddingLabel = new JLabel("Horizontal padding [%]:");
        add(mHorizontalLedPaddingLabel);

        mHorizontalLedPaddingSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedPadding() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mHorizontalLedPaddingSpinner.addChangeListener(mChangeListener);
        add(mHorizontalLedPaddingSpinner);

        mHorizontalLedOffsetLabel = new JLabel("Horizontal offset [%]:");
        add(mHorizontalLedOffsetLabel);

        mHorizontalLedOffsetSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedOffset() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mHorizontalLedOffsetSpinner.addChangeListener(mChangeListener);
        add(mHorizontalLedOffsetSpinner);

        mVerticalLedStepLabel = new JLabel("Vertical step [%]:");
        add(mVerticalLedStepLabel);

        mVerticalLedStepSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getVerticalLedStep() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mVerticalLedStepSpinner.addChangeListener(mChangeListener);
        add(mVerticalLedStepSpinner);

        mVerticalLedPaddingLabel = new JLabel("Vertical padding [%]:");
        add(mVerticalLedPaddingLabel);

        mVerticalLedPaddingSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getVerticalLedPadding() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mVerticalLedPaddingSpinner.addChangeListener(mChangeListener);
        add(mVerticalLedPaddingSpinner);

        mVerticalLedOffsetLabel = new JLabel("Vertical offset [%]:");
        add(mVerticalLedOffsetLabel);

        mVerticalLedOffsetSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getVerticalLedOffset() * 100.0,
                0.0, maximumPercentage, percentageStep));
        mVerticalLedOffsetSpinner.addChangeListener(mChangeListener);
        add(mVerticalLedOffsetSpinner);

        mBlackborderDetectorLabel = new JLabel("Blackborder Detector:");
        add(mBlackborderDetectorLabel);

        mBlackborderDetectorCombo = new JComboBox<>(new String[]{"On", "Off"});
        mBlackborderDetectorCombo.setSelectedItem(mProcessConfig.isBlackBorderRemoval() ? "On" : "Off");
        mBlackborderDetectorCombo.setToolTipText("Enables or disables the blackborder detection and removal");
        mBlackborderDetectorCombo.addActionListener(mActionListener);
        add(mBlackborderDetectorCombo);

        mBlackborderThresholdLabel = new JLabel("Blackborder Threshold [%]:");
        add(mBlackborderThresholdLabel);

        mBlackborderThresholdSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getBlackborderThreshold() * 100.0, -100.0, 100.0, 0.5));
        mBlackborderThresholdSpinner.addChangeListener(mChangeListener);
        add(mBlackborderThresholdSpinner);

        // set gui state of threshold spinner
        mBlackborderThresholdLabel.setEnabled(mProcessConfig.isBlackBorderRemoval());
        mBlackborderThresholdSpinner.setEnabled(mProcessConfig.isBlackBorderRemoval());

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(mHorizontalLedWidthLabel)
                        .addComponent(mHorizontalLedHeightLabel)
                        .addComponent(mVerticalLedWidthLabel)
                        .addComponent(mVerticalLedHeightLabel)
                        .addComponent(mHorizontalLedStepLabel)
                        .addComponent(mHorizontalLedPaddingLabel)
                        .addComponent(mHorizontalLedOffsetLabel)
                        .addComponent(mVerticalLedStepLabel)
                        .addComponent(mVerticalLedPaddingLabel)
                        .addComponent(mVerticalLedOffsetLabel)
                        .addComponent(mBlackborderDetectorLabel)
                        .addComponent(mBlackborderThresholdLabel)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mHorizontalLedWidthSpinner)
                        .addComponent(mHorizontalLedHeightSpinner)
                        .addComponent(mVerticalLedWidthSpinner)
                        .addComponent(mVerticalLedHeightSpinner)
                        .addComponent(mHorizontalLedStepSpinner)
                        .addComponent(mHorizontalLedPaddingSpinner)
                        .addComponent(mHorizontalLedOffsetSpinner)
                        .addComponent(mVerticalLedStepSpinner)
                        .addComponent(mVerticalLedPaddingSpinner)
                        .addComponent(mVerticalLedOffsetSpinner)
                        .addComponent(mBlackborderDetectorCombo)
                        .addComponent(mBlackborderThresholdSpinner)
                )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(mHorizontalLedWidthLabel)
                        .addComponent(mHorizontalLedWidthSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mHorizontalLedHeightLabel)
                        .addComponent(mHorizontalLedHeightSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mVerticalLedWidthLabel)
                        .addComponent(mVerticalLedWidthSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mVerticalLedHeightLabel)
                        .addComponent(mVerticalLedHeightSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mHorizontalLedStepLabel)
                        .addComponent(mHorizontalLedStepSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mHorizontalLedPaddingLabel)
                        .addComponent(mHorizontalLedPaddingSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mHorizontalLedOffsetLabel)
                        .addComponent(mHorizontalLedOffsetSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mVerticalLedStepLabel)
                        .addComponent(mVerticalLedStepSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mVerticalLedPaddingLabel)
                        .addComponent(mVerticalLedPaddingSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mVerticalLedOffsetLabel)
                        .addComponent(mVerticalLedOffsetSpinner)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mBlackborderDetectorLabel)
                        .addComponent(mBlackborderDetectorCombo)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(mBlackborderThresholdLabel)
                        .addComponent(mBlackborderThresholdSpinner)
                )
        );
    }
}
