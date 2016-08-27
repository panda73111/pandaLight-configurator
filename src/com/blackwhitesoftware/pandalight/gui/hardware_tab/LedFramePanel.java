package com.blackwhitesoftware.pandalight.gui.hardware_tab;

import com.blackwhitesoftware.pandalight.spec.LedFrameConstruction;
import com.blackwhitesoftware.pandalight.spec.LedFrameConstruction.Direction;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Transient;

public class LedFramePanel extends JPanel {

    private final LedFrameConstruction mLedFrameSpec;

    private JLabel mHorizontalCountLabel;
    private JSpinner mHorizontalCountSpinner;
    private JLabel mVerticalCountLabel;
    private JSpinner mVerticalCountSpinner;

    private JLabel mDirectionLabel;
    private JComboBox<LedFrameConstruction.Direction> mDirectionCombo;

    private JLabel mOffsetLabel;
    private JSpinner mOffsetSpinner;
    private final ActionListener mActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateLedConstruction();
        }
    };
    private final ChangeListener mChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateLedConstruction();
        }
    };

    public LedFramePanel(LedFrameConstruction ledFrameSpec) {
        super();

        mLedFrameSpec = ledFrameSpec;

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
        setBorder(BorderFactory.createTitledBorder("Construction"));

        mDirectionLabel = new JLabel("Direction");
        add(mDirectionLabel);
        mDirectionCombo = new JComboBox<>(LedFrameConstruction.Direction.values());
        mDirectionCombo.setSelectedItem(mLedFrameSpec.direction);
        mDirectionCombo.addActionListener(mActionListener);
        add(mDirectionCombo);

        mHorizontalCountLabel = new JLabel("Horizontal #:");
        add(mHorizontalCountLabel);
        mHorizontalCountSpinner = new JSpinner(new SpinnerNumberModel(
                mLedFrameSpec.horizontalLedCount, 0, 1024, 1));
        mHorizontalCountSpinner.addChangeListener(mChangeListener);
        add(mHorizontalCountSpinner);

        mVerticalCountLabel = new JLabel("Vertical #:");
        add(mVerticalCountLabel);
        mVerticalCountSpinner = new JSpinner(new SpinnerNumberModel(
                mLedFrameSpec.verticalLedCount, 0, 1024, 1));
        mVerticalCountSpinner.addChangeListener(mChangeListener);
        add(mVerticalCountSpinner);

        mOffsetLabel = new JLabel("1st LED offset");
        add(mOffsetLabel);
        mOffsetSpinner = new JSpinner(new SpinnerNumberModel(
                mLedFrameSpec.firstLedOffset, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        mOffsetSpinner.addChangeListener(mChangeListener);
        add(mOffsetSpinner);

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mDirectionLabel)
                                .addComponent(mHorizontalCountLabel)
                                .addComponent(mVerticalCountLabel)
                                .addComponent(mOffsetLabel))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mDirectionCombo)
                                .addComponent(mHorizontalCountSpinner)
                                .addComponent(mVerticalCountSpinner)
                                .addComponent(mOffsetSpinner))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(mDirectionLabel)
                        .addComponent(mDirectionCombo))
                .addGroup(layout.createParallelGroup()
                        .addComponent(mHorizontalCountLabel)
                        .addComponent(mHorizontalCountSpinner))
                .addGroup(layout.createParallelGroup()
                        .addComponent(mVerticalCountLabel)
                        .addComponent(mVerticalCountSpinner))
                .addGroup(layout.createParallelGroup()
                        .addComponent(mOffsetLabel)
                        .addComponent(mOffsetSpinner)));

    }

    void updateLedConstruction() {
        mLedFrameSpec.direction = (Direction) mDirectionCombo.getSelectedItem();
        mLedFrameSpec.firstLedOffset = (Integer) mOffsetSpinner.getValue();

        mLedFrameSpec.horizontalLedCount = (Integer) mHorizontalCountSpinner.getValue();
        mLedFrameSpec.verticalLedCount = (Integer) mVerticalCountSpinner.getValue();

        mLedFrameSpec.setChanged();
        mLedFrameSpec.notifyObservers();
    }

}
