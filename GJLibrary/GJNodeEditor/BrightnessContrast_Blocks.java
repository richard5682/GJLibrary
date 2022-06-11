package GJNodeEditor;

import java.awt.Point;

import GJImageProcessing.GJColorProcess;
import GJNeural.TrainingData;
import GJObject.GJTextBox;

public class BrightnessContrast_Blocks extends Blocks{
	TrainingData[] training_data;
	Node inputdata_node;
	Node output_node;
	GJTextBox brightness = new GJTextBox(10,10,100,50,"Brightness","0");
	GJTextBox contrast = new GJTextBox(10,65,100,50,"Contrast","100");
	public BrightnessContrast_Blocks() {
		super(new Point(0,0), 100, 50, "Brightness", 300, 210, true,Blocks.BRIGHTNESSCONTRAST);
		inputdata_node = this.AddInputNode("Image Data", TrainingData[].class);
		output_node = this.AddOutputNode("Output", new output_nodedata(), TrainingData[].class);
		this.AddSettingsComponent(brightness);
		this.AddSettingsComponent(contrast);
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
					GJColorProcess.Brightness(data.processed_image,brightness.getInt());
					GJColorProcess.Contrast(data.processed_image,contrast.getInt());
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
		
	}

	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		
	}
	@Override
	public String GetBlocksData(String path) {
		String returnval = this.brightness.getText()+","+this.contrast.getText();
		return returnval;
	}

	@Override
	public void LoadBlocksData(String data) {
		String[] values = data.split(",");
		this.brightness.setText(values[0]);
		this.contrast.setText(values[1]);
	}

}
