package com.blackwhitesoftware.pandalight.gui.connect_tab;

import com.blackwhitesoftware.pandalight.Bitfile;
import com.blackwhitesoftware.pandalight.ErrorHandling;
import com.blackwhitesoftware.pandalight.connect.PandaLightSerialConnection;
import com.blackwhitesoftware.pandalight.spec.MiscConfig;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Transient;
import java.io.File;
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
    private MiscConfig miscConfig;
    private final ActionListener uploadButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            byte bitfileIndex = (byte) bitfileIndexComboBox.getSelectedIndex();
            Bitfile bitfile = readBitfile();

            if (bitfile == null) return;

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            browseButton.setEnabled(false);
            uploadButton.setEnabled(false);

            serialConnection.sendBitfile(bitfileIndex, bitfile);

            browseButton.setEnabled(true);
            uploadButton.setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    };
    private final ActionListener browseButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            chooser.setCurrentDirectory(selectedFile);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new FileNameExtensionFilter("Bitfile", "bit"));

            int answer = chooser.showOpenDialog(getParent());
            if (answer != JFileChooser.APPROVE_OPTION)
                return;

            selectedFile = chooser.getSelectedFile();
            fileDisplay.setText(selectedFile.getName());

            miscConfig.mUploadBitfilePath = selectedFile.getAbsolutePath();

            boolean connected = serialConnection.isConnected();
            setConnectionFieldsAccess(connected);
        }
    };
    private final ActionListener bitfileIndexComboBoxListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            miscConfig.mUploadBitfileIndex = bitfileIndexComboBox.getSelectedIndex();
        }
    };
    /**
     * Constructor
     *
     * @param serialConnection
     */
    public UploadBitfilePanel(MiscConfig miscConfig, PandaLightSerialConnection serialConnection) {
        super();
        this.serialConnection = serialConnection;
        this.serialConnection.addObserver(this);
        this.selectedFile = null;
        this.miscConfig = miscConfig;

        File bitfile = new File(miscConfig.mUploadBitfilePath);
        if (bitfile.exists())
            selectedFile = bitfile;

        initialise();
    }

    private Bitfile readBitfile() {
        try {
            return new Bitfile(selectedFile);
        } catch (FileNotFoundException e1) {
            ErrorHandling.ShowMessage("The selected bitfile could not be found!");
        } catch (IOException e1) {
            ErrorHandling.ShowMessage("Could not read bitfile!");
        }
        return null;
    }

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
        setBorder(BorderFactory.createTitledBorder("Upload Bitfile"));

        fileLabel = new JLabel("Bitfile: ");
        add(fileLabel);

        fileDisplay = new JLabel("", JLabel.CENTER);
        if (selectedFile != null)
            fileDisplay.setText(selectedFile.getName());
        add(fileDisplay);

        browseButton = new JButton("browse");
        browseButton.addActionListener(browseButtonListener);
        add(browseButton);

        bitfileIndexComboBox = new JComboBox<>(new String[]{"0", "1"});
        if (miscConfig.mUploadBitfileIndex <= bitfileIndexComboBox.getItemCount())
            bitfileIndexComboBox.setSelectedIndex(miscConfig.mUploadBitfileIndex);
        bitfileIndexComboBox.addActionListener(bitfileIndexComboBoxListener);
        add(bitfileIndexComboBox);

        uploadButton = new JButton("upload");
        uploadButton.addActionListener(uploadButtonListener);
        add(uploadButton);

        setConnectionFieldsAccess(false);

        Component filler1 = Box.createHorizontalGlue();
        Component filler2 = Box.createHorizontalGlue();
        add(filler1);
        add(filler2);

        //The Layout

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(fileLabel)
                .addComponent(filler1)
                .addGroup(layout.createParallelGroup()
                        .addComponent(fileDisplay)
                        .addComponent(bitfileIndexComboBox))
                .addComponent(filler2)
                .addGroup(layout.createParallelGroup()
                        .addComponent(browseButton)
                        .addComponent(uploadButton)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(fileLabel)
                        .addComponent(filler1)
                        .addComponent(fileDisplay)
                        .addComponent(filler2)
                        .addComponent(browseButton))
                .addGroup(layout.createParallelGroup()
                        .addComponent(bitfileIndexComboBox)
                        .addComponent(uploadButton)));
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
