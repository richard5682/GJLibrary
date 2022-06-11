package GJNodeEditor;

import java.awt.Color;
import java.awt.Point;

import GJNeural.NeuralNet.Network;
import GJNeural.TrainUpdater;
import GJNeural.Trainer;
import GJNeural.TrainingData;
import GJObject.GJButton;
import GJObject.GJProgressBar;
import GJObject.GJTextBox;
import GJObject.GJText;
import GJSWING.GJButtonAction;

public class Trainer_Blocks extends Blocks{
	Node data_input,network_input,eval_input,eval_output,index_input;
	TrainingData[] evaluation_data;
	Trainer trainer;
	GJTextBox learningrate_textbox = new GJTextBox(10,10,80,40,"Learning Rate","0.001");
	GJTextBox epoch_textbox = new GJTextBox(10,55,80,40,"Epoch",Integer.class,"100");
	GJTextBox maxdata_textbox = new GJTextBox(10,100,80,40,"Maxdata",Integer.class,"5");
	GJButton start_button = new GJButton(new Point(50,30), 100, 30, new Color(50,50,50), "Start Training", new start_button_action(), 10);
	GJText average_label = new GJText(new Point(50,40), 100, 70, "Fitness: N/A", Color.white, 13);
	GJText input_label = new GJText(new Point(50,65), 100, 70, "Input Size : N/A", Color.white, 13);
	GJProgressBar progress_bar = new GJProgressBar(new Point(10,135),180,15);
	float[] evaluation;
	int index;
	public Trainer_Blocks(Trainer trainer) {
		super(new Point(0,0), 200, 140, "Trainer",300,260,false,Blocks.TRAINER);
		this.trainer = trainer;
		data_input = this.AddInputNode("Data",TrainingData[].class);
		network_input = this.AddInputNode("Network",Network.class);
		eval_input = this.AddInputNode("Evaluation", TrainingData[].class);
		eval_output = this.AddOutputNode("Output", new eval_output_nodedata(), Double[].class);
		index_input = this.AddInputNode("Index", Double.class);
		this.AddSettingsComponent(learningrate_textbox);
		this.AddSettingsComponent(epoch_textbox);
		this.AddSettingsComponent(maxdata_textbox);
		this.addComponent(start_button);
		this.addComponent(average_label);
		this.addComponent(input_label);
		this.addComponent(progress_bar);
		// TODO Auto-generated constructor stub
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
			// TODO Auto-generated method stub
			
			if(network_input.isConnected() && data_input.isConnected()){
				progress = 0;
				updatei=0;
				progress_bar.ChangeProgress(0);
				Trainer_Runnable train_runnable = new Trainer_Runnable();
				Thread train_thread = new Thread(train_runnable);
				train_thread.start();
			}else{
				average_label.changeText("Fitness: N/A");
			}
		}
	}
	public class Trainer_Runnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			trainer.StartTraining(epoch_textbox.getInt(), maxdata_textbox.getInt(), (float)learningrate_textbox.getDouble(),new trainer_update(10));
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
			progress = (float)(updatei*10)/epoch_textbox.getInt();
			progress_bar.ChangeProgress(progress);
			average_label.changeText("Fitness: "+String.valueOf(trainer.GetFitness()));
			render();
		}
	}
	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub
		if(index_input.isConnected()){
			Double index = (Double)index_input.connection_node.nodedata.getValue();
			if(index != null){
				this.index = index.intValue();
			}
		}
		if(network_input.isConnected()){
			Network network = (Network)network_input.connection_node.nodedata.getValue();
			trainer.SetNetwork(network);
		}else{
			trainer.SetNetwork(null);
		}
		if(eval_input.isConnected()){
			TrainingData[] datas = (TrainingData[])eval_input.connection_node.nodedata.getValue();
			if(datas!= null){
				for(TrainingData data:datas){
					data.ConvertData();
				}
				evaluation_data = datas;
				evaluation = trainer.network.GetOutput(evaluation_data[index].data);
			}
		}
		if(data_input.isConnected()){
			int datasize = 0;
			TrainingData[] datas = (TrainingData[])data_input.connection_node.nodedata.getValue();
			if(datas != null){
				trainer.training_data.clear();
				for(TrainingData data:datas){
					if(data.output_type == TrainingData.IMAGE_DATA){
						data.ConvertData();
						if(datasize == 0){
							datasize = datas[0].data.length;
						}
						if(datasize != data.data.length){
							System.out.println("DATA INPUT ERROR");
							input_label.changeText("INPUT SIZE ERROR");
							break;
						}
					}
					trainer.training_data.add(data);
				} 
				input_label.changeText("Input Size : "+datasize);
			}
		}else{
			trainer.training_data.clear();
		}
		if(network_input.isConnected() && data_input.isConnected()){
			average_label.changeText("Fitness "+String.valueOf(trainer.GetFitness()));
		}
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
