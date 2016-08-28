package com.blackwhitesoftware.pandalight.gui.hardware_tab;

import com.blackwhitesoftware.pandalight.spec.ImageProcessConfig;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Transient;
import java.util.ArrayList;

public class ImageProcessPanel extends JPanel {

    private final ImageProcessConfig mProcessConfig;
    private boolean mInitializing;

    private JSpinner mHorizontalLedWidthSpinner;
    private JSpinner mHorizontalLedHeightSpinner;
    private JSpinner mVerticalLedWidthSpinner;
    private JSpinner mVerticalLedHeightSpinner;
    private JSpinner mHorizontalLedStepSpinner;
    private JSpinner mHorizontalLedPaddingSpinner;
    private JSpinner mHorizontalLedOffsetSpinner;
    private JSpinner mVerticalLedStepSpinner;
    private JSpinner mVerticalLedPaddingSpinner;
    private JSpinner mVerticalLedOffsetSpinner;

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
            if (mInitializing)
                return;

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

        mInitializing = true;
        initialise();
        mInitializing = false;
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

        SpinnerNumberModel horizontalLedWidthNumberModel = new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedWidth() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel horizontalLedHeightNumberModel = new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedHeight() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel horizontalLedStepNumberModel = new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedStep() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel horizontalLedPaddingNumberModel = new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedPadding() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel horizontalLedOffsetNumberModel = new SpinnerNumberModel(
                mProcessConfig.getHorizontalLedOffset() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel verticalLedWidthNumberModel = new SpinnerNumberModel(
                mProcessConfig.getVerticalLedWidth() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel verticalLedHeightNumberModel = new SpinnerNumberModel(
                mProcessConfig.getVerticalLedHeight() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel verticalLedStepNumberModel = new SpinnerNumberModel(
                mProcessConfig.getVerticalLedStep() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel verticalLedPaddingNumberModel = new SpinnerNumberModel(
                mProcessConfig.getVerticalLedPadding() * 100.0,
                0.0, maximumPercentage, percentageStep);

        SpinnerNumberModel verticalLedOffsetNumberModel = new SpinnerNumberModel(
                mProcessConfig.getVerticalLedOffset() * 100.0,
                0.0, maximumPercentage, percentageStep);

        JLabel mHorizontalLedWidthLabel = new JLabel();
        mHorizontalLedWidthSpinner = new JSpinner(horizontalLedWidthNumberModel);

        JLabel mHorizontalLedHeightLabel = new JLabel();
        mHorizontalLedHeightSpinner = new JSpinner(horizontalLedHeightNumberModel);

        JLabel mHorizontalLedStepLabel = new JLabel();
        mHorizontalLedStepSpinner = new JSpinner(horizontalLedStepNumberModel);

        JLabel mHorizontalLedPaddingLabel = new JLabel();
        mHorizontalLedPaddingSpinner = new JSpinner(horizontalLedPaddingNumberModel);

        JLabel mHorizontalLedOffsetLabel = new JLabel();
        mHorizontalLedOffsetSpinner = new JSpinner(horizontalLedOffsetNumberModel);

        JLabel mVerticalLedWidthLabel = new JLabel();
        mVerticalLedWidthSpinner = new JSpinner(verticalLedWidthNumberModel);

        JLabel mVerticalLedHeightLabel = new JLabel();
        mVerticalLedHeightSpinner = new JSpinner(verticalLedHeightNumberModel);

        JLabel mVerticalLedStepLabel = new JLabel();
        mVerticalLedStepSpinner = new JSpinner(verticalLedStepNumberModel);

        JLabel mVerticalLedPaddingLabel = new JLabel();
        mVerticalLedPaddingSpinner = new JSpinner(verticalLedPaddingNumberModel);

        JLabel mVerticalLedOffsetLabel = new JLabel();
        mVerticalLedOffsetSpinner = new JSpinner(verticalLedOffsetNumberModel);

        JLabel mBlackborderDetectorLabel = new JLabel();
        mBlackborderDetectorCombo = new JComboBox<>(new String[]{"On", "Off"});
        mBlackborderDetectorCombo.setSelectedItem(mProcessConfig.isBlackBorderRemoval() ? "On" : "Off");

        mBlackborderThresholdLabel = new JLabel();
        mBlackborderThresholdSpinner = new JSpinner(new SpinnerNumberModel(
                mProcessConfig.getBlackborderThreshold() * 100.0, -100.0, 100.0, 0.5));

        // set gui state of threshold spinner
        mBlackborderThresholdLabel.setEnabled(mProcessConfig.isBlackBorderRemoval());
        mBlackborderThresholdSpinner.setEnabled(mProcessConfig.isBlackBorderRemoval());

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        ArrayList<SettingsInput> inputs = new ArrayList<>();

        inputs.add(new SettingsInput(mHorizontalLedWidthLabel, mHorizontalLedWidthSpinner, "Horizontal width [%]:", mChangeListener));
        inputs.add(new SettingsInput(mHorizontalLedHeightLabel, mHorizontalLedHeightSpinner, "Horizontal height [%]:", mChangeListener));
        inputs.add(new SettingsInput(mHorizontalLedStepLabel, mHorizontalLedStepSpinner, "Horizontal step [%]:", mChangeListener));
        inputs.add(new SettingsInput(mHorizontalLedPaddingLabel, mHorizontalLedPaddingSpinner, "Horizontal padding [%]:", mChangeListener));
        inputs.add(new SettingsInput(mHorizontalLedOffsetLabel, mHorizontalLedOffsetSpinner, "Horizontal offset [%]:", mChangeListener));

        inputs.add(new SettingsInput(mVerticalLedWidthLabel, mVerticalLedWidthSpinner, "Vertical width [%]:", mChangeListener));
        inputs.add(new SettingsInput(mVerticalLedHeightLabel, mVerticalLedHeightSpinner, "Vertical height [%]:", mChangeListener));
        inputs.add(new SettingsInput(mVerticalLedStepLabel, mVerticalLedStepSpinner, "Vertical step [%]:", mChangeListener));
        inputs.add(new SettingsInput(mVerticalLedPaddingLabel, mVerticalLedPaddingSpinner, "Vertical padding [%]:", mChangeListener));
        inputs.add(new SettingsInput(mVerticalLedOffsetLabel, mVerticalLedOffsetSpinner, "Vertical offset [%]:", mChangeListener));

        inputs.add(new SettingsInput(mBlackborderDetectorLabel, mBlackborderDetectorCombo, "Blackborder Detector:", mActionListener));
        inputs.add(new SettingsInput(mBlackborderThresholdLabel, mBlackborderThresholdSpinner, "Blackborder Threshold [%]:", mChangeListener));

        GroupLayout.ParallelGroup horizontalLabelGroup = layout.createParallelGroup();
        GroupLayout.ParallelGroup horizontalInputGroup = layout.createParallelGroup();
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

        for (SettingsInput input : inputs) {
            JLabel label = input.getLabel();
            JComponent component = input.getComponent();

            label.setText(input.getLabelText());

            if (component instanceof JSpinner) {
                JSpinner spinner = (JSpinner) component;
                spinner.addChangeListener(input.getChangeListener());
            }
            else if (component instanceof  JComboBox) {
                ((JComboBox) component).addActionListener(input.getActionListener());
            }

            add(label);
            add(component);

            horizontalLabelGroup.addComponent(label);
            horizontalInputGroup.addComponent(component);

            GroupLayout.ParallelGroup verticalPairGroup = layout.createParallelGroup();
            verticalPairGroup.addComponent(label);
            verticalPairGroup.addComponent(component);
            verticalGroup.addGroup(verticalPairGroup);
        }

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(horizontalLabelGroup)
                .addGroup(horizontalInputGroup));

        layout.setVerticalGroup(verticalGroup);
    }

    private class SettingsInput {
        private JLabel mLabel;
        private JComponent mComponent;
        private String mLabelText;
        private ChangeListener mChangeListener;
        private ActionListener mActionListener;

        SettingsInput(JLabel label, JComponent component, String labelText) {
            mLabel = label;
            mComponent = component;
            mLabelText = labelText;
        }

        SettingsInput(JLabel label, JComponent input, String labelText, ChangeListener changeListener) {
            this(label, input, labelText);
            mChangeListener = changeListener;
        }

        SettingsInput(JLabel label, JComponent input, String labelText, ActionListener actionListener) {
            this(label, input, labelText);
            mActionListener = actionListener;
        }

        JLabel getLabel() {
            return mLabel;
        }

        JComponent getComponent() {
            return mComponent;
        }

        String getLabelText() {
            return mLabelText;
        }

        ChangeListener getChangeListener() {
            return mChangeListener;
        }

        ActionListener getActionListener() {
            return mActionListener;
        }
    }
}
