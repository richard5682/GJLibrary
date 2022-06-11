package GJNodeEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import GJFile.GJFileLoader;
import GJNeural.TrainingData;
import GJNodeEditor.ConvTrainer_Blocks.start_button_action;
import GJNodeEditor.Data_Blocks.datapanel_handler;
import GJObject.GJButton;
import GJObject.GJImage;
import GJObject.GJText;
import GJSWING.GJButtonAction;
import GJSWING.GJLoader;
import GJSWING.GJPanelHandler;

public class DataViewer_Blocks extends Blocks{
	Node data_input,index_input,value_input,box_input;
	GJText data_text;
	Image_Preview image;
	ArrayList<TrainingData> training_data = new ArrayList<TrainingData>();
	GJButton save_button = new GJButton(new Point(30,410), 100, 30, new Color(50,50,50), "Save Images", new save_button_action(), 10);
	double[] box_data;
	public DataViewer_Blocks() {
		super(new Point(0,0), 200, 50, "Data Viewer",800,500,true,Blocks.DATAVIEWER);
		panel.addPanelHandler(new datapanel_handler());
		data_input = this.AddInputNode("Data", TrainingData[].class);
		index_input = this.AddInputNode("Index", Double.class);
		value_input = this.AddInputNode("Value", Double.class);
		box_input = this.AddInputNode("Box", Double[].class);
		data_text = new GJText(new Point(0,0), width, height,"NO DATA" , Color.white, 10);
		image = new Image_Preview(new Point(17,20),width-70,width-70,null);
		this.addComponent(data_text);
		this.AddSettingsComponent(save_button);
		this.addComponent(image);
		// TODO Auto-generated constructor stub
	}
	public class save_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			if(data_input.isConnected()) {
				TrainingData[] datas = (TrainingData[])data_input.connection_node.nodedata.getValue();
				if(datas != null) {
					String link = GJFileLoader.OpenDirChooser(null, null, false);
					if(link != null) {
						SaveImage_Runnable runnable = new SaveImage_Runnable(datas,link);
						Thread t1 = new Thread(runnable);
						t1.start();
					}
				}
			}
		}
		
	}
	public class SaveImage_Runnable implements Runnable{
		TrainingData[] datas;
		String link;
		public SaveImage_Runnable(TrainingData[] datas,String link) {
			this.datas = datas;
			this.link = link;
		}
		@Override
		public void run() {
			GJLoader loader = new GJLoader(frame);
			loader.showLoader("Saving Images");
			int i=0;
			for(TrainingData data:datas) {
				loader.setValue((float)i/datas.length);
				String folder_link = link+"//"+data.folder_link;
				File file = new File(folder_link);
				if(!file.exists()) {
					try {
						GJFileLoader.CreateDIR(file.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(file.exists()) {
					BufferedImage image = data.processed_image;
					String image_link = folder_link+"//"+i+".jpg";
					GJFileLoader.SaveImage(image, image_link);
					i++;
				}
			}
			loader.exitLoader();
		}
		
	}
	public class datapanel_handler extends GJPanelHandler{
		int size = 78;
		@Override
		public void draw(Graphics2D g2d) {
			int length = training_data.size();
			if(length > 50) {
				length = 50;
			}
			int x=0,y=0;
			for(int i=0;i<length;i++) {
				g2d.drawImage(training_data.get(i).processed_image,(size+2)*x,(size+2)*y,size,size,null);
				x++;
				if(x>=10) {
					x=0;
					y++;
				}
			}
		}
		
	}
	public class Image_Preview extends GJImage{

		public Image_Preview(Point point0, int width, int height, Image image) {
			super(point0, width, height, image);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void drawImage(GJGraphics2D gj2d) {
			// TODO Auto-generated method stub
			if(box_data != null){
				int x = (int)(box_data[0]*(image.width));
				int y = (int)(box_data[1]*(image.width));
				int w = (int)(box_data[2]*(image.width));
				int h = (int)(box_data[3]*(image.width));
				gj2d.setColor(Color.red);
				gj2d.drawRect(x+17, y+20, w, h);
			}
		}
		
	}
	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub\
		
		if(value_input.isConnected()){
			double value = (double)value_input.connection_node.nodedata.getValue();
			String output_text = String.valueOf(value);
			this.data_text.centered = false;
			this.width = 200;
			this.label.width = this.width;
			this.data_text.width = this.width;
			this.height = 65;
			this.data_text.point0.y = 40;
			this.data_text.point0.x = 100;
			this.data_text.changeText(output_text);
			image.ChangeImage(null);
			this.render();
		}else if(data_input.isConnected()){
			TrainingData[] data = (TrainingData[])data_input.connection_node.nodedata.getValue();
			if(data != null){
				int index = 0;
				if(index_input.isConnected()){
					Double d = (Double)index_input.connection_node.nodedata.getValue();
					index = d.intValue();
					if(index > data.length-1){
						index = data.length-1;
					}
					if(index < 0) {
						index = 0;
					}
				}
				if(data.length == 0) {
					return;
				}
				TrainingData data_display = data[index];
				if(data_display != null){
					if(data_display.output_type == TrainingData.RAW_DATA){
						String[] data_packet = data[index].toString().split(">");
						String data_text = data_packet[0];
						this.width = (int)(data_text.length()*6);
						this.label.width = this.width;
						this.data_text.width = this.width;
						String output_text = data_packet[1];
						this.data_text.centered = false;
						this.data_text.point0.y = 40;
						this.data_text.point0.x = 30;
						this.data_text.changeText(data_text+"\n"+output_text);
					}else if(data_display.output_type == TrainingData.IMAGE_DATA){
						BufferedImage image_preview = data_display.processed_image;
						this.height = width;
						this.data_text.changeText("");
						image.ChangeImage(image_preview);
					}
					this.render();
				}
			}else{
				resetViewer();
			}
		}else{
			resetViewer();
		}
		if(box_input.isConnected()){
			double[] data = (double[])box_input.connection_node.nodedata.getValue();
			if(data != null && data.length == 4){
				this.box_data = data;
				this.render();
			}
		}
	}
	public void resetViewer(){
		this.data_text.centered = true;
		data_text.changeText("NO DATA");
		this.width = 200;
		this.height = 65;
		this.data_text.point0.y = 0;
		this.data_text.point0.x = 0;
		this.label.width = this.width;
		this.data_text.width = this.width;
		image.ChangeImage(null);
		this.render();
	}
	@Override
	public void DoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		training_data.clear();
		if(data_input.isConnected()){
			TrainingData[] datas = (TrainingData[])data_input.connection_node.nodedata.getValue();
			if(datas != null) {
				for(TrainingData data:datas) {
					training_data.add(data);
					panel.repaint();
				}
			}
		}
		
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
