package com.blackwhitesoftware.pandalight.gui.color_tab;

import com.blackwhitesoftware.pandalight.spec.ColorConfig;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.Transient;
import java.util.Vector;

/**
 * Created by Sebastian.Huether on 01.09.2016.
 */
public class ColorPanel extends JPanel {
    private final ColorConfig mColorConfig;

    private JSpinner mGammaSpinner;
    private JSpinner mMinRedSpinner;
    private JSpinner mMaxRedSpinner;
    private JSpinner mMinGreenSpinner;
    private JSpinner mMaxGreenSpinner;
    private JSpinner mMinBlueSpinner;
    private JSpinner mMaxBlueSpinner;
    private ColorChannelDisplay mColorDisplay;

    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            Object source = changeEvent.getSource();

            Color minValues = new Color(
                    (int) (double) mMinRedSpinner.getValue(),
                    (int) (double) mMinGreenSpinner.getValue(),
                    (int) (double) mMinBlueSpinner.getValue());

            Color maxValues = new Color(
                    (int) (double) mMaxRedSpinner.getValue(),
                    (int) (double) mMaxGreenSpinner.getValue(),
                    (int) (double) mMaxBlueSpinner.getValue());

            if (source == mMinRedSpinner)
                mMaxRedSpinner.setValue((double) Math.max(minValues.getRed(), maxValues.getRed()));
            else if (source == mMaxRedSpinner)
                mMinRedSpinner.setValue((double) Math.min(minValues.getRed(), maxValues.getRed()));
            else if (source == mMinGreenSpinner)
                mMaxGreenSpinner.setValue((double) Math.max(minValues.getGreen(), maxValues.getGreen()));
            else if (source == mMaxGreenSpinner)
                mMinGreenSpinner.setValue((double) Math.min(minValues.getGreen(), maxValues.getGreen()));
            else if (source == mMinBlueSpinner)
                mMaxBlueSpinner.setValue((double) Math.max(minValues.getBlue(), maxValues.getBlue()));
            else if (source == mMaxBlueSpinner)
                mMinBlueSpinner.setValue((double) Math.min(minValues.getBlue(), maxValues.getBlue()));

            double gamma = (double) mGammaSpinner.getValue();
            mColorConfig.setGammaCorrection(gamma);

            mColorConfig.setMinChannelValues(minValues);
            mColorConfig.setMaxChannelValues(maxValues);

            byte[] redLookup = mColorConfig.getRedLookupTable();
            byte[] greenLookup = mColorConfig.getGreenLookupTable();
            byte[] blueLookup = mColorConfig.getBlueLookupTable();

            for (int i = 0; i < 256; i++) {
                int value = (int) (Math.pow(i / 255.0, gamma) * 255);
                int redValue, greenValue, blueValue;

                redValue = Math.max(minValues.getRed(), value);
                redValue = Math.min(maxValues.getRed(), redValue);

                greenValue = Math.max(minValues.getGreen(), value);
                greenValue = Math.min(maxValues.getGreen(), greenValue);

                blueValue = Math.max(minValues.getBlue(), value);
                blueValue = Math.min(maxValues.getBlue(), blueValue);

                redLookup[i] = (byte) redValue;
                greenLookup[i] = (byte) greenValue;
                blueLookup[i] = (byte) blueValue;
            }

            mColorDisplay.setRed(redLookup);
            mColorDisplay.setGreen(greenLookup);
            mColorDisplay.setBlue(blueLookup);

            mColorConfig.setRedLookupTable(redLookup);
            mColorConfig.setGreenLookupTable(greenLookup);
            mColorConfig.setBlueLookupTable(blueLookup);
        }
    };

    public ColorPanel(ColorConfig colorConfig) {
        super();

        mColorConfig = colorConfig;

        initialize();
    }

    @Override
    @Transient
    public Dimension getMaximumSize() {
        Dimension maxSize = super.getMaximumSize();
        Dimension prefSize = super.getPreferredSize();
        return new Dimension(maxSize.width, prefSize.height);
    }

    private void initialize() {
        setBorder(BorderFactory.createTitledBorder("Color Correction"));

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        Color minColorValues = mColorConfig.getMinChannelValues();
        Color maxColorValues = mColorConfig.getMaxChannelValues();

        Vector<NumberSettingsInput> inputs = new Vector<>();

        mGammaSpinner = new JSpinner();
        mMinRedSpinner = new JSpinner();
        mMaxRedSpinner = new JSpinner();
        mMinGreenSpinner = new JSpinner();
        mMaxGreenSpinner = new JSpinner();
        mMinBlueSpinner = new JSpinner();
        mMaxBlueSpinner = new JSpinner();

        mColorDisplay = new ColorChannelDisplay(
                mColorConfig.getRedLookupTable(),
                mColorConfig.getGreenLookupTable(),
                mColorConfig.getBlueLookupTable()
        );
        mColorDisplay.setMinimumSize(new Dimension(0, 150));

        inputs.add(new NumberSettingsInput(mGammaSpinner, "Gamma:", mColorConfig.getGammaCorrection(), changeListener, 0, 15, 0.1));
        inputs.add(new ColorChannelSettingsInput(mMinRedSpinner, "Minimum Red:", minColorValues.getRed(), changeListener));
        inputs.add(new ColorChannelSettingsInput(mMaxRedSpinner, "Maximum Red:", maxColorValues.getRed(), changeListener));
        inputs.add(new ColorChannelSettingsInput(mMinGreenSpinner, "Minimum Green:", minColorValues.getGreen(), changeListener));
        inputs.add(new ColorChannelSettingsInput(mMaxGreenSpinner, "Maximum Green:", maxColorValues.getGreen(), changeListener));
        inputs.add(new ColorChannelSettingsInput(mMinBlueSpinner, "Minimum Blue:", minColorValues.getBlue(), changeListener));
        inputs.add(new ColorChannelSettingsInput(mMaxBlueSpinner, "Maximum Blue:", maxColorValues.getBlue(), changeListener));

        GroupLayout.ParallelGroup horizontalLabelGroup = layout.createParallelGroup();
        GroupLayout.ParallelGroup horizontalInputGroup = layout.createParallelGroup();
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

        for (NumberSettingsInput input : inputs) {
            input.add();

            horizontalLabelGroup.addComponent(input.getLabel());
            horizontalInputGroup.addComponent(input.getSpinner());

            GroupLayout.ParallelGroup verticalLineGroup = layout.createParallelGroup();
            verticalLineGroup.addComponent(input.getLabel());
            verticalLineGroup.addComponent(input.getSpinner());
            verticalGroup.addGroup(verticalLineGroup);
        }

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addGroup(horizontalLabelGroup)
                        .addGroup(horizontalInputGroup))
                .addComponent(mColorDisplay));

        layout.setVerticalGroup(verticalGroup.addComponent(mColorDisplay));
    }

    private class NumberSettingsInput {
        protected final JLabel mLabel;
        protected final JSpinner mSpinner;

        public NumberSettingsInput(
                JSpinner spinner, String labelText, double value,
                ChangeListener listener,
                double min, double max, double step) {
            mLabel = new JLabel(labelText);
            mSpinner = spinner;
            spinner.setModel(new SpinnerNumberModel(value, min, max, step));
            spinner.addChangeListener(listener);
        }

        void add() {
            ColorPanel.this.add(mLabel);
            ColorPanel.this.add(mSpinner);
        }

        public JLabel getLabel() {
            return mLabel;
        }

        public JSpinner getSpinner() {
            return mSpinner;
        }
    }

    private class ColorChannelSettingsInput extends NumberSettingsInput {
        ColorChannelSettingsInput(JSpinner spinner, String labelText, int value, ChangeListener listener) {
            super(spinner, labelText, value, listener, 0, 255, 1);
        }
    }
}
