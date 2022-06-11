package GJNeural;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import GJFile.GJFileLoader;
import GJNeural.Conv2DNet.ConvNetwork;
import GJNeural.NeuralNet.Network;
import GJSWING.GJLoader;
import GJUtil.LogBackAction;

public class ConvTrainer {
	public static final int ALL_DATA = -1;
	public ConvNetwork network;
	public ArrayList<TrainingData> training_data = new ArrayList<TrainingData>();
	public ArrayList<TrainingData> test_training_data = new ArrayList<TrainingData>();
	public ArrayList<Float> train_cost_log = new ArrayList<Float>();
	public ArrayList<Float> eval_cost_log = new ArrayList<Float>();
	
	public static LogBackAction logback;
	
	public ConvTrainer(ConvNetwork network){
		this.network = network;
	}
	public ConvTrainer(ConvNetwork network,LogBackAction logback){
		this.network = network;
		this.logback = logback;
	}
	public ConvTrainer(){

	}
	public ConvTrainer(LogBackAction logback){
		this.logback = logback;
	}
	public TrainingData[][] GetRandomData(int maxdata){
		ArrayList<TrainingData> random_data = training_data;
		Collections.shuffle(random_data);
		ArrayList<TrainingData[]> return_data = new ArrayList<TrainingData[]>();
		int v=0;
		while(v < random_data.size()){
			ArrayList<TrainingData> buffer_data = new ArrayList<TrainingData>();
			for(int i=0;i<maxdata;i++){
				if(v == random_data.size()){
					break;
				}
				buffer_data.add(random_data.get(v));
				v++;
			}
			TrainingData[] datatoadd = new TrainingData[buffer_data.size()];
			buffer_data.toArray(datatoadd);
			return_data.add(datatoadd);
		}
		TrainingData[][] datatoreturn = new TrainingData[return_data.size()][];
		return_data.toArray(datatoreturn);
		return datatoreturn;
	}
	public void StartTraining(int epoch,int maxdata,float learningrate,float dropout,TrainUpdater updator){
		int update_timer=0;
		float minfitness=100,mintestfitness=100;
		network.densenetwork.ChangeDropout(dropout);
		train_cost_log.clear();
		eval_cost_log.clear();
		network.ApplyChanges(0);
		for(int i=0;i<epoch;i++){
			long start_time = System.currentTimeMillis();
			if(updator != null){
				if(update_timer >= updator.update){
					updator.Update();
					update_timer=0;
				}
			}
			update_timer++;
			//RANDOMIZE TRIANING DATA
			TrainingData[][] trainingdata;
			if(maxdata != ALL_DATA){
				trainingdata = GetRandomData(maxdata);
			}else{
				trainingdata = new TrainingData[1][training_data.size()];
				training_data.toArray(trainingdata[0]);
			}
			//TODO: YOU CHANGE SOMETHING HERE FOR 1D
			//ACTUAL TRAINING
			//IMAGE TRAINING
			if(network.IMAGE_INPUT){
				for(int v=0;v<trainingdata.length;v++){
					for(int u=0;u<trainingdata[v].length;u++){
						network.Train(trainingdata[v][u].data2D, trainingdata[v][u].output);
					}
					network.ApplyChanges(learningrate);
				}
			}else{
				//RAW DATA TRAINING
				for(int v=0;v<trainingdata.length;v++){
					for(int u=0;u<trainingdata[v].length;u++){
						network.Train1D(trainingdata[v][u].data, trainingdata[v][u].output);
					}
					network.ApplyChanges(learningrate);
				}
			}
			
			float train_fitness = GetFitness();
			float test_fitness = GetTestFitness();
			if(train_fitness < minfitness){
				minfitness = train_fitness;
			}
			if(test_fitness < mintestfitness){
				mintestfitness = test_fitness;
			}
			System.out.print("EPOCH : "+i+"    Fitness : ");
			System.out.printf("%.4f",minfitness);
			System.out.print("/");
			System.out.printf("%.4f",train_fitness);
			System.out.print("   Test-Fitness : ");
			System.out.printf("%.4f",mintestfitness);
			System.out.print("/");
			System.out.printf("%.4f",test_fitness);
			train_cost_log.add(train_fitness);
			eval_cost_log.add(test_fitness);
			long end_time = System.currentTimeMillis();
			long time_elapsed = end_time-start_time;
			System.out.println("   Time Ellapsed : "+time_elapsed+"ms");
			if(logback != null){
				logback.println("EPOCH : "+i+"    Fitness : ");
				logback.printf("%.4f",minfitness);
				logback.print("/");
				logback.printf("%.4f",train_fitness);
				logback.print("   Test-Fitness : ");
				logback.printf("%.4f",mintestfitness);
				logback.print("/");
				logback.printf("%.4f",test_fitness);
				logback.print("   Time Ellapsed : "+time_elapsed+"ms");
			}
		
			if(Float.isNaN(train_fitness)){
				System.out.println("RESETING TRAINING... REDUCING LEARNING RATE");
				network.Reset();
				train_cost_log.clear();
				eval_cost_log.clear();
				learningrate = learningrate/2;
				if(logback != null){
					logback.println("RESETING TRAINING... REDUCING LEARNING RATE: "+learningrate);
				}
				i=0;
			}
		}
		if(logback != null){
			logback.println("DONE TRAINING!");
		}
	}
	public void ConvertData2D(){
		for(TrainingData data:training_data){
			data.ConvertData2D();
		}
	}
	public float GetFitness(){
		float sum=0;
		if(network.IMAGE_INPUT){
			for(int i=0;i<training_data.size();i++){
				sum += network.GetScore(training_data.get(i).data2D, training_data.get(i).output);
			}
		}else{
			for(int i=0;i<training_data.size();i++){
				sum += network.GetScore1D(training_data.get(i).data, training_data.get(i).output);
			}
		}
		return sum/training_data.size();
	}
	public float GetTestFitness(){
		float sum=0;
		//TODO: YOU DID SOMETHING HERE
		if(network.IMAGE_INPUT){
			for(int i=0;i<test_training_data.size();i++){
				sum += network.GetScore(test_training_data.get(i).data2D, test_training_data.get(i).output);
			}
		}else{
			//RAW TEST FITNESS
			for(int i=0;i<test_training_data.size();i++){
				sum += network.GetScore1D(test_training_data.get(i).data, test_training_data.get(i).output);
			}
		}
		return sum/test_training_data.size();
	}
	public void SetNetwork(ConvNetwork network){
		this.network = network;
	}
	public void AddData(TrainingData data){
		training_data.add(data);
	}
	public void AddData(float[] data,float[] output,String[] data_names,String folder_link){
		training_data.add(new TrainingData(TrainingData.RAW_DATA,data,output,data_names,folder_link));
	}
	public void AddData(String link){
		TrainingData[] data_toadd = LoadData(link);
		for(int i=0;i<data_toadd.length;i++){
			training_data.add(data_toadd[i]);
		}
	}
//	public static TrainingData[] LoadData(String link){
//		ArrayList<TrainingData> collection = new ArrayList<TrainingData>();
//		String[] datalines = GJFileLoader.LoadText(link);
//		int data_type = Integer.parseInt(datalines[0].split(",")[0]);
//		int start_collumn = Integer.parseInt(datalines[0].split(",")[1]);
//		if(data_type == TrainingData.RAW_DATA){
//			int data_number = Integer.parseInt(datalines[0].split(",")[2]);
//			for(int i=start_collumn;i<datalines.length;i++){
//				String[] datas = datalines[i].split(",");
//				float[] data = new float[data_number];
//				float[] output = new float[datas.length-data_number];
//				for(int v=0;v<data_number;v++){
//					data[v] = Float.parseFloat(datas[v]);
//				}
//				for(int v=data_number;v<datas.length;v++){
//					output[v-data_number] = Float.parseFloat(datas[v]);
//				}
//				collection.add(new TrainingData(TrainingData.RAW_DATA,data,output));
//			}
//		}else if(data_type == TrainingData.IMAGE_DATA){
//			File file = new File(link);
//			File parent_file = file.getParentFile();
//			String directory = parent_file.getAbsolutePath();
//			String src_directory = directory+"\\src";
//			for(int i=start_collumn;i<datalines.length;i++){
//				String[] datas = datalines[i].split(",");
//				String imagelink = src_directory+"\\"+datas[0];
//				float[] output = new float[datas.length-1];
//				for(int v=1;v<datas.length;v++){
//					output[v-1] = Float.parseFloat(datas[v]);
//				}
//				File image_file = new File(imagelink);
//				if(image_file.exists()){
//					BufferedImage bi = GJFileLoader.LoadImage(imagelink);
//					collection.add(new TrainingData(TrainingData.IMAGE_DATA,bi,output));
//				}else{
//					System.out.println("FILE NOT FOUND : "+imagelink);
//				}
//			}
//		}else if(data_type == TrainingData.IMAGESOFTMAX_DATA){
//			File file = new File(link);
//			File parent_file = file.getParentFile();
//			String directory = parent_file.getAbsolutePath();
//			String src_directory = directory+"\\src";
//			for(int i=start_collumn;i<datalines.length;i++){
//				String[] datas = datalines[i].split(",");
//				String imagelink = src_directory+"\\"+datas[0];
//				
//				float[] output = new float[datas.length-1];
//				for(int v=1;v<datas.length;v++){
//					output[v-1] = Float.parseFloat(datas[v]);
//				}
//				ArrayList<BufferedImage> imagelist = new ArrayList<BufferedImage>();
//				File dir = new File(imagelink);
//				File[] imagesfile = dir.listFiles();
//				for(File image:imagesfile){
//					System.out.println(image.getAbsolutePath());
//					BufferedImage bi = GJFileLoader.LoadImage(image.getAbsolutePath());
//					imagelist.add(bi);
//				}
//				for(int v=0;v<imagelist.size();v++){
//					collection.add(new TrainingData(TrainingData.IMAGE_DATA,imagelist.get(v),output));
//				}
//			}
//		}else if(data_type == TrainingData.IMAGE_CUSTOM_DATA){
//			File file = new File(link);
//			File parent_file = file.getParentFile();
//			String directory = parent_file.getAbsolutePath();
//			String src_directory = directory+"\\src";
//			String type = datalines[0].split(",")[2];
//			for(int i=start_collumn;i<datalines.length;i++){
//				String[] datas = datalines[i].split(",");
//				String imagelink = src_directory+"\\"+datas[0]+"."+type;
//				float[] output = new float[datas.length-1];
//				for(int v=1;v<datas.length;v++){
//					output[v-1] = Float.parseFloat(datas[v]);
//				}
//				File image_file = new File(imagelink);
//				if(image_file.exists()){
//					System.out.println(image_file.getAbsolutePath());
//					BufferedImage bi = GJFileLoader.LoadImage(imagelink);
//					collection.add(new TrainingData(TrainingData.IMAGE_DATA,bi,output));
//				}else{
//					System.out.println("FILE NOT FOUND : "+imagelink);
//				}
//			}
//		}
//		TrainingData[] returnval = new TrainingData[collection.size()];
//		collection.toArray(returnval);
//		return returnval;
//	}
	public static TrainingData[] LoadData(String link){
		ArrayList<TrainingData> collection = new ArrayList<TrainingData>();
		String[] datalines = GJFileLoader.LoadText(link);
		int data_type = Integer.parseInt(datalines[0].split(",")[0]);
		int start_collumn = Integer.parseInt(datalines[0].split(",")[1]);
		String[] data_names = datalines[1].split(",");
		if(data_type == TrainingData.RAW_DATA){
			int data_number = Integer.parseInt(datalines[0].split(",")[2]);
			for(int i=start_collumn;i<datalines.length;i++){
				String[] datas = datalines[i].split(",");
				float[] data = new float[data_number];
				float[] output = new float[datas.length-data_number];
				for(int v=0;v<data_number;v++){
					data[v] = Float.parseFloat(datas[v]);
				}
				for(int v=data_number;v<datas.length;v++){
					output[v-data_number] = Float.parseFloat(datas[v]);
				}
				collection.add(new TrainingData(TrainingData.RAW_DATA,data,output,data_names,null));
			}
		}else if(data_type == TrainingData.IMAGE_DATA){
			//LOADER
			GJLoader loader = new GJLoader(null);
			loader.showLoader("Loading Training Data");
			File file = new File(link);
			File parent_file = file.getParentFile();
			String directory = parent_file.getAbsolutePath();
			String src_directory = directory+"\\src";
			for(int i=start_collumn;i<datalines.length;i++){
				loader.setValue((float)i/datalines.length);
				String[] datas = datalines[i].split(",");
				String imagelink = src_directory+"\\"+datas[0];
				float[] output = new float[datas.length-1];
				for(int v=1;v<datas.length;v++){
					output[v-1] = Float.parseFloat(datas[v]);
				}
				File image_file = new File(imagelink);
				if(image_file.exists()){
					BufferedImage bi = GJFileLoader.LoadImage(imagelink);
					if(bi == null) {
						collection.add(new TrainingData(TrainingData.IMAGE_DATA,bi,output,data_names,datas[0]));
					}
				}else{
					logback.println("FILE NOT FOUND : "+imagelink);
				}
			}
			loader.exitLoader();
		}else if(data_type == TrainingData.IMAGESOFTMAX_DATA || data_type == TrainingData.IMAGE_CUSTOM_DATA){
			//LOADER
			GJLoader loader = new GJLoader(null);
			loader.showLoader("Loading Training Data");
			File file = new File(link);
			File parent_file = file.getParentFile();
			String directory = parent_file.getAbsolutePath();
			String src_directory = directory+"\\src";
			for(int i=start_collumn;i<datalines.length;i++){
				loader.setValue((float)i/datalines.length);
				String[] datas = datalines[i].split(",");
				String imagelink = src_directory+"\\"+datas[0];
				float[] output = new float[datas.length-1];
				for(int v=1;v<datas.length;v++){
					output[v-1] = Float.parseFloat(datas[v]);
				}
				ArrayList<BufferedImage> imagelist = new ArrayList<BufferedImage>();
				File main_file = new File(imagelink);
				if(main_file.isDirectory()){
					File[] imagesfile = main_file.listFiles();
					for(File image:imagesfile){
						logback.println("LOADING DATA : "+image.getAbsolutePath());
						BufferedImage bi = GJFileLoader.LoadImage(image.getAbsolutePath());
						if(bi != null) {
							imagelist.add(bi);
						}
					}
					for(int v=0;v<imagelist.size();v++){
						collection.add(new TrainingData(TrainingData.IMAGE_DATA,imagelist.get(v),output,data_names,datas[0]));
					}
				}else if(!main_file.isDirectory()){
					File image_file = new File(imagelink);
					if(image_file.exists()){
						logback.println("LOADING DATA : "+image_file.getAbsolutePath());
						BufferedImage bi = GJFileLoader.LoadImage(imagelink);
						collection.add(new TrainingData(TrainingData.IMAGE_DATA,bi,output,data_names,datas[0]));
					}else{
						logback.println("FILE NOT FOUND : "+imagelink);
					}
				}
			}
			loader.exitLoader();
		}
		//TODO : REMOVE THIS IF ITS NOT NESCESSARY ANYMORE
//		else if(data_type == TrainingData.IMAGE_CUSTOM_DATA){
//			File file = new File(link);
//			File parent_file = file.getParentFile();
//			String directory = parent_file.getAbsolutePath();
//			String src_directory = directory+"\\src";
//			String type = datalines[0].split(",")[2];
//			for(int i=start_collumn;i<datalines.length;i++){
//				String[] datas = datalines[i].split(",");
//				String filename = datas[0];
//				if(datas[0].contains(".")){
//					filename = datas[0].split("\\.")[0];
//				}
//				String imagelink = src_directory+"\\"+filename+"."+type;
//				float[] output = new float[datas.length-1];
//				for(int v=1;v<datas.length;v++){
//					output[v-1] = Float.parseFloat(datas[v]);
//				}
//				File image_file = new File(imagelink);
//				if(image_file.exists()){
//					System.out.println(image_file.getAbsolutePath());
//					BufferedImage bi = GJFileLoader.LoadImage(imagelink);
//					collection.add(new TrainingData(TrainingData.IMAGE_DATA,bi,output));
//				}else{
//					System.out.println("FILE NOT FOUND : "+imagelink);
//				}
//			}
//		}
		TrainingData[] returnval = new TrainingData[collection.size()];
		collection.toArray(returnval);
		return returnval;
	}
	public float[] GetTrainingCostData() {
		Float[] costdata = new Float[train_cost_log.size()];
		train_cost_log.toArray(costdata);
		float[] returnval = new float[costdata.length];
		for(int i=0;i<returnval.length;i++){
			returnval[i] = (float)costdata[i];
		}
		return returnval;
	}
	public float[] GetEvaluationCostData() {
		Float[] costdata = new Float[eval_cost_log.size()];
		eval_cost_log.toArray(costdata);
		float[] returnval = new float[costdata.length];
		for(int i=0;i<returnval.length;i++){
			returnval[i] = (float)costdata[i];
		}
		return returnval;
	}
}
