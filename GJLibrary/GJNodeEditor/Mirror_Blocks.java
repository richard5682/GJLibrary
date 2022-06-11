package GJNodeEditor;

import java.awt.Point;

import GJImageProcessing.GJColorProcess;
import GJNeural.TrainingData;

public class Mirror_Blocks extends Blocks{
	Node inputdata_node;
	Node output_node;
	TrainingData[] outputdata;
	public Mirror_Blocks() {
		super(new Point(0,0),120,50,"Mirror",true,Blocks.MIRROR);
		inputdata_node = this.AddInputNode("Data", TrainingData[].class);
		output_node = this.AddOutputNode("Output", new output_nodeadata(), TrainingData[].class);
		// TODO Auto-generated constructor stub
	}
	public class output_nodeadata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) outputdata;
		}
	}
	@Override
	public String GetBlocksData(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void LoadBlocksData(String data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub
		if(!inputdata_node.isConnected()){
			return;
		}
		if(inputdata_node.connection_node.nodedata.getValue() == null){
			return;
		}
		TrainingData[] input_data = (TrainingData[]) inputdata_node.connection_node.nodedata.getValue();
		outputdata = TrainingData.CloneTrainingData(input_data);
		for(int i=0;i<outputdata.length;i++){
			outputdata[i].processed_image = GJColorProcess.createFlipped(outputdata[i].processed_image, 1, 1);
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
