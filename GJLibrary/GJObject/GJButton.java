package GJObject;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import GJSWING.GJAction;

public class GJButton extends GJObject{
	float ICON_SIZE = 0.65f;
	
	GJAction main_action;
	boolean isPressed = false;
	String text;
	Color normal_color=new Color(50,50,50),hover_color = Color.DARK_GRAY,press_color = Color.gray,text_color = Color.white;
	int fontsize =15;
	Image icon;
	public GJButton(Point point0, int width, int height,Color c,String text,GJAction action) {
		super(point0, width, height);
		this.main_action = action;
		this.text=text;
		this.normal_color = c;
		// TODO Auto-generated constructor stub
	}
	public GJButton(Point point0, int width, int height,Color c,String text,GJAction action,int fontsize) {
		super(point0, width, height);
		this.main_action = action;
		this.text=text;
		this.fontsize = fontsize;
		this.normal_color = c;
		// TODO Auto-generated constructor stub
	}
	public void setIcon(Image icon){
		this.icon = icon;
	}
	public void setIconSize(float val){
		this.ICON_SIZE = val;
	}
	public void setFontSize(int fontsize){
		this.fontsize = fontsize;
	}
	public void setColor(Color c){
		this.normal_color = c;
	}
	public void setHoverColor(Color c){
		this.hover_color = c;
	}
	public void setPressColor(Color c){
		this.press_color = c;
	}
	public void setTextColor(Color c){
		this.text_color = c;
	}
	@Override
	public void mouseEntered() {
		// TODO Auto-generated method stub
		this.panel_container.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.render();
	}

	@Override
	public void mouseLeave() {
		// TODO Auto-generated method stub
		this.panel_container.setCursor(this.panel_container.default_cursor);
		this.isPressed = false;
		this.render();
	}

	@Override
	public void mouseMoved(Point pos) {
		// TODO Auto-generated method stub
		if(this.panel_container.getCursor().getType() != Cursor.HAND_CURSOR){
			this.panel_container.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void mouseClick(int button, Point pos) {
		// TODO Auto-generated method stub
		this.main_action.mouseClick(button, pos);
	}

	@Override
	public void mouseDragged(int button, Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(int button, Point pos) {
		// TODO Auto-generated method stub
		this.isPressed = false;
		this.render();
	}

	@Override
	public void mousePressed(int button, Point pos) {
		// TODO Auto-generated method stub
		this.isPressed = true;
		this.render();
	}

	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		if(icon != null){
			if(this.MouseInside){
				gj2d.setColor(new Color(85,85,85));
			}else{
				gj2d.setColor(new Color(40,40,40));
			}
			if(this.isPressed){
				gj2d.setColor(press_color);
			}
			gj2d.fillRoundRect(0, 0, width, height,20);
			Font f = new Font("Calibri",Font.BOLD,10);
			int IMAGE_SIZE = (int)(height*ICON_SIZE);
			int IMAGE_POS = ((width-IMAGE_SIZE)/2);
			gj2d.drawImage(icon, IMAGE_POS, 5, IMAGE_SIZE, IMAGE_SIZE);
			gj2d.setColor(Color.white);
			gj2d.drawCenteredString(this.text, new Rectangle(0,IMAGE_SIZE+5,width,10), f);
		}else{
			if(this.MouseInside){
				gj2d.setColor(hover_color);
			}else{
				gj2d.setColor(normal_color);
			}
			if(this.isPressed){
				gj2d.setColor(press_color);
			}
			gj2d.fillRect(0, 0, width, height);
			gj2d.setColor(text_color);
			gj2d.drawCenteredString(this.text, new Rectangle(0,0,width,height), new Font("Arial",Font.BOLD,fontsize));
		}
		
	}

	@Override
	public void mouseDoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		this.main_action.mouseClick(MouseEvent.BUTTON1, pos);
	}

}
