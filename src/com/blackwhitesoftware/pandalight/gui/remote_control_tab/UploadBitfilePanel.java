package com.blackwhitesoftware.pandalight.gui.remote_control_tab;

import com.blackwhitesoftware.pandalight.ErrorHandling;
import com.blackwhitesoftware.pandalight.remote_control.PandaLightProtocol;
import com.blackwhitesoftware.pandalight.remote_control.PandaLightSerialConnection;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Sebastian Hüther on 27.01.2016.
 */
public class UploadBitfilePanel extends JPanel implements Observer {
    private PandaLightSerialConnection serialConnection;
    private JLabel fileLabel;
    private JLabel fileDisplay;
    private JButton browseButton;
    private JButton uploadButton;
    private JComboBox<String> bitfileIndexComboBox;
    private File selectedFile;
    private final ActionListener uploadButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            byte bitfileIndex = (byte) bitfileIndexComboBox.getSelectedIndex();
            byte[] bitfile = new byte[PandaLightProtocol.BITFILE_SIZE];

            if (!readBitfile(bitfile)) return;

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            browseButton.setEnabled(false);
            uploadButton.setEnabled(false);

            try {
                serialConnection.sendBitfile(bitfileIndex, bitfile);
            } catch (IOException e1) {
                ErrorHandling.ShowMessage("Error while sending bitfile:\n" + e1.getLocalizedMessage());
            }

            browseButton.setEnabled(true);
            uploadButton.setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    };
    private final ActionListener browseButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new FileNameExtensionFilter("Bitfile", "bit"));
            int answer = chooser.showOpenDialog(getParent());
            if (answer != JFileChooser.APPROVE_OPTION)
                return;

            selectedFile = chooser.getSelectedFile();
            fileDisplay.setText(selectedFile.getName());

            boolean connected = serialConnection.isConnected();
            setConnectionFieldsAccess(connected);
        }
    };
    /**
     * Constructor
     *
     * @param serialConnection
     */
    public UploadBitfilePanel(PandaLightSerialConnection serialConnection) {
        super();
        this.serialConnection = serialConnection;
        this.serialConnection.addObserver(this);
        this.selectedFile = null;
        initialise();
    }

    private boolean readBitfile(byte[] bitfile) {
        try {
            FileInputStream stream = new FileInputStream(selectedFile);
            int bytesRead = stream.read(bitfile);

            if (bytesRead < PandaLightProtocol.BITFILE_SIZE) {
                ErrorHandling.ShowMessage("The selected bitfile is too small!");
                return false;
            }
        } catch (FileNotFoundException e1) {
            ErrorHandling.ShowMessage("The selected bitfile could not be found!");
            return false;
        } catch (IOException e1) {
            ErrorHandling.ShowMessage("Could not read bitfile!");
            return false;
        }
        return true;
    }

    /**
     * Create Gui elements and layout
     */
    private void initialise() {
        //All the Gui elements
        setBorder(BorderFactory.createTitledBorder("Upload Bitfile"));

        fileLabel = new JLabel("Bitfile: ");
        add(fileLabel);

        fileDisplay = new JLabel("", JLabel.CENTER);
        fileDisplay.setMinimumSize(new Dimension(100, 0));
        add(fileDisplay);

        browseButton = new JButton("browse");
        browseButton.addActionListener(browseButtonListener);
        add(browseButton);

        bitfileIndexComboBox = new JComboBox<>(new String[]{"0", "1"});
        add(bitfileIndexComboBox);

        uploadButton = new JButton("upload");
        uploadButton.addActionListener(uploadButtonListener);
        add(uploadButton);

        setConnectionFieldsAccess(false);

        //The Layout

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        Component filler = Box.createHorizontalGlue();
        add(filler);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(fileLabel))
                .addGroup(layout.createParallelGroup()
                        .addComponent(filler))
                .addGroup(layout.createParallelGroup()
                        .addComponent(fileDisplay)
                        .addComponent(bitfileIndexComboBox))
                .addGroup(layout.createParallelGroup()
                        .addComponent(browseButton)
                        .addComponent(uploadButton))
                );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(fileLabel)
                        .addComponent(filler)
                        .addComponent(fileDisplay))
                .addGroup(layout.createParallelGroup()
                        .addComponent(browseButton))
                .addGroup(layout.createParallelGroup()
                        .addComponent(bitfileIndexComboBox)
                        .addComponent(uploadButton))
        );
    }

    /**
     * Enable or disable the Gui elements which depend on a shh connection
     *
     * @param enabled
     */
    private void setConnectionFieldsAccess(boolean enabled) {
        uploadButton.setEnabled(enabled);
    }

    /**
     * is called when the remote control connection status changes
     */
    @Override
    public void update(Observable o, Object arg) {
        boolean connected = serialConnection.isConnected();
        boolean fileSelected = selectedFile != null;
        setConnectionFieldsAccess(connected && fileSelected);
    }
}
