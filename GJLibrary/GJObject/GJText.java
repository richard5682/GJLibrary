package GJObject;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class GJText extends GJObject{
	String text;
	public Color textcolor = Color.black;
	int fontsize = 15;
	public boolean centered= true;
	public Font font;
	String style = "Arial";
	public int font_stroke = Font.PLAIN;
	public GJText(Point point0, int width, int height,String text) {
		super(point0, width, height);
		// TODO Auto-generated constructor stub
		font = new Font(style,font_stroke,fontsize);
		this.text = text;
	}
	public GJText(Point point0, int width, int height,String text,Color c,int fontsize) {
		super(point0, width, height);
		// TODO Auto-generated constructor stub
		this.fontsize = fontsize;
		this.textcolor = c;
		font = new Font(style,font_stroke,fontsize);
		this.text = text;
	}
	public void changeText(String text){
		this.text = text;
		this.render();
	}
	public void changeColor(Color c){
		this.textcolor = c;
	}
	public void changeBold(boolean bold){
		if(bold){
			font_stroke = Font.BOLD;
			font = new Font(style,Font.BOLD,fontsize);
		}else{
			font_stroke = Font.PLAIN;
			font = new Font(style,Font.PLAIN,fontsize);
		}
	}
	public void changeFontSize(int fontsize){
		this.fontsize = fontsize;
		font = new Font(style,font_stroke,fontsize);
	}
	public void changeFontStyle(String style){
		this.style = style;
		font = new Font(style,font_stroke,fontsize);
	}
	public FontMetrics GetFontMetrics(){
		return g2d.getFontMetrics();
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
		gj2d.setColor(textcolor);
		if(centered){
			gj2d.drawCenteredString(this.text, new Rectangle(0,0,width,height), font);
		}else{
			gj2d.drawString(0,0,this.text, font);
		}
	}
	@Override
	public void mouseDoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}

}
