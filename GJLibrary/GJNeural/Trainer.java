package GJNeural;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import GJFile.GJFileLoader;
import GJNeural.NeuralNet.Network;

public class Trainer {
	public static final int ALL_DATA = -1;
	public Network network;
	public ArrayList<TrainingData> training_data = new ArrayList<TrainingData>();
	public Trainer(Network network){
		this.network = network;
	}
	public Trainer(){
		
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
	public void StartTraining(int epoch,int maxdata,float learningrate,TrainUpdater updator){
		int update_timer=0;
		for(int i=0;i<epoch;i++){
			if(update_timer > updator.update){
				updator.Update();
				update_timer=0;
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
			
			//ACTUAL TRAINING
			for(int v=0;v<trainingdata.length;v++){
				for(int u=0;u<trainingdata[v].length;u++){
					network.Train(trainingdata[v][u].data, trainingdata[v][u].output);
				}
				network.ApplyChanges(learningrate);
			}
		}
	}
	public float GetFitness(){
		float sum=0;
		for(int i=0;i<training_data.size();i++){
			sum += network.GetScore(training_data.get(i).data, training_data.get(i).output);
		}
		return sum/training_data.size();
	}
	public void SetNetwork(Network network){
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
			File file = new File(link);
			File parent_file = file.getParentFile();
			String directory = parent_file.getAbsolutePath();
			String src_directory = directory+"\\src";
			for(int i=start_collumn;i<datalines.length;i++){
				String[] datas = datalines[i].split(",");
				String imagelink = src_directory+"\\"+datas[0];
				float[] output = new float[datas.length-1];
				for(int v=1;v<datas.length;v++){
					output[v-1] = Float.parseFloat(datas[v]);
				}
				File image_file = new File(imagelink);
				if(image_file.exists()){
					BufferedImage bi = GJFileLoader.LoadImage(imagelink);
					collection.add(new TrainingData(TrainingData.IMAGE_DATA,bi,output,data_names,datas[0]));
				}else{
					System.out.println("FILE NOT FOUND : "+imagelink);
				}
			}
		}else if(data_type == TrainingData.IMAGESOFTMAX_DATA){
			File file = new File(link);
			File parent_file = file.getParentFile();
			String directory = parent_file.getAbsolutePath();
			String src_directory = directory+"\\src";
			for(int i=start_collumn;i<datalines.length;i++){
				String[] datas = datalines[i].split(",");
				String imagelink = src_directory+"\\"+datas[0];
				
				float[] output = new float[datas.length-1];
				for(int v=1;v<datas.length;v++){
					output[v-1] = Float.parseFloat(datas[v]);
				}
				ArrayList<BufferedImage> imagelist = new ArrayList<BufferedImage>();
				File dir = new File(imagelink);
				File[] imagesfile = dir.listFiles();
				for(File image:imagesfile){
					System.out.println(image.getAbsolutePath());
					BufferedImage bi = GJFileLoader.LoadImage(image.getAbsolutePath());
					imagelist.add(bi);
				}
				for(int v=0;v<imagelist.size();v++){
					collection.add(new TrainingData(TrainingData.IMAGE_DATA,imagelist.get(v),output,data_names,datas[0]));
				}
			}
		}else if(data_type == TrainingData.IMAGE_CUSTOM_DATA){
			File file = new File(link);
			File parent_file = file.getParentFile();
			String directory = parent_file.getAbsolutePath();
			String src_directory = directory+"\\src";
			String type = datalines[0].split(",")[2];
			for(int i=start_collumn;i<datalines.length;i++){
				String[] datas = datalines[i].split(",");
				String imagelink = src_directory+"\\"+datas[0]+"."+type;
				float[] output = new float[datas.length-1];
				for(int v=1;v<datas.length;v++){
					output[v-1] = Float.parseFloat(datas[v]);
				}
				File image_file = new File(imagelink);
				if(image_file.exists()){
					System.out.println(image_file.getAbsolutePath());
					BufferedImage bi = GJFileLoader.LoadImage(imagelink);
					collection.add(new TrainingData(TrainingData.IMAGE_DATA,bi,output,data_names,datas[0]));
				}else{
					System.out.println("FILE NOT FOUND : "+imagelink);
				}
			}
		}
		TrainingData[] returnval = new TrainingData[collection.size()];
		collection.toArray(returnval);
		return returnval;
	}
}
