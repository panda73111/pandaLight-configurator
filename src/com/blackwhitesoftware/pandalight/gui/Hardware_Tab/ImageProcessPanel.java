package com.blackwhitesoftware.pandalight.gui.Hardware_Tab;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Transient;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.blackwhitesoftware.pandalight.spec.ImageProcessConfig;

public class ImageProcessPanel extends JPanel {
	
	private final ImageProcessConfig mProcessConfig;
	
	private JLabel mHorizontalDepthLabel;
	private JSpinner mHorizontalDepthSpinner;
	private JLabel mVerticalDepthLabel;
	private JSpinner mVerticalDepthSpinner;

	private JLabel mHorizontalGapLabel;
	private JSpinner mHorizontalGapSpinner;
	private JLabel mVerticalGapLabel;
	private JSpinner mVerticalGapSpinner;

	private JLabel mOverlapLabel;
	private JSpinner mOverlapSpinner;
	
	private JLabel mBlackborderDetectorLabel;
	private JComboBox<String> mBlackborderDetectorCombo;

	private JLabel mBlackborderThresholdLabel;
	private JSpinner mBlackborderThresholdSpinner;

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
		
		mHorizontalDepthLabel = new JLabel("Horizontal depth [%]:");
		add(mHorizontalDepthLabel);
		
		mHorizontalDepthSpinner = new JSpinner(new SpinnerNumberModel(mProcessConfig.mHorizontalDepth*100.0, 1.0, 100.0, 1.0));
		mHorizontalDepthSpinner.addChangeListener(mChangeListener);
		add(mHorizontalDepthSpinner);

		mVerticalDepthLabel = new JLabel("Vertical depth [%]:");
		add(mVerticalDepthLabel);
		
		mVerticalDepthSpinner = new JSpinner(new SpinnerNumberModel(mProcessConfig.mVerticalDepth*100.0, 1.0, 100.0, 1.0));
		mVerticalDepthSpinner.addChangeListener(mChangeListener);
		add(mVerticalDepthSpinner);

		mHorizontalGapLabel = new JLabel("Horizontal gap [%]:");
		add(mHorizontalGapLabel);
		
		mHorizontalGapSpinner = new JSpinner(new SpinnerNumberModel(mProcessConfig.mHorizontalGap*100.0, 0.0, 50.0, 1.0));
		mHorizontalGapSpinner.addChangeListener(mChangeListener);
		add(mHorizontalGapSpinner);

		mVerticalGapLabel = new JLabel("Vertical gap [%]:");
		add(mVerticalGapLabel);
		
		mVerticalGapSpinner = new JSpinner(new SpinnerNumberModel(mProcessConfig.mVerticalGap*100.0, 0.0, 50.0, 1.0));
		mVerticalGapSpinner.addChangeListener(mChangeListener);
		add(mVerticalGapSpinner);

		mOverlapLabel = new JLabel("Overlap [%]:");
		add(mOverlapLabel);
		
		mOverlapSpinner = new JSpinner(new SpinnerNumberModel(mProcessConfig.mOverlapFraction*100.0, -100.0, 100.0, 1.0));
		mOverlapSpinner.addChangeListener(mChangeListener);
		add(mOverlapSpinner);
		
		mBlackborderDetectorLabel = new JLabel("Blackborder Detector:");
		add(mBlackborderDetectorLabel);
		
		mBlackborderDetectorCombo = new JComboBox<>(new String[] {"On", "Off"});
		mBlackborderDetectorCombo.setSelectedItem(mProcessConfig.mBlackBorderRemoval?"On":"Off");
		mBlackborderDetectorCombo.setToolTipText("Enables or disables the blackborder detection and removal");
		mBlackborderDetectorCombo.addActionListener(mActionListener);
		add(mBlackborderDetectorCombo);

		mBlackborderThresholdLabel = new JLabel("Blackborder Threshold [%]:");
		add(mBlackborderThresholdLabel);
		
		mBlackborderThresholdSpinner = new JSpinner(new SpinnerNumberModel(mProcessConfig.mBlackBorderThreshold*100.0, -100.0, 100.0, 0.5));
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
						.addComponent(mHorizontalDepthLabel)
						.addComponent(mVerticalDepthLabel)
						.addComponent(mHorizontalGapLabel)
						.addComponent(mVerticalGapLabel)
						.addComponent(mOverlapLabel)
						.addComponent(mBlackborderDetectorLabel)
						.addComponent(mBlackborderThresholdLabel)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(mHorizontalDepthSpinner)
						.addComponent(mVerticalDepthSpinner)
						.addComponent(mHorizontalGapSpinner)
						.addComponent(mVerticalGapSpinner)
						.addComponent(mOverlapSpinner)
						.addComponent(mBlackborderDetectorCombo)
						.addComponent(mBlackborderThresholdSpinner)
						)
						);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(mHorizontalDepthLabel)
						.addComponent(mHorizontalDepthSpinner)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(mVerticalDepthLabel)
						.addComponent(mVerticalDepthSpinner)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(mHorizontalGapLabel)
						.addComponent(mHorizontalGapSpinner)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(mVerticalGapLabel)
						.addComponent(mVerticalGapSpinner)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(mOverlapLabel)
						.addComponent(mOverlapSpinner)
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
			mProcessConfig.setHorizontalDepth(((Double)mHorizontalDepthSpinner.getValue())/100.0);
			mProcessConfig.setVerticalDepth(((Double)mVerticalDepthSpinner.getValue())/100.0);
			mProcessConfig.setHorizontalGap(((Double)mHorizontalGapSpinner.getValue())/100.0);
			mProcessConfig.setVerticalGap(((Double)mVerticalGapSpinner.getValue())/100.0);
			mProcessConfig.setOverlapFraction(((Double)mOverlapSpinner.getValue())/100.0);
			mProcessConfig.setBlackborderThreshold(((Double)mBlackborderThresholdSpinner.getValue())/100.0);

			// Notify observers
			mProcessConfig.notifyObservers(this);
		}
	};
}
