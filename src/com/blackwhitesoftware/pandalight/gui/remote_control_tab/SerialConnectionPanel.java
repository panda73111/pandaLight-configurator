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
import java.util.Observable;
import java.util.Observer;

/**
 * @author Sebastian Hüther
 */
public class SerialConnectionPanel extends JPanel implements Observer, PropertyChangeListener {

    private final ActionListener connectButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                serialConnection.connect((String) portComboBox.getSelectedItem());
            } catch (PortInUseException e1) {
                ErrorHandling.ShowMessage("Serial port is already in use!");
            } catch (NoSuchPortException e1) {
                ErrorHandling.ShowMessage("This serial port could not be found!");
            } catch (UnsupportedCommOperationException | IOException e1) {
                ErrorHandling.ShowMessage("Error while opening serial port:\n" + e1.getLocalizedMessage());
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    };
    private PandaLightSerialConnection serialConnection;
    private JComboBox<String> portComboBox;
    private JButton connectButton;
    private SerialAndColorPickerConfig serialConfig;

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
     * to set the Guielements sizes
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

        portComboBox = new JComboBox<>(SerialConnection.getSerialPorts());
        add(portComboBox);

        connectButton = new JButton("Connect");
        connectButton.addActionListener(connectButtonListener);
        add(connectButton);

        //The Layout

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(portComboBox))
                .addGroup(layout.createSequentialGroup()
                                .addComponent(connectButton)
                ));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                                .addComponent(portComboBox)
                                .addComponent(connectButton)
                ));
    }

    /**
     * is called when the remote control connection status changes
     */
    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * Is called from the color picker when the color changed
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

}
