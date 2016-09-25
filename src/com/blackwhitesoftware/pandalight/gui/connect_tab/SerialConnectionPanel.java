package com.blackwhitesoftware.pandalight.gui.connect_tab;

import com.blackwhitesoftware.pandalight.ErrorHandling;
import com.blackwhitesoftware.pandalight.ConfigurationContainer;
import com.blackwhitesoftware.pandalight.connect.PandaLightSerialConnection;
import com.blackwhitesoftware.pandalight.connect.SerialConnection;
import com.blackwhitesoftware.pandalight.spec.PandaLightSettings;
import com.blackwhitesoftware.pandalight.spec.SerialAndColorPickerConfig;
import jssc.SerialPortException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Sebastian Hüther
 */
public class SerialConnectionPanel extends JPanel implements Observer, PropertyChangeListener {

    private final ActionListener mConnectButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (mConnected) {
                mSerialConnection.disconnect();
                return;
            }

            mSerialConfig.portName = (String) mPortComboBox.getSelectedItem();

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                mSerialConnection.connect((String) mPortComboBox.getSelectedItem());
            } catch (SerialPortException e1) {
                ErrorHandling.ShowMessage("Error while opening serial port:\n" + e1.getLocalizedMessage());
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    };
    private final ActionListener mUploadSettingsButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            mSerialConnection.sendSettings(new PandaLightSettings(mConfiguration));
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    };
    private ConfigurationContainer mConfiguration;
    private PandaLightSerialConnection mSerialConnection;
    private JLabel mPortLabel;
    private JComboBox<String> mPortComboBox;
    private JButton mConnectButton;
    private JButton mUploadSettingsButton;
    private SerialAndColorPickerConfig mSerialConfig;
    private boolean mConnected = false;

    /**
     * Constructor
     *
     * @param serialConfig
     * @param serialConnection
     */
    public SerialConnectionPanel(
            ConfigurationContainer configuration,
            PandaLightSerialConnection serialConnection) {
        super();
        mConfiguration = configuration;
        mSerialConfig = configuration.mSerialConfig;
        mSerialConnection = serialConnection;
        mSerialConnection.addObserver(this);
        initialise();
    }

    /**
     * to set the GUI elements sizes
     */
    @Override
    @Transient
    public Dimension getMaximumSize() {
        Dimension maxSize = super.getMaximumSize();
        Dimension prefSize = super.getPreferredSize();
        return new Dimension(maxSize.width, prefSize.height);
    }

    /**
     * Create Gui elements and layout
     */
    private void initialise() {

        //All the Gui elements
        setBorder(BorderFactory.createTitledBorder("Serial Connection"));

        mPortLabel = new JLabel("Port: ");
        add(mPortLabel);

        String[] ports = SerialConnection.getSerialPorts();
        mPortComboBox = new JComboBox<>(ports);
        if (Arrays.asList(ports).contains(mSerialConfig.portName)) {
            mPortComboBox.setSelectedItem(mSerialConfig.portName);
        }
        add(mPortComboBox);

        mConnectButton = new JButton("Connect");
        mConnectButton.addActionListener(mConnectButtonListener);
        add(mConnectButton);

        mUploadSettingsButton = new JButton("Upload Settings");
        mUploadSettingsButton.addActionListener(mUploadSettingsButtonListener);
        mUploadSettingsButton.setEnabled(false);
        add(mUploadSettingsButton);

        //The Layout

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(mPortLabel)
                        .addComponent(mConnectButton))
                .addGroup(layout.createParallelGroup()
                        .addComponent(mPortComboBox)
                        .addComponent(mUploadSettingsButton)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(mPortLabel)
                        .addComponent(mPortComboBox))
                .addGroup(layout.createParallelGroup()
                        .addComponent(mConnectButton)
                        .addComponent(mUploadSettingsButton)));
    }

    /**
     * Enable or disable the Gui elements which depend on a COM connection
     * @param connected
     */
    private void setConnectionFieldsAccess(boolean connected) {
        mPortComboBox.setEnabled(!connected);
        mConnectButton.setEnabled(!connected);
        mUploadSettingsButton.setEnabled(connected);
    }

    /**
     * is called when the remote control connection status changes
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        mConnected = mSerialConnection.isConnected();
        setConnectionFieldsAccess(mConnected);
        mConnectButton.setText(mConnected ? "Disconnect" : "Connect");
    }

    /**
     * Is called from the color picker when the color changed
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

}
