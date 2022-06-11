package GJNodeEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;

import GJFile.GJFileLoader;
import GJImageProcessing.GJImageUtil;
import GJNeural.TrainingData;
import GJNodeEditor.Trainer_Blocks.start_button_action;
import GJObject.GJButton;
import GJSWING.GJButtonAction;
import GJSWING.GJPanel;
import GJSWING.GJPanelHandler;
import GJUtil.GJMath;

public class ScreenCapture_Blocks extends Blocks{
	final int resize_size = 10;
	Node output_node;
	String last_link= "C:";
	TrainingData[] training_data = null;
	GJButton capture_button = new GJButton(new Point(10,40), 100, 30, new Color(50,50,50), "Capture", new capture_button_action(), 10);
	GJButton save_button = new GJButton(new Point(10,70), 100, 30, new Color(50,50,50), "Save", new save_button_action(), 10);
	GJButton play_button = new GJButton(new Point(10,100), 100, 30, new Color(50,50,50), "Play", new play_button_action(), 10);
	GJButton stop_button = new GJButton(new Point(10,130), 100, 30, new Color(50,50,50), "Stop", new stop_button_action(), 10);
	Thread capture_thread;
	public ScreenCapture_Blocks() {
		super(new Point(0,0), 120, 150, "Screen Capture",300,300, true,Blocks.SCREENCAP);
		this.body_trigger = false;
		frame.setBackground(new Color(0,0,0,0));
		frame.setBackgroundColor(new Color(0,0,0,0));
		frame.setUndecorated(true);
		frame.remove(panel);
		frame.BORDER_FRAME_SIZE = 0;
		frame.setAlwaysOnTop(true);
		panel = new GJPanel(300, 300,0,0, new PanelHandler());
		frame.addComponent(panel);
		panel.setBackground(new Color(0,0,0,0));
		panel.clearObject();
		this.addComponent(capture_button);
		this.addComponent(save_button);
		this.addComponent(play_button);
		this.addComponent(stop_button);
		frame.setResizable(true);
		this.AddOutputNode("TrainingData", new image_nodedata(), TrainingData[].class);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void addComponent(Component c) {
		// TODO Auto-generated method stub
		super.addComponent(c);
	}
	public class image_nodedata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) training_data;
		}
	}
	public class Capturing_Runnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				training_data = new TrainingData[1];
				training_data[0] = new TrainingData();
				training_data[0].output_type = TrainingData.IMAGE_DATA;
				int x = frame.getX()+panel.getX()+3;
				int y = frame.getY()+panel.getY()+3;
				int w = panel.getWidth()-resize_size-6;
				int h = panel.getHeight()-resize_size-6;
				training_data[0].data_image = GJImageUtil.CaptureScreen(x, y, w, h);
				training_data[0].processed_image = GJImageUtil.CaptureScreen(x, y, w, h);
				BlockConnectionUpdate();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
			
		}
		
	}
	public class PanelHandler extends GJPanelHandler {
		Point start_pressed;
		Point start_size_frame;
		Point start_size_panel;
		@Override
		public void mousePressed(int button, Point pos) {
			if(button == 1) {
				start_pressed = new Point(pos.x,pos.y);
				start_size_panel = new Point(panel.getWidth(),panel.getHeight());
				start_size_frame = new Point(frame.getWidth(),frame.getHeight());
			}
		}
		@Override
		public void mouseMoved(Point pos) {
			int resize_xpos = panel.getWidth()-resize_size-1;
			int resize_ypos = panel.getHeight()-resize_size-1;
			if(GJMath.checkPointInside(new Point(resize_xpos,resize_ypos), pos, resize_size, resize_size)) {
				panel.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
			}else {
				panel.setCursor(Cursor.getDefaultCursor());
			}
		}
		@Override
		public void mouseDragged(int button,Point pos) {
			if(button == 1) {
				int d_width = pos.x - start_pressed.x;
				int d_height = pos.y - start_pressed.y;
				Point new_size_panel = new Point(start_size_panel.x+d_width,start_size_panel.y+d_height);
				Point new_size_frame = new Point(start_size_frame.x+d_width,start_size_frame.y+d_height);
				if(new_size_frame.x > 300) {
					frame.setSize(new_size_frame.x, frame.getHeight());
				}
				if(new_size_frame.y > 300){
					frame.setSize(frame.getWidth(), new_size_frame.y);
				}
				if(new_size_panel.x > 50) {
					panel.setSize(new_size_panel.x, panel.getHeight());
				}
				if(new_size_panel.y > 50) {
					panel.setSize(panel.getWidth(), new_size_panel.y);
				}
			}
		}
		@Override
		public void draw(Graphics2D g2d) {
			g2d.setColor(Color.red);
			g2d.drawRect(0, 0, panel.getWidth()-1-resize_size, panel.getHeight()-1-resize_size);
			g2d.drawRect(1, 1, panel.getWidth()-3-resize_size, panel.getHeight()-3-resize_size);
			g2d.drawRect(2, 2, panel.getWidth()-5-resize_size, panel.getHeight()-5-resize_size);
			g2d.setColor(Color.green);
			g2d.fillRect(panel.getWidth()-resize_size-1, panel.getHeight()-resize_size-1, resize_size, resize_size);
		}
		
	}
	public class play_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			Capturing_Runnable cap_run = new Capturing_Runnable();
			capture_thread = new Thread(cap_run);
			capture_thread.start();
		}
	}
	public class stop_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			capture_thread.stop();
		}
	}
	public class capture_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			training_data = new TrainingData[1];
			training_data[0] = new TrainingData();
			training_data[0].output_type = TrainingData.IMAGE_DATA;
			int x = frame.getX()+panel.getX()+3;
			int y = frame.getY()+panel.getY()+3;
			int w = panel.getWidth()-resize_size-6;
			int h = panel.getHeight()-resize_size-6;
			training_data[0].data_image = GJImageUtil.CaptureScreen(x, y, w, h);
			training_data[0].processed_image = GJImageUtil.CaptureScreen(x, y, w, h);
			BlockConnectionUpdate();
		}
	}
	public class save_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			BufferedImage image = training_data[0].processed_image;
			String link = GJFileLoader.OpenDirChooser(last_link, frame,GJFileLoader.SAVING);
			last_link = link;
			int counter = 0;
			File file = new File(link+"\\"+counter+".jpg");
			while(file.exists()){
				counter++;
				file = new File(link+"\\"+counter+".jpg");
			}
			if(link != null){
				GJFileLoader.SaveImage(image, file.getAbsolutePath());
			}
		}
	}
	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void DoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String GetBlocksData(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void LoadBlocksData(String data) {
	
	}
}
