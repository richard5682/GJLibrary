package GJImageProcessing;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class GJImageUtil {
	public static BufferedImage CaptureScreen(int x,int y,int width,int height){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = ge.getScreenDevices();

		Rectangle allScreenBounds = new Rectangle();
		allScreenBounds.x = x;
		allScreenBounds.y = y;
		allScreenBounds.width = width;
		allScreenBounds.height = height;
		Robot robot;
		try {
			robot = new Robot();
			BufferedImage screenShot = robot.createScreenCapture(allScreenBounds);
			return screenShot;
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Image makeColorTransparent(BufferedImage im, final Color color) {
	    ImageFilter filter = new RGBImageFilter() {
	        // the color we are looking for... Alpha bits are set to opaque
	        public int markerRGB = color.getRGB() | 0xFF000000;
	        public final int filterRGB(int x, int y, int rgb) {
	            if ((rgb | 0xFF000000) == markerRGB) {
	                // Mark the alpha bits as zero - transparent
	                return 0x00FFFFFF & rgb;
	            } else {
	                // nothing to do
	                return rgb;
	            }
	        }
	    };
	    ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	    return Toolkit.getDefaultToolkit().createImage(ip);
	}
}
