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

    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            mColorConfig.setGammaCorrection((double) mGammaSpinner.getValue());

            Color minValues = new Color(
                    (int) (double) mMinRedSpinner.getValue(),
                    (int) (double) mMinGreenSpinner.getValue(),
                    (int) (double) mMinBlueSpinner.getValue());

            Color maxValues = new Color(
                    (int) (double) mMaxRedSpinner.getValue(),
                    (int) (double) mMaxGreenSpinner.getValue(),
                    (int) (double) mMaxBlueSpinner.getValue());

            mColorConfig.setMinChannelValues(minValues);
            mColorConfig.setMaxChannelValues(maxValues);

            mColorConfig.notifyObservers(this);
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

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(horizontalLabelGroup)
                .addGroup(horizontalInputGroup));

        layout.setVerticalGroup(verticalGroup);
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
