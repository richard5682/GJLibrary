package GJObject;

import java.awt.Color;
import java.awt.Point;

public class GJProgressBar extends GJObject{
	float progress = 0;
	GJText progress_text;
	public GJProgressBar(Point point0, int width, int height) {
		super(point0, width, height);
		progress_text = new GJText(new Point(0,0), width, height, "0%",Color.black,10);
		this.addComponent(progress_text);
		// TODO Auto-generated constructor stub
	}
	public void ChangeProgress(float progress){
		this.progress = progress;
		if(this.progress > 1){
			this.progress = 1;
		}
		progress_text.changeText(String.valueOf(progress*100)+"%");
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
	public void mouseDoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
	
		gj2d.setColor(Color.LIGHT_GRAY);
		gj2d.fillRect(0, 0, width, height);
		gj2d.setColor(Color.GREEN);
		gj2d.fillRect(0,0,(int)(width*progress),height);
		gj2d.setColor(Color.black);
	}

}
