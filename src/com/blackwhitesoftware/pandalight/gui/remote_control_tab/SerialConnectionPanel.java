package com.blackwhitesoftware.pandalight.gui.remote_control_tab;

import com.blackwhitesoftware.pandalight.ErrorHandling;
import com.blackwhitesoftware.pandalight.PandaLightSerialConnection;
import com.blackwhitesoftware.pandalight.remote_control.SerialConnection;
import com.blackwhitesoftware.pandalight.spec.SerialAndColorPickerConfig;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Sebastian Hüther
 */
public class SerialConnectionPanel extends JPanel implements Observer, PropertyChangeListener {

    private final ActionListener connectButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (connected) {
                serialConnection.disconnect();
                return;
            }

            serialConfig.portName = (String) portComboBox.getSelectedItem();

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            connectButton.setEnabled(false);
            try {
                serialConnection.connect((String) portComboBox.getSelectedItem());
            } catch (PortInUseException e1) {
                ErrorHandling.ShowMessage("Serial port is already in use!");
            } catch (NoSuchPortException e1) {
                ErrorHandling.ShowMessage("This serial port could not be found!");
            } catch (UnsupportedCommOperationException | IOException e1) {
                ErrorHandling.ShowMessage("Error while opening serial port:\n" + e1.getLocalizedMessage());
            }
            connectButton.setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    };
    private PandaLightSerialConnection serialConnection;
    private JLabel portLabel;
    private JComboBox<String> portComboBox;
    private JButton connectButton;
    private SerialAndColorPickerConfig serialConfig;
    private boolean connected = false;

    /**
     * Constructor
     *
     * @param serialConfig
     * @param serialConnection
     */
    public SerialConnectionPanel(SerialAndColorPickerConfig serialConfig, PandaLightSerialConnection serialConnection) {
        super();
        this.serialConfig = serialConfig;
        this.serialConnection = serialConnection;
        this.serialConnection.addObserver(this);
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

        portLabel = new JLabel("Port: ");
        add(portLabel);

        String[] ports = SerialConnection.getSerialPorts();
        portComboBox = new JComboBox<>(ports);
        if (Arrays.asList(ports).contains(serialConfig.portName)) {
            portComboBox.setSelectedItem(serialConfig.portName);
        }
        add(portComboBox);

        connectButton = new JButton("Connect");
        connectButton.addActionListener(connectButtonListener);
        add(connectButton);

        //The Layout

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(portLabel)
                        .addComponent(connectButton))
                .addGroup(layout.createParallelGroup()
                                .addComponent(portComboBox)
                ));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(portLabel)
                        .addComponent(portComboBox))
                .addGroup(layout.createParallelGroup()
                                .addComponent(connectButton)
                ));
    }

    /**
     * Enable or disable the Gui elements which depend on a shh connection
     * @param enabled
     */
    private void setConnectionFieldsAccess(boolean enabled) {
        portComboBox.setEnabled(enabled);
    }

    /**
     * is called when the remote control connection status changes
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        connected = serialConnection.isConnected();
        setConnectionFieldsAccess(!connected);
        connectButton.setText(connected ? "Disconnect" : "Connect");
    }

    /**
     * Is called from the color picker when the color changed
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

}
