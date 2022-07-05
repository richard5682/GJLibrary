import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import GJFile.GJFileLoader;
import GJImageProcessing.GJColorProcess;
import GJImageProcessing.GJImageUtil;
import GJNeural.Conv2DNet;
import GJNeural.ConvTrainer;
import GJNeural.TrainingData;
import GJNeural.Conv2DNet.ConvNetwork;
import GJSWING.GJFrame;
import GJSWING.GJMenuAction;
import GJSWING.GJMenuBar;
import GJSWING.GJPanel;
import GJSWING.GJPanelHandler;

public class main {
	static GJFrame main_frame = new GJFrame("FacePauser",500,500,0,0,GJFrame.EXIT_ON_CLOSE,true,false);
	static GJPanel main_panel = new GJPanel(500,5000,0,30,new main_handler());
	static JFrame frame1 = new JFrame();
	static GJMenuBar menu_bar = new GJMenuBar(new menu_action());
	static BufferedImage image;
	static ConvNetwork network = null;
	static float[] output;
	static boolean paused = true;
	public static void main(String args[]) {
		frame1.setSize(500,500);
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception ex) {
	        ex.printStackTrace();
	    }
		main_frame.addComponent(main_panel);
		JMenu file_menu = menu_bar.AddMenu("File");
		menu_bar.AddMenuItem(file_menu, 0, "Open NNET");
		main_frame.addMenuBar(menu_bar);
		main_frame.show();
		frame1.show();
		Thread t1 = new Thread(new main_loop());
		t1.start();
	}
	int cd = 500;
	static long lastclick;
	public static class main_loop implements Runnable{
		@Override
		public void run() {
			while(true) {
				image = GJImageUtil.CaptureScreen(frame1.getX(), frame1.getY(), frame1.getWidth(), frame1.getHeight());
				if(network != null) {
					TrainingData data = new TrainingData();
					BufferedImage resize_image = GJColorProcess.ResizeImage(image, network.input_size, network.input_size);
					data.processed_image = resize_image;
					if(network.image_no == 1) {
						data.bw_channel = true;
						data.DeactivateRGB();
						BufferedImage bw_image = GJColorProcess.ConvertBW(resize_image);
						data.processed_image = bw_image;
						System.out.println("BLACK");
					}
					data.output_type = 1;
					data.ConvertData2D();
					float[] output = network.GetOutput(data.data2D);
					main.output = output;
					for(int i=0;i<output.length;i++) {
						System.out.print(output[i]+",");
					}
					System.out.println("");
					long timenow = System.currentTimeMillis();
					if(timenow-lastclick > 500) {
						if(output[0]>0.5) {//LOOKING
							if(paused) {
								paused = false;
								click();
							}
							
						}else {//NOTLOOKING
							if(!paused) {
								paused = true;
								click();
							}
						}
					}
				}
				main_panel.repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}

	public static void click(){
	    Robot bot;
	    lastclick = System.currentTimeMillis();
		try {
			bot = new Robot();
			bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	  
	}
	public static class menu_action implements GJMenuAction{
		@Override
		public void actionPerformed(int index) {
			switch(index) {
				case 0:
					OpenNetwork();
				break;
			}
		}
	}
	public static void OpenNetwork() {
		String link = GJFileLoader.OpenLoadChooser(null, main_frame);
		if(link != null) {
			String netdata = GJFileLoader.LoadText(link)[0];
			network = ConvNetwork.LoadData(netdata, null);
		}
	}
	public static class main_handler extends GJPanelHandler{
		@Override
		public void draw(Graphics2D g2d) {
			g2d.drawImage(image, 0, 0, 500, 500, null);
			Font f = new Font("Arial",100,100);
			g2d.setFont(f);
			if(output != null) {
				if(output[0] > 0.5) {
					g2d.setColor(Color.GREEN);
					g2d.drawString("PLAY", 100, 400);
				}else {
					g2d.setColor(Color.RED);
					g2d.drawString("PAUSE", 100, 400);
				}
			}
		}
	}
}