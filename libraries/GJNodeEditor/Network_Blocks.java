package GJNodeEditor;

import java.awt.Point;

import GJNeural.NeuralNet.Network;

public class Network_Blocks extends Blocks{
	Node network_output;
	Network network;
	public Network_Blocks(Network network) {
		super(new Point(0,0), 100, 100, "Network",true,Blocks.NETWORK);
		this.network = network;
		this.AddOutputNode("Neural Net", new network_output_data(), Network.class);
		// TODO Auto-generated constructor stub
	}
	public class network_output_data extends NodeData<Network>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) network;
		}
	}
	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DoubleClicked(Point pos) {
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
