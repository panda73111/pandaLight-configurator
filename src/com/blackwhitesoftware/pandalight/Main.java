package com.blackwhitesoftware.pandalight;

import com.blackwhitesoftware.pandalight.gui.ConfigPanel;
import com.blackwhitesoftware.pandalight.remote_control.PandaLightSerialConnection;
import com.blackwhitesoftware.pandalight.spec.SerialAndColorPickerConfig;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * (static) Main-class for starting the GUI as a standard JAVA application (contains the entry-point).
 */
public class Main {
    public static final String configFilename = "settings.dat";

    /**
     * Entry point to start GUI
     *
     * @param pArgs This does not have command line arguments
     */
    public static void main(String[] pArgs) {
        final String versionStr = Main.class.getPackage().getSpecificationVersion();
        final String appTitleStr = Main.class.getPackage().getSpecificationTitle();
        final ConfigurationContainer pandaLightConfig = new ConfigurationContainer();
        final SerialAndColorPickerConfig serialConfig = new SerialAndColorPickerConfig();
        final PandaLightSerialConnection serialConnection = new PandaLightSerialConnection();

        try {
            // Configure swing to use the system default look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        // Create a frame for the configuration panel
        JFrame frame = new JFrame();
        ErrorHandling.mainframe = frame;
        String title = appTitleStr + " v" + versionStr;
        frame.setTitle(title);
        frame.setSize(1300, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Main.class.getResource("icon64.png")).getImage());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    ConfigurationFile configFile = new ConfigurationFile();
                    configFile.store(pandaLightConfig.mDeviceConfig);
                    configFile.store(pandaLightConfig.mLedFrameConfig);
                    configFile.store(pandaLightConfig.mProcessConfig);
                    configFile.store(pandaLightConfig.mColorCorrectionConfig);
                    configFile.store(pandaLightConfig.mMiscConfig);
                    configFile.store(pandaLightConfig.mSerialConfig);
                    configFile.save(configFilename);
                } catch (Throwable t) {
                    System.err.println("Failed to save " + configFilename);
                }
                serialConnection.disconnect();
            }
        });

        if (new File(configFilename).exists()) {
            try {
                ConfigurationFile configFile = new ConfigurationFile();
                configFile.load(configFilename);
                configFile.restore(pandaLightConfig.mDeviceConfig);
                configFile.restore(pandaLightConfig.mLedFrameConfig);
                configFile.restore(pandaLightConfig.mProcessConfig);
                configFile.restore(pandaLightConfig.mColorCorrectionConfig);
                configFile.restore(pandaLightConfig.mMiscConfig);
                configFile.restore(pandaLightConfig.mSerialConfig);
            } catch (Throwable t) {
                System.err.println("Failed to load " + configFilename);
            }
        }

        // Add the configuration panel
        frame.setContentPane(new ConfigPanel(pandaLightConfig, serialConnection));

        // Show the frame
        frame.setVisible(true);
    }

    static void ShowError(String message) {
        new JOptionPane(message, JOptionPane.ERROR_MESSAGE);

    }
}
