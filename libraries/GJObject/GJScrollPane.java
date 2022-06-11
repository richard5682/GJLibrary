package GJObject;

import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import GJSWING.GJScrollAction;

public class GJScrollPane extends GJObject implements GJScrollAction,AdjustmentListener{

	public GJScrollPane(Point point0, int width, int height) {
		super(point0, width, height);
		// TODO Auto-generated constructor stub
	}
	public void ScrollUpdate(float value){
		if(highest_yvalue > height){
			this.point0.y = -(int)((this.highest_yvalue-(this.height-30))*value);
			this.render();
		}
	}
	public void adjustmentValueChanged(AdjustmentEvent e) {
		this.ScrollUpdate((float)e.getValue()/90);
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
		
	}
}
