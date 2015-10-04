package com.blackwhitesoftware.pandalight.gui.led_simulation;

import javax.swing.*;
import java.awt.*;

public class ImageComponent extends JComponent {

    private Image mImage;

    public ImageComponent() {
        super();
    }

    public void setImage(Image pImage) {
        mImage = pImage;
    }

    @Override
    public void paint(Graphics g) {
        if (mImage == null) {
            return;
        }
        g.drawImage(mImage, 0, 0, getWidth(), getHeight(), null);
    }
}
