package GJSWING;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import GJObject.GJObject;
import GJObject.GJScrollPane;
import GJUtil.GJMath;

public class GJPanel extends JPanel{
	static ArrayList<GJPanel> panel_list = new ArrayList<GJPanel>();
	public ArrayList<GJObject> objectslist = new ArrayList<GJObject>();
	GJGraphics renderer;
	GJAction actionhandler;
	Graphics2D g2d;
	int last_button_pressed = 0;
	Listener listener = new Listener();
	public JScrollBar scroll_bar_vertical;
	public GJScrollPane scroll_pane;
	public Cursor default_cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	public GJPanel(int width,int height,int x,int y,GJGraphics renderer,GJAction actionhandler){
		panel_list.add(this);
		this.setLocation(x, y);
		this.setSize(width, height);
		this.renderer = renderer;
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.actionhandler = actionhandler;
		this.setLayout(null);
		this.show();
	}
	public GJPanel(int width,int height,int x,int y,GJPanelHandler handler){
		panel_list.add(this);
		this.setLocation(x, y);
		this.setSize(width, height);
		this.renderer = handler;
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.actionhandler = handler;
		this.setLayout(null);
		this.show();
	}
	public void addPanelHandler(GJPanelHandler handler) {
		this.renderer = handler;
		this.actionhandler = handler;
	}
	public void setDefaultCursor(int cursortype){
		this.default_cursor = new Cursor(cursortype);
	}
	public void clearObject() {
		//Not compatible when there is scroll bar
		while(objectslist.size()>0) {
			objectslist.get(0).delete();
		}
	}
	public void addObjectScroll(GJObject object){
		scroll_pane.addComponent(object);
		this.addObject(object);
		if(object.point0.y+object.height > scroll_pane.highest_yvalue){
			scroll_pane.highest_yvalue = object.point0.y+object.height;
			if(scroll_pane.highest_yvalue < scroll_pane.height) {
				this.scroll_bar_vertical.hide();
			}else {
				this.scroll_bar_vertical.show();
			}
		}
	}
	public void addObject(GJObject object){
		objectslist.add(object);
		object.panel_container = this;
		for(int i=0;i<object.Components.size();i++){
			this.add(object.Components.get(i));
		}
		for(int i=0;i<object.Objects.size();i++){
			objectslist.add(object.Objects.get(i));
			object.Objects.get(i).panel_container = this;
			addObjectExcept(object.Objects.get(i));
		}
	}
	public void addObjectExcept(GJObject object){
		object.panel_container = this;
		for(int i=0;i<object.Components.size();i++){
			this.add(object.Components.get(i));
		}
		for(int i=0;i<object.Objects.size();i++){
			objectslist.add(object.Objects.get(i));
			object.Objects.get(i).panel_container = this;
			addObjectExcept(object.Objects.get(i));
		}
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		this.g2d = g2d;
		if(renderer != null){
			this.renderer.draw(g2d);
		}
		for(int i=0;i<objectslist.size();i++){
			if(objectslist.get(i).showing){
				objectslist.get(i).setGraphics(g2d);
				objectslist.get(i).draw(g2d);
			}
		}
	}
	public void addScrollBar(){
		scroll_pane = new GJScrollPane(new Point(0,0),this.getWidth()-25,this.getHeight());
		scroll_bar_vertical = new JScrollBar();
		scroll_bar_vertical.setBounds(this.getWidth()-25, 0, 25, this.getHeight());
		scroll_bar_vertical.addAdjustmentListener(scroll_pane);
		this.addMouseWheelListener(new scrollbar_mousewheel());
		this.addObject(scroll_pane);
		this.add(scroll_bar_vertical);
	}
	public void ScrollUpdate(float value){
		scroll_pane.ScrollUpdate(value);
	}
	public void ResetScroll(){
		scroll_pane.ScrollUpdate(0);
		this.scroll_bar_vertical.setValue(0);
		this.scroll_pane.highest_yvalue = 0;
	}
	public class scrollbar_mousewheel implements MouseWheelListener{

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if(e.getUnitsToScroll() > 0){
				scroll_bar_vertical.setValue(scroll_bar_vertical.getValue()+5);
			}else{
				scroll_bar_vertical.setValue(scroll_bar_vertical.getValue()-5);
			}
		}
		
	}
// TODO: DO SOMETHING HERE
//	public void RenderPanel(){
//		boolean render = false;
//		for(GJObject object:objectslist){
//			if(object.RenderFlag){
//				render = true;
//				object.RenderFlag = false;
//			}
//		}
//		if(render){
//			this.repaint();
//		}
//	}
//	public void RenderAllPanel(){
//		for(GJPanel panel:panel_list){
//			panel.RenderPanel();
//		}
//	}
	public class Listener implements MouseListener,MouseMotionListener{
		@Override
		public void mouseClicked(MouseEvent event) {
			// TODO Auto-generated method stub
			if(actionhandler != null){
				actionhandler.mouseClick(event.getButton(), event.getPoint());
				actionhandler.mouseClick(event);
			}
			for(int i=0;i<objectslist.size();i++){
				GJObject obj = objectslist.get(i);
				if(obj.showing){
					Point parent = obj.GetParentOffset();
					if(GJMath.checkPointInside(GJMath.Add(obj.point0,parent), event.getPoint(), obj.width, obj.height)){
						if(event.getClickCount() == 2 && event.getButton() == event.BUTTON1){
							objectslist.get(i).mouseDoubleClicked(event.getPoint());
						}else if(event.getClickCount() == 1){
							objectslist.get(i).mouseClick(event.getButton(), event.getPoint());
						}else{
							objectslist.get(i).mouseClick(event.getButton(), event.getPoint());
						}
					}
				}
			}
			//RenderAllPanel();
		}

		@Override
		public void mouseEntered(MouseEvent event) {
			// TODO Auto-generated method stub
			if(actionhandler != null){
				actionhandler.mouseEntered();
			}
			//RenderAllPanel();
		}

		@Override
		public void mouseExited(MouseEvent event) {
			// TODO Auto-generated method stub
			if(actionhandler != null){
				actionhandler.mouseLeave();
			}
			for(int i=0;i<objectslist.size();i++){
				if(objectslist.get(i).showing){
					if(objectslist.get(i).MouseInside){
						objectslist.get(i).MouseInside = false;
						objectslist.get(i).mouseLeave();
					}
				}
			}
			//RenderAllPanel();
		}

		@Override
		public void mousePressed(MouseEvent event) {
			// TODO Auto-generated method stub
			last_button_pressed = event.getButton();
			if(actionhandler != null){
				actionhandler.mousePressed(event.getButton(), event.getPoint());
				actionhandler.mousePressed(event);
			}
			for(int i=0;i<objectslist.size();i++){
				if(objectslist.get(i).showing){
					GJObject obj = objectslist.get(i);
					Point parent = obj.GetParentOffset();
					if(GJMath.checkPointInside(GJMath.Add(obj.point0,parent), event.getPoint(), obj.width, obj.height)){
						objectslist.get(i).mousePressed(event.getButton(), event.getPoint());
					}
				}
			}
			//RenderAllPanel();
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			// TODO Auto-generated method stub
			if(actionhandler != null){
				actionhandler.mouseReleased(event.getButton(), event.getPoint());
				actionhandler.mouseReleased(event);
			}
			for(int i=0;i<objectslist.size();i++){
				if(objectslist.get(i).showing){
					GJObject obj = objectslist.get(i);
					Point parent = obj.GetParentOffset();
					if(GJMath.checkPointInside(GJMath.Add(obj.point0,parent), event.getPoint(), obj.width, obj.height)){
						objectslist.get(i).mouseReleased(event.getButton(), event.getPoint());
					}
				}
			}
			//RenderAllPanel();
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			// TODO Auto-generated method stub
			if(actionhandler != null){
				actionhandler.mouseDragged(last_button_pressed, event.getPoint());	
				actionhandler.mouseDragged(event);
			}
			for(int i=0;i<objectslist.size();i++){
				if(objectslist.get(i).showing){
					GJObject obj = objectslist.get(i);
					Point parent = obj.GetParentOffset();
					if(GJMath.checkPointInside(GJMath.Add(obj.point0,parent), event.getPoint(), obj.width, obj.height)){
						objectslist.get(i).mouseDragged(last_button_pressed, event.getPoint());
					}
					if(GJMath.checkPointInside(GJMath.Add(obj.point0,parent), event.getPoint(), obj.width, obj.height)){
						if(!objectslist.get(i).MouseInside){
							objectslist.get(i).mouseEntered();
							objectslist.get(i).MouseInside = true;
						}else{
							objectslist.get(i).mouseMoved(event.getPoint());
						}
					}else{
						if(objectslist.get(i).MouseInside){
							objectslist.get(i).MouseInside = false;
							objectslist.get(i).mouseLeave();
						}
					}
				}
			}
			//RenderAllPanel();
		}

		@Override
		public void mouseMoved(MouseEvent event) {
			// TODO Auto-generated method st
			if(actionhandler != null){
				actionhandler.mouseMoved(event.getPoint());
				actionhandler.mouseMoved(event);
			}
			for(int i=0;i<objectslist.size();i++){
				if(objectslist.get(i).showing){
					GJObject obj = objectslist.get(i);
					Point parent = obj.GetParentOffset();
					if(GJMath.checkPointInside(GJMath.Add(obj.point0,parent), event.getPoint(), obj.width, obj.height)){
						if(!objectslist.get(i).MouseInside){
							objectslist.get(i).mouseEntered();
							objectslist.get(i).MouseInside = true;
						}else{
							objectslist.get(i).mouseMoved(event.getPoint());
						}
					}else{
						if(objectslist.get(i).MouseInside){
							objectslist.get(i).MouseInside = false;
							objectslist.get(i).mouseLeave();
						}
					}
				}
			}
			//RenderAllPanel();
		}
	}
	public void delete(GJObject gjObject) {
		// TODO Auto-generated method stub
		for(int i=0;i<objectslist.size();i++){
			if(objectslist.get(i) == gjObject){
				objectslist.remove(i);
				break;
			}
		}
		//RenderAllPanel();
	}
}
