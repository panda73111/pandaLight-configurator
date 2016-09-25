package com.blackwhitesoftware.pandalight.gui;

import com.blackwhitesoftware.pandalight.ConfigurationContainer;
import com.blackwhitesoftware.pandalight.gui.color_tab.ColorPanel;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.DevicePanel;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.ImageProcessPanel;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.LedFramePanel;
import com.blackwhitesoftware.pandalight.gui.led_simulation.LedSimulationComponent;
import com.blackwhitesoftware.pandalight.gui.connect_tab.BoardInfoPanel;
import com.blackwhitesoftware.pandalight.gui.connect_tab.ManualColorPickingPanel;
import com.blackwhitesoftware.pandalight.gui.connect_tab.SerialConnectionPanel;
import com.blackwhitesoftware.pandalight.gui.connect_tab.UploadBitfilePanel;
import com.blackwhitesoftware.pandalight.connect.PandaLightSerialConnection;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * The main-config panel. Includes the configuration and the panels to edit and
 * write-out the configuration. This can be placed on JFrame, JDialog or JApplet as required.
 */
public class ConfigPanel extends JPanel {

    /**
     * The LED configuration information
     */
    private final ConfigurationContainer pandaLightConfig;
    private final PandaLightSerialConnection serialConnection;

    /**
     * The panel for containing the example TV with ambient lighting
     */
    private JPanel mTvPanel;
    /**
     * The simulated TV with ambient lighting
     */
    private LedSimulationComponent mLightedTV;

    private JTabbedPane mSpecificationTabs = null;
    /**
     * The left (WEST) side panel containing the different configuration panels
     */
    private JPanel mHardwarePanel = null;
    private JPanel mColorPanel = null;
    private JPanel mConnectPanel = null;

    /**
     * Constructs the configuration panel with a default initialised led-frame and configuration
     */
    public ConfigPanel(
            ConfigurationContainer pandaLightConfig,
            PandaLightSerialConnection serialConnection) {
        super();

        this.pandaLightConfig = pandaLightConfig;
        this.serialConnection = serialConnection;
        initialise();

        // Compute the individual leds for the current configuration
        this.pandaLightConfig.leds = LedFrameFactory.construct(this.pandaLightConfig);
        mLightedTV.setLeds(this.pandaLightConfig.leds);

        // Add Observer to update the individual leds if the configuration changes
        final Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                ConfigPanel.this.pandaLightConfig.leds = LedFrameFactory.construct(ConfigPanel.this.pandaLightConfig);
                mLightedTV.setLeds(ConfigPanel.this.pandaLightConfig.leds);
                mLightedTV.repaint();
            }
        };
        this.pandaLightConfig.mLedFrameConfig.addObserver(observer);
        this.pandaLightConfig.mProcessConfig.addObserver(observer);
        this.pandaLightConfig.mColorConfig.addObserver(observer);
    }

    /**
     * Initialises the config-panel
     */
    private void initialise() {
        createHardwarePanel();
        createColorCorrectionPanel();
        createRemoteControlPanel();

        setLayout(new BorderLayout());

        add(getTvPanel(), BorderLayout.CENTER);
        add(getWestPanel(), BorderLayout.WEST);
    }

    private JPanel getWestPanel() {
        JPanel mWestPanel = new JPanel();
        mWestPanel.setLayout(new BorderLayout());
        mWestPanel.add(getSpecificationTabs(), BorderLayout.CENTER);

        return mWestPanel;
    }

    private JTabbedPane getSpecificationTabs() {
        if (mSpecificationTabs == null) {
            mSpecificationTabs = new JTabbedPane();
            mSpecificationTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

            mSpecificationTabs.addTab("Hardware", new JScrollPane(mHardwarePanel));
            mSpecificationTabs.addTab("Color", new JScrollPane(mColorPanel));
            mSpecificationTabs.addTab("Connect", new JScrollPane(mConnectPanel));
        }
        return mSpecificationTabs;
    }

    /**
     * Created, if not exists, and returns the panel holding the simulated TV with ambient lighting
     *
     * @return The Tv panel
     */
    private JPanel getTvPanel() {
        if (mTvPanel == null) {
            mTvPanel = new JPanel();
            mTvPanel.setLayout(new BorderLayout());

            mLightedTV = new LedSimulationComponent(pandaLightConfig);
            mTvPanel.add(mLightedTV, BorderLayout.CENTER);
        }
        return mTvPanel;
    }

    private void createHardwarePanel() {
        mHardwarePanel = new JPanel();
        mHardwarePanel.setLayout(new BoxLayout(mHardwarePanel, BoxLayout.Y_AXIS));

        mHardwarePanel.add(new DevicePanel(pandaLightConfig.mDeviceConfig));
        mHardwarePanel.add(new LedFramePanel(pandaLightConfig.mLedFrameConfig));
        mHardwarePanel.add(new ImageProcessPanel(pandaLightConfig.mProcessConfig));
        mHardwarePanel.add(Box.createVerticalGlue());
    }

    private void createColorCorrectionPanel() {
        mColorPanel = new JPanel();
        mColorPanel.setLayout(new BoxLayout(mColorPanel, BoxLayout.Y_AXIS));

        mColorPanel.add(new ColorPanel(pandaLightConfig.mColorConfig));
        mColorPanel.add(Box.createVerticalGlue());
    }

    private void createRemoteControlPanel() {
        mConnectPanel = new JPanel();
        mConnectPanel.setLayout(new BoxLayout(mConnectPanel, BoxLayout.Y_AXIS));
        mConnectPanel.add(new SerialConnectionPanel(pandaLightConfig, serialConnection));
        mConnectPanel.add(new BoardInfoPanel(serialConnection));
        mConnectPanel.add(new UploadBitfilePanel(pandaLightConfig.mMiscConfig, serialConnection));
        mConnectPanel.add(new ManualColorPickingPanel(pandaLightConfig, serialConnection));
        mConnectPanel.add(Box.createVerticalGlue());
    }
}

