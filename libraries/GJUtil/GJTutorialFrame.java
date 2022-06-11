package GJUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JFrame;

import GJObject.GJButton;
import GJObject.GJObject;
import GJObject.GJObject.GJGraphics2D;
import GJSWING.GJButtonAction;
import GJSWING.GJFrame;
import GJSWING.GJPanel;
import GJSWING.GJPanelHandler;

public class GJTutorialFrame extends GJFrame{
	static ArrayList<Clips> clips = new ArrayList<Clips>();
	Image image = null;
	GJPanel clip_panel = new GJPanel(900,600,0,0,new clip_panel_handler());
	GJButton left_button = new GJButton(new Point(0,480), 50, 50,Color.WHITE, "PREV", new left_button_action());
	GJButton right_button = new GJButton(new Point(850,480), 50, 50,Color.BLACK, "NEXT", new right_button_action());
	clip_object clipobject;
	Tutorial_Clip[] current_clip;
	int current_index = 0;
	public GJTutorialFrame(Image left,Image right){
		super("Guide", 900, 600, 0, 0, JFrame.HIDE_ON_CLOSE,false,false);
		this.setAlwaysOnTop(true);
		this.setBackgroundColor(Color.BLACK);
		clipobject = new clip_object(new Point(0,0),900,600);
		this.addComponent(clip_panel);
		clipobject.addComponent(left_button);
		clipobject.addComponent(right_button);
		clip_panel.addObject(clipobject);
		LoadIcon(left,right);
	}
	public void LoadIcon(Image left,Image right){
//		left_button.setIcon(imageresource.GetImage("prev_button_icon"));
//		right_button.setIcon(imageresource.GetImage("next_button_icon"));
		left_button.setIcon(left);
		right_button.setIcon(right);
	}
//	public void LoadTutorial(){
//		main_guide = new Tutorial_Clip[2];
//		main_guide[0] = new Tutorial_Clip("NodalNET Introduction","Welcome to NodalNET an application to help you develop and train your very own Neural Network",imageresource.GetImage("guide1_main"));
//		main_guide[1] = new Tutorial_Clip("TITLE 2","DESCRIPTION",imageresource.GetImage("datalabeler_image"));
//		ShowClip(main_guide);
//	}
	public void ShowClip(Tutorial_Clip[] clip){
		this.current_clip = clip;
		this.current_index = 0;
		this.show();;;
	}
	public static void AddClips(Clips clip) {
		clips.add(clip);
	}
	public void SelectClip(String name) {
		for(int i=0;i<clips.size();i++) {
			String c_name = clips.get(i).name;	
			if(name.equals(c_name)){
				ShowClip(clips.get(i).tutorial_clips);
				return;
			}
		}
	}
	public static class Clips{
		Tutorial_Clip[] tutorial_clips;
		String name;
		public Clips(Tutorial_Clip[] tutorial_clips,String name) {
			this.tutorial_clips = tutorial_clips;
			this.name = name;
		}
	}
	public class clip_object extends GJObject{
		public clip_object(Point point0, int width, int height) {
			super(point0, width, height);
			// TODO Auto-generated constructor stub
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
			if(current_clip != null) {
				current_clip[current_index].draw(gj2d);
			}
		}
		
	}
	public class clip_panel_handler extends GJPanelHandler{
		@Override
		public void draw(Graphics2D g2d) {
			// TODO Auto-generated method stub
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
		}
	}
	public class right_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			current_index++;
			if(current_index >= current_clip.length){
				current_index = current_clip.length-1;
			}
			clipobject.render();
		}
		
	}
	public class left_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			current_index--;
			if(current_index < 0){
				current_index = 0;
			}
			clipobject.render();
		}
		
	}
	public static class Tutorial_Clip{
		public String title;
		public String description;
		public Image backgroundimage;
		public Tutorial_Clip(String title,String description,Image backgroundimage){
			this.title = title;
			this.description = description;
			this.backgroundimage = backgroundimage;
		}
		public void draw(GJGraphics2D gj2d){
			gj2d.setColor(Color.black);
			gj2d.fillRect(0, 0, 900, 600);
			gj2d.setColor(Color.white);
			gj2d.setFontSize(25);
			gj2d.setFont("Monospaced");
			gj2d.drawCenteredString(title, new Rectangle(0,0,900,30));
			gj2d.drawImage(backgroundimage, 0, 30, 900, 450);
			gj2d.setFontSize(14);
			gj2d.drawCenteredString(description, new Rectangle(0,460,900,120));
		}
	}
}
