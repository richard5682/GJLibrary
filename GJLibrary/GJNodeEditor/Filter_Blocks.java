package GJNodeEditor;

import java.awt.Point;
import java.awt.image.BufferedImage;

import GJImageProcessing.GJColorProcess;
import GJNeural.TrainingData;
import GJNodeEditor.BrightnessContrast_Blocks.output_nodedata;
import GJObject.GJSelector;

public class Filter_Blocks extends Blocks{
	static final String[] FILTERS = {"Black and White","Edge Sobel","Inverse"};
	static final int BLACKANDWHITE=0,EDGESOBEL=1,INVERSE=2;
	TrainingData[] training_data;
	Node inputdata_node;
	Node output_node;
	GJSelector filter_selector = new GJSelector(10, 10, 100, 30, "Filter", FILTERS);
	public Filter_Blocks() {
		super(new Point(0,0), 100, 50, "Filter", 300, 150, true,Blocks.FILTER);
		inputdata_node = this.AddInputNode("Image Data", TrainingData[].class);
		output_node = this.AddOutputNode("Output", new output_nodedata(), TrainingData[].class);
		this.AddSettingsComponent(filter_selector);
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
					if(filter_selector.getSelectedIndex() == BLACKANDWHITE){
						data.processed_image = GJColorProcess.ConvertBW(data.processed_image);
						data.bw_channel = true;
						data.DeactivateRGB();
					}else if(filter_selector.getSelectedIndex() == EDGESOBEL){
						BufferedImage bwimage = GJColorProcess.ConvertBW(data.processed_image);
						BufferedImage image1 = GJColorProcess.ConvolveImage(bwimage, GJColorProcess.HORIZONTALSOBEL3x3, false, false, false, true);
						BufferedImage image2 = GJColorProcess.ConvolveImage(bwimage, GJColorProcess.VERTICALSOBEL3x3, false, false, false, true);
						data.processed_image = GJColorProcess.AddImage(image1, image2);
						data.bw_channel = true;
						data.DeactivateRGB();
					}else if(filter_selector.getSelectedIndex() == INVERSE){
						data.processed_image = GJColorProcess.InverseImage(data.processed_image);
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
		return String.valueOf(filter_selector.getSelectedIndex());
	}

	@Override
	public void LoadBlocksData(String data) {
		filter_selector.setSelectedIndex(Integer.parseInt(data));
	}
}
