package GJNodeEditor;

import java.awt.Point;

import GJImageProcessing.GJColorProcess;
import GJNeural.TrainingData;
import GJNodeEditor.BrightnessContrast_Blocks.output_nodedata;
import GJObject.GJSelector;
import GJObject.GJTextBox;

public class Convolution_Blocks extends Blocks{
	
	TrainingData[] training_data;
	Node inputdata_node;
	Node output_node;
	GJSelector kernel_selector = new GJSelector(10, 10, 100, 25, "Kernel", GJColorProcess.KERNEL_CHOICE);
	GJSelector poolingtype_selector = new GJSelector(10, 90, 100, 25, "Kernel", GJColorProcess.POOLING_CHOICE);
	GJTextBox poolsize_text = new GJTextBox(10,50,100,25,"Pooling Size","1");
	public Convolution_Blocks() {
		super(new Point(0,0), 100, 70, "Convolution",300,300, true,Blocks.CONVOLUTION);
		inputdata_node = this.AddInputNode("Image Data", TrainingData[].class);
		output_node = this.AddOutputNode("Output", new output_nodedata(), TrainingData[].class);
		this.AddSettingsComponent(kernel_selector);
		this.AddSettingsComponent(poolsize_text);
		this.AddSettingsComponent(poolingtype_selector);
	}
	public class output_nodedata extends NodeData<TrainingData[]>{
		@Override
		public <T> T getValue() {
			return (T) training_data;
		}
	}
	@Override
	public void BlockUpdate() {
		if(inputdata_node.isConnected()){
			TrainingData[] trainingdata = (TrainingData[])inputdata_node.connection_node.nodedata.getValue();
			if(trainingdata != null){
				training_data = TrainingData.CloneTrainingData(trainingdata);
				for(TrainingData data:training_data){
					if(kernel_selector.getSelectedIndex() != 0){
						data.processed_image = GJColorProcess.ConvolveImage(data.processed_image, GJColorProcess.GetKernel(kernel_selector.getSelectedIndex()),data.red_channel,data.green_channel,data.blue_channel,data.bw_channel);
						data.processed_image = GJColorProcess.PoolImage(data.processed_image, poolsize_text.getInt(), poolingtype_selector.getSelectedIndex(),data.red_channel,data.green_channel,data.blue_channel,data.bw_channel);
					}else{
						data.processed_image = GJColorProcess.PoolImage(data.processed_image, poolsize_text.getInt(), poolingtype_selector.getSelectedIndex(),data.red_channel,data.green_channel,data.blue_channel,data.bw_channel);
					}
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
		String returnval = this.kernel_selector.getSelectedIndex()+","+this.poolingtype_selector.getSelectedIndex()+","+this.poolsize_text.getInt();
		return returnval;
	}
	@Override
	public void LoadBlocksData(String data) {
		String[] values = data.split(",");
		int kernel_type = Integer.parseInt(values[0]);
		int pooling_type = Integer.parseInt(values[1]);
		this.kernel_selector.setSelectedIndex(kernel_type);
		this.poolsize_text.setText(values[2]);
		this.poolingtype_selector.setSelectedIndex(pooling_type);
	}
}
