package GJSWING;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class GJMouseListListener implements MouseListener{
	public void ListClicked(){}
	public void ListDoubleClicked(){}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getClickCount() >= 2){
			ListDoubleClicked();
		}else if(e.getClickCount() == 1){
			ListClicked();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
