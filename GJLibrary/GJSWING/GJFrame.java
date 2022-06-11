package GJSWING;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import GJObject.GJButton;
import GJObject.GJText;
import GJUtil.GJMath;

public class GJFrame extends JFrame {
	public static final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static final GraphicsDevice gd = env.getDefaultScreenDevice();
	public static final DisplayMode dm = gd.getDisplayMode();
	public static final int SCREEN_WIDTH = dm.getWidth();
	public static final int SCREEN_HEIGHT = dm.getHeight();
	public int TOPBAR_HEIGHT = 30;
	public int BORDER_SIZE = 3;
	public int BORDER_FRAME_SIZE = 2;
	public static final int BUTTON_SIZE = 70;
	public int MENUBAR_SIZE = 0;
	public static final Color MINMIZE_NORMAL_COLOR = Color.DARK_GRAY;
	public static final Color MINMIZE_HOVER_COLOR = new Color(91, 201, 255);
	public static final Color MINMIZE_PRESS_COLOR = new Color(169, 227, 255);
	public static final Color EXIT_NORMAL_COLOR = Color.DARK_GRAY;
	public static final Color EXIT_HOVER_COLOR = Color.RED;
	public static final Color EXIT_PRESS_COLOR = new Color(255,171,171);
	public Color background_color = Color.gray;
	
	public ArrayList<GJPanel> panellist = new ArrayList<GJPanel>();
	public GJPanel top_panel;
	public GJButton close_button;
	public GJButton minimize_button;
	public GJButton always_ontop_button;
	public GJText title_text;
	public JPanel body_pane = new DrawPane();
	public GJFrameChangeListener framechange;
	public boolean isWindowActive = false;

	public static Image icon = null;
	
	public GJFrame(int width,int height,int offsetx,int closeoperation){
		this.setContentPane(body_pane);
		this.setAlwaysOnTop(true);
		this.TOPBAR_HEIGHT = 0;
		BORDER_SIZE = 0;
		FrameSetIcon();
		this.setBounds((SCREEN_WIDTH-width)/2, (SCREEN_HEIGHT-height)/2, width+2*BORDER_SIZE, height+TOPBAR_HEIGHT+2*BORDER_SIZE);;
		this.setDefaultCloseOperation(closeoperation);
		this.setResizable(false);
		this.setLayout(null);
		this.setUndecorated(true);
	}
	public GJFrame(String name,int width,int height,int offsetx,int offsety,int closeoperation,boolean iconable,boolean option_ontop){
		this.setContentPane(body_pane);
		title_text = new GJText(new Point(0,0),width+2*BORDER_SIZE,TOPBAR_HEIGHT,name);
		title_text.textcolor = Color.white;
		top_panel = new GJPanel(width+2*BORDER_SIZE,TOPBAR_HEIGHT,0,0,new top_panel_handler());
		close_button = new GJButton(new Point(width-BUTTON_SIZE+BORDER_SIZE*2,0),BUTTON_SIZE,TOPBAR_HEIGHT,EXIT_NORMAL_COLOR,"X",new close_button_action());
		minimize_button = new GJButton(new Point(width-BUTTON_SIZE*2+BORDER_SIZE*2,0),BUTTON_SIZE,TOPBAR_HEIGHT,MINMIZE_NORMAL_COLOR,"_",new minimize_button_action());
		always_ontop_button = new GJButton(new Point(2,2),TOPBAR_HEIGHT-4,TOPBAR_HEIGHT-4,Color.DARK_GRAY,"T",new alwaysontop_button_action());
		close_button.setHoverColor(EXIT_HOVER_COLOR);
		close_button.setPressColor(EXIT_PRESS_COLOR);
		minimize_button.setHoverColor(MINMIZE_HOVER_COLOR);
		minimize_button.setPressColor(MINMIZE_PRESS_COLOR);
		FrameSetIcon();
		this.addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        //set flag
		        isWindowActive = true;
		        top_panel.repaint();
		    }
		    public void windowLostFocus(WindowEvent e) {
		        isWindowActive = false;
		        top_panel.repaint();
		    }
		});
		this.setBounds((SCREEN_WIDTH-width)/2+offsetx, (SCREEN_HEIGHT-height)/2+offsety, width+2*BORDER_SIZE, height+TOPBAR_HEIGHT+2*BORDER_SIZE);;
		this.setDefaultCloseOperation(closeoperation);
		this.setTitle(name);
		this.setResizable(false);
		this.setLayout(null);
		this.setUndecorated(true);
		this.add(top_panel);
		if(iconable){
			top_panel.addObject(minimize_button);
		}
		if(option_ontop){
			top_panel.addObject(always_ontop_button);
		}
		top_panel.addObject(close_button);
		top_panel.addObject(title_text);
	}
	public GJFrame(String name,int width,int height,int offsetx,int closeoperation,boolean iconable,boolean option_ontop){
		this.setContentPane(body_pane);
		title_text = new GJText(new Point(0,0),width+2*BORDER_SIZE,TOPBAR_HEIGHT,name);
		title_text.textcolor = Color.white;
		top_panel = new GJPanel(width+2*BORDER_SIZE,TOPBAR_HEIGHT,0,0,new top_panel_handler());
		close_button = new GJButton(new Point(width-BUTTON_SIZE+BORDER_SIZE*2,0),BUTTON_SIZE,TOPBAR_HEIGHT,EXIT_NORMAL_COLOR,"X",new close_button_action());
		minimize_button = new GJButton(new Point(width-BUTTON_SIZE*2+BORDER_SIZE*2,0),BUTTON_SIZE,TOPBAR_HEIGHT,MINMIZE_NORMAL_COLOR,"_",new minimize_button_action());
		always_ontop_button = new GJButton(new Point(2,2),TOPBAR_HEIGHT-4,TOPBAR_HEIGHT-4,Color.DARK_GRAY,"T",new alwaysontop_button_action());
		close_button.setHoverColor(EXIT_HOVER_COLOR);
		close_button.setPressColor(EXIT_PRESS_COLOR);
		minimize_button.setHoverColor(MINMIZE_HOVER_COLOR);
		minimize_button.setPressColor(MINMIZE_PRESS_COLOR);
		FrameSetIcon();
		this.addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        //set flag
		        isWindowActive = true;
		        top_panel.repaint();
		    }
		    public void windowLostFocus(WindowEvent e) {
		        isWindowActive = false;
		        top_panel.repaint();
		    }
		});
		this.setBounds((SCREEN_WIDTH-width)/2, (SCREEN_HEIGHT-height)/2, width+2*BORDER_SIZE, height+TOPBAR_HEIGHT+2*BORDER_SIZE);;
		this.setDefaultCloseOperation(closeoperation);
		this.setTitle(name);
		this.setResizable(false);
		this.setLayout(null);
		this.setUndecorated(true);
		this.add(top_panel);
		if(iconable){
			top_panel.addObject(minimize_button);
		}
		if(option_ontop){
			top_panel.addObject(always_ontop_button);
		}
		top_panel.addObject(close_button);
		top_panel.addObject(title_text);
	}
	public void FrameSetIcon() {
		if(icon != null) {
			this.setIconImage(icon);
		}
	}
	public static void SetIcon(Image image) {
		GJFrame.icon = image;
	}
	public void addFrameChangeListener(GJFrameChangeListener framechange){
		this.framechange = framechange;
	}
	public void addMenuBar(JMenuBar menubar){
		MENUBAR_SIZE = 30;
		menubar.setBounds(0, TOPBAR_HEIGHT, this.getWidth(), MENUBAR_SIZE);
		body_pane.add(menubar,BorderLayout.NORTH);
	}
	public void setBackgroundColor(Color c){
		this.background_color = c;
	}
	public class DrawPane extends JPanel{
		public void paintComponent(Graphics g){
			g.setColor(background_color);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.lightGray);
			for(int i=0;i<BORDER_FRAME_SIZE;i++){
				g.drawRect(i, 10, getWidth()-(2*i+1), getHeight()-(2*i+1));
			}
		}
	}
	public class alwaysontop_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			if(button == 1){
				if(isAlwaysOnTop()){
					setAlwaysOnTop(false);
					always_ontop_button.setColor(Color.DARK_GRAY);
				}else{
					setAlwaysOnTop(true);
					always_ontop_button.setColor(new Color(25,200,25));
				}
			}
		}
	}
	public class minimize_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			if(button == 1){
				if(framechange != null){
					framechange.onHide();
				}
				setState(JFrame.ICONIFIED);
			}
		}
	}
	public class close_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			if(button == 1){
				if(getDefaultCloseOperation() == JFrame.HIDE_ON_CLOSE){
					if(framechange != null){
						framechange.onClose();
					}
					
					 show(false);
				}else if(getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE){
					if(framechange != null){
						framechange.onClose();
					}
					System.exit(0);
				}
			}
		}
	}
	public class top_panel_handler extends GJPanelHandler{
		Point start_press;
		@Override
		public void draw(Graphics2D g2d) {
			// TODO Auto-generated method stub
			if(isWindowActive){
				g2d.setColor(new Color(50,50,50));
			}else{
				g2d.setColor(new Color(125,125,125));
			}
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			if(isAlwaysOnTop()){
				always_ontop_button.setColor(new Color(25,200,25));
			}else{
				always_ontop_button.setColor(Color.DARK_GRAY);
			}
		}
		@Override
		public void mouseDragged(int button, Point pos) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseDragged(MouseEvent e){
			if(start_press != null){
				if(e.getButton() == 0){
					Point end_press =  new Point(e.getXOnScreen(),e.getYOnScreen());
					setLocation(GJMath.Subtract(end_press, start_press));
				}
			}
		}
		@Override
		public void mousePressed(MouseEvent e){

			if(e.getButton() == 1){
				start_press = new Point(e.getX(),e.getY());
			}
		}
		@Override
		public void mouseReleased(int button, Point pos) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(int button, Point pos) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public void addComponent(Component component){
		Point point =  component.getLocation();
		component.setLocation(point.x+BORDER_SIZE, point.y+TOPBAR_HEIGHT+BORDER_SIZE+MENUBAR_SIZE);
		this.add(component);
	}
	public void addComponent(GJPanel panel){
		this.panellist.add(panel);
		Point point =  panel.getLocation();
		panel.setLocation(point.x+BORDER_SIZE, point.y+TOPBAR_HEIGHT+BORDER_SIZE+MENUBAR_SIZE);
		this.add(panel);
	}
//	public Component add(Component component){
//		ThrowError("DEPRECIATED CHANGE TO ADDCOMPONENT");
//		return null;
//	}
	public void ThrowError(String errormsg){
		String classerror = Thread.currentThread().getStackTrace()[1].getClassName();
		int lineerror = Thread.currentThread().getStackTrace()[3].getLineNumber();
		System.err.print(" Error Message : "+errormsg+"  class: ");
		System.err.println(classerror+" at line:"+lineerror);
	}
	public void render(){
		top_panel.repaint();
		for(int i=0;i<panellist.size();i++){
			this.panellist.get(i).repaint();
		}
	}
}
