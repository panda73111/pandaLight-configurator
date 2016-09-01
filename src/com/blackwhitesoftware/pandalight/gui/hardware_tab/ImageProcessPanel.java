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
    private JSpinner mHorizontalLedStepSpinner;
    private JSpinner mHorizontalLedPaddingSpinner;
    private JSpinner mHorizontalLedOffsetSpinner;
    private JSpinner mVerticalLedWidthSpinner;
    private JSpinner mVerticalLedHeightSpinner;
    private JSpinner mVerticalLedStepSpinner;
    private JSpinner mVerticalLedPaddingSpinner;
    private JSpinner mVerticalLedOffsetSpinner;

    private JSpinner mFrameDelaySpinner;

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
            valuesChanged(mInitializing);
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

    private void valuesChanged(boolean onlyDisplay) {
        double horizontalLedWidth = percentageToFraction((double) mHorizontalLedWidthSpinner.getValue());
        double horizontalLedHeight = percentageToFraction((double) mHorizontalLedHeightSpinner.getValue());
        double horizontalLedStep = percentageToFraction((double) mHorizontalLedStepSpinner.getValue());
        double horizontalLedPadding = percentageToFraction((double) mHorizontalLedPaddingSpinner.getValue());
        double horizontalLedOffset = percentageToFraction((double) mHorizontalLedOffsetSpinner.getValue());
        double verticalLedWidth = percentageToFraction((double) mVerticalLedWidthSpinner.getValue());
        double verticalLedHeight = percentageToFraction((double) mVerticalLedHeightSpinner.getValue());
        double verticalLedStep = percentageToFraction((double) mVerticalLedStepSpinner.getValue());
        double verticalLedPadding = percentageToFraction((double) mVerticalLedPaddingSpinner.getValue());
        double verticalLedOffset = percentageToFraction((double) mVerticalLedOffsetSpinner.getValue());
        double blackborderThreshold = percentageToFraction((double) mBlackborderThresholdSpinner.getValue());
        int frameDelay = (int) (double) mFrameDelaySpinner.getValue();
        mFrameDelaySpinner.setValue((double) frameDelay);

        if (onlyDisplay)
            return;

        // Update the processing configuration
        mProcessConfig.horizontal.setLedWidth(horizontalLedWidth);
        mProcessConfig.horizontal.setLedHeight(horizontalLedHeight);
        mProcessConfig.horizontal.setLedStep(horizontalLedStep);
        mProcessConfig.horizontal.setLedPadding(horizontalLedPadding);
        mProcessConfig.horizontal.setLedOffset(horizontalLedOffset);
        mProcessConfig.vertical.setLedWidth(verticalLedWidth);
        mProcessConfig.vertical.setLedHeight(verticalLedHeight);
        mProcessConfig.vertical.setLedStep(verticalLedStep);
        mProcessConfig.vertical.setLedPadding(verticalLedPadding);
        mProcessConfig.vertical.setLedOffset(verticalLedOffset);
        mProcessConfig.setBlackborderThreshold(blackborderThreshold);
        mProcessConfig.setFrameDelay(frameDelay);

        // Notify observers
        mProcessConfig.notifyObservers(this);
    }

    private void initialise() {
        setBorder(BorderFactory.createTitledBorder("Image Process"));

        mHorizontalLedWidthSpinner = new JSpinner();
        mHorizontalLedHeightSpinner = new JSpinner();
        mHorizontalLedStepSpinner = new JSpinner();
        mHorizontalLedPaddingSpinner = new JSpinner();
        mHorizontalLedOffsetSpinner = new JSpinner();
        mVerticalLedWidthSpinner = new JSpinner();
        mVerticalLedHeightSpinner = new JSpinner();
        mVerticalLedStepSpinner = new JSpinner();
        mVerticalLedPaddingSpinner = new JSpinner();
        mVerticalLedOffsetSpinner = new JSpinner();

        mFrameDelaySpinner = new JSpinner();

        mBlackborderDetectorCombo = new JComboBox<>(new String[]{"On", "Off"});
        mBlackborderDetectorCombo.setSelectedItem(mProcessConfig.isBlackBorderRemoval() ? "On" : "Off");

        mBlackborderThresholdLabel = new JLabel();
        mBlackborderThresholdSpinner = new JSpinner();

        // set gui state of threshold spinner
        mBlackborderThresholdLabel.setEnabled(mProcessConfig.isBlackBorderRemoval());
        mBlackborderThresholdSpinner.setEnabled(mProcessConfig.isBlackBorderRemoval());

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        ArrayList<SettingsInput> inputs = new ArrayList<>();

        inputs.add(new FractionSettingsInput(mHorizontalLedWidthSpinner, "Horizontal width [%]:", mChangeListener, mProcessConfig.horizontal.getLedWidth()));
        inputs.add(new FractionSettingsInput(mHorizontalLedHeightSpinner, "Horizontal height [%]:", mChangeListener, mProcessConfig.horizontal.getLedHeight()));
        inputs.add(new FractionSettingsInput(mHorizontalLedStepSpinner, "Horizontal step [%]:", mChangeListener, mProcessConfig.horizontal.getLedStep()));
        inputs.add(new FractionSettingsInput(mHorizontalLedPaddingSpinner, "Horizontal padding [%]:", mChangeListener, mProcessConfig.horizontal.getLedPadding()));
        inputs.add(new FractionSettingsInput(mHorizontalLedOffsetSpinner, "Horizontal offset [%]:", mChangeListener, mProcessConfig.horizontal.getLedOffset()));

        inputs.add(new FractionSettingsInput(mVerticalLedWidthSpinner, "Vertical width [%]:", mChangeListener, mProcessConfig.vertical.getLedWidth()));
        inputs.add(new FractionSettingsInput(mVerticalLedHeightSpinner, "Vertical height [%]:", mChangeListener, mProcessConfig.vertical.getLedHeight()));
        inputs.add(new FractionSettingsInput(mVerticalLedStepSpinner, "Vertical step [%]:", mChangeListener, mProcessConfig.vertical.getLedStep()));
        inputs.add(new FractionSettingsInput(mVerticalLedPaddingSpinner, "Vertical padding [%]:", mChangeListener, mProcessConfig.vertical.getLedPadding()));
        inputs.add(new FractionSettingsInput(mVerticalLedOffsetSpinner, "Vertical offset [%]:", mChangeListener, mProcessConfig.vertical.getLedOffset()));

        inputs.add(new SettingsInput(mFrameDelaySpinner, "Frame delay:", mChangeListener, mProcessConfig.getFrameDelay()));

        inputs.add(new SettingsInput(mBlackborderDetectorCombo, "Blackborder detector:", mActionListener));
        inputs.add(new FractionSettingsInput(mBlackborderThresholdSpinner, "Blackborder threshold [%]:", mChangeListener, mProcessConfig.getBlackborderThreshold()));

        GroupLayout.ParallelGroup horizontalLabelGroup = layout.createParallelGroup();
        GroupLayout.ParallelGroup horizontalInputGroup = layout.createParallelGroup();
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

        for (SettingsInput input : inputs) {
            JLabel label = new JLabel(input.getLabelText());
            JComponent component = input.getComponent();

            if (component instanceof JSpinner) {
                JSpinner spinner = (JSpinner) component;
                spinner.setModel(new SpinnerNumberModel(input.getValue(), 0.0, 100.0, 1.0));
                spinner.addChangeListener(input.getChangeListener());
            } else if (component instanceof JComboBox) {
                ((JComboBox) component).addActionListener(input.getActionListener());
            }

            add(label);
            add(component);

            horizontalLabelGroup.addComponent(label);
            horizontalInputGroup.addComponent(component);

            GroupLayout.ParallelGroup verticalLineGroup = layout.createParallelGroup();
            verticalLineGroup.addComponent(label);
            verticalLineGroup.addComponent(component);
            verticalGroup.addGroup(verticalLineGroup);
        }

        valuesChanged(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(horizontalLabelGroup)
                .addGroup(horizontalInputGroup));

        layout.setVerticalGroup(verticalGroup);
    }

    private double fractionToPercentage(double fraction) {
        return fraction * 100;
    }

    private double percentageToFraction(double percentage) {
        return percentage / 100;
    }

    private class SettingsInput {
        private final JComponent mComponent;
        private final String mLabelText;
        private double mValue;
        private ChangeListener mChangeListener;
        private ActionListener mActionListener;

        SettingsInput(JComponent component, String labelText) {
            mComponent = component;
            mLabelText = labelText;
        }

        SettingsInput(JComponent component, String labelText, ChangeListener changeListener, double value) {
            this(component, labelText);
            mChangeListener = changeListener;
            mValue = value;
        }

        SettingsInput(JComponent component, String labelText, ActionListener actionListener) {
            this(component, labelText);
            mActionListener = actionListener;
        }

        JComponent getComponent() {
            return mComponent;
        }

        String getLabelText() {
            return mLabelText;
        }

        double getValue() {
            return mValue;
        }

        ChangeListener getChangeListener() {
            return mChangeListener;
        }

        ActionListener getActionListener() {
            return mActionListener;
        }
    }

    private class FractionSettingsInput extends SettingsInput {
        public FractionSettingsInput(JComponent component, String labelText) {
            super(component, labelText);
        }

        public FractionSettingsInput(JComponent component, String labelText, ChangeListener changeListener, double value) {
            super(component, labelText, changeListener, fractionToPercentage(value));
        }

        public FractionSettingsInput(JComponent component, String labelText, ActionListener actionListener) {
            super(component, labelText, actionListener);
        }
    }
}
