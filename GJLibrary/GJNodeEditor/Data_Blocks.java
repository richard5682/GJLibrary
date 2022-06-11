package GJNodeEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import GJFile.GJFileLoader;
import GJNeural.ConvTrainer;
import GJNeural.Trainer;
import GJNeural.TrainingData;
import GJObject.GJText;
import GJSWING.GJPanelHandler;

public class Data_Blocks extends Blocks{
	public ArrayList<TrainingData> training_data = new ArrayList<TrainingData>();
	GJText filename_label;
	String link;
	String filename = "NO FILE";
	public Data_Blocks(String link) {
		super(new Point(0,0), 100, 70, "Data",800,500,true,Blocks.DATABLOCK);
		panel.addPanelHandler(new datapanel_handler());
		this.link = link;
		this.LoadData(link);
		File parent_file = new File(new File(link).getParent());
		if(parent_file.exists()) {
			filename = parent_file.getName();
		}else {
			filename = "MISSING FILE";
		}
		filename_label = new GJText(new Point(0,20), 100, 70, filename, Color.white, 11);
		this.AddOutputNode("Data",new data_output(),TrainingData[].class);
		this.addComponent(filename_label);
	}
	public class data_output extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			TrainingData[] returndata = new TrainingData[training_data.size()];
			training_data.toArray(returndata);
			return (T) returndata;
		}
		
	}
	public void LoadData(String link){
		File f = new File(link);
		if(!f.exists()) {
			return;
		}
		TrainingData[] data_toadd = ConvTrainer.LoadData(link);
		for(int i=0;i<data_toadd.length;i++){
			training_data.add(data_toadd[i]);
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
	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		
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
	public String GetBlocksData(String path) {
		return link;
	}

	@Override
	public void LoadBlocksData(String data) {
	
	}
}
