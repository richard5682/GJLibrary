package GJNodeEditor;

import java.awt.Point;

import GJImageProcessing.GJColorProcess;
import GJNeural.TrainingData;
import GJNodeEditor.Convolution_Blocks.output_nodedata;

public class ChannelSeparator_Blocks extends Blocks{
	Node inputdata_node;
	Node red_output_node,green_output_node,blue_output_node;
	TrainingData[] red_data,green_data,blue_data;
	public ChannelSeparator_Blocks() {
		super(new Point(0,0), 100, 100, "Separator", true,Blocks.SEPERATOR);
		inputdata_node = this.AddInputNode("Image Data", TrainingData[].class);
		red_output_node = this.AddOutputNode("Red", new red_output_nodedata(), TrainingData[].class);
		green_output_node = this.AddOutputNode("Green", new green_output_nodedata(), TrainingData[].class);
		blue_output_node = this.AddOutputNode("Blue", new blue_output_nodedata(), TrainingData[].class);
		// TODO Auto-generated constructor stub
	}
	public class red_output_nodedata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) red_data;
		}
	}
	public class green_output_nodedata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) green_data;
		}
	}public class blue_output_nodedata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) blue_data;
		}
	}
	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub
		if(inputdata_node.isConnected()){
			TrainingData[] trainingdata = (TrainingData[])inputdata_node.connection_node.nodedata.getValue();
			if(trainingdata != null){
				red_data = TrainingData.CloneTrainingData(trainingdata);
				green_data = TrainingData.CloneTrainingData(trainingdata);
				blue_data = TrainingData.CloneTrainingData(trainingdata);
				for(int i=0;i<trainingdata.length;i++){
					red_data[i].red_channel = true;
					red_data[i].green_channel = false;
					red_data[i].blue_channel = false;
					green_data[i].red_channel = false;
					green_data[i].green_channel = true;
					green_data[i].blue_channel = false;
					blue_data[i].red_channel = false;
					blue_data[i].green_channel = false;
					blue_data[i].blue_channel = true;
					GJColorProcess.ColorFilterImage(red_data[i].processed_image,true,false,false);
					GJColorProcess.ColorFilterImage(green_data[i].processed_image,false,true,false);
					GJColorProcess.ColorFilterImage(blue_data[i].processed_image,false,false,true);
				}
				
			}else{
				ClearData();
			}
		}else{
			ClearData();
		}
	}
	public void ClearData(){
		red_data = null;
		green_data = null;
		blue_data = null;
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
