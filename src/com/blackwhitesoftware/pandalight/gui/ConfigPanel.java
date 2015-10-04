package com.blackwhitesoftware.pandalight.gui;

import com.blackwhitesoftware.pandalight.*;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.DevicePanel;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.ImageProcessPanel;
import com.blackwhitesoftware.pandalight.gui.hardware_tab.LedFramePanel;
import com.blackwhitesoftware.pandalight.gui.led_simulation.LedSimulationComponent;
import com.blackwhitesoftware.pandalight.gui.remote_control_tab.ManualColorPickingPanel;
import com.blackwhitesoftware.pandalight.gui.remote_control_tab.SerialConnectionPanel;
import com.blackwhitesoftware.pandalight.spec.SerialAndColorPickerConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * The main-config panel of HyperCon. Includes the configuration and the panels to edit and
 * write-out the configuration. This can be placed on JFrame, JDialog or JApplet as required.
 */
public class ConfigPanel extends JPanel {

    /**
     * The LED configuration information
     */
    private final PandaLightConfigurationContainer pandaLightConfig;
    private final SerialAndColorPickerConfig serialConfig;
    private final PandaLightSerialConnection serialConnection;

    /**
     * Action for write the Hyperion deamon configuration file
     */
    private final Action saveConfigAction = new AbstractAction("Create Hyperion Configuration") {
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
                pandaLightConfig.saveConfigFile(fileChooser.getSelectedFile().getAbsolutePath());

                ConfigurationFile configFile = new ConfigurationFile();
                configFile.store(pandaLightConfig.mDeviceConfig);
                configFile.store(pandaLightConfig.mLedFrameConfig);
                configFile.store(pandaLightConfig.mProcessConfig);
                configFile.store(pandaLightConfig.mColorConfig);
                configFile.store(pandaLightConfig.mMiscConfig);
                configFile.save(Main.configFilename);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    };

    /**
     * The panel for containing the example 'Hyperion TV'
     */
    private JPanel mTvPanel;
    /**
     * The simulated 'Hyperion TV'
     */
    private LedSimulationComponent mHyperionTv;

    private JTabbedPane mSpecificationTabs = null;
    /**
     * The left (WEST) side panel containing the different configuration panels
     */
    private JPanel mHardwarePanel = null;
    private JPanel mRemoteControlPanel = null;


    /**
     * The button connected to saveConfigAction
     */
    private JButton saveConfigButton;

    /**
     * Constructs the configuration panel with a default initialised led-frame and configuration
     */
    public ConfigPanel(
            final PandaLightConfigurationContainer pandaLightConfig,
            final SerialAndColorPickerConfig serialConfig,
            PandaLightSerialConnection serialConnection) {
        super();

        this.pandaLightConfig = pandaLightConfig;
        this.serialConfig = serialConfig;
        this.serialConnection = serialConnection;
        initialise();

        // Compute the individual leds for the current configuration
        this.pandaLightConfig.leds = LedFrameFactory.construct(
                this.pandaLightConfig.mLedFrameConfig, this.pandaLightConfig.mProcessConfig);
        mHyperionTv.setLeds(this.pandaLightConfig.leds);

        // Add Observer to update the individual leds if the configuration changes
        final Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                ConfigPanel.this.pandaLightConfig.leds = LedFrameFactory.construct(
                        ConfigPanel.this.pandaLightConfig.mLedFrameConfig,
                        ConfigPanel.this.pandaLightConfig.mProcessConfig);
                mHyperionTv.setLeds(ConfigPanel.this.pandaLightConfig.leds);
                mHyperionTv.repaint();
            }
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        saveConfigButton = new JButton(saveConfigAction);
        panel.add(saveConfigButton, BorderLayout.SOUTH);
        mWestPanel.add(panel, BorderLayout.SOUTH);

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
     * Created, if not exists, and returns the panel holding the simulated 'Hyperion TV'
     *
     * @return The Tv panel
     */
    private JPanel getTvPanel() {
        if (mTvPanel == null) {
            mTvPanel = new JPanel();
            mTvPanel.setLayout(new BorderLayout());

            mHyperionTv = new LedSimulationComponent(pandaLightConfig.leds);
            mTvPanel.add(mHyperionTv, BorderLayout.CENTER);
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
            mRemoteControlPanel.add(new SerialConnectionPanel(serialConfig, serialConnection));
            mRemoteControlPanel.add(new ManualColorPickingPanel(serialConfig, serialConnection));

            mRemoteControlPanel.add(Box.createVerticalGlue());

        }

        return mRemoteControlPanel;
    }


}

