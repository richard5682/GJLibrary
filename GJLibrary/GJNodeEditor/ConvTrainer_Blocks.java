package GJNodeEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JFrame;

import GJNeural.Conv2DNet.ConvNetwork;
import GJNeural.Conv2DNet.Conv_Layer2D;
import GJNeural.Conv2DNet.Hidden_Layer;
import GJNeural.Conv2DNet.Image2D;
import GJNeural.Conv2DNet.Input_Layer;
import GJNeural.Conv2DNet.Layer2D;
import GJNeural.Conv2DNet.Nodes2D;
import GJNeural.Conv2DNet.Output_Layer;
import GJNeural.Conv2DNet.Pool_Layer2D;
import GJNeural.Conv2DNet.Softmax_Layer;
import GJNeural.Conv2DNet;
import GJNeural.ConvTrainer;
import GJNeural.NeuralNet.Network;
import GJNeural.TrainUpdater;
import GJNeural.Trainer;
import GJNeural.TrainingData;
import GJObject.GJButton;
import GJObject.GJGrapher;
import GJObject.GJObject;
import GJObject.GJProgressBar;
import GJObject.GJTextBox;
import GJObject.GJText;
import GJSWING.GJAlert;
import GJSWING.GJButtonAction;
import GJSWING.GJFrame;
import GJSWING.GJLoader;
import GJSWING.GJPanel;
import GJSWING.GJPanelHandler;
import GJUtil.LogBackAction;
public class ConvTrainer_Blocks extends Blocks{
	Node data_input,testdata_input,network_input,eval_input,eval_output,index_input;
	TrainingData[] evaluation_data,training_data,test_training_data;
	ConvTrainer trainer;
	
	GJFrame grapher_frame = new GJFrame("Grapher", 980, 450, 0,JFrame.HIDE_ON_CLOSE,true,true); 
	GJFrame dataviewer_frame = new GJFrame("Data Viewer",980,600,0,JFrame.HIDE_ON_CLOSE,true,true);
	GJFrame displaynetwork_frame = new GJFrame("Display Network",980,600,0,JFrame.HIDE_ON_CLOSE,true,true);
	
	GJPanel grapher_panel = new GJPanel(1000, 500, 0, 0, new grapher_panel_handler());
	GJPanel dataviewer_panel = new GJPanel(980, 600, 0, 0, new dataviewer_panel_handler());
	GJPanel displaynetwork_panel = new GJPanel(980, 600, 0, 0, new displaynetwork_panel_handler());
	
	GJTextBox learningrate_textbox = new GJTextBox(10,10,80,40,"Learning Rate","0.001");
	GJTextBox epoch_textbox = new GJTextBox(10,55,80,40,"Epoch",Integer.class,"100");
	GJTextBox maxdata_textbox = new GJTextBox(10,100,80,40,"Batch Size",Integer.class,"5");
	GJTextBox dropout_textbox = new GJTextBox(10,145,80,40,"DropOut","0.0");
	GJButton start_button = new GJButton(new Point(50,30), 100, 30, new Color(50,50,50), "Start Training", new start_button_action(), 10);
	GJButton stop_button = new GJButton(new Point(50,60), 100, 30, new Color(50,50,50), "Stop Training", new stop_button_action(), 10);
	GJButton load_button = new GJButton(new Point(30,180), 70, 30, new Color(50,50,50), "Load Data", new load_button_action(), 10);
	GJButton showgraph_button = new GJButton(new Point(100,180), 70, 30, new Color(50,50,50), "Show Graph", new showgraph_button_action(), 10);
	GJButton showaccuracy_button = new GJButton(new Point(100,210), 70, 30, new Color(50,50,50), "Accuracy", new showaccuracy_button_action(), 10);
	GJButton display_button = new GJButton(new Point(30,210), 70, 30, new Color(50,50,50), "Display", new display_button_action(), 10);
	GJText average_label = new GJText(new Point(50,70), 100, 70, "Fitness: N/A", Color.white, 13);
	GJText input_label = new GJText(new Point(50,95), 100, 70, "Input C: N/A I:N/A", Color.white, 13);
	GJProgressBar progress_bar = new GJProgressBar(new Point(10,140),180,15);
	
	GJGrapher trainingcost_grapher = new GJGrapher(new Point(10,10), 460, 400,"Training Cost", "EPOCH","COST", new Point(0,0));
	GJGrapher evaluationcost_grapher = new GJGrapher(new Point(500,10), 460, 400,"Evaluation Cost", "EPOCH","COST", new Point(0,0));
	
	ArrayList<dataviewer_object> data_objects = new ArrayList<dataviewer_object>();
	
	Thread train_thread;
	float[] evaluation;
	int index;
	
	LogBackAction logback = null;
	public ConvTrainer_Blocks(ConvTrainer trainer,LogBackAction logback) {
		super(new Point(0,0), 200, 230, "Trainer",300,300,false,Blocks.CONVTRAINER);
		this.body_trigger = false;
		this.logback = logback;
		this.trainer = trainer;
		data_input = this.AddInputNode("Data",TrainingData[].class);
		testdata_input = this.AddInputNode("Test Data",TrainingData[].class);
		network_input = this.AddInputNode("Network",ConvNetwork.class);
		eval_input = this.AddInputNode("Evaluation", TrainingData[].class);
		eval_output = this.AddOutputNode("Output", new eval_output_nodedata(), Double[].class);
		index_input = this.AddInputNode("Index", Double.class);
		grapher_frame.addComponent(grapher_panel);
		dataviewer_frame.addComponent(dataviewer_panel);
		dataviewer_panel.addScrollBar();
		displaynetwork_frame.addComponent(displaynetwork_panel);
		displaynetwork_panel.addScrollBar();
		grapher_panel.setBackground(Color.DARK_GRAY);
		grapher_panel.addObject(trainingcost_grapher);
		grapher_panel.addObject(evaluationcost_grapher);
		grapher_frame.hide();
		this.AddSettingsComponent(learningrate_textbox);
		this.AddSettingsComponent(epoch_textbox);
		this.AddSettingsComponent(maxdata_textbox);
		this.AddSettingsComponent(dropout_textbox);
		this.addComponent(start_button);
		this.addComponent(stop_button);
		this.addComponent(average_label);
		this.addComponent(input_label);
		this.addComponent(progress_bar);
		this.addComponent(load_button);
		this.addComponent(showgraph_button);
		this.addComponent(showaccuracy_button);
		this.addComponent(display_button);
	}
	public ConvTrainer_Blocks(ConvTrainer trainer) {
		super(new Point(0,0), 200, 210, "Trainer",300,300,false,Blocks.CONVTRAINER);
		this.logback = logback;
		this.trainer = trainer;
		data_input = this.AddInputNode("Data",TrainingData[].class);
		testdata_input = this.AddInputNode("Test Data",TrainingData[].class);
		network_input = this.AddInputNode("Network",ConvNetwork.class);
		eval_input = this.AddInputNode("Evaluation", TrainingData[].class);
		eval_output = this.AddOutputNode("Output", new eval_output_nodedata(), Double[].class);
		index_input = this.AddInputNode("Index", Double.class);
		grapher_frame.add(grapher_panel);
		dataviewer_frame.addComponent(dataviewer_panel);
		dataviewer_panel.addScrollBar();
		grapher_panel.addObject(trainingcost_grapher);
		grapher_panel.addObject(evaluationcost_grapher);
		grapher_frame.hide();
		this.AddSettingsComponent(learningrate_textbox);
		this.AddSettingsComponent(epoch_textbox);
		this.AddSettingsComponent(maxdata_textbox);
		this.AddSettingsComponent(dropout_textbox);
		this.addComponent(start_button);
		this.addComponent(stop_button);
		this.addComponent(average_label);
		this.addComponent(input_label);
		this.addComponent(progress_bar);
		this.addComponent(load_button);
		this.addComponent(showgraph_button);
		this.addComponent(showaccuracy_button);
	}
	public class grapher_panel_handler  extends GJPanelHandler{

		@Override
		public void draw(Graphics2D g2d) {
			g2d.setColor(Color.DARK_GRAY);
			g2d.fillRect(0, 0, grapher_frame.getWidth(), grapher_frame.getHeight());
		}
	}
	public int Trunchate(float val) {
		if(val < 0) {
			return 0;
		}else if(val > 255){
			return 255;
		}
		return (int)val;
	}
	public class displaynetwork_panel_handler  extends GJPanelHandler{
		@Override
		public void draw(Graphics2D g2d) {
		}
	}
	public boolean spawnimage_object() {
		this.displaynetwork_panel.clearObject();
		int image_size = 60;
		ConvNetwork network = trainer.network;
		int image_input_size = 150;
		if(network == null) {
			this.ThrowAlertNetwork();
			return false;
		}
		Image2D[] image2d_input = network.input_layer2d.image2d;
		if(network != null) {
			for(int i=0;i<image2d_input.length;i++) {
				int color = 0;
				if(image2d_input.length == 3) {
					if(i == 0) {
						color = 1;
					}else if(i==1){
						color = 2;
					}else if(i==2) {
						color = 3;
					}
				}
				ImageObject imageobj = new ImageObject(new Point(50+image_input_size*i,50),network.input_layer2d.image2d[i],image_input_size,color);
				imageobj.calculatePixels();
				displaynetwork_panel.addObjectScroll(imageobj);
			}
			ArrayList<Layer2D> conv_layer2d = network.layer_order;
			int last_y = 0;
			for(int v=0;v<conv_layer2d.size();v++) {
				Layer2D convlayer = conv_layer2d.get(v);
				Image2D[] image2d_conv = convlayer.image2d;
				int length = (int)Math.round(Math.sqrt(image2d_conv.length));
				int ix = 0,iy = 0;
				for(int i=0;i<image2d_conv.length;i++) {
					ImageObject imageobj = new ImageObject(new Point(50+(iy*(image_size+2)),210+(ix*(image_size+2))+last_y),image2d_conv[i],image_size);
					imageobj.calculatePixels();
					this.displaynetwork_panel.addObjectScroll(imageobj);
					ix++;
					if(ix >= length) {
						ix=0;
						iy++;
					}
				}
				last_y += (length*image_size+40);
			}
			last_y+=200;
			GJNeural.Conv2DNet.Network dense = network.densenetwork;
			Input_Layer input_layer = dense.input_layer;
			Hidden_Layer[] hidden_layers = dense.hidden_layers;
			Output_Layer output_layer = dense.output_layer;
			Softmax_Layer softmax_layer = dense.softmax_layer;
			
			NodesImageObject nodeimage_input = new NodesImageObject(new Point(50,last_y),300,300,input_layer.nodes2d);
			nodeimage_input.CalculateImage();
			last_y+=320;
			this.displaynetwork_panel.addObjectScroll(nodeimage_input);
			for(int i=0;i<hidden_layers.length;i++) {
				NodesImageObject nodeimage = new NodesImageObject(new Point(50,last_y),300,300,hidden_layers[i].nodes);
				nodeimage.CalculateImage();
				this.displaynetwork_panel.addObjectScroll(nodeimage);
				last_y+=320;
			}
			NodesImageObject nodeimage_output = new NodesImageObject(new Point(50,last_y),300,300,output_layer.nodes);
			nodeimage_output.CalculateImage();
			this.displaynetwork_panel.addObjectScroll(nodeimage_output);
			last_y+=320;
			NodesImageObject nodeimage_softmax = new NodesImageObject(new Point(50,last_y),300,300,softmax_layer.nodes);
			nodeimage_softmax.CalculateImage();
			this.displaynetwork_panel.addObjectScroll(nodeimage_softmax);
			
			NetworkImageObject net_image = new NetworkImageObject(new Point(650,50),200,600,network);
			this.displaynetwork_panel.addObjectScroll(net_image);
			this.displaynetwork_panel.repaint();
		}
		return true;
	}
	public void drawImage2D(Graphics2D g2d,Image2D input,Point point,int size,int color) {
		Nodes2D[][] nodes =  input.nodes2d;
		int no_pixel = nodes.length;
		int pixel_size = Math.round(size/no_pixel);
		for(int x=0;x<no_pixel;x++) {
			for(int y=0;y<no_pixel;y++) {
				int c = Trunchate(nodes[x][y].yn*255);
				Color pixel_color = new Color(c,c,c);
				if(color == 1) {
					pixel_color = new Color(c,0,0);
				}else if(color == 2) {
					pixel_color = new Color(0,c,0);
				}else if(color == 3){
					pixel_color = new Color(0,0,c);
				}
				g2d.setColor(pixel_color);
				g2d.fillRect(point.x+x*pixel_size, point.y+y*pixel_size, pixel_size, pixel_size);
			}
		}
	}
	public class dataviewer_panel_handler  extends GJPanelHandler{
		@Override
		public void draw(Graphics2D g2d) {
			g2d.setColor(Color.DARK_GRAY);
			g2d.fillRect(0, 0, dataviewer_frame.getWidth(), dataviewer_frame.getHeight());
			
		}
	}
	public class dataviewer_object extends GJObject{
		public Image image;
		public float accuracy;
		public dataviewer_object(Point point,Image image,float accuracy){
			super(point,100,100);
			this.image = image;
			this.accuracy = accuracy;
		}
		@Override
		public void mouseEntered() {
			// TODO Auto-generated method stub
			this.render();
		}
		@Override
		public void mouseLeave() {
			// TODO Auto-generated method stub
			this.render();
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
		@Override
		public void mouseDoubleClicked(Point pos) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void drawLocal(GJGraphics2D gj2d) {
			// TODO Auto-generated method stub
			gj2d.drawImage(image, 0, 0, 100, 100);
			gj2d.setColor(Color.red);
			gj2d.drawCenteredString(""+(1-accuracy/(accuracy+1))*100+"%", new Rectangle(0,0,100,20));
			if(this.MouseInside){
				gj2d.drawRect(0, 0, 100, 100);
			}
		}
	}
	public void ShowNetwork(){
		displaynetwork_panel.repaint();
		displaynetwork_frame.show();
	}
	public void CalculateAccuracy(){
		ConvNetwork network = trainer.network;
		ArrayList<TrainingData> trainingdata = trainer.training_data;
		for(int i=0;i<data_objects.size();i++){
			data_objects.get(i).delete();
		}
		data_objects.clear();
		int y=0,x=0;
		for(int i=0;i<trainingdata.size();i++){
			dataviewer_object dataobj = new dataviewer_object(new Point(10+x*120,10+y*120),trainingdata.get(i).processed_image,network.GetScore(trainingdata.get(i).data2D,trainingdata.get(i).output));
			x++;
			if(x>7){
				x=0;
				y++;
			}
			data_objects.add(dataobj);
			dataviewer_panel.addObjectScroll(dataobj);
		}
		dataviewer_panel.repaint();
	}
	public class eval_output_nodedata extends NodeData{
		@Override
		public Object getValue() {
			// TODO Auto-generated method stub
			if(evaluation != null){
				double[] deval = new double[evaluation.length];
				for(int i=0;i<evaluation.length;i++){
					deval[i] = evaluation[i];
				}
				return deval;
			}else{
				return null;
			}
		}
		
	}
	float progress = 0,updatei=0;;
	public class start_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			if(trainer.network == null) {
				ThrowAlertNetwork();
				return;
			}
			if((train_thread==null||!train_thread.isAlive())) {
				int channelsize = 0;
				int imagesize = 0;
				if(data_input.isConnected() && testdata_input.isConnected() && network_input.isConnected()){
					trainer.training_data.clear();
					trainer.test_training_data.clear();
					for(TrainingData data:training_data){
						if(data.output_type == TrainingData.IMAGE_DATA){
							data.ConvertData2D();
							if(channelsize == 0){
								channelsize = training_data[0].data2D.length;
								imagesize = training_data[0].data2D[0].length;
							}
							if(channelsize != data.data2D.length){
								System.out.println("DATA INPUT ERROR");
								logback.println("DATA INPUT ERROR");
								input_label.changeText("INPUT SIZE ERROR");
								break;
							}
						}
						trainer.training_data.add(data);
					}
					for(TrainingData data:test_training_data){
						if(data.output_type == TrainingData.IMAGE_DATA){
							data.ConvertData2D();
							if(channelsize == 0){
								channelsize = test_training_data[0].data2D.length;
							}
							if(channelsize != data.data2D.length){
								System.out.println("DATA INPUT ERROR");
								input_label.changeText("INPUT SIZE ERROR");
								break;
							}
						}
						trainer.test_training_data.add(data);
					} 
					input_label.changeText("Input C: "+channelsize+" I:"+imagesize+"x"+imagesize);
				}
				if(evaluation_data != null && eval_input.isConnected()){
					for(TrainingData data:evaluation_data){
						data.ConvertData2D();
					}
					evaluation = trainer.network.GetOutput(evaluation_data[index].data2D);
				}
				boolean condition=true;
				if(logback != null){
					if(!network_input.isConnected()){
						logback.println("ERROR: CANNOT START TRAINING NETWORK INPUT NOT CONNECTED");
					}
					if(!data_input.isConnected()){
						logback.println("ERROR: CANNOT START TRAINING DATA INPUT NOT CONNECTED");
					}
					if(trainer.network.input_size != imagesize){
						logback.println("TRAINING ERROR: DATA SIZE MISMATCH");
						condition = false;
					}
					if(trainer.network.image_no != channelsize){
						logback.println("TRAINING ERROR: DATA CHANNEL MISMATCH");
						condition = false;
					}
				}
				if(network_input.isConnected() && data_input.isConnected() && condition){
					progress = 0;
					updatei=0;
					progress_bar.ChangeProgress(0);
					Trainer_Runnable train_runnable = new Trainer_Runnable();
					train_thread = new Thread(train_runnable);
					train_thread.start();
					logback.println("Starting Training @ EPOCH: "+epoch_textbox.getInt()+", LEARNING RATE: "+learningrate_textbox.getDouble()+", BATCH SIZE: "+maxdata_textbox.getInt()+", DROPOUT: "+dropout_textbox.getDouble());
				}else{
					average_label.changeText("Fitness: N/A");
				}
			}else{
				GJAlert alert = new GJAlert(null);
				alert.ShowAlert_OK("TRAINING ALREADY IN PROGRESS");
				logback.println("TRAINING ALREADY STARTED");
			}
		}
	}
	public class stop_button_action extends GJButtonAction{

		@SuppressWarnings("deprecation")
		@Override
		public void mouseClick(int button, Point pos) {
			if(trainer.network == null) {
				ThrowAlertNetwork();
				return;
			}
			if(train_thread != null && train_thread.isAlive()){
				logback.println("STOPPED TRAINING");
				progress = 0;
				updatei=0;
				progress_bar.ChangeProgress(0);
				train_thread.stop();
				trainer.network.setState(Conv2DNet.IDLE_STATE);
			}else{
				trainer.network.setState(Conv2DNet.IDLE_STATE);
				logback.println("NO TRAINING IN PROGRESS");
			}
		}
	}
	public class display_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			if(spawnimage_object()) {
				displaynetwork_frame.show();
			}
		}
	}
	public class showaccuracy_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			if((train_thread==null || !train_thread.isAlive())) {
				CalculateAccuracy();
				dataviewer_frame.show();
			}else {
				logback.println("Cant calculate accuracy training Ongoing...");
			}
		}
		
	}
	public class showgraph_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			grapher_frame.show();
		}
		
	}
	public class load_button_action extends GJButtonAction{

		@Override
		public void mouseClick(int button, Point pos) {
			Thread load_data_thread = new Thread(new DataLoad_Runnable());
			load_data_thread.start();
		}
	}
	public class DataLoad_Runnable implements Runnable{
		@Override
		public void run() {
			int datasize = 0;
			if(data_input.isConnected() && testdata_input.isConnected() && network_input.isConnected()){
				GJLoader loader = new GJLoader(null);
				trainer.training_data.clear();
				trainer.test_training_data.clear();
				int i=0;
				int length = training_data.length+1;
				loader.showLoader("Loading Training Data");
				if(training_data.length == 0) {
					GJAlert alert = new GJAlert(null);
					alert.ShowAlert_OK("DATA SET IS EMPTY");
					loader.exitLoader();
					return;
				}
				for(TrainingData data:training_data){
					i++;
					loader.setValue((float)i/length);
					logback.println("LOADING TRAINING DATA : "+i+" out of "+length);
					if(data.output_type == TrainingData.IMAGE_DATA){
						data.ConvertData2D();
						if(datasize == 0){
							datasize = training_data[0].data2D.length;
						}
						if(datasize != data.data2D.length){
							System.out.println("DATA INPUT ERROR");
							logback.println("DATA INPUT ERROR");
							input_label.changeText("INPUT SIZE ERROR");
							break;
						}
					}
					trainer.training_data.add(data);
				} 
				if(datasize != trainer.network.input_layer2d.image2d.length) {
					GJAlert alert = new GJAlert(null);
					alert.ShowAlert_OK("DATA SET INPUT CHANNEL SIZE IS NOT\nEQUAL TO NETWORK CHANNEL SIZE");
					loader.exitLoader();
					return;
				}
				if(training_data[0].data2D[0].length != trainer.network.input_size) {
					GJAlert alert = new GJAlert(null);
					alert.ShowAlert_OK("DATA SET INPUT IMAGE SIZE IS NOT\nEQUAL TO NETWORK INPUT SIZE");
					loader.exitLoader();
					return;
				}
				i=0;
				length = test_training_data.length+1;
				loader.showLoader("Loading Evaluation Data");
				for(TrainingData data:test_training_data){
					i++;
					loader.setValue((float)i/length);
					logback.println("LOADING TEST DATA : "+i+" out of "+length);
					if(data.output_type == TrainingData.IMAGE_DATA){
						data.ConvertData2D();
						if(datasize == 0){
							datasize = test_training_data[0].data2D.length;
						}
						if(datasize != data.data2D.length){
							System.out.println("DATA INPUT ERROR");
							input_label.changeText("INPUT SIZE ERROR");
							break;
						}
					}
					trainer.test_training_data.add(data);
				} 
				input_label.changeText("Input Size : "+datasize);
				loader.exitLoader();
			}
			if(eval_input.isConnected() && evaluation_data != null){
				for(TrainingData data:evaluation_data){
					data.ConvertData2D();
				}
				evaluation = trainer.network.GetOutput(evaluation_data[index].data2D);
			}
			//TODO: PUT THIS IN A CONDITION
			if(network_input.isConnected() && data_input.isConnected()){
				average_label.changeText("Fitness "+String.valueOf(trainer.GetFitness())); 
			}
		}
	}
	public class Trainer_Runnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			trainer.StartTraining(epoch_textbox.getInt(), maxdata_textbox.getInt(), (float)learningrate_textbox.getDouble(),(float)dropout_textbox.getDouble(),new trainer_update(1));
			average_label.changeText("Fitness: "+String.valueOf(trainer.GetFitness()));
			progress_bar.ChangeProgress(1);
		}
		
	}
	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
	}
	public class trainer_update extends TrainUpdater{

		public trainer_update(int update) {
			super(update);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void Update() {
			// TODO Auto-generated method stub
			updatei++;
			trainingcost_grapher.setData(trainer.GetTrainingCostData());
			evaluationcost_grapher.setData(trainer.GetEvaluationCostData());
			progress = (float)(updatei*update)/epoch_textbox.getInt();
			progress_bar.ChangeProgress(progress);
			average_label.changeText("Fitness: "+String.valueOf(trainer.GetFitness()));
			render();
		}
	}
	public void PrintEval() {
		if(evaluation != null) {
			logback.println("OUTPUT : ");
			for(int i=0;i<evaluation.length;i++){
				logback.print(evaluation[i]+", ");
			}
		}
	}
	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub
		if(network_input.isConnected()){
			ConvNetwork network = (ConvNetwork)network_input.connection_node.nodedata.getValue();
			trainer.SetNetwork(network);
		}else{
			trainer.SetNetwork(null);
		}
		if(eval_input.isConnected() && trainer.network != null && (train_thread == null||!train_thread.isAlive())){
			TrainingData[] datas = (TrainingData[])eval_input.connection_node.nodedata.getValue();
			if(datas!= null){
				evaluation_data = datas;
				for(TrainingData data:evaluation_data){
					data.ConvertData2D();
				}
				if(index < evaluation_data.length){
					if(trainer.network.IMAGE_INPUT){
						evaluation = trainer.network.GetOutput(evaluation_data[index].data2D);
					}else{
						evaluation = trainer.network.GetOutput1D(evaluation_data[index].data);
					}
					PrintEval();
				}else{
					logback.println("Index Out of Bounds Maximum Index : "+evaluation_data.length);
				}
			}
		}
		if(index_input.isConnected() && eval_input.isConnected() && trainer.network != null  && (train_thread == null||!train_thread.isAlive())){
			Double index = (Double)index_input.connection_node.nodedata.getValue();
			if(index != null && evaluation_data != null){
				this.index = index.intValue();
				if(trainer.network.IMAGE_INPUT){
					evaluation = trainer.network.GetOutput(evaluation_data[this.index].data2D);
				}else{
					evaluation = trainer.network.GetOutput1D(evaluation_data[this.index].data);
				}
				PrintEval();
				System.out.println(evaluation[0]);
			}
		}
		if(data_input.isConnected()){
			TrainingData[] datas = (TrainingData[])data_input.connection_node.nodedata.getValue();
			if(datas != null){
				this.training_data = datas;
			}
		}else{
			this.training_data = null;
			trainer.training_data.clear();
		}
		if(testdata_input.isConnected()){
			TrainingData[] datas = (TrainingData[])testdata_input.connection_node.nodedata.getValue();
			if(datas != null){
				this.test_training_data = datas;
			}
		}else{
			this.test_training_data = null;
			trainer.test_training_data.clear();
		}
	}
	@Override
	public void DoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String GetBlocksData(String path) {
		String returnval="";
		returnval+= learningrate_textbox.getDouble()+",";
		returnval+= epoch_textbox.getInt()+",";
		returnval+= maxdata_textbox.getInt()+",";
		returnval+= dropout_textbox.getDouble();
		return returnval;
	}
	@Override
	public void LoadBlocksData(String data) {
		String[] values = data.split(",");
		learningrate_textbox.setText(values[0]);
		epoch_textbox.setText(values[1]);
		maxdata_textbox.setText(values[2]);
		dropout_textbox.setText(values[3]);
	}
	public void ThrowAlertNetwork() {
		GJAlert alert = new GJAlert(null);
		alert.ShowAlert_OK("NO NETWORK CONNECTED");
	}
}
