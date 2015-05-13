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
	
	public LedSimulationWorker(BufferedImage pTvImage, Vector<Led> pLeds) {
		super();
		
		tvImage = pTvImage;
		mLeds    = pLeds;
	}
	
	class LedPaint {
		Color color;
		Point point;
		double angle_rad;
	}
	
	private final List<LedPaint> ledPaints = new Vector<>();
	

	@Override
	protected BufferedImage doInBackground() throws Exception {
		Dimension imageDim = new Dimension(1280, 720);
		BufferedImage backgroundImage = new BufferedImage(imageDim.width, imageDim.height, BufferedImage.TYPE_INT_ARGB);

		if(mLeds == null){
			return backgroundImage;
		}

		ledPaints.clear();
		
		setProgress(5);
		
		int imageWidth  = tvImage.getWidth();
		int imageHeight = tvImage.getHeight();
		for (Led led : mLeds) {
			LedPaint ledPaint = new LedPaint();
			
			// Determine the location and orientation of the led on the image
			ledPaint.point = tv2image(imageDim, led.mLocation);
			ledPaint.angle_rad = 0.5*Math.PI - led.mSide.getAngle_rad();
			
			// Determine the color of the led
			int xMin = (int)(led.mImageRectangle.getMinX() * (imageWidth-1));
			int xMax = (int)(led.mImageRectangle.getMaxX() * (imageWidth-1));
			int yMin = (int)(led.mImageRectangle.getMinY() * (imageHeight-1));
			int yMax = (int)(led.mImageRectangle.getMaxY() * (imageHeight-1));
			ledPaint.color = determineColor(xMin, xMax, yMin, yMax);
			
			ledPaints.add(ledPaint);
		}
		
		setProgress(10);
		
		Graphics2D g2d = backgroundImage.createGraphics();
		// Clear the image with a black rectangle
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
		paintAllLeds(g2d);

		return backgroundImage;
	}
	
	Point tv2image(Dimension pImageDim, Point2D point) {
		double tvWidthFraction  = (1.0 - 2*0.1);
		double tvHeightFraction = (1.0 - 2*0.2);
		
		double tvWidth = tvWidthFraction * pImageDim.width;
		double tvXIndex = point.getX()*tvWidth;
		double imageXIndex = tvXIndex + 0.1*pImageDim.width;
		
		double tvHeight = tvHeightFraction * pImageDim.height;
		double tvYIndex = point.getY()*tvHeight;
		double imageYIndex = tvYIndex + 0.2*pImageDim.height;

		return new Point((int)imageXIndex, (int)imageYIndex);
	}
	
	private Color determineColor(int xMin, int xMax, int yMin, int yMax) {
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
		
		return new Color(red, green, blue);
	}
	
	private void paintAllLeds(Graphics2D g2d) {
        int ledsDrawn = 0;
        final float ledSize = 500;
        final Color transparent = new Color(0, 0, 0, 0);

        g2d.setComposite(new LightingComposite());

        for(LedPaint led : ledPaints) {
            if (isCancelled()) {
                return;
            }

            int rgb = 0xFFFFFF & led.color.getRGB();
            RadialGradientPaint paint = new RadialGradientPaint(
                    led.point, ledSize / 2.0f,
                    new float[] {
                            0.3f,
                            0.5f,
                            1.0f
                    },
                    new Color[] {
                            new Color(0xB0000000 | rgb, true),
                            new Color(0x50000000 | rgb, true),
                            transparent
                    },
                    MultipleGradientPaint.CycleMethod.NO_CYCLE
            );
            g2d.setPaint(paint);
            g2d.fillRect(
                    (int) (led.point.getX() - ledSize / 2),
                    (int) (led.point.getY() - ledSize / 2),
                    (int) ledSize, (int) ledSize);

            ledsDrawn++;
            int progress = 10 + (int)(ledsDrawn*90.0/ ledPaints.size());
            setProgress(progress);
        }
	}
}
