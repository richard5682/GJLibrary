package GJNodeEditor;

import java.awt.Point;

import GJImageProcessing.GJColorProcess;
import GJNeural.TrainingData;
import GJNodeEditor.Filter_Blocks.output_nodedata;
import GJObject.GJTextBox;

public class CropResize_Blocks extends Blocks {
	TrainingData[] training_data;
	Node inputdata_node;
	Node output_node;
	GJTextBox width_textbox = new GJTextBox(10,10,100,50,"Width","100");
	GJTextBox height_textbox = new GJTextBox(10,65,100,50,"Height","100");
	GJTextBox trigger_textbox = new GJTextBox(10,115,100,50,"Trigger","200");
	public CropResize_Blocks() {
		super(new Point(0,0), 100, 50, "Crop/Resize", 300, 250, true,Blocks.CROPRESIZE);
		inputdata_node = this.AddInputNode("Image Data", TrainingData[].class);
		output_node = this.AddOutputNode("Output", new output_nodedata(), TrainingData[].class);
		this.AddSettingsComponent(height_textbox);
		this.AddSettingsComponent(width_textbox);
		this.AddSettingsComponent(trigger_textbox);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		if(inputdata_node.isConnected()){
			TrainingData[] trainingdata = (TrainingData[])inputdata_node.connection_node.nodedata.getValue();
			if(trainingdata != null){
				training_data = TrainingData.CloneTrainingData(trainingdata);
				for(TrainingData data:training_data){
					data.processed_image = GJColorProcess.CropImage(data.processed_image, true, trigger_textbox.getInt());
					data.processed_image = GJColorProcess.ResizeImage(data.processed_image, width_textbox.getInt(), height_textbox.getInt());
				}
			}
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
	@Override
	public String GetBlocksData(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void LoadBlocksData(String data) {
	
	}
}
