package GJSWING;

import java.awt.Point;
import java.awt.event.MouseEvent;

public abstract class GJButtonAction implements GJAction{
	public void mouseEntered() {
	}
	public void mouseLeave() {
	}
	public void mouseMoved(Point pos) {
	}
	public abstract void mouseClick(int button,Point pos);
	public void mouseDragged(int button,Point pos) {
	}
	public void mouseReleased(int button,Point pos) {
	}
	public void mousePressed(int button,Point pos) {
	}
	public void mouseDoubleClicked(Point pos){
		
	}
	public void mouseMoved(MouseEvent event){}
	public void mouseClick(MouseEvent event){}
	public void mouseDragged(MouseEvent event){}
	public void mouseReleased(MouseEvent event){}
	public void mousePressed(MouseEvent event){}
}
