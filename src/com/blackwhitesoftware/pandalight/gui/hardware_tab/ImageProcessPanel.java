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
    public static final int SLIDER_SCALE = 200;

    private final ImageProcessConfig mProcessConfig;
    private boolean mInitializing;

    private JSlider mHorizontalLedWidthSlider;
    private JSlider mHorizontalLedHeightSlider;
    private JSlider mVerticalLedWidthSlider;
    private JSlider mVerticalLedHeightSlider;
    private JSlider mHorizontalLedStepSlider;
    private JSlider mHorizontalLedPaddingSlider;
    private JSlider mHorizontalLedOffsetSlider;
    private JSlider mVerticalLedStepSlider;
    private JSlider mVerticalLedPaddingSlider;
    private JSlider mVerticalLedOffsetSlider;

    private JComboBox<String> mBlackborderDetectorCombo;

    private JLabel mBlackborderThresholdLabel;
    private JSlider mBlackborderThresholdSlider;
    private final ActionListener mActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Update the processing configuration
            mProcessConfig.setBlackBorderRemoval((mBlackborderDetectorCombo.getSelectedItem() == "On"));

            // set gui state of spinner
            mBlackborderThresholdLabel.setEnabled(mProcessConfig.isBlackBorderRemoval());
            mBlackborderThresholdSlider.setEnabled(mProcessConfig.isBlackBorderRemoval());

            // Notify observers
            mProcessConfig.notifyObservers(this);
        }
    };
    private final ChangeListener mChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (mInitializing)
                return;

            if (e.getSource() instanceof JSlider) {
                JSlider slider = (JSlider) e.getSource();
                if (slider.getValueIsAdjusting())
                    return;
            }

            // Update the processing configuration
            mProcessConfig.setHorizontalLedWidth(intToFraction(mHorizontalLedWidthSlider.getValue()));
            mProcessConfig.setHorizontalLedHeight(intToFraction(mHorizontalLedHeightSlider.getValue()));
            mProcessConfig.setVerticalLedWidth(intToFraction(mVerticalLedWidthSlider.getValue()));
            mProcessConfig.setVerticalLedHeight(intToFraction(mVerticalLedHeightSlider.getValue()));
            mProcessConfig.setHorizontalLedStep(intToFraction(mHorizontalLedStepSlider.getValue()));
            mProcessConfig.setHorizontalLedPadding(intToFraction(mHorizontalLedPaddingSlider.getValue()));
            mProcessConfig.setHorizontalLedOffset(intToFraction(mHorizontalLedOffsetSlider.getValue()));
            mProcessConfig.setVerticalLedStep(intToFraction(mVerticalLedStepSlider.getValue()));
            mProcessConfig.setVerticalLedPadding(intToFraction(mVerticalLedPaddingSlider.getValue()));
            mProcessConfig.setVerticalLedOffset(intToFraction(mVerticalLedOffsetSlider.getValue()));
            mProcessConfig.setBlackborderThreshold(mBlackborderThresholdSlider.getValue());

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

        JLabel mHorizontalLedWidthLabel = new JLabel();
        mHorizontalLedWidthSlider = new JSlider();

        JLabel mHorizontalLedHeightLabel = new JLabel();
        mHorizontalLedHeightSlider = new JSlider();

        JLabel mHorizontalLedStepLabel = new JLabel();
        mHorizontalLedStepSlider = new JSlider();

        JLabel mHorizontalLedPaddingLabel = new JLabel();
        mHorizontalLedPaddingSlider = new JSlider();

        JLabel mHorizontalLedOffsetLabel = new JLabel();
        mHorizontalLedOffsetSlider = new JSlider();

        JLabel mVerticalLedWidthLabel = new JLabel();
        mVerticalLedWidthSlider = new JSlider();

        JLabel mVerticalLedHeightLabel = new JLabel();
        mVerticalLedHeightSlider = new JSlider();

        JLabel mVerticalLedStepLabel = new JLabel();
        mVerticalLedStepSlider = new JSlider();

        JLabel mVerticalLedPaddingLabel = new JLabel();
        mVerticalLedPaddingSlider = new JSlider();

        JLabel mVerticalLedOffsetLabel = new JLabel();
        mVerticalLedOffsetSlider = new JSlider();

        JLabel mBlackborderDetectorLabel = new JLabel();
        mBlackborderDetectorCombo = new JComboBox<>(new String[]{"On", "Off"});
        mBlackborderDetectorCombo.setSelectedItem(mProcessConfig.isBlackBorderRemoval() ? "On" : "Off");

        mBlackborderThresholdLabel = new JLabel();
        mBlackborderThresholdSlider = new JSlider();

        // set gui state of threshold spinner
        mBlackborderThresholdLabel.setEnabled(mProcessConfig.isBlackBorderRemoval());
        mBlackborderThresholdSlider.setEnabled(mProcessConfig.isBlackBorderRemoval());

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        ArrayList<SettingsInput> inputs = new ArrayList<>();

        inputs.add(new SettingsInput(mHorizontalLedWidthLabel, mHorizontalLedWidthSlider, "Horizontal width [%]:", mChangeListener, mProcessConfig.getHorizontalLedWidth()));
        inputs.add(new SettingsInput(mHorizontalLedHeightLabel, mHorizontalLedHeightSlider, "Horizontal height [%]:", mChangeListener, mProcessConfig.getHorizontalLedHeight()));
        inputs.add(new SettingsInput(mHorizontalLedStepLabel, mHorizontalLedStepSlider, "Horizontal step [%]:", mChangeListener, mProcessConfig.getHorizontalLedStep()));
        inputs.add(new SettingsInput(mHorizontalLedPaddingLabel, mHorizontalLedPaddingSlider, "Horizontal padding [%]:", mChangeListener, mProcessConfig.getHorizontalLedPadding()));
        inputs.add(new SettingsInput(mHorizontalLedOffsetLabel, mHorizontalLedOffsetSlider, "Horizontal offset [%]:", mChangeListener, mProcessConfig.getHorizontalLedOffset()));

        inputs.add(new SettingsInput(mVerticalLedWidthLabel, mVerticalLedWidthSlider, "Vertical width [%]:", mChangeListener, mProcessConfig.getVerticalLedWidth()));
        inputs.add(new SettingsInput(mVerticalLedHeightLabel, mVerticalLedHeightSlider, "Vertical height [%]:", mChangeListener, mProcessConfig.getVerticalLedHeight()));
        inputs.add(new SettingsInput(mVerticalLedStepLabel, mVerticalLedStepSlider, "Vertical step [%]:", mChangeListener, mProcessConfig.getVerticalLedStep()));
        inputs.add(new SettingsInput(mVerticalLedPaddingLabel, mVerticalLedPaddingSlider, "Vertical padding [%]:", mChangeListener, mProcessConfig.getVerticalLedPadding()));
        inputs.add(new SettingsInput(mVerticalLedOffsetLabel, mVerticalLedOffsetSlider, "Vertical offset [%]:", mChangeListener, mProcessConfig.getVerticalLedOffset()));

        inputs.add(new SettingsInput(mBlackborderDetectorLabel, mBlackborderDetectorCombo, "Blackborder Detector:", mActionListener));
        inputs.add(new SettingsInput(mBlackborderThresholdLabel, mBlackborderThresholdSlider, "Blackborder Threshold [%]:", mChangeListener, mProcessConfig.getBlackborderThreshold()));

        GroupLayout.ParallelGroup horizontalLabelGroup = layout.createParallelGroup();
        GroupLayout.ParallelGroup horizontalInputGroup = layout.createParallelGroup();
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

        for (SettingsInput input : inputs) {
            JLabel label = input.getLabel();
            JComponent component = input.getComponent();

            label.setText(input.getLabelText());

            if (component instanceof JSlider) {
                JSlider slider = (JSlider) component;
                slider.setMinimum(0);
                slider.setMaximum(SLIDER_SCALE);
                slider.setValue(fractionToInt(input.getValue()));
                slider.setMaximumSize(new Dimension(100, 0));
                slider.addChangeListener(input.getChangeListener());
            }
            else if (component instanceof JSpinner) {
                ((JSpinner) component).addChangeListener(input.getChangeListener());
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

    private int fractionToInt(double fraction) {
        return (int) (fraction * SLIDER_SCALE);
    }

    private double intToFraction(int integer) {
        return (double) integer / SLIDER_SCALE;
    }

    private class SettingsInput {
        private final JLabel mLabel;
        private final JComponent mComponent;
        private final String mLabelText;
        private double mValue;
        private ChangeListener mChangeListener;
        private ActionListener mActionListener;

        SettingsInput(JLabel label, JComponent component, String labelText) {
            mLabel = label;
            mComponent = component;
            mLabelText = labelText;
        }

        SettingsInput(JLabel label, JComponent input, String labelText, ChangeListener changeListener, double value) {
            this(label, input, labelText);
            mChangeListener = changeListener;
            mValue = value;
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

        public double getValue() {
            return mValue;
        }

        ChangeListener getChangeListener() {
            return mChangeListener;
        }

        ActionListener getActionListener() {
            return mActionListener;
        }
    }
}
