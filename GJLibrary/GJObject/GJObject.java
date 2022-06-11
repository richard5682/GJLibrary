package GJObject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import GJSWING.GJAction;
import GJSWING.GJGraphics;
import GJSWING.GJPanel;
import GJUtil.GJMath;

public abstract class GJObject extends GJGraphics implements GJAction{
	public Point point0 = new Point(0,0);
	public boolean MouseInside = false;
	public boolean showing = true;
	public int width,height;
	public Graphics2D g2d;
	public GJGraphics2D gj2d;
	public GJPanel panel_container;
	public ArrayList<Component> Components = new ArrayList<Component>();
	public ArrayList<GJObject> Objects = new ArrayList<GJObject>();
	public GJObject parent;
	public boolean RenderFlag = false;
	public int highest_yvalue;
	public int highest_xvalue;
	public GJObject(Point point0,int width,int height){
		this.point0 = point0;
		this.width = width;
		this.height = height;
	}
	public GJObject(Point point0,int width,int height,GJObject parent){
		this.point0 = point0;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}
	public Point GetParentOffset(){
		Point p = new Point(0,0);
		GJObject parent_obj = parent;
		while(parent_obj != null){
			p = GJMath.Add(p, parent_obj.point0);
			parent_obj = parent_obj.parent;
		}
		return p;	
	}
	public Point GetWorldPoint(){
		return GJMath.Add(point0, GetParentOffset());
	}
	public Point GetLocalPoint(){
		return point0;
	}
	public void addComponent(Component c){
		Components.add(c);
	}
	public void addComponent(GJObject c){
		Objects.add(c);
		c.parent = this;
		c.panel_container = this.panel_container;
	}
	public void setGraphics(Graphics2D g2d){
		this.g2d = g2d;
		gj2d = new GJGraphics2D(g2d);
	}
	protected Point GetLocalPoint(Point p){
		return new Point(p.x-this.point0.x,p.y-this.point0.y);
	}
	public final void draw(Graphics2D g2d){
		drawLocal(gj2d);
	}
	public final void render(){
		if(this.panel_container != null) {
			this.panel_container.repaint();
		}
//		RenderFlag = true;
	}
	public final void delete(){
		for(int i=0;i<Objects.size();i++){
			Objects.get(i).delete();
		}
		this.panel_container.delete(this);
	}
	public void mouseMoved(MouseEvent event){}
	public void mouseClick(MouseEvent event){}
	public void mouseDragged(MouseEvent event){}
	public void mouseReleased(MouseEvent event){}
	public void mousePressed(MouseEvent event){}
	public abstract void drawLocal(GJGraphics2D gj2d);
	public class GJGraphics2D{
		public Graphics2D g2d;
		String font_type = "Arial";
		int default_font_size = 15;
		boolean bold = true;
		Font default_font = new Font(font_type,Font.BOLD,default_font_size);
		public GJGraphics2D(Graphics2D g2d){
			this.g2d = g2d;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		}
		public void setFontSize(int fontsize){
			default_font_size = fontsize;
			reloadFont();
		}
		public void setBold(boolean setBold){
			bold = setBold;
			reloadFont();
		}
		public void setFont(String font){
			font_type = font;
			reloadFont();
		}
		public void reloadFont() {
			if(bold){
				default_font = new Font(font_type,Font.BOLD,default_font_size);
			}else{
				default_font = new Font(font_type,Font.PLAIN,default_font_size);
			}
		}
		public void setColor(Color c){
			this.g2d.setColor(c);
			
		}
		public void drawCubicBezier(Point p0,Point p1,Point p2,Point p3,float thickness) {
			//Point last_p = new Point(p3.x,p3.y);
			Path2D path = new Path2D.Double();
			path.moveTo(p3.x,p3.y);
			g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			float dist = GJMath.GetDistance(p0, p3);
			int res = Math.round(dist/5);
			if(res > 100) {
				res = 100;
			}
			float t=0;
			
			for(float i=0;i<res;i++) {
				Point p01 = GJMath.PointInterpolate(p0, p1, t);
				Point p12 = GJMath.PointInterpolate(p1, p2, t);
				Point p23 = GJMath.PointInterpolate(p2, p3, t);
				Point p0112 = GJMath.PointInterpolate(p01, p12, t);
				Point p1223 = GJMath.PointInterpolate(p12, p23, t);
				Point p = GJMath.PointInterpolate(p0112, p1223, t);	
				t+=(float)1/res;
				//g2d.drawLine(last_p.x, last_p.y, p.x, p.y);
				path.lineTo(p.x,p.y);
				//last_p = p;
			}
			//g2d.drawLine(last_p.x, last_p.y, p0.x, p0.y);
			path.lineTo(p0.x,p0.y);
			g2d.draw(path);
		}
		public void drawLine(int x,int y,int x1,int y1){
			Point parent = GetParentOffset();
			int yy0 = y+point0.y+parent.y;
			int yy1 = y1+point0.y+parent.y;
			this.g2d.drawLine(x+point0.x+parent.x,yy0 , x1+point0.x+parent.x, yy1);
		}
		public void drawRect(int x,int y,int w,int h){
			Point parent = GetParentOffset();
			int yy0 = y+point0.y+parent.y;
			this.g2d.drawRect(x+point0.x+parent.x, yy0, w, h);
		}
		public void drawRoundRect(int x,int y,int w,int h,int r){
			Point parent = GetParentOffset();
			int yy0 = y+point0.y+parent.y;
			this.g2d.drawRoundRect(x+point0.x+parent.x, yy0, w, h,r ,r);
		}
		public void fillRoundRect(int x,int y,int w,int h,int r){
			Point parent = GetParentOffset();
			int yy0 = y+point0.y+parent.y;
			this.g2d.fillRoundRect(x+point0.x+parent.x, yy0, w, h,r,r);
		}
		public void fillRect(int x,int y,int w,int h){
			Point parent = GetParentOffset();
			int yy0 = y+point0.y+parent.y;
			this.g2d.fillRect(x+point0.x+parent.x, yy0, w, h);
		}
		public void fillOval(int x,int y,int w,int h){
			Point parent = GetParentOffset();
			int yy0 = y+point0.y+parent.y;
			this.g2d.fillOval(x+point0.x+parent.x, yy0, w, h);
		}
		public void drawCenteredString(String text, Rectangle rect, Font font) {
		    // Get the FontMetrics
			Point parent = GetParentOffset();
		    FontMetrics metrics = g2d.getFontMetrics(font);
		    int i=0;
		    for(String line:text.split("\n")){
		    	// Determine the X coordinate for the text
			    int x = rect.x + (rect.width - metrics.stringWidth(line)) / 2;
			    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
			    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
			    // Set the font
			    g2d.setFont(font);
			    // Draw the String
		    	 g2d.drawString(line, x+point0.x+parent.x, y+point0.y+parent.y+metrics.getHeight()*i);
		    	 i++;
		    }
		}
		public void drawCenteredString(String text, Rectangle rect) {
			drawCenteredString(text,rect,this.default_font);
		}
		public void drawImage(BufferedImage image,int x,int y,int width,int height){
			Point parent = GetParentOffset();
			int yy0 = y+point0.y+parent.y;
			g2d.drawImage(image, x+point0.x+parent.x, yy0, width, height, null);
		}
		public void drawImage(Image image,int x,int y,int width,int height){
			Point parent = GetParentOffset();
			int yy0 = y+point0.y+parent.y;
			g2d.drawImage(image, x+point0.x+parent.x, yy0, width, height, null);
		}
		public void drawString(int x,int y,String text, Font font) {
		    // Get the FontMetrics 
			Point parent = GetParentOffset();
		    FontMetrics metrics = g2d.getFontMetrics(font);
		    // Set the font
		    g2d.setFont(font);
		    // Draw the String
		    int i=0;
		    for(String line:text.split("\n")){
		    	 g2d.drawString(line, point0.x+parent.x+x, y+point0.y+parent.y+metrics.getHeight()*i);
		    	 i++;
		    }
		}
	}
}
