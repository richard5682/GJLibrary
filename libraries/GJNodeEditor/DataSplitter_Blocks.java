package GJNodeEditor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import GJNeural.TrainingData;
import GJObject.GJTextBox;

public class DataSplitter_Blocks extends Blocks{
	Node inputdata_node;
	Node output1_node;
	Node output2_node;
	TrainingData[] training_data1;
	TrainingData[] training_data2;
	
	GJTextBox textbox1 = new GJTextBox(10,10,100,40,"Data1 Percentage","0.5");
	GJTextBox textbox2 = new GJTextBox(10,50,100,40,"Seed",Integer.class,"123456");
	public DataSplitter_Blocks() {
		super(new Point(0,0), 100, 50, "Data Splitter",300,200,true,Blocks.DATASPLITTER);
		inputdata_node = this.AddInputNode("Data", TrainingData[].class);
		output1_node = this.AddOutputNode("Data 1", new output1_nodedata(), TrainingData[].class);
		output2_node = this.AddOutputNode("Data 2", new output2_nodedata(), TrainingData[].class);
		this.AddSettingsComponent(textbox1);
		this.AddSettingsComponent(textbox2);
	}
	public class output1_nodedata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) training_data1;
		}
	}
	public class output2_nodedata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) training_data2;
		}
	}
	@Override
	public String GetBlocksData(String path) {
		return textbox1.getText()+","+textbox2.getInt();
	}

	@Override
	public void LoadBlocksData(String data) {
		try {
			if(data != null && data.length() > 0 && !data.equals("null")) {
				String[] params = data.split(",");
				textbox1.setText(params[0]);
				textbox2.setText(params[1]);
			}
		}catch(Exception ex) {
			
		}
	}
	@Override
	public void BlockUpdate() {
		if(inputdata_node.isConnected()){
			TrainingData[] input_data = (TrainingData[]) inputdata_node.connection_node.nodedata.getValue();
			if(input_data != null){
				int data_size = input_data.length;
				float percentage = (float) textbox1.getDouble();
				if(textbox1.getDouble() > 1){
					percentage = 1;
				}else if(textbox1.getDouble() < 0){
					percentage = 0;
				}
				int data1_size = (int)Math.round(data_size*percentage);
				int data2_size = data_size-data1_size;
				ArrayList<TrainingData> data2 = new ArrayList<TrainingData>();
				ArrayList<TrainingData> data1 = new ArrayList<TrainingData>();
				for(int i=0;i<input_data.length;i++){
					data1.add(input_data[i]);
				}
				Random rand = new Random(textbox2.getInt());
				while(data1.size() > data1_size){
					int random_index = rand.nextInt(data1.size());
					data2.add(data1.get(random_index));
					data1.remove(random_index);
				}
				training_data1 = new TrainingData[data1_size];
				training_data2 = new TrainingData[data2_size];
				data1.toArray(training_data1);
				data2.toArray(training_data2);
			}else{
				training_data1 = null;
				training_data2 = null;
			}
		}else{
			training_data1 = null;
			training_data2 = null;
		}
	}
	@Override
	public void DoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		
	}

}
