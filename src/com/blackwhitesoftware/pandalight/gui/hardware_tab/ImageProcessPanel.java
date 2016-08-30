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
    private JSlider mHorizontalLedStepSlider;
    private JSlider mHorizontalLedPaddingSlider;
    private JSlider mHorizontalLedOffsetSlider;
    private JSlider mVerticalLedWidthSlider;
    private JSlider mVerticalLedHeightSlider;
    private JSlider mVerticalLedStepSlider;
    private JSlider mVerticalLedPaddingSlider;
    private JSlider mVerticalLedOffsetSlider;

    private JLabel mHorizontalLedWidthDisplay;
    private JLabel mHorizontalLedHeightDisplay;
    private JLabel mHorizontalLedStepDisplay;
    private JLabel mHorizontalLedPaddingDisplay;
    private JLabel mHorizontalLedOffsetDisplay;
    private JLabel mVerticalLedWidthDisplay;
    private JLabel mVerticalLedHeightDisplay;
    private JLabel mVerticalLedStepDisplay;
    private JLabel mVerticalLedPaddingDisplay;
    private JLabel mVerticalLedOffsetDisplay;

    private JComboBox<String> mBlackborderDetectorCombo;
    private JLabel mBlackborderThresholdDisplay;

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
            valuesChanged(mInitializing, e.getSource());
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
        valuesChanged(onlyDisplay, null);
    }

   private void valuesChanged(boolean onlyDisplay, Object source) {
        double horizontalLedWidth = intToFraction(mHorizontalLedWidthSlider.getValue());
        double horizontalLedHeight = intToFraction(mHorizontalLedHeightSlider.getValue());
        double horizontalLedStep = intToFraction(mHorizontalLedStepSlider.getValue());
        double horizontalLedPadding = intToFraction(mHorizontalLedPaddingSlider.getValue());
        double horizontalLedOffset = intToFraction(mHorizontalLedOffsetSlider.getValue());
        double verticalLedWidth = intToFraction(mVerticalLedWidthSlider.getValue());
        double verticalLedHeight = intToFraction(mVerticalLedHeightSlider.getValue());
        double verticalLedStep = intToFraction(mVerticalLedStepSlider.getValue());
        double verticalLedPadding = intToFraction(mVerticalLedPaddingSlider.getValue());
        double verticalLedOffset = intToFraction(mVerticalLedOffsetSlider.getValue());
        double blackborderThreshold = intToFraction(mBlackborderThresholdSlider.getValue());

        String formatString = "[%3.0f%%]";
        mHorizontalLedWidthDisplay.setText(String.format(formatString, horizontalLedWidth * 100));
        mHorizontalLedHeightDisplay.setText(String.format(formatString, horizontalLedHeight * 100));
        mHorizontalLedStepDisplay.setText(String.format(formatString, horizontalLedStep * 100));
        mHorizontalLedPaddingDisplay.setText(String.format(formatString, horizontalLedPadding * 100));
        mHorizontalLedOffsetDisplay.setText(String.format(formatString, horizontalLedOffset * 100));
        mVerticalLedWidthDisplay.setText(String.format(formatString, verticalLedWidth * 100));
        mVerticalLedHeightDisplay.setText(String.format(formatString, verticalLedHeight * 100));
        mVerticalLedStepDisplay.setText(String.format(formatString, verticalLedStep * 100));
        mVerticalLedPaddingDisplay.setText(String.format(formatString, verticalLedPadding * 100));
        mVerticalLedOffsetDisplay.setText(String.format(formatString, verticalLedOffset * 100));
        mBlackborderThresholdDisplay.setText(String.format(formatString, blackborderThreshold * 100));

        if (onlyDisplay)
            return;

        if (source != null && source instanceof JSlider) {
            JSlider slider = (JSlider) source;
            if (slider.getValueIsAdjusting())
                return;
        }

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

        // Notify observers
        mProcessConfig.notifyObservers(this);
    }

    private void initialise() {
        setBorder(BorderFactory.createTitledBorder("Image Process"));

        mHorizontalLedWidthSlider = new JSlider();
        mHorizontalLedHeightSlider = new JSlider();
        mHorizontalLedStepSlider = new JSlider();
        mHorizontalLedPaddingSlider = new JSlider();
        mHorizontalLedOffsetSlider = new JSlider();
        mVerticalLedWidthSlider = new JSlider();
        mVerticalLedHeightSlider = new JSlider();
        mVerticalLedStepSlider = new JSlider();
        mVerticalLedPaddingSlider = new JSlider();
        mVerticalLedOffsetSlider = new JSlider();

        mHorizontalLedWidthDisplay = new JLabel();
        mHorizontalLedHeightDisplay = new JLabel();
        mHorizontalLedStepDisplay = new JLabel();
        mHorizontalLedPaddingDisplay = new JLabel();
        mHorizontalLedOffsetDisplay = new JLabel();
        mVerticalLedWidthDisplay = new JLabel();
        mVerticalLedHeightDisplay = new JLabel();
        mVerticalLedStepDisplay = new JLabel();
        mVerticalLedPaddingDisplay = new JLabel();
        mVerticalLedOffsetDisplay = new JLabel();

        mBlackborderDetectorCombo = new JComboBox<>(new String[]{"On", "Off"});
        mBlackborderDetectorCombo.setSelectedItem(mProcessConfig.isBlackBorderRemoval() ? "On" : "Off");

        mBlackborderThresholdLabel = new JLabel();
        mBlackborderThresholdSlider = new JSlider();
        mBlackborderThresholdDisplay = new JLabel();

        // set gui state of threshold spinner
        mBlackborderThresholdLabel.setEnabled(mProcessConfig.isBlackBorderRemoval());
        mBlackborderThresholdSlider.setEnabled(mProcessConfig.isBlackBorderRemoval());

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        ArrayList<SettingsInput> inputs = new ArrayList<>();

        inputs.add(new SettingsInput(mHorizontalLedWidthSlider, "Horizontal width:", mHorizontalLedWidthDisplay, mChangeListener, mProcessConfig.horizontal.getLedWidth()));
        inputs.add(new SettingsInput(mHorizontalLedHeightSlider, "Horizontal height:", mHorizontalLedHeightDisplay, mChangeListener, mProcessConfig.horizontal.getLedHeight()));
        inputs.add(new SettingsInput(mHorizontalLedStepSlider, "Horizontal step:", mHorizontalLedStepDisplay, mChangeListener, mProcessConfig.horizontal.getLedStep()));
        inputs.add(new SettingsInput(mHorizontalLedPaddingSlider, "Horizontal padding:", mHorizontalLedPaddingDisplay, mChangeListener, mProcessConfig.horizontal.getLedPadding()));
        inputs.add(new SettingsInput(mHorizontalLedOffsetSlider, "Horizontal offset:", mHorizontalLedOffsetDisplay, mChangeListener, mProcessConfig.horizontal.getLedOffset()));

        inputs.add(new SettingsInput(mVerticalLedWidthSlider, "Vertical width:", mVerticalLedWidthDisplay, mChangeListener, mProcessConfig.vertical.getLedWidth()));
        inputs.add(new SettingsInput(mVerticalLedHeightSlider, "Vertical height:", mVerticalLedHeightDisplay, mChangeListener, mProcessConfig.vertical.getLedHeight()));
        inputs.add(new SettingsInput(mVerticalLedStepSlider, "Vertical step:", mVerticalLedStepDisplay, mChangeListener, mProcessConfig.vertical.getLedStep()));
        inputs.add(new SettingsInput(mVerticalLedPaddingSlider, "Vertical padding:", mVerticalLedPaddingDisplay, mChangeListener, mProcessConfig.vertical.getLedPadding()));
        inputs.add(new SettingsInput(mVerticalLedOffsetSlider, "Vertical offset:", mVerticalLedOffsetDisplay, mChangeListener, mProcessConfig.vertical.getLedOffset()));

        inputs.add(new SettingsInput(mBlackborderDetectorCombo, "Blackborder Detector:", mActionListener));
        inputs.add(new SettingsInput(mBlackborderThresholdSlider, "Blackborder Threshold:", mBlackborderThresholdDisplay, mChangeListener, mProcessConfig.getBlackborderThreshold()));

        GroupLayout.ParallelGroup horizontalLabelGroup = layout.createParallelGroup();
        GroupLayout.ParallelGroup horizontalDisplayGroup = layout.createParallelGroup();
        GroupLayout.ParallelGroup horizontalInputGroup = layout.createParallelGroup();
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

        for (SettingsInput input : inputs) {
            JLabel label = new JLabel(input.getLabelText());
            JLabel display = input.getDisplay();
            display.setMinimumSize(new Dimension(40, 0));
            display.setHorizontalAlignment(JLabel.RIGHT);
            JComponent component = input.getComponent();

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
            add(display);

            horizontalLabelGroup.addComponent(label);
            horizontalInputGroup.addComponent(component);
            horizontalDisplayGroup.addComponent(display);

            GroupLayout.ParallelGroup verticalLineGroup = layout.createParallelGroup();
            verticalLineGroup.addComponent(label);
            verticalLineGroup.addComponent(component);
            verticalLineGroup.addComponent(display);
            verticalGroup.addGroup(verticalLineGroup);
        }

        valuesChanged(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(horizontalLabelGroup)
                .addGroup(horizontalInputGroup)
                .addGroup(horizontalDisplayGroup));

        layout.setVerticalGroup(verticalGroup);
    }

    private int fractionToInt(double fraction) {
        return (int) (fraction * SLIDER_SCALE);
    }

    private double intToFraction(int integer) {
        return (double) integer / SLIDER_SCALE;
    }

    private class SettingsInput {
        private final JLabel mDisplay;
        private final JComponent mComponent;
        private final String mLabelText;
        private double mValue;
        private ChangeListener mChangeListener;
        private ActionListener mActionListener;

        SettingsInput(JComponent component, String labelText, JLabel display) {
            mDisplay = display;
            mComponent = component;
            mLabelText = labelText;
        }

        SettingsInput(JComponent component, String labelText, JLabel display, ChangeListener changeListener, double value) {
            this(component, labelText, display);
            mChangeListener = changeListener;
            mValue = value;
        }

        SettingsInput(JComponent component, String labelText, ChangeListener changeListener, double value) {
            this(component, labelText, new JLabel(), changeListener, value);
        }

        SettingsInput(JComponent component, String labelText, JLabel display, ActionListener actionListener) {
            this(component, labelText, display);
            mActionListener = actionListener;
        }

        SettingsInput(JComponent component, String labelText, ActionListener actionListener) {
            this(component, labelText, new JLabel(), actionListener);
        }

        public JLabel getDisplay() {
            return mDisplay;
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
