package GJNodeEditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import GJImageProcessing.GJColorProcess;
import GJUtil.GJMath;

public class OutputVisualizer_Blocks extends Blocks{
	Node data_input;
	double[] data_value;
	public OutputVisualizer_Blocks() {
		super(new Point(0,0), 100, 60, "OUTPUT", true,Blocks.OUTPUT);
		// TODO Auto-generated constructor stub
		data_input = this.AddInputNode("Data", Double[].class);
	}

	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub
		if(data_input.isConnected()){
			double[] value = (double[]) data_input.connection_node.nodedata.getValue();
			if(value != null){
				data_value = new double[value.length];
				if(data_value.length>=3){
					this.width = (data_value.length)*35+20;
				}
				for(int i=0;i<value.length;i++){
					data_value[i] = value[i];
				}
				System.out.print("OUTPUT : ");
				for(int i=0;i<value.length;i++){
					System.out.print(value[i]+", ");
				}
				System.out.println("");
			}else{
				data_value = null;
				this.width = 100;
			}
			
		}else{
			data_value = null;
			this.width = 100;
		}
	}

	@Override
	public void DoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		if(data_value != null){
			for(int i=0;i<data_value.length;i++){
				int color = GJColorProcess.Truncate((float)data_value[i]*255);
				gj2d.setColor(new Color(0,color,0));
				gj2d.fillRect(10+(i*35), 40, 30, 30);
				gj2d.setColor(Color.WHITE);
				if(color > 150) {
					gj2d.setColor(Color.BLACK);
				}
				gj2d.setFontSize(10);
				gj2d.drawCenteredString(GJMath.GetFloatString((float) data_value[i], 2), new Rectangle(10+(i*35), 40, 30, 30));
			}
		}
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
