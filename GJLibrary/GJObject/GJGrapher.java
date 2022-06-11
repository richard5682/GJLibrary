package GJObject;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class GJGrapher extends GJObject{
	float[] data;
	float xscale,yscale;
	String xlabel,ylabel,title;
	Point offset;
	float highestvalue=0;
	Font digit_font = new Font("Arial",Font.BOLD,10);
	public GJGrapher(Point point0, int width, int height,String title,String xlabel,String ylabel,Point offset) {
		super(point0, width, height);
		this.offset = offset;
		this.xlabel = xlabel;
		this.ylabel = ylabel;
		this.title = title;
		// TODO Auto-generated constructor stub
	}
	public void setData(float[] data){
		this.data = data;
		float highestvalue=0;
		for(int i=0;i<data.length;i++){
			if(data[i]>highestvalue){
				highestvalue = data[i];
			}
		}
		this.highestvalue=highestvalue+0.3f;
		this.render();
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
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		int NUMBER_OF_XVALUE = 15;
		int NUMBER_OF_YVALUE = 15;
	
		gj2d.setColor(new Color(85,85,85));
		gj2d.fillRect(0, 0, width, height+15);
		gj2d.setColor(new Color(120,120,120));
		gj2d.fillRect(30, 30, width-60, height-60);
		gj2d.setColor(Color.WHITE);
		gj2d.drawCenteredString(xlabel,new Rectangle(0,height-10,width,20), new Font("Arial",Font.BOLD,10));
		gj2d.drawCenteredString(title,new Rectangle(0,0,width,30), new Font("Arial",Font.BOLD,20));
		if(data != null && data.length > 1){
			xscale = (this.width-60)/data.length;
			yscale = (this.height-60)/highestvalue;
		
			int prevx,prevy;
			int ylevel=0,xlevel=0;
			
			float yspacing = (height-30)/NUMBER_OF_YVALUE;
			float yval = highestvalue/NUMBER_OF_YVALUE;
			for(int i=0;i<NUMBER_OF_YVALUE;i++){
				int xpos = 5;
				int ypos = height-(int)Math.round(i*yspacing+offset.y+35);
				gj2d.setColor(Color.lightGray);
				gj2d.drawLine(xpos+25, ypos+5, width-30, ypos+5);
				gj2d.setColor(Color.white);
				gj2d.drawCenteredString(String.format("%.2f",(i*yval)),new Rectangle(xpos,ypos,20,10), digit_font);
				gj2d.drawLine(xpos+25, ypos+5, xpos+30, ypos+5);
			}
			float spacing = data.length/NUMBER_OF_XVALUE;
			int extravalue = Math.round((data.length%NUMBER_OF_XVALUE)/spacing);
			if(data.length < NUMBER_OF_XVALUE){
				extravalue = data.length;
				spacing = 1;
			}
			for(int i=0;i<NUMBER_OF_XVALUE+extravalue;i++){
				int xpos = (int)Math.round(i*spacing*xscale)+30-10;
				int ypos = (int)height-30;
				gj2d.setColor(Color.lightGray);
				gj2d.drawLine(xpos+10, 30, xpos+10, ypos);
				gj2d.setColor(Color.white);
				gj2d.drawCenteredString(String.valueOf((int)Math.round(i*spacing)),new Rectangle(xpos,ypos+5,20,10),digit_font );
				gj2d.drawLine(xpos+10, ypos-5, xpos+10, ypos);
			}
			gj2d.setColor(Color.ORANGE);
			for(int i=0;i<data.length;i++){
				prevy = ylevel;
				prevx = xlevel;
				ylevel = (int)Math.round(data[i]*yscale+offset.y+30);
				xlevel = (int)Math.round(i*xscale+this.offset.x+30);
				if(i>=1){
					gj2d.drawLine(prevx, height-prevy, xlevel, -ylevel+height);
				}
			}
		}
		gj2d.setColor(Color.black);
		gj2d.drawLine(30,height-30,width-30,height-30);
		gj2d.drawLine(30,30,30,height-30);
		gj2d.drawLine(30,30,width-30,30);
		gj2d.drawLine(width-30,30,width-30,height-30);
		
	}
	@Override
	public void mouseDoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}

}
