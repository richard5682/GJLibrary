package GJNodeEditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import GJNeural.Conv2DNet.Nodes;
import GJNeural.Conv2DNet.Nodes2D;
import GJObject.GJObject;

public class NodesImageObject extends GJObject{
	final int image_width = 500;
	int pixel_size = 2;
	Nodes2D[] nodes2d = null;
	Nodes[] nodes = null;
	BufferedImage image = null;
	public NodesImageObject(Point point0, int width, int height,Nodes2D[] nodes2d) {
		super(point0, width, height);
		this.nodes2d = nodes2d;
	}
	public NodesImageObject(Point point0, int width, int height,Nodes[] nodes) {
		super(point0, width, height);
		this.nodes = nodes;
	}
	public void CalculateImage() {
		float[] values = null;
		int size = 0;
		if(nodes2d != null) {
			size = (int)Math.ceil(Math.sqrt(nodes2d.length));
			values = new float[nodes2d.length];
			for(int i=0;i<nodes2d.length;i++) {
				values[i] = nodes2d[i].yn;
			}
			
		}else if(nodes != null) {
			size = (int)Math.ceil(Math.sqrt(nodes.length));
			values = new float[nodes.length];
			for(int i=0;i<nodes.length;i++) {
				values[i] = nodes[i].yn;
			}
		}
		image = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
		int x=0,y=0;
		float highestval=0;
		for(int i=0;i<values.length;i++) {
			float val = values[i];
			if(val > highestval) {
				highestval = val;
			}
		}
		for(int i=0;i<values.length;i++) {
			float val = values[i];
			float val_rectify = val;
			if(val_rectify < 0) {
				val_rectify = val_rectify*-1;
			}
			int c = Trunchate(val_rectify/highestval*255);
			if(val < 0) {
				image.setRGB(x, y, new Color(0,c,0).getRGB());
			}else {
				image.setRGB(x, y, new Color(c,0,0).getRGB());
			}
			x++;
			if(x>=size) {
				x=0;
				y++;
			}
		}
		while(y<size) {
			image.setRGB(x, y, new Color(150,150,150).getRGB());
			x++;
			if(x>=size) {
				x=0;
				y++;
			}
		}
	}
	public int Trunchate(float val) {
		if(val < -255) {
			return 255;
		}else if(val > 255){
			return 255;
		}
		if(val < 0) {
			return -(int)val;
		}
		return (int)val;
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

	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		gj2d.drawImage(image, 0, 0, width, height);
	}

}
