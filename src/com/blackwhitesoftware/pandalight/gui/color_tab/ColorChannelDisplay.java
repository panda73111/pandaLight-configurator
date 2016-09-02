package com.blackwhitesoftware.pandalight.gui.color_tab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

/**
 * Created by Sebastian.Huether on 02.09.2016.
 */
public class ColorChannelDisplay extends JComponent {
    private final byte[] red;
    private final byte[] green;
    private final byte[] blue;

    public ColorChannelDisplay() {
        this(new byte[256], new byte[256], new byte[256]);
    }

    public ColorChannelDisplay(byte[] red, byte[] green, byte[] blue) {
        this.red = Arrays.copyOf(red, 256);
        this.green = Arrays.copyOf(green, 256);
        this.blue = Arrays.copyOf(blue, 256);
        setBackground(Color.BLACK);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });
    }

    public byte[] getRed() {
        return Arrays.copyOf(red, 256);
    }

    public byte[] getGreen() {
        return Arrays.copyOf(green, 256);
    }

    public byte[] getBlue() {
        return Arrays.copyOf(blue, 256);
    }

    public void setRed(byte[] red) {
        System.arraycopy(red, 0, this.red, 0, 256);
        repaint();
    }

    public void setRed(byte index, byte value) {
        red[(int) index] = value;
        repaint();
    }

    public void setGreen(byte[] green) {
        System.arraycopy(green, 0, this.green, 0, 256);
        repaint();
    }

    public void setGreen(byte index, byte value) {
        green[(int) index] = value;
        repaint();
    }

    public void setBlue(byte[] blue) {
        System.arraycopy(blue, 0, this.blue, 0, 256);
        repaint();
    }

    public void setBlue(byte index, byte value) {
        blue[(int) index] = value;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        int w = getWidth();
        int h = getHeight();

        graphics.setColor(getBackground());
        graphics.fillRect(0, 0, w, h);
        graphics.setXORMode(Color.BLACK);

        Polygon redPoly = new Polygon();
        Polygon greenPoly = new Polygon();
        Polygon bluePoly = new Polygon();

        Polygon[] allPolys = new Polygon[]{redPoly, greenPoly, bluePoly};
        byte[][] allValues = new byte[][]{red, green, blue};

        double wScale = w / 255.0;
        double hScale = h / 255.0;

        for (int channelI = 0; channelI < 3; channelI++) {
            Polygon poly = allPolys[channelI];
            byte[] values = allValues[channelI];

            // bottom line
            poly.addPoint(w, h);
            poly.addPoint(0, h);

            for (int i = 0; i < 256; i++) {
                poly.addPoint(
                        (int) (wScale * i),
                        h - (int) (hScale * (values[i] & 0xFF))
                );
            }
        }

        graphics.setColor(Color.RED);
        graphics.fillPolygon(redPoly);
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(greenPoly);
        graphics.setColor(Color.BLUE);
        graphics.fillPolygon(bluePoly);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];

        for (int i = 0; i < 256; i++) {
            red[i] = (byte) (Math.pow(i - 128, 2) / 64 * 255 / 256);
            green[i] = (byte) ((Math.pow(i - 128, 2) / -64 + 256) * 255 / 256);
            blue[i] = (byte) i;
        }

        frame.getContentPane().add(new ColorChannelDisplay(red, green, blue));
        frame.setVisible(true);
    }
}
