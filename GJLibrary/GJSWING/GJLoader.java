package GJSWING;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JFrame;

import GJObject.GJProgressBar;
import GJObject.GJText;

public class GJLoader{
	public static GJFrame null_frame;
	GJFrame loader_frame = new GJFrame(300,150,0,JFrame.HIDE_ON_CLOSE);
	GJPanel loader_panel = new GJPanel(294,144,0,0,new handler());
	GJProgressBar loader_bar = new GJProgressBar(new Point(10,10), 280, 40);
	GJText loader_text = new GJText(new Point(0,40),300, 100, "LOADING...");
	GJFrame parent_frame;
	Thread renderload_thread;
	Render_Runnable current_runnable;
	float val = 0;
	public GJLoader(GJFrame parent_frame){
		this.parent_frame = parent_frame;
		loader_frame.BORDER_SIZE = 3;
		loader_text.changeColor(Color.white);
		loader_frame.addComponent(loader_panel);
		loader_panel.addObject(loader_bar);
		loader_panel.addObject(loader_text);
		loader_panel.setBackground(Color.DARK_GRAY);
		loader_frame.setAlwaysOnTop(true);
		loader_frame.show();
		
	}
	public class handler extends GJPanelHandler{

		@Override
		public void draw(Graphics2D g2d) {
			// TODO Auto-generated method stub
			g2d.setColor(Color.darkGray);
			g2d.drawRect(0, 0, 299, 149);
		}
		
	}
	public void setValue(float val){
		this.val = val;
		current_runnable.render();
	}
	public void showLoader(String text){
		if(current_runnable != null){
			exitLoader();
		}
		loader_text.changeText(text);
		if(parent_frame != null){
			parent_frame.setEnabled(false);
		}
		if(null_frame != null) {
			null_frame.setEnabled(false);
		}
		loader_frame.show();
		val=0;
		current_runnable = new Render_Runnable();
		renderload_thread  = new Thread(current_runnable,"render_thread");
		renderload_thread.start();
	}
	public void exitLoader(){
		if(parent_frame != null){
			parent_frame.setEnabled(true);
		}
		if(null_frame != null) {
			null_frame.setEnabled(true);
		}
		loader_frame.hide();
		current_runnable.exit();
		current_runnable = null;
	}
	public class Render_Runnable implements Runnable{
		private volatile boolean exit = false;
		private volatile boolean render = false;
		public void run(){
			while(!exit){
				if(render){
					render = false;
					loader_bar.ChangeProgress(val);
					loader_panel.repaint();
				}
			}
		}
		public void render(){
			render = true;
		}
		public void exit(){
			exit = true;
		}
	}
}
