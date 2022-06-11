package GJNodeEditor;

import java.awt.Point;
import java.util.ArrayList;

import GJNeural.TrainingData;

public class ChannelAdder_Blocks extends Blocks{
	Node inputdata1_node,inputdata2_node;
	Node output_node;
	TrainingData[] training_data;
	public ChannelAdder_Blocks() {
		super(new Point(0,0), 120, 50, "Channel Adder", true,Blocks.CHANNELADDER);
		inputdata1_node = this.AddInputNode("Data1", TrainingData[].class);
		inputdata2_node = this.AddInputNode("Data2", TrainingData[].class);
		output_node = this.AddOutputNode("Data", new output_nodedata(), TrainingData[].class);
		// TODO Auto-generated 1 stub
	}
	public class output_nodedata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) training_data;
		}
	}
	@Override
	public void BlockUpdate() {
		if(inputdata1_node.isConnected() && inputdata2_node.isConnected()){
			TrainingData[] main_trainingdata = TrainingData.CloneTrainingData((TrainingData[])inputdata1_node.connection_node.nodedata.getValue());
			TrainingData[] extra_trainingdata = TrainingData.CloneTrainingData((TrainingData[])inputdata2_node.connection_node.nodedata.getValue());
			if(main_trainingdata != null && extra_trainingdata != null && main_trainingdata.length == extra_trainingdata.length){
				for(int i=0;i<main_trainingdata.length;i++){
					main_trainingdata[i].AddExtraImageData(extra_trainingdata[i]);
				}
			}
			this.training_data = main_trainingdata;
		}
		
//		ArrayList<TrainingData> datalist = new ArrayList<TrainingData>();
//		// TODO Auto-generated method stub
//		if(inputdata1_node.isConnected()){
//			TrainingData[] trainingdata = (TrainingData[])inputdata1_node.connection_node.nodedata.getValue();
//			if(trainingdata != null){
//				for(TrainingData data:trainingdata){
//					datalist.add(data);
//				}
//			}
//		}
//		if(inputdata2_node.isConnected()){
//			TrainingData[] trainingdata = (TrainingData[])inputdata2_node.connection_node.nodedata.getValue();
//			if(trainingdata != null){
//				for(TrainingData data:trainingdata){
//					datalist.add(data);
//				}
//			}
//		}
//		training_data = new TrainingData[datalist.size()];
//		datalist.toArray(training_data);
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
