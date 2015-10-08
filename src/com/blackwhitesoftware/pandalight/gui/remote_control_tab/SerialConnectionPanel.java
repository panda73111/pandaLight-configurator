package com.blackwhitesoftware.pandalight.gui.remote_control_tab;

import com.blackwhitesoftware.pandalight.PandaLightSerialConnection;
import com.blackwhitesoftware.pandalight.remote_control.SerialConnection;
import com.blackwhitesoftware.pandalight.spec.SerialAndColorPickerConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Sebastian Hüther
 */
public class SerialConnectionPanel extends JPanel implements Observer, PropertyChangeListener {

    private PandaLightSerialConnection serialConnection;
    private JComboBox portComboBox;
    private JButton connectButton;
    private SerialAndColorPickerConfig serialConfig;

    /**
     * Listener for the buttons and checkboxes
     */
    private final ActionListener mActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };

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
        portComboBox.addActionListener(mActionListener);
        add(portComboBox);

        connectButton = new JButton("Connect");
        connectButton.addActionListener(mActionListener);
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
