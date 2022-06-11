package GJNodeEditor;

import java.awt.Point;
import java.util.ArrayList;

import GJNeural.TrainingData;

public class DataAdder_Blocks extends Blocks{
	Node inputdata1_node,inputdata2_node;
	Node output_node;
	TrainingData[] training_data;
	public DataAdder_Blocks() {
		super(new Point(0,0), 120, 50, "Data Adder", true,Blocks.DATADDER);
		inputdata1_node = this.AddInputNode("Main Data", TrainingData[].class);
		inputdata2_node = this.AddInputNode("Extra Data", TrainingData[].class);
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
		ArrayList<TrainingData> datalist = new ArrayList<TrainingData>();
		if(inputdata1_node.isConnected()){
			TrainingData[] trainingdata = (TrainingData[])inputdata1_node.connection_node.nodedata.getValue();
			if(trainingdata != null){
				for(TrainingData data:trainingdata){
					datalist.add(data);
				}
			}
		}
		if(inputdata2_node.isConnected()){
			TrainingData[] trainingdata = (TrainingData[])inputdata2_node.connection_node.nodedata.getValue();
			if(trainingdata != null){
				for(TrainingData data:trainingdata){
					datalist.add(data);
				}
			}
		}
		training_data = new TrainingData[datalist.size()];
		datalist.toArray(training_data);
//		if(inputdata1_node.isConnected() && inputdata2_node.isConnected()){
//			TrainingData[] maindata = TrainingData.CloneTrainingData((TrainingData[])inputdata1_node.connection_node.nodedata.getValue());
//			TrainingData[] extradata = TrainingData.CloneTrainingData((TrainingData[])inputdata2_node.connection_node.nodedata.getValue());
//			if(maindata != null && extradata != null){
//				for(int i=0;i<maindata.length;i++){
//					extradata[i].ConvertData();
//					maindata[i].AddExtraData(extradata[i].data);
//				}
//				training_data =TrainingData.CloneTrainingData(maindata);
//			}else{
//				training_data = null;
//			}
//		}else{
//			training_data = null;
//		}
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
