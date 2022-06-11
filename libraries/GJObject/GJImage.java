package GJObject;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

public abstract class GJImage extends GJObject{
	Image image;
	public GJImage(Point point0, int width, int height,Image image) {
		super(point0, width, height);
		this.image = image;
		// TODO Auto-generated constructor stub
	}
	public void ChangeImage(BufferedImage image){
		this.image = image;
	}
	public void ChangeImage(Image image){
		this.image = image;
	}
	@Override
	public void mouseEntered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseLeave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClick(int button, Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(int button, Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(int button, Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(int button, Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}
	public abstract void drawImage(GJGraphics2D gj2d);
	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		if(image != null){
			gj2d.drawImage(image, point0.x, point0.y, width, height);
			drawImage(gj2d);
		}
	}

}
