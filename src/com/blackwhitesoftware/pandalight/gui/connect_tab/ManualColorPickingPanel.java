package com.blackwhitesoftware.pandalight.gui.connect_tab;

import com.blackwhitesoftware.pandalight.ConfigurationContainer;
import com.blackwhitesoftware.pandalight.connect.PandaLightSerialConnection;
import com.bric.swing.ColorPicker;

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
 * @author Fabian Hertwig
 * @author Sebastian Hüther
 */
public class ManualColorPickingPanel extends JPanel implements Observer, PropertyChangeListener {

    private PandaLightSerialConnection serialConnection;
    private ColorPicker colorPicker;
    private JButton setLedColorButton;
    private JCheckBox autoUpdateCheckbox;
    private JCheckBox expertViewCheckBox;
    private JCheckBox showColorWheelCheckbox;
    private ConfigurationContainer config;
    /**
     * Listener for the buttons and checkboxes
     */
    private final ActionListener mActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == setLedColorButton) {
                int[] chosenColor = colorPicker.getRGB();
                sendLedColor(chosenColor);
            } else if (e.getSource() == expertViewCheckBox) {
                colorPicker.setExpertControlsVisible(expertViewCheckBox.isSelected());
                config.mSerialConfig.colorPickerInExpertmode = expertViewCheckBox.isSelected();

            } else if (e.getSource() == showColorWheelCheckbox) {
                config.mSerialConfig.colorPickerShowColorWheel = showColorWheelCheckbox.isSelected();
                if (!showColorWheelCheckbox.isSelected()) {
                    colorPicker.setMode(ColorPicker.HUE);
                } else {
                    colorPicker.setMode(ColorPicker.BRI);
                }
            }


        }
    };

    /**
     * Constructor
     *
     * @param config
     * @param serialConnection
     */
    public ManualColorPickingPanel(ConfigurationContainer config, PandaLightSerialConnection serialConnection) {
        super();
        this.config = config;
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
        setBorder(BorderFactory.createTitledBorder("Set Led Color"));

        expertViewCheckBox = new JCheckBox("Expertview");
        expertViewCheckBox.setSelected(config.mSerialConfig.colorPickerInExpertmode);
        expertViewCheckBox.addActionListener(mActionListener);
        add(expertViewCheckBox);

        showColorWheelCheckbox = new JCheckBox("Colorwheel");
        showColorWheelCheckbox.setSelected(config.mSerialConfig.colorPickerShowColorWheel);
        showColorWheelCheckbox.addActionListener(mActionListener);
        add(showColorWheelCheckbox);

        colorPicker = new ColorPicker(false, false);
        colorPicker.setRGBControlsVisible(true);
        colorPicker.setPreviewSwatchVisible(true);
        colorPicker.setHexControlsVisible(false);
        colorPicker.setHSBControlsVisible(false);

        colorPicker.setMode(ColorPicker.HUE);
        colorPicker.setMinimumSize(new Dimension(150, 150));
        colorPicker.addPropertyChangeListener(this);
        //TODO: make the color picker size less static
        colorPicker.setPreferredSize(new Dimension(200, 200));
        colorPicker.setRGB(255, 255, 255);
        if (!showColorWheelCheckbox.isSelected()) {
            colorPicker.setMode(ColorPicker.HUE);
        } else {
            colorPicker.setMode(ColorPicker.BRI);
        }
        add(colorPicker, BorderLayout.CENTER);

        autoUpdateCheckbox = new JCheckBox("Auto Update");
        autoUpdateCheckbox.setToolTipText("Automatically send new color selections, this may be a bit slow and laggy!");
        add(autoUpdateCheckbox);

        setLedColorButton = new JButton("Set Led Color");
        setLedColorButton.addActionListener(mActionListener);
        add(setLedColorButton);

        setGuiElementsEnabled(false);

        //The Layout

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(showColorWheelCheckbox)
                        .addComponent(expertViewCheckBox))
                .addComponent(colorPicker)
                .addComponent(autoUpdateCheckbox)
                .addComponent(setLedColorButton));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(showColorWheelCheckbox)
                        .addComponent(expertViewCheckBox))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(colorPicker)
                        .addComponent(autoUpdateCheckbox))
                .addComponent(setLedColorButton));
    }

    /**
     * is called when the remote control connection status changes
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        if (serialConnection.isConnected()) {
            setGuiElementsEnabled(true);
        } else {
            setGuiElementsEnabled(false);
        }
    }

    /**
     * Is called from the color picker when the color changed
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (autoUpdateCheckbox != null && autoUpdateCheckbox.isSelected() && evt.getPropertyName().equals("selected color")) {
            int[] chosenColor = colorPicker.getRGB();
            sendLedColor(chosenColor);
        }
    }

    /**
     * Enable or disabel all guielements which shouldnt be editable if there is no connection
     *
     * @param enabled
     */
    private void setGuiElementsEnabled(boolean enabled) {
        setLedColorButton.setEnabled(enabled);
        autoUpdateCheckbox.setEnabled(enabled);

    }

    private void sendLedColor(int[] color) {
        int ledCount = config.mLedFrameConfig.getTotalLedCount();
        Color[] leds = new Color[ledCount];
        Arrays.fill(leds, new Color(color[0], color[1], color[2]));
        serialConnection.sendLedColors(leds);
    }
}
