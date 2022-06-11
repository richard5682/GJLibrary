package GJNeural;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import GJImageProcessing.GJColorProcess;

public class TrainingData {
	public final static int RAW_DATA = 0,IMAGE_DATA = 1, IMAGESOFTMAX_DATA=2, IMAGE_CUSTOM_DATA = 3,
			REDCHANNEL=0,GREENCHANNEL=1,BLUECHANNEL=2,ALLCHANNEL=3,
			RGB = 0, HSV = 1;
	public int output_type=0;
	public float[] data;
	public float[][][] data2D;
	public BufferedImage processed_image;
	public BufferedImage data_image;
	public ArrayList<Float> extra_data = new ArrayList<Float>();
	public ArrayList<TrainingData> extra_image_data = new ArrayList<TrainingData>();
	public float[] output;
	public boolean red_channel = true,blue_channel = true,green_channel = true,bw_channel=false;
	public int color_type = RGB;
	public String[] data_names;
	public String folder_link;
	public TrainingData(int output_type,float[] data,float[] output,String[] data_names,String folder_link){
		this.output_type = output_type;
		this.data = data;
		this.output = output;
		this.data_names = data_names;
		this.folder_link = folder_link;
	}
	public TrainingData(int output_type,BufferedImage data_image,float[] output,String[] data_names,String folder_link){
		this.output_type = output_type;
		this.data_image = data_image;
		this.processed_image = data_image;
		this.output = output;
		this.data_names = data_names;
		this.folder_link = folder_link;
	}
	public TrainingData(){
		
	}
	public void AddExtraData(float[] extradata){
		for(int i=0;i<extradata.length;i++){
			extra_data.add(extradata[i]);
		}
	}
	public void AddExtraImageData(TrainingData data){
		extra_image_data.add(data);
	}
	public void SetData(float[] data){
		this.data=data;
	}
	public void SetOutput(float[] output){
		this.output=output;
	}
	public void DeactivateRGB(){
		this.red_channel = false;
		this.blue_channel = false;
		this.green_channel = false;
	}
	public void ConvertData(){
		if(output_type == 1 && processed_image != null){
			ArrayList<Float> data = new ArrayList<Float>();
			if(color_type == RGB){
				if(!this.bw_channel){
					if(this.red_channel){
						for(int y=0;y<processed_image.getHeight();y++){
							for(int x=0;x<processed_image.getWidth();x++){
								Color pixel_color = new Color(processed_image.getRGB(x, y));
								data.add((float)pixel_color.getRed()/255);
							}
						}
					}
					if(this.green_channel){
						for(int y=0;y<processed_image.getHeight();y++){
							for(int x=0;x<processed_image.getWidth();x++){
								Color pixel_color = new Color(processed_image.getRGB(x, y));
								data.add((float)pixel_color.getGreen()/255);
							}
						}
					}
					if(this.blue_channel){
						for(int y=0;y<processed_image.getHeight();y++){
							for(int x=0;x<processed_image.getWidth();x++){
								Color pixel_color = new Color(processed_image.getRGB(x, y));
								data.add((float)pixel_color.getBlue()/255);
							}
						}
					}
				}else{
					for(int y=0;y<processed_image.getHeight();y++){
						for(int x=0;x<processed_image.getWidth();x++){
							Color pixel_color = new Color(processed_image.getRGB(x, y));
							data.add((float)pixel_color.getRed()/255);
						}
					}
				}
				if(extra_data != null){
					for(int i=0;i<extra_data.size();i++){
						data.add(extra_data.get(i));
					}
				}
			}
			this.data = new float[data.size()];
			for(int i=0;i<data.size();i++){
				this.data[i] = data.get(i).floatValue();
			}
		}
	}
	public void ConvertData2D(){
		if(output_type == 1 && processed_image != null){
			float[][] red2D = null;
			float[][] blue2D = null;
			float[][] green2D = null;
			ArrayList<float[][]> extra_channel = new ArrayList<float[][]>();
			int i=0;
			for(int v=0;v<this.extra_image_data.size();v++){
				extra_image_data.get(v).ConvertData2D();
				float[][][] extra_data2D = extra_image_data.get(v).data2D;
				for(int u=0;u<extra_data2D.length;u++){
					extra_channel.add(extra_data2D[u]);
				}
			}
			if(color_type == RGB){
				if(!this.bw_channel){
					if(this.red_channel){
						i++;
						red2D = new float[processed_image.getWidth()][processed_image.getHeight()];
					}
					if(this.green_channel){
						i++;
						green2D = new float[processed_image.getWidth()][processed_image.getHeight()];
					}
					if(this.blue_channel){
						i++;
						blue2D = new float[processed_image.getWidth()][processed_image.getHeight()];
					}
					for(int y=0;y<processed_image.getHeight();y++){
						for(int x=0;x<processed_image.getWidth();x++){
							Color pixel_color = new Color(processed_image.getRGB(x, y));
							if(this.red_channel){
								red2D[x][y] = (float)pixel_color.getRed()/255;
							}
							if(this.green_channel){
								green2D[x][y] = (float)pixel_color.getGreen()/255;
							}
							
							if(this.blue_channel){
								blue2D[x][y] = (float)pixel_color.getBlue()/255;
							}
						}
					}
				}else{
					i++;
					red2D = new float[processed_image.getWidth()][processed_image.getHeight()];
					for(int y=0;y<processed_image.getHeight();y++){
						for(int x=0;x<processed_image.getWidth();x++){
							Color pixel_color = new Color(processed_image.getRGB(x, y));
							red2D[x][y] = (float)pixel_color.getRed()/255;
						}
					}
				}
			}
			this.data2D = new float[i+extra_channel.size()][][];
			int v=0;
			if(red2D != null){
				data2D[v] = red2D;
				v++;
			}
			if(green2D != null){
				data2D[v] = green2D;
				v++;
			}
			if(blue2D != null){
				data2D[v] = blue2D;
			}
			for(int a=0;a<extra_channel.size();a++){
				this.data2D[a+i] = extra_channel.get(a);
			}
		}
	}
	public static TrainingData[] CloneTrainingData(TrainingData[] trainingdata){
		if(trainingdata != null){
			TrainingData[] returnval = new TrainingData[trainingdata.length];
			for(int i=0;i<returnval.length;i++){
				TrainingData data = trainingdata[i];
				if(data.output_type == TrainingData.RAW_DATA){
					returnval[i] = new TrainingData(data.output_type,data.data,data.output,data.data_names,data.folder_link);
					returnval[i].extra_data= (ArrayList<Float>) data.extra_data.clone();
				}else if(data.output_type == TrainingData.IMAGE_DATA){
					returnval[i] = new TrainingData(data.output_type,GJColorProcess.CloneImage(data.processed_image),data.output,data.data_names,data.folder_link);
					returnval[i].extra_data= (ArrayList<Float>) data.extra_data.clone();
					returnval[i].red_channel= data.red_channel;
					returnval[i].green_channel= data.green_channel;
					returnval[i].blue_channel= data.blue_channel;
					returnval[i].bw_channel= data.bw_channel;
				}
			}
			return returnval;
		}
		return null;
	
	}
	public String toString(){
		String data = "";
		String output = "";
		for(int i=0;i<this.data.length;i++){
			data += this.data[i];
			if(i!=this.data.length-1){
				data += " ,";
			}
		}
		for(int i=0;i<this.output.length;i++){
			output += this.output[i];
			if(i!=this.output.length-1){
				output += " ,";
			}
		}
		return "DATA : "+data+">OUTPUT : "+output;
	}
}
