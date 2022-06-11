package GJObject;

import java.awt.Color;
import java.awt.Point;

import GJSWING.GJScrollAction;

public class GJScrollBar extends GJObject{
	BarObject bar_object;
	GJScrollAction scroll_action;
	public GJScrollBar(int width, int height,GJScrollAction scroll_action) {
		super(new Point(width-30,0), 30, height);
		this.scroll_action = scroll_action;
		bar_object = new BarObject(new Point(0,0),30,50,scroll_action);
		this.addComponent(bar_object);
		// TODO Auto-generated constructor stub
	}
	public float GetScrollValue(){
		return bar_object.y_value;
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
		gj2d.setColor(Color.DARK_GRAY);
		gj2d.fillRect(0, 0, 30, height);
	}

	
	public class BarObject extends GJObject{
		final Color COLOR_UNPRESSED = Color.RED;
		final Color COLOR_PRESSED = Color.GREEN;
		final Color COLOR_HOVER = Color.BLUE;
		Color color_bar = COLOR_UNPRESSED;
		int ypos=0;
		int starting_ypos=0,orig_ypos=0;
		float y_value=0;
		GJScrollAction scroll_action;
		public BarObject(Point point0, int width, int height,GJScrollAction scroll_action) {
			super(point0, width, height);
			this.scroll_action = scroll_action;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void mouseEntered() {
			// TODO Auto-generated method stub
			color_bar = COLOR_HOVER;
			this.render();
		}
		@Override
		public void mouseLeave() {
			// TODO Auto-generated method stub
			color_bar = COLOR_UNPRESSED;
			this.render();
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
			this.point0.y = pos.y-starting_ypos+orig_ypos;
			if(this.point0.y < 0){
				this.point0.y = 0;
			}else if(this.point0.y > this.parent.height-50){
				this.point0.y = this.parent.height-50;
			}
			y_value = (float)point0.y/this.parent.height;
			scroll_action.ScrollUpdate(y_value);
			this.render();
		}

		@Override
		public void mouseReleased(int button, Point pos) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(int button, Point pos) {
			// TODO Auto-generated method stub
			color_bar = COLOR_PRESSED;
			starting_ypos = pos.y;
			orig_ypos = point0.y;
			this.render();
		}

		@Override
		public void mouseDoubleClicked(Point pos) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void drawLocal(GJGraphics2D gj2d) {
			// TODO Auto-generated method stub
			gj2d.setColor(color_bar);
			gj2d.fillRect(0, 0, width, height);
		}
		
	}
}
