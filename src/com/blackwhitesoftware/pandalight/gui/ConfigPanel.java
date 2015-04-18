package com.blackwhitesoftware.pandalight.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.blackwhitesoftware.pandalight.ConfigurationFile;
import com.blackwhitesoftware.pandalight.Main;
import com.blackwhitesoftware.pandalight.gui.External_Tab.InterfacePanel;
import com.blackwhitesoftware.pandalight.gui.External_Tab.XbmcPanel;
import com.blackwhitesoftware.pandalight.gui.Hardware_Tab.LedFramePanel;
import com.blackwhitesoftware.pandalight.gui.Process_Tab.ColorsPanel;
import com.blackwhitesoftware.pandalight.gui.SSH_Tab.SshColorPickingPanel;
import com.blackwhitesoftware.pandalight.gui.SSH_Tab.SshCommandSenderPanel;
import com.blackwhitesoftware.pandalight.spec.SshAndColorPickerConfig;
import com.blackwhitesoftware.pandalight.LedFrameFactory;
import com.blackwhitesoftware.pandalight.LedString;
import com.blackwhitesoftware.pandalight.gui.External_Tab.EffectEnginePanel;
import com.blackwhitesoftware.pandalight.gui.Grabber_Tab.FrameGrabberPanel;
import com.blackwhitesoftware.pandalight.gui.Grabber_Tab.Grabberv4l2Panel;
import com.blackwhitesoftware.pandalight.gui.Hardware_Tab.DevicePanel;
import com.blackwhitesoftware.pandalight.gui.Hardware_Tab.ImageProcessPanel;
import com.blackwhitesoftware.pandalight.gui.LedSimulation.LedSimulationComponent;
import com.blackwhitesoftware.pandalight.gui.Process_Tab.ColorSmoothingPanel;
import com.blackwhitesoftware.pandalight.gui.SSH_Tab.SshConnectionPanel;

/**
 * The main-config panel of HyperCon. Includes the configuration and the panels to edit and 
 * write-out the configuration. This can be placed on JFrame, JDialog or JApplet as required.
 */
public class ConfigPanel extends JPanel {

	/** The LED configuration information*/
	private final LedString ledString;
	private final SshAndColorPickerConfig sshConfig;
	
	/** Action for write the Hyperion deamon configuration file */
	private final Action mSaveConfigAction = new AbstractAction("Create Hyperion Configuration") {
		JFileChooser fileChooser = new JFileChooser();
		{
			fileChooser.setSelectedFile(new File("hyperion.config.json"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (fileChooser.showSaveDialog(ConfigPanel.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			try {
				ledString.saveConfigFile(fileChooser.getSelectedFile().getAbsolutePath());
				
				ConfigurationFile configFile = new ConfigurationFile();
				configFile.store(ledString.mDeviceConfig);
				configFile.store(ledString.mLedFrameConfig);
				configFile.store(ledString.mProcessConfig);
				configFile.store(ledString.mColorConfig);
				configFile.store(ledString.mMiscConfig);
				configFile.save(Main.configFilename);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	
	/** The panel for containing the example 'Hyperion TV' */
	private JPanel mTvPanel;
	/** The simulated 'Hyperion TV' */
	private LedSimulationComponent mHyperionTv;
	
	private JTabbedPane mSpecificationTabs = null;
	/** The left (WEST) side panel containing the different configuration panels */
	private JPanel mHardwarePanel = null;
	private JPanel mProcessPanel = null;
	private JPanel mExternalPanel = null;
	private JPanel mTestingPanel = null;
	private JPanel mGrabberPanel = null;


	/** The button connected to mSaveConfigAction */
	private JButton mSaveConfigButton;
	
	/**
	 * Constructs the configuration panel with a default initialised led-frame and configuration
	 */
	public ConfigPanel(final LedString pLedString, final SshAndColorPickerConfig pSshConfig) {
		super();
		
		ledString = pLedString;
		sshConfig = pSshConfig;
		initialise();
		
		// Compute the individual leds for the current configuration
		ledString.leds = LedFrameFactory.construct(ledString.mLedFrameConfig, ledString.mProcessConfig);
		mHyperionTv.setLeds(ledString.leds);
		
		// Add Observer to update the individual leds if the configuration changes
		final Observer observer = new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				ledString.leds = LedFrameFactory.construct(ledString.mLedFrameConfig, ledString.mProcessConfig);
				mHyperionTv.setLeds(ledString.leds);
				mHyperionTv.repaint();
			}
		};
		ledString.mLedFrameConfig.addObserver(observer);
		ledString.mProcessConfig.addObserver(observer);
	}
	
	/**
	 * Initialises the config-panel 
	 */
	private void initialise() {
		setLayout(new BorderLayout());
		
		add(getTvPanel(), BorderLayout.CENTER);
		add(getWestPanel(), BorderLayout.WEST);
		
	}
	private JPanel getWestPanel() {
		JPanel mWestPanel = new JPanel();
		mWestPanel.setLayout(new BorderLayout());
		
		mWestPanel.add(getSpecificationTabs(), BorderLayout.CENTER);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		mSaveConfigButton = new JButton(mSaveConfigAction);
		panel.add(mSaveConfigButton, BorderLayout.SOUTH);
		mWestPanel.add(panel, BorderLayout.SOUTH);

		return mWestPanel;
	}
	private JTabbedPane getSpecificationTabs() {
		if (mSpecificationTabs == null) {
			mSpecificationTabs = new JTabbedPane();
			mSpecificationTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			
			mSpecificationTabs.addTab("Hardware", new JScrollPane(getHardwarePanel()));
			mSpecificationTabs.addTab("Process",  new JScrollPane(getProcessPanel()));
			mSpecificationTabs.addTab("Grabber",  new JScrollPane(getGrabberPanel()));
			mSpecificationTabs.addTab("External", new JScrollPane(getExternalPanel()));
			mSpecificationTabs.addTab("SSH",      new JScrollPane(getTestingPanel()));
		}
		return mSpecificationTabs;
	}
	
	/**
	 * Created, if not exists, and returns the panel holding the simulated 'Hyperion TV'
	 * 
	 * @return The Tv panel
	 */
	private JPanel getTvPanel() {
		if (mTvPanel == null) {
			mTvPanel = new JPanel();
			mTvPanel.setLayout(new BorderLayout());
				
			mHyperionTv = new LedSimulationComponent(ledString.leds, ledString.mGrabberv4l2Config);
			mTvPanel.add(mHyperionTv, BorderLayout.CENTER);
		}
		return mTvPanel;
	}
	
	private JPanel getHardwarePanel() {
		if (mHardwarePanel == null) {
			mHardwarePanel = new JPanel();
			mHardwarePanel.setLayout(new BoxLayout(mHardwarePanel, BoxLayout.Y_AXIS));
			
			mHardwarePanel.add(new DevicePanel(ledString.mDeviceConfig));
			mHardwarePanel.add(new LedFramePanel(ledString.mLedFrameConfig));
			mHardwarePanel.add(new ImageProcessPanel(ledString.mProcessConfig));
			mHardwarePanel.add(Box.createVerticalGlue());
		}
		return mHardwarePanel;
	}
	
	private JPanel getProcessPanel() {
		if (mProcessPanel == null) {
			mProcessPanel = new JPanel();
			mProcessPanel.setLayout(new BoxLayout(mProcessPanel, BoxLayout.Y_AXIS));
			

			mProcessPanel.add(new ColorSmoothingPanel(ledString.mColorConfig));
			mProcessPanel.add(new ColorsPanel(ledString.mColorConfig));
			mProcessPanel.add(Box.createVerticalGlue());
		}
		return mProcessPanel;
	}
	
	private JPanel getExternalPanel() {
		if (mExternalPanel == null) {
			mExternalPanel = new JPanel();
			mExternalPanel.setLayout(new BoxLayout(mExternalPanel, BoxLayout.Y_AXIS));
			
			mExternalPanel.add(new XbmcPanel(ledString.mMiscConfig));
			mExternalPanel.add(new InterfacePanel(ledString.mMiscConfig));
			mExternalPanel.add(new EffectEnginePanel(ledString.mMiscConfig));
			mExternalPanel.add(Box.createVerticalGlue());
		}
		return mExternalPanel;
	}
	
	private JPanel getTestingPanel(){
		if( mTestingPanel == null){
			mTestingPanel = new JPanel();
			mTestingPanel.setLayout(new BoxLayout(mTestingPanel, BoxLayout.Y_AXIS));
			mTestingPanel.add(new SshConnectionPanel(sshConfig));
			mTestingPanel.add(new SshColorPickingPanel(sshConfig));
			mTestingPanel.add(new SshCommandSenderPanel(sshConfig));

			mTestingPanel.add(Box.createVerticalGlue());

		}
		
		return mTestingPanel;
	}

	private JPanel getGrabberPanel(){
		if( mGrabberPanel == null){
			mGrabberPanel = new JPanel();
			mGrabberPanel.setLayout(new BoxLayout(mGrabberPanel, BoxLayout.Y_AXIS));

			mGrabberPanel.add(new FrameGrabberPanel(ledString.mMiscConfig));
			mGrabberPanel.add(new Grabberv4l2Panel(ledString.mGrabberv4l2Config));
			mGrabberPanel.add(Box.createVerticalGlue());

		}

		return mGrabberPanel;
	}


	}

