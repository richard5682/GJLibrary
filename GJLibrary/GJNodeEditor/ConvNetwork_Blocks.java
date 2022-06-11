package GJNodeEditor;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import GJFile.GJFileLoader;
import GJNeural.Conv2DNet.ConvNetwork;
import GJNeural.NeuralNet.Network;
import GJNodeEditor.ConvTrainer_Blocks.Trainer_Runnable;
import GJNodeEditor.ConvTrainer_Blocks.start_button_action;
import GJObject.GJButton;
import GJObject.GJText;
import GJSWING.GJButtonAction;
import GJSWING.GJLoader;

public class ConvNetwork_Blocks extends Blocks{
	Node network_output;
	ConvNetwork network;
	GJButton save_button = new GJButton(new Point(10,50), 80, 30, new Color(50,50,50), "Save Data", new save_button_action(), 10);
	GJButton reset_button = new GJButton(new Point(10,80), 80, 30, new Color(50,50,50), "Reset Data", new reset_button_action(), 10);
	GJText input_label = new GJText(new Point(0,8), 100, 70, "size", Color.white, 10);
	public ConvNetwork_Blocks(ConvNetwork network) {
		super(new Point(0,0), 100, 100, "Network",true,Blocks.CONVNETWORK);
		this.network = network;
		input_label.changeText("I:"+network.input_size+"x"+network.input_size);
		this.AddOutputNode("Neural Net", new network_output_data(), ConvNetwork.class);
		this.addComponent(input_label);
		this.addComponent(save_button);
		this.addComponent(reset_button);
		// TODO Auto-generated constructor stub
	}
	public class network_output_data extends NodeData<ConvNetwork>{
		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T) network;
		}
	}
	public class reset_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			network.Reset();
		}
	}
	public class SaveData_Runnable implements Runnable{
		String path;
		GJLoader loader;
		public SaveData_Runnable(String path,GJLoader loader) {
			this.path = path;
			this.loader = loader;
		}
		@Override
		public void run() {
			try {
				String networkdata = network.GetData();
				loader.showLoader("SAVING NETWORK DATA");
				loader.setValue(0.5f);
				GJFileLoader.SaveText(networkdata, path+".nnet");
				loader.setValue(1f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loader.exitLoader();
		}
	}
	public class save_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			String path = GJFileLoader.OpenSaveChooser("", null);
			if(path != null){
				GJLoader loader = new GJLoader(null);
				Thread save_thread = new Thread(new SaveData_Runnable(path,loader));
				loader.showLoader("PARSING NETWORK");
				save_thread.start();
			}
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
		File f = new File(path);
		int i=0;
		while(f.exists()){
			i++;
			f = new File(path+"//network_data"+i+".nnet");
		}
		try {
			GJFileLoader.SaveText(network.GetData(), f.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return f.getName();
	}

	@Override
	public void LoadBlocksData(String data) {
		
	}
}
