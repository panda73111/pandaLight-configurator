package com.blackwhitesoftware.pandalight.gui.LedSimulation;

import com.blackwhitesoftware.pandalight.spec.Led;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.List;
import java.util.Vector;

public class LedSimulationWorker extends SwingWorker<BufferedImage, Object> {

    private final BufferedImage tvImage;

    private final Vector<Led> mLeds;
    private final List<LedPaint> ledPaints = new Vector<>();

    public LedSimulationWorker(BufferedImage pTvImage, Vector<Led> pLeds) {
        super();

        tvImage = pTvImage;
        mLeds = pLeds;
    }

    @Override
    protected BufferedImage doInBackground() throws Exception {
        Dimension imageDim = new Dimension(1280, 720);
        BufferedImage backgroundImage = new BufferedImage(imageDim.width, imageDim.height, BufferedImage.TYPE_INT_ARGB);

        if (mLeds == null) {
            return backgroundImage;
        }

        ledPaints.clear();

        int imageWidth = tvImage.getWidth();
        int imageHeight = tvImage.getHeight();
        for (Led led : mLeds) {
            LedPaint ledPaint = new LedPaint();

            // Determine the location and orientation of the led on the image
            ledPaint.point = tv2image(imageDim, led.mLocation);
            ledPaint.angle_rad = led.mSide.getAngle_rad();

            // Determine the color of the led
            int xMin = (int) (led.mImageRectangle.getMinX() * (imageWidth - 1));
            int xMax = (int) (led.mImageRectangle.getMaxX() * (imageWidth - 1));
            int yMin = (int) (led.mImageRectangle.getMinY() * (imageHeight - 1));
            int yMax = (int) (led.mImageRectangle.getMaxY() * (imageHeight - 1));
            ledPaint.rgb = determineRGB(xMin, xMax, yMin, yMax);

            ledPaints.add(ledPaint);
        }

        Graphics2D g2d = backgroundImage.createGraphics();
        paintAllLeds(g2d);

        return backgroundImage;
    }

    Point tv2image(Dimension pImageDim, Point2D point) {
        double tvWidthFraction = (1.0 - 2 * 0.1);
        double tvHeightFraction = (1.0 - 2 * 0.2);

        double tvWidth = tvWidthFraction * pImageDim.width;
        double tvXIndex = point.getX() * tvWidth;
        double imageXIndex = tvXIndex + 0.1 * pImageDim.width;

        double tvHeight = tvHeightFraction * pImageDim.height;
        double tvYIndex = point.getY() * tvHeight;
        double imageYIndex = tvYIndex + 0.2 * pImageDim.height;

        return new Point((int) imageXIndex, (int) imageYIndex);
    }

    private int determineRGB(int xMin, int xMax, int yMin, int yMax) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int[] color = new int[4];

        int w = xMax - xMin + 1;
        int h = yMax - yMin + 1;
        Raster raster = tvImage.getData(new Rectangle(xMin, yMin, w, h));

        for (int y = yMin; y <= yMax; y++) {
            for (int x = xMin; x <= xMax; x++) {
                raster.getPixel(x, y, color);
                red = (red + color[0]) / 2;
                green = (green + color[1]) / 2;
                blue = (blue + color[2]) / 2;
            }
        }

        return (red << 16) | (green << 8) | blue;
    }

    private void paintAllLeds(Graphics2D g2d) {
        final int ledSize = 200;
        // pixels of space between the LED and its beam, outwards
        // (negative = move beam inside)
        final int ledOffset = -10;
        // 0 = ambient light, LED directed at the wall
        // ledSize/2 = beam follows the wall at 90 degrees
        final int directionDistance = 80;

        g2d.setComposite(new LightingComposite());

        for (LedPaint led : ledPaints) {
            if (isCancelled()) {
                return;
            }

            double xFactor = Math.sin(led.angle_rad);
            double yFactor = Math.cos(led.angle_rad);

            Point directionPoint = new Point(led.point);
            directionPoint.translate(
                    (int) (-1.0 * xFactor * (directionDistance + ledOffset)),
                    (int) (-1.0 * yFactor * (directionDistance + ledOffset)));

            Point virtualLedPoint = new Point(led.point);
            virtualLedPoint.translate(
                    (int) (-1.0 * xFactor * ledOffset),
                    (int) (-1.0 * yFactor * ledOffset));

            Color Color1 = new Color(0xFF000000 | led.rgb, true);
            Color Color2 = new Color(0xAA000000 | led.rgb, true);

            RadialGradientPaint paint = new RadialGradientPaint(
                    directionPoint, ledSize / 2.0f, virtualLedPoint,
                    new float[]{
                            0.0f,
                            0.5f,
                            1.0f
                    },
                    new Color[]{
                            Color1,
                            Color2,
                            Color.BLACK
                    },
                    MultipleGradientPaint.CycleMethod.NO_CYCLE
            );
            g2d.setPaint(paint);
            g2d.fillRect(
                    directionPoint.x - ledSize / 2,
                    directionPoint.y - ledSize / 2,
                    ledSize, ledSize);
        }
    }

    class LedPaint {
        int rgb;
        Point point;
        double angle_rad;
    }
}
