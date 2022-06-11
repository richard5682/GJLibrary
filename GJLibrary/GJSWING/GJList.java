package GJSWING;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JScrollPane;

public class GJList extends JScrollPane{
	public GJListAction action;
	JList<String> list = new JList<String>();
	public GJList(int x,int y,int width,int height,GJListAction action){
		this.setBounds(x, y, width, height);
		this.setViewportView(list);
		list.setLayoutOrientation(JList.VERTICAL);
		this.action = action;
		list.addMouseListener(new Listener());
	}
	public class Listener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			action.ListClicked(list.getSelectedIndex(), e.getClickCount());
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public void SetData(String[] data){
		list.setListData(data);
	}
}
