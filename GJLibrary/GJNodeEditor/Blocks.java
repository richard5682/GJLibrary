package GJNodeEditor;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;

import GJFile.GJFileLoader;
import GJImageProcessing.GJColorProcess;
import GJNeural.ConvTrainer;
import GJNeural.Conv2DNet.ConvNetwork;
import GJNodeEditor.Trainer_Blocks.start_button_action;
import GJObject.GJButton;
import GJObject.GJObject;
import GJObject.GJText;
import GJSWING.GJButtonAction;
import GJSWING.GJFrame;
import GJSWING.GJPanel;
import GJUtil.LogBackAction;

public abstract class Blocks extends GJObject{
	public static final int DATAVIEWER=1,BRIGHTNESSCONTRAST=2,RESIZE=3,CONVOLUTION=4,SEPERATOR=5,NUMBER=6,FILTER=7,OUTPUT=8,
							CHANNELADDER=9,DATADDER=10,SCREENCAP=11,CROPRESIZE=12,CONVNETWORK=13,CONVTRAINER=14,DATABLOCK=15,
							NETWORK=16,TRAINER=17,MIRROR=18,DATASPLITTER=19;
	static final int ARC_RADIUS = 15;
	public GJText label;
	public Color block_color;
	public ArrayList<Node> input_nodes = new ArrayList<Node>();
	public ArrayList<Node> output_nodes = new ArrayList<Node>();
	public NodeEditor nodeEditor;
	public GJFrame frame = null;
	public GJPanel panel = null;
	public GJButton done_button;
	public GJButton close_button;
	public GJButton info_button;
	public Point blocks_original_pos = null;
	public int block_type;
	public int block_index;
	public boolean body_trigger = true;
	public Blocks(Point point0, int width, int height,String name,boolean deletable,int type) {
		super(point0, width, height+15);
		this.block_type = type;
		label = new GJText(new Point(0,0), width, 20, name,Color.white,12);
		label.parent = this;
		this.block_color = Color.darkGray;
		if(deletable){
			close_button = new GJButton(new Point(width-16,1),15,15,Color.red,"X",new close_button_action(),10);
			this.addComponent(close_button);
		}
		info_button = new GJButton(new Point(3,3),15,15,new Color(100,100,200),"?",new info_button_action(),10);
		this.addComponent(info_button);
		this.addComponent(label);
	}
	public Blocks(Point point0, int width, int height,String name,int s_width,int s_height,boolean deletable,int type) {
		super(point0, width, height+15);
		this.block_type = type;
		label = new GJText(new Point(0,0), width, 20, name,Color.white,12);
		label.parent = this;
		this.block_color = Color.darkGray;
		this.addComponent(label);
		frame  = new GJFrame(name+" Settings", s_width, s_height, 0, JFrame.HIDE_ON_CLOSE,false,true);
		panel = new GJPanel(s_width, s_height,0,0, null);
		frame.addComponent(panel);
		done_button = new GJButton(new Point(s_width/2-50,s_height-90), 100,40, new Color(50,50,50), "DONE", new done_button_action());
		if(deletable){
			close_button = new GJButton(new Point(width-16,1),15,15,Color.red,"X",new close_button_action(),10);
			this.addComponent(close_button);
		}
		info_button = new GJButton(new Point(3,3),15,15,new Color(100,100,200,100),"?",new info_button_action(),10);
		this.addComponent(info_button);
		panel.addObject(done_button);
	}
	//TODO: REFER TO MAIN CREATE CLASS TO HOLD THE INPUT
	public void ShowInfo() {
		if(this.nodeEditor.tutorialframe != null)
			switch(block_type){
			case Blocks.CONVTRAINER:
				this.nodeEditor.tutorialframe.SelectClip("trainer");
				break;
			case Blocks.DATABLOCK:
				this.nodeEditor.tutorialframe.SelectClip("data_block");
				break;
			case Blocks.NUMBER:
				this.nodeEditor.tutorialframe.SelectClip("number");
				break;
			case Blocks.CHANNELADDER:
				this.nodeEditor.tutorialframe.SelectClip("channel_adder");
				break;
			case Blocks.BRIGHTNESSCONTRAST:
				this.nodeEditor.tutorialframe.SelectClip("brightness_contrast");
				break;
			case Blocks.CONVOLUTION:
				this.nodeEditor.tutorialframe.SelectClip("convolution");
				break;
			case Blocks.RESIZE:
				this.nodeEditor.tutorialframe.SelectClip("resize");
				break;
			case Blocks.FILTER:
				this.nodeEditor.tutorialframe.SelectClip("filter");
				break;
			case Blocks.DATAVIEWER:
				this.nodeEditor.tutorialframe.SelectClip("data_viewer");
				break;
			case Blocks.OUTPUT:
				this.nodeEditor.tutorialframe.SelectClip("output");
				break;
			case Blocks.SCREENCAP:
				this.nodeEditor.tutorialframe.SelectClip("screencap");
				break;
			case Blocks.DATADDER:
				this.nodeEditor.tutorialframe.SelectClip("data_adder");
				break;
			case Blocks.CROPRESIZE:
				this.nodeEditor.tutorialframe.SelectClip("cropresize");
				break;
			case Blocks.CONVNETWORK:
				this.nodeEditor.tutorialframe.SelectClip("convnetwork");
				break;
			case Blocks.MIRROR:
				this.nodeEditor.tutorialframe.SelectClip("mirror");
				break;
			case Blocks.DATASPLITTER:
				this.nodeEditor.tutorialframe.SelectClip("data_splitter");
				break;
			default:
				System.err.println("ERROR NO BLOCK_TYPE OF "+block_type+"'block_type'"+" FOUND");
			}
	}
	public static Blocks LoadBlock(String nativedata,String path,String specificdata,LogBackAction consolelogback){
		String[] native_block_data = nativedata.split(",");
		int block_type = Integer.parseInt(native_block_data[0]);
		int x_pos = Integer.parseInt(native_block_data[1]);
		int y_pos = Integer.parseInt(native_block_data[2]);
		Blocks blocks;
		switch(block_type){
			case Blocks.CONVTRAINER:
				ConvTrainer convtrainer = new ConvTrainer(consolelogback);
				blocks = new ConvTrainer_Blocks(convtrainer,consolelogback);
				break;
			case Blocks.DATABLOCK:
				blocks = new Data_Blocks(specificdata);
				break;
			case Blocks.NUMBER:
				blocks = new Number_Blocks();
				break;
			case Blocks.CHANNELADDER:
				blocks = new ChannelAdder_Blocks();
				break;
			case Blocks.BRIGHTNESSCONTRAST:
				blocks = new BrightnessContrast_Blocks();
				break;
			case Blocks.CONVOLUTION:
				blocks = new Convolution_Blocks();
				break;
			case Blocks.RESIZE:
				blocks = new Resize_Blocks();
				break;
			case Blocks.FILTER:
				blocks = new Filter_Blocks();
				break;
			case Blocks.DATAVIEWER:
				blocks = new DataViewer_Blocks();
				break;
			case Blocks.OUTPUT:
				blocks = new OutputVisualizer_Blocks();
				break;
			case Blocks.SCREENCAP:
				blocks = new ScreenCapture_Blocks();
				break;
			case Blocks.DATADDER:
				blocks = new DataAdder_Blocks();
				break;
			case Blocks.CROPRESIZE:
				blocks = new CropResize_Blocks();
				break;
			case Blocks.CONVNETWORK:
				String text;
				if(specificdata.contains("\\")) {
					text = GJFileLoader.LoadText(specificdata)[0];
				}else {
					text = GJFileLoader.LoadText(path+"//"+specificdata)[0];
				}
				ConvNetwork convnetwork = ConvNetwork.LoadData(text,consolelogback);
				blocks = new ConvNetwork_Blocks(convnetwork);
				break;
			case Blocks.MIRROR:
				blocks = new Mirror_Blocks();
				break;
			case Blocks.DATASPLITTER:
				blocks = new DataSplitter_Blocks();
				break;
			default:
				System.err.println("ERROR NO BLOCK_TYPE OF "+"'block_type'"+" FOUND");
				blocks = null;
				return null;
		}
		blocks.point0.x = x_pos;
		blocks.point0.y = y_pos;
		return blocks;
	}
	public int GetInputNodeIndex(Node n){
		for(int i=0;i<input_nodes.size();i++){
			if(n.equals(input_nodes.get(i))){
				return i;
			}
		}
		return -1;
	}
	public int GetOutputNodeIndex(Node n){
		for(int i=0;i<output_nodes.size();i++){
			if(n.equals(output_nodes.get(i))){
				return i;
			}
		}
		return -1;
	}
	public void BlockConnectionUpdate(){
		BlockUpdate();
		for(int i=0;i<output_nodes.size();i++){
			Node[] input_connected = nodeEditor.GetOutputConnection(output_nodes.get(i));
			for(Node input:input_connected){
				input.block.BlockConnectionUpdate();
			}
		}
	}
	public String GetNativeData(){
		String returnval = "";
		returnval += this.block_type+","+this.point0.x+","+this.point0.y;
		return returnval;
	}
	public abstract String GetBlocksData(String path);
	public abstract void LoadBlocksData(String data);
	public abstract void BlockUpdate();
	public Node AddInputNode(String label,Class input_type){
		Node n= new Node(new Point(-10,0), 10, 10,label, this,true,input_type);
		input_nodes.add(n);
		this.addComponent(n);
		int spacing = (this.height-25)/input_nodes.size();
		for(int i=0;i<input_nodes.size();i++){
			input_nodes.get(i).point0.y = spacing*i+25;
		}
		return n;
	}
	public Node AddOutputNode(String label,NodeData nodedata,Class output_type){
		Node n= new Node(new Point(width,0), 10, 10,label, this,false,nodedata,output_type);
		n.label.point0.x -= n.label.width+10;
		output_nodes.add(n);
		this.addComponent(n);
		int spacing = (this.height-25)/output_nodes.size();
		for(int i=0;i<output_nodes.size();i++){
			output_nodes.get(i).point0.y = spacing*i+25;
		}
		return n;
	}
	public void deleteBlock(){
		nodeEditor.DeleteBlock(this);
		this.delete();
	}
	public void AddSettingsComponent(GJObject object){
		panel.addObject(object);
	}
	public void HideFrame(){
		frame.hide();
	}
	@Override
	public void mouseEntered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseLeave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(Point pos) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClick(int button, Point pos) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(int button, Point pos) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(int button, Point pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(int button, Point pos) {
		// TODO Auto-generated method stub
		
	}
	public void mouseDoubleClicked(Point pos){
		DoubleClicked(pos);
		if(body_trigger) {
			if(frame != null){
				this.frame.show();
			}
		}else {
			if(label.MouseInside) {
				if(frame != null){
					this.frame.show();
				}
			}
		}
//		for(GJObject obj:this.Objects) {
//			if(obj.MouseInside) {
//				return;
//			}
//		}
	}
	public void SetOriginalPos(){
		this.blocks_original_pos = new Point(point0.x,point0.y);
	}
	public abstract void DoubleClicked(Point pos);
	public class done_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			BlockConnectionUpdate();
			frame.hide();
		}
	}
	public class info_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			ShowInfo();
		}
	}
	public class close_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			if(frame != null) {
				frame.hide();
			}
			deleteBlock();
		}
	}
	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		gj2d.setColor(Color.white);
		gj2d.drawRoundRect(-1, -1, width+2, height+2,ARC_RADIUS);
		gj2d.setColor(Color.GRAY);
		gj2d.fillRect(0, 0, width, 20);
		gj2d.setColor(block_color);
		gj2d.fillRect(0, 20, width, height-20);
		drawBlocks(gj2d);
	}
	public abstract void drawBlocks(GJGraphics2D gj2d);
	
}
