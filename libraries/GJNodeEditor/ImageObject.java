package GJNodeEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import GJNeural.Conv2DNet.Filter;
import GJNeural.Conv2DNet.Image2D;
import GJNeural.Conv2DNet.Nodes2D;
import GJNodeEditor.ConvTrainer_Blocks.displaynetwork_panel_handler;
import GJObject.GJObject;
import GJObject.GJObject.GJGraphics2D;
import GJSWING.GJFrame;
import GJSWING.GJPanel;
import GJSWING.GJPanelHandler;

public class ImageObject extends GJObject {
	int size;
	Image2D image2d;
	GJFrame filter_frame = new GJFrame("Filter",900,580,0,JFrame.HIDE_ON_CLOSE,true,true);
	GJPanel filter_panel = new GJPanel(900, 600, 0, 0, new filter_panel_handler());
	int color = -1;
	BufferedImage image;
	public ImageObject(Point p,Image2D image2d,int size) {
		super(p, size, size);
		this.image2d = image2d;
		this.size = size;
		this.filter_frame.add(filter_panel);
		// TODO Auto-generated constructor stub
	}
	public ImageObject(Point p,Image2D image2d,int size,int color) {
		super(p, size, size);
		this.color = color;
		this.image2d = image2d;
		this.size = size;
		this.filter_frame.add(filter_panel);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void mouseEntered() {
		this.render();
	}

	@Override
	public void mouseLeave() {
		this.render();
	}

	@Override
	public void mouseMoved(Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClick(int button, Point pos) {
		this.filter_frame.show();
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
	public void calculatePixels() {
		Nodes2D[][] nodes =  image2d.nodes2d;
		int no_pixel = nodes.length;
		int pixel_size = Math.round(size/no_pixel);
		int summer_size = 1;
		if(pixel_size < 1) {
			pixel_size = 1;
			summer_size = (int) Math.ceil(no_pixel/size);
		}
		float highest_val = 0;
		for(int x=0;x<no_pixel;x++) {
			for(int y=0;y<no_pixel;y++) {
				if(nodes[x][y].yn > highest_val) {
					highest_val = nodes[x][y].yn;
				}
			}
		}
		int sx=0,sy=0;
		if(size > no_pixel) {
			image = new BufferedImage(pixel_size*no_pixel, pixel_size*no_pixel, BufferedImage.TYPE_INT_RGB);
		}else{
			image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g = image.createGraphics();
		for(int x=0;x<no_pixel;x+=summer_size) {
			for(int y=0;y<no_pixel;y+=summer_size) {
				float avg_val = 0;
				for(int ix=0;ix<summer_size;ix+=1) {
					for(int iy=0;iy<summer_size;iy+=1) {
						avg_val += nodes[x+ix][y+iy].yn;
					}
				}
				avg_val = avg_val/(summer_size*summer_size);
				int c = Trunchate(avg_val*255/highest_val);
				Color pixel_color = null;
				if(highest_val-0.0000001 < 0) {
					pixel_color = Color.black;
				}else {
					if(nodes[x][y].yn > 0) {
						pixel_color = new Color(c,0,0);
					}else if(nodes[x][y].yn <= 0) {
						pixel_color = new Color(0,c,0);
					}
				}
				if(color == 1) {
					pixel_color = new Color(c,0,0);
				}else if(color == 2) {
					pixel_color = new Color(0,c,0);
				}else if(color == 3){
					pixel_color = new Color(0,0,c);
				}else if(color == 0) {
					pixel_color = new Color(c,c,c);
				}
				g.setColor(pixel_color);
				g.fillRect(sx*pixel_size, sy*pixel_size, pixel_size, pixel_size);
				g.setColor(Color.white);
				sy++;
			}
			sx++;
			sy=0;
		}
		
	}
	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		gj2d.drawImage(image, 0, 0, size, size);
		if(this.MouseInside) {
			gj2d.setColor(Color.green);
			gj2d.drawRect(0, 0, size, size);
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
	public class filter_panel_handler extends GJPanelHandler {
		@Override
		public void draw(Graphics2D g2d) {
			int size = 50;
			if(image2d.kernels == null) {
				return;
			}
			g2d.drawImage(image, 300, 0, 300, 300, null);
			Filter[] filters = image2d.kernels.filters;
			int ix=0,iy=0;
			int filters_length = (int)Math.round(Math.sqrt(filters.length));
			for(int i=0;i<filters.length;i++) {
				float[][] weights = filters[i].weights;
				int pixel_size = size/weights.length;
				float highest_val = 0;
				for(int x=0;x<weights.length;x++) {
					for(int y=0;y<weights.length;y++) {
						if(weights[x][y] > highest_val) {
							highest_val = weights[x][y];
						}
					}
				}
				for(int x=0;x<weights.length;x++) {
					for(int y=0;y<weights.length;y++) {
						float value = weights[x][y]/highest_val;
						int c = Trunchate(value*255);
						
						if(weights[x][y] > 0) {
							g2d.setColor(new Color(0,c,0));
						}else if(weights[x][y] <= 0) {
							g2d.setColor(new Color(c,0,0));
						}
				
						g2d.fillRect(50+(pixel_size*x)+(ix*(size+20)), 320+(pixel_size*y)+(iy*(size+20)), pixel_size, pixel_size);
						
					}
				}
				ix++;
				if(ix>11) {
					ix=0;
					iy++;
				}
			}
		}
	}
}
