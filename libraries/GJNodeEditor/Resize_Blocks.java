package GJNodeEditor;

import java.awt.Point;

import GJImageProcessing.GJColorProcess;
import GJNeural.TrainingData;
import GJNodeEditor.BrightnessContrast_Blocks.output_nodedata;
import GJObject.GJTextBox;

public class Resize_Blocks extends Blocks{
	TrainingData[] training_data;
	Node inputdata_node;
	Node output_node;
	GJTextBox width_textbox = new GJTextBox(10,10,100,50,"Width","100");
	GJTextBox height_textbox = new GJTextBox(10,65,100,50,"Height","100");
	public Resize_Blocks() {
		super(new Point(0,0), 100, 60, "Resize", 300, 300, true,Blocks.RESIZE);
		inputdata_node = this.AddInputNode("Image Data", TrainingData[].class);
		output_node = this.AddOutputNode("Output", new output_nodedata(), TrainingData[].class);
		this.AddSettingsComponent(width_textbox);
		this.AddSettingsComponent(height_textbox);
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
					data.processed_image = GJColorProcess.ResizeImage(data.processed_image, width_textbox.getInt(), height_textbox.getInt());
				}
			}else{
				training_data = null;
			}
		}else{
			training_data = null;
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
		String returnval = "";
		returnval += width_textbox.getText()+",";
		returnval += height_textbox.getText();
		return returnval;
	}
	@Override
	public void LoadBlocksData(String data) {
		String[] values = data.split(",");
		width_textbox.setText(values[0]);
		height_textbox.setText(values[1]);
	}
}
