package com.blackwhitesoftware.pandalight.gui.connect_tab;

import com.blackwhitesoftware.pandalight.connect.*;
import jssc.SerialPortException;

import javax.swing.*;
import java.awt.*;
import java.beans.Transient;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Sebastian Hüther
 */
public class BoardInfoPanel extends JPanel implements Observer {

    private PandaLightSerialConnection serialConnection;
    private JLabel versionLabel;
    private JLabel versionDisplay;

    /**
     * Constructor
     *
     * @param serialConnection
     */
    public BoardInfoPanel(final PandaLightSerialConnection serialConnection) {
        super();
        this.serialConnection = serialConnection;
        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void connected() {
                try {
                    serialConnection.sendCommand(PandaLightCommand.SYSINFO);
                } catch (SerialPortException ignored) { }
            }

            @Override
            public void gotPacket(PandaLightPacket packet) {
                if (!(packet instanceof PandaLightSysinfoPacket))
                    return;

                PandaLightSysinfoPacket sysinfoPacket = (PandaLightSysinfoPacket) packet;
                String versionString = versionToString(
                        sysinfoPacket.getMajorVersion(),
                        sysinfoPacket.getMinorVersion());
                versionDisplay.setText(versionString);
            }
        };
        this.serialConnection.addConnectionListener(listener);
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
        setBorder(BorderFactory.createTitledBorder("Board Information"));

        versionLabel = new JLabel("Version: ");
        add(versionLabel);

        versionDisplay = new JLabel("", JLabel.CENTER);
        versionDisplay.setMinimumSize(new Dimension(100, 0));
        versionDisplay.setFont(versionDisplay.getFont().deriveFont(Font.BOLD));
        add(versionDisplay);

        setConnectionFieldsAccess(false);

        //The Layout

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        Component filler = Box.createHorizontalGlue();
        add(filler);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(versionLabel)
                        .addComponent(filler)
                        .addComponent(versionDisplay)));

        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(versionLabel)
                .addComponent(filler)
                .addComponent(versionDisplay));
    }

    private static String versionToString(int major, int minor) {
        return String.format("%d.%d", major, minor);
    }

    /**
     * Enable or disable the Gui elements which depend on a shh connection
     * @param enabled
     */
    private void setConnectionFieldsAccess(boolean enabled) {
        versionLabel.setEnabled(enabled);
        versionDisplay.setEnabled(enabled);
    }

    /**
     * is called when the remote control connection status changes
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        boolean connected = serialConnection.isConnected();
        setConnectionFieldsAccess(connected);
    }

}
