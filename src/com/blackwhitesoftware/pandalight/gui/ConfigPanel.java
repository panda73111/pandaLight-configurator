package com.blackwhitesoftware.pandalight.gui;

import com.blackwhitesoftware.pandalight.ConfigurationContainer;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.DevicePanel;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.ImageProcessPanel;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.LedFramePanel;
import com.blackwhitesoftware.pandalight.gui.led_simulation.LedSimulationComponent;
import com.blackwhitesoftware.pandalight.gui.remote_control_tab.BoardInfoPanel;
import com.blackwhitesoftware.pandalight.gui.remote_control_tab.ManualColorPickingPanel;
import com.blackwhitesoftware.pandalight.gui.remote_control_tab.SerialConnectionPanel;
import com.blackwhitesoftware.pandalight.gui.remote_control_tab.UploadBitfilePanel;
import com.blackwhitesoftware.pandalight.remote_control.PandaLightSerialConnection;

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
    private JPanel mRemoteControlPanel = null;

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
        final Observer observer = (o, arg) -> {
            ConfigPanel.this.pandaLightConfig.leds = LedFrameFactory.construct(ConfigPanel.this.pandaLightConfig);
            mLightedTV.setLeds(ConfigPanel.this.pandaLightConfig.leds);
            mLightedTV.repaint();
        };
        this.pandaLightConfig.mLedFrameConfig.addObserver(observer);
        this.pandaLightConfig.mProcessConfig.addObserver(observer);
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

        return mWestPanel;
    }

    private JTabbedPane getSpecificationTabs() {
        if (mSpecificationTabs == null) {
            mSpecificationTabs = new JTabbedPane();
            mSpecificationTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

            mSpecificationTabs.addTab("Hardware", new JScrollPane(getHardwarePanel()));
            mSpecificationTabs.addTab("Remote Control", new JScrollPane(getRemoteControlPanel()));
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

            mLightedTV = new LedSimulationComponent(pandaLightConfig.leds);
            mTvPanel.add(mLightedTV, BorderLayout.CENTER);
        }
        return mTvPanel;
    }

    private JPanel getHardwarePanel() {
        if (mHardwarePanel == null) {
            mHardwarePanel = new JPanel();
            mHardwarePanel.setLayout(new BoxLayout(mHardwarePanel, BoxLayout.Y_AXIS));

            mHardwarePanel.add(new DevicePanel(pandaLightConfig.mDeviceConfig));
            mHardwarePanel.add(new LedFramePanel(pandaLightConfig.mLedFrameConfig));
            mHardwarePanel.add(new ImageProcessPanel(pandaLightConfig.mProcessConfig));
            mHardwarePanel.add(Box.createVerticalGlue());
        }
        return mHardwarePanel;
    }

    private JPanel getRemoteControlPanel() {
        if (mRemoteControlPanel == null) {
            mRemoteControlPanel = new JPanel();
            mRemoteControlPanel.setLayout(new BoxLayout(mRemoteControlPanel, BoxLayout.Y_AXIS));
            mRemoteControlPanel.add(new SerialConnectionPanel(pandaLightConfig, serialConnection));
            mRemoteControlPanel.add(new BoardInfoPanel(serialConnection));
            mRemoteControlPanel.add(new UploadBitfilePanel(pandaLightConfig.mMiscConfig, serialConnection));
            mRemoteControlPanel.add(new ManualColorPickingPanel(pandaLightConfig.mSerialConfig, serialConnection));
            mRemoteControlPanel.add(Box.createVerticalGlue());
        }

        return mRemoteControlPanel;
    }
}

