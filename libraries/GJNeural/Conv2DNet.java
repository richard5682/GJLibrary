package GJNeural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import GJNeural.Conv2DNet.Conv_Layer2D;
import GJNeural.Conv2DNet.Filter;
import GJNeural.Conv2DNet.Image2D;
import GJNeural.Conv2DNet.Input_Layer2D;
import GJNeural.Conv2DNet.Kernel;
import GJNeural.Conv2DNet.Layer2D;
import GJNeural.Conv2DNet.Network;
import GJNeural.Conv2DNet.Nodes2D;
import GJNeural.Conv2DNet.Pool_Layer2D;
import GJNeural.NeuralNet.Hidden_Layer;
import GJNeural.NeuralNet.Input_Layer;
import GJNeural.NeuralNet.Layer;
import GJNeural.NeuralNet.Nodes;
import GJNeural.NeuralNet.Output_Layer;
import GJNeural.NeuralNet.Softmax_Layer;
import GJUtil.GJMath;
import GJUtil.LogBackAction;

public class Conv2DNet {
	public static final int MEANPOOLING = 0,MAXPOOLING = 1,HE_INIT=0,XAV_INIT=1;
	public static final float DROPOUTRATE = 0.9f,LEAKYSLOPE = 0.001f,LEAKYCEIL = 0.01f,CEILINGRELU = 100;
	public static final int NO_FIGURES=4,BUSY_STATE=1,IDLE_STATE=0;
	public static float GetRandomFloat(int lb,int hb){
		Random r = new Random();
		return (float)r.nextGaussian()/20;
	}
	public static float randn(float mean,float std_dev){
		Random r = new Random();
		float randomValue = (float) r.nextGaussian();
		return randomValue;
	}
	public static float GetHEFloat(int layerdim,int prevlayerdim){
		float returnval = randn(layerdim,prevlayerdim)*(float)Math.sqrt((float)2/(layerdim+prevlayerdim));
		return returnval;
	}
	public static float GetXAVFloat(int prevlayerdim){
		float returnval = randn(0,prevlayerdim)*(float)Math.sqrt((float)1/prevlayerdim);
		return returnval;
	}
	public static float RELU(float val){
		if(val < 0){
			return val*LEAKYSLOPE;
		}
//		else if(val > CEILINGRELU){
//			return CEILINGRELU+(val*LEAKYCEIL);
//		}
		return val;
	}
	public static float RELUDERIVATIVE(float val){
		if(val < 0){
			return LEAKYSLOPE;
		}
//		else if(val > CEILINGRELU){
//			return LEAKYCEIL;
//		}
		return 1;
	}
	public static float TANH(float val){
		return (float)Math.tanh(val);
	}
	public static float TANHDERIVATIVE(float val){
		return (float)(1-(Math.tanh(val)*Math.tanh(val)));
	}

	public static class ConvNetwork{
		public int image_no,input_size;
		public Input_Layer2D input_layer2d;
		public ArrayList<Layer2D> layer_order = new ArrayList<Layer2D>();
		public ArrayList<Conv_Layer2D> conv_layer2d = new ArrayList<Conv_Layer2D>();
		public ArrayList<Pool_Layer2D> pool_layer2d = new ArrayList<Pool_Layer2D>();
		public Network densenetwork;
		Layer2D previous_layer;
		public int state = IDLE_STATE;
		public boolean IMAGE_INPUT = true;
		public ConvNetwork(int input_size){
			IMAGE_INPUT = false;
			this.input_size = input_size;
		}
		public ConvNetwork(int image_no,int input_size){
			IMAGE_INPUT = true;
			this.image_no = image_no;
			this.input_size = input_size;
			input_layer2d = new Input_Layer2D(image_no,input_size);
			previous_layer = input_layer2d;
		}
		public boolean isBusy() {
			if(state == BUSY_STATE) {
				return true;
			}
			return false;
		}
		public void setState(int state) {
			this.state = state;
		}
		public void AddConvLayer(int no_kernel,int size_kernel){
			Conv_Layer2D conv_layer2d = new Conv_Layer2D(no_kernel,size_kernel,previous_layer);
			previous_layer = conv_layer2d;
			this.conv_layer2d.add(conv_layer2d);
			layer_order.add(conv_layer2d);
		}
		public void AddConvLayer(int no_kernel,int size_kernel,String kernels_data){
			Conv_Layer2D conv_layer2d = new Conv_Layer2D(no_kernel,size_kernel,previous_layer);
			conv_layer2d.LoadData(kernels_data);
			previous_layer = conv_layer2d;
			this.conv_layer2d.add(conv_layer2d);
			layer_order.add(conv_layer2d);
		}
		public void AddPoolLayer(int pool_size,int type_pooling){
			Pool_Layer2D pool_layer2d = new Pool_Layer2D(pool_size,previous_layer,type_pooling);
			previous_layer = pool_layer2d;
			this.pool_layer2d.add(pool_layer2d);
			layer_order.add(pool_layer2d);
		}
		public Network GetDenseNetwork(int no_output_nodes,int output_activation,int output_type){
			Network densenetwork = null;
			if(IMAGE_INPUT){
				densenetwork = new Network(previous_layer,no_output_nodes,output_activation,output_type);
			}else{
				densenetwork = new Network(input_size,no_output_nodes,output_activation,output_type);
			}
			this.densenetwork = densenetwork;
			return densenetwork;
		}
		public void LoadDenseNetwork(String data){
			Network densenetwork;
			if(IMAGE_INPUT){
				densenetwork = new Network(data,previous_layer);
			}else{
				densenetwork = new Network(data,input_size);
			}
			this.densenetwork = densenetwork;
		}
		public void Reset(){
			for(int i=0;i<layer_order.size();i++){
				layer_order.get(i).Reset();
			}
			densenetwork.Reset();
		}
		public void Init(){
			densenetwork.init();
		}
		public void Test(float[][][] input){
			if(!isBusy()) {
				setState(BUSY_STATE);
				input_layer2d.SetInput(input);
				for(int i=0;i<layer_order.size();i++){
					layer_order.get(i).Evaluate();
				}
				input_layer2d.PrintValue();
				for(int i=0;i<layer_order.size();i++){
					layer_order.get(i).PrintValue();
				}
				setState(IDLE_STATE);
			}else {
				System.out.println("NETWORK BUSY");
			}
		}
		public void Train(float[][][] inputimage,float[] output){
			input_layer2d.SetInput(inputimage);
			for(int i=0;i<layer_order.size();i++){
				layer_order.get(i).Evaluate();
			}
			densenetwork.TrainConv(output);
			BackPropagate();
		}
		public float[] GetOutput(float[][][] inputs){
			if(!isBusy()) {
				setState(BUSY_STATE);
				input_layer2d.SetInput(inputs);
				for(int i=0;i<layer_order.size();i++){
					layer_order.get(i).Evaluate();
				}
				setState(IDLE_STATE);
				return densenetwork.GetOutputConv();
			}else {
				System.out.println("NETWORK BUSY");
				return null;
			}
		}
		public float GetScore(float[][][] inputs,float[] expected_output){
			if(!isBusy()) {
				setState(BUSY_STATE);
				input_layer2d.SetInput(inputs);
				for(int i=0;i<layer_order.size();i++){
					layer_order.get(i).Evaluate();
				}
				setState(IDLE_STATE);
				return densenetwork.GetScoreConv(expected_output);
			}else {
				System.out.println("NETWORK BUSY");
				return 0;
			}
		}
		//TODO: 1D
		public void Train1D(float[] input,float[] output){
			densenetwork.Train(input, output);
		}
		public float[] GetOutput1D(float[] inputs){
			return densenetwork.GetOutput(inputs);
		}
		public float GetScore1D(float[] input,float[] output){
			return densenetwork.GetScore(input, output);
		}
		public void BackPropagate(){
			for(int i=layer_order.size()-1;i>=0;i--){
				layer_order.get(i).GetChanges();
			}
			for(int i=layer_order.size()-1;i>=0;i--){
				layer_order.get(i).StoreChanges();
			}
		}
		public void ApplyChanges(float learningrate){
			densenetwork.ApplyChanges(learningrate);
			for(int i=layer_order.size()-1;i>=0;i--){
				layer_order.get(i).ApplyChanges(-learningrate);
			}
		}
		public void printKernel() {
			for(int i=0;i<layer_order.size();i++){
				if(layer_order.get(i).getClass() == Conv_Layer2D.class){
					layer_order.get(i).printKernels();
				}
			}
		}
		public String GetData(){
			String convnetwork_Data = image_no+","+input_size;
			if(IMAGE_INPUT){
				convnetwork_Data += "::::::";
				String convlayer_packet = "";
				boolean buffbool=false;
				for(Layer2D layer:layer_order){
					if(buffbool){
						convlayer_packet += ":::::";
					}else{
						buffbool=true;
					}
					convlayer_packet += layer.GetData();
				}
				convnetwork_Data += convlayer_packet;
			}else{
				convnetwork_Data = "0:::::::"+input_size;
			}
			String densenetwork_data = densenetwork.GetData();
			return convnetwork_Data+":::::::"+densenetwork_data;
		}
		public static ConvNetwork LoadData(String data,LogBackAction logback){
			//TODO: LOAD CONVNETWORK FIRST THEN USE THE GETDENSENETWORK HINT!
			String[] data_packet = data.split(":::::::");
			if(data_packet.length == 2){
				String ConvNetwork_Packet = data_packet[0];
				String DenseNetwork_Packet = data_packet[1];
				String[] ConvNetwork_Packet_Array = ConvNetwork_Packet.split("::::::");
				String ConvNetwork_Init_data =  ConvNetwork_Packet_Array[0];
				String ConvNetwork_Layer_Packet =  ConvNetwork_Packet_Array[1];
				String[] ConvNetwork_Init_data_Array =  ConvNetwork_Init_data.split(",");
				int no_channel = Integer.parseInt(ConvNetwork_Init_data_Array[0]);
				int image_size = Integer.parseInt(ConvNetwork_Init_data_Array[1]);
				ConvNetwork convnetwork = new ConvNetwork(no_channel,image_size);
				String[] ConvNetwork_Layer_Packet_Array = ConvNetwork_Layer_Packet.split(":::::");
				for(int i=0;i<ConvNetwork_Layer_Packet_Array.length;i++){
					String[] Layer_Packet = ConvNetwork_Layer_Packet_Array[i].split("::::");
					int layer_type = Integer.parseInt(Layer_Packet[0]);
					if(layer_type == 0){//POOLING
						int pool_size = Integer.parseInt(Layer_Packet[1]);
						convnetwork.AddPoolLayer(pool_size, MAXPOOLING);
					}else if(layer_type == 1){//CONV_PACKET
						String Layer_Data = Layer_Packet[1];
						String Kernels_Data_Packet = Layer_Packet[2];
						String[] Layer_Data_Array = Layer_Data.split(",");
						int no_kernel = Integer.parseInt(Layer_Data_Array[0]);
						int filter_size = Integer.parseInt(Layer_Data_Array[1]);
						convnetwork.AddConvLayer(no_kernel, filter_size,Kernels_Data_Packet);
					}
				}
				convnetwork.LoadDenseNetwork(DenseNetwork_Packet);
				return convnetwork;
			}else if(data_packet.length == 3){
				int input_size = Integer.parseInt(data_packet[1]);
				String DenseNetwork_Packet = data_packet[2];
				ConvNetwork convnetwork = new ConvNetwork(input_size);
				convnetwork.LoadDenseNetwork(DenseNetwork_Packet);
				return convnetwork;
			}
			System.err.println("ERROR ON LOADING NETWORK");
			return null;
			
		}
	}
	public static Nodes2D[][][] GetInputSpaceNodes(Image2D[] refimage,int x,int y,int kernel_size){
		Nodes2D[][][] returnnodes2d = new Nodes2D[refimage.length][kernel_size][kernel_size];
		for(int i=0;i<refimage.length;i++){
			Nodes2D[][] refnodes2d = refimage[i].nodes2d;
			
			for(int ix=0;ix<kernel_size;ix++){
				for(int iy=0;iy<kernel_size;iy++){
					returnnodes2d[i][ix][iy] = refnodes2d[ix+x][iy+y];
				}
			}
		}
		return returnnodes2d;
	}
	public static Nodes2D[][] GetPoolSpaceNodes(Image2D refimage,int x,int y,int pool_size){
		Nodes2D[][] returnnodes2d = new Nodes2D[pool_size][pool_size];
		Nodes2D[][] refnodes2d = refimage.nodes2d;
		for(int ix=0;ix<pool_size;ix++){
			for(int iy=0;iy<pool_size;iy++){
				returnnodes2d[ix][iy] = refnodes2d[ix+(x*pool_size)][iy+(y*pool_size)];
			}
		}
		return returnnodes2d;
	}
	public static float PoolValue(Nodes2D[][] node,int type_pooling){
		if(type_pooling == MEANPOOLING){
			float sum = 0;
			for(int x=0;x<node.length;x++){
				for(int y=0;y<node[x].length;y++){
					sum += node[x][y].yn;
				}
			}
			return sum/(node.length*node.length);
		}else if(type_pooling == MAXPOOLING){
			float max = 0;
			for(int x=0;x<node.length;x++){
				for(int y=0;y<node[x].length;y++){
					if(node[x][y].yn > max){
						max = node[x][y].yn;
					}
				}
			}
			return max;
		}
		System.out.println("ERROR");
		return 0;
	}
	public static int[] GetMaxCoord(Nodes2D[][] node){
		float max = 0;
		int[] coord = new int[2];
		for(int x=0;x<node.length;x++){
			for(int y=0;y<node[x].length;y++){
				if(node[x][y].yn > max){
					max = node[x][y].yn;
					coord[0] = x;
					coord[1] = y;
				}
			}
		}
		return coord;
	}
	public static float DotProduct(Nodes2D[][] node,Filter filter){
		float sum = 0;
		for(int x=0;x<node.length;x++){
			for(int y=0;y<node[x].length;y++){
				sum += node[x][y].yn*filter.weights[x][y];
			}
		}
		return sum+filter.bias;
	}
	public static float DotProduct3D(Nodes2D[][][] nodes_connected,Kernel kernels){
		float sum=0;
		for(int i=0;i<nodes_connected.length;i++){
			Filter filter = kernels.filters[i];
			sum += DotProduct(nodes_connected[i],filter);
		}
		return sum;
	}
	public static class Layer2D{
		public static final int POOL_LAYER=0,CONV_LAYER=1;
		public Image2D[] image2d;
		Layer2D prev_layer;
		int layer_type = 0;
		int pool_size,no_kernel,filter_size;
		public Layer2D(int nokernel,int kernelsize,Layer2D prev_layer){
			layer_type = CONV_LAYER;
			image2d = new Image2D[nokernel];
			this.no_kernel = nokernel;
			this.filter_size = kernelsize;
			this.prev_layer = prev_layer;
			for (int i = 0; i < image2d.length; i++) {
				image2d[i] = new Image2D(nokernel,kernelsize,prev_layer);
			}
		}
		public void Reset() {
			for(int i=0;i<image2d.length;i++){
				image2d[i].Reset();
			}
		}
		public void LoadData(String kernels_data){
			String[] Kernels_Data_Array = kernels_data.split(":::");
			for(int i=0;i<image2d.length;i++){
				image2d[i].LoadData(Kernels_Data_Array[i]);
			}
		}
		public void printKernels() {
			for(int i=0;i<image2d.length;i++){
				image2d[i].printKernels();
			}
		}
		public Layer2D(int noimage,int size){
			image2d = new Image2D[noimage];
			for (int i = 0; i < image2d.length; i++) {
				image2d[i] = new Image2D(size);
			}
		}
		public Layer2D(int poolsize,Layer2D prev_layer,int type_pooling){
			layer_type = POOL_LAYER;
			this.pool_size = poolsize;
			image2d = new Image2D[prev_layer.image2d.length];
			for (int i = 0; i < image2d.length; i++) {
				image2d[i] = new Image2D(poolsize,type_pooling,prev_layer.image2d[i]);
			}
		}
		public Nodes2D[] Flattened(){
			ArrayList<Nodes2D> nodelist = new ArrayList<Nodes2D>();
			for(int i=0;i<image2d.length;i++){
				Nodes2D[] nodes2d = image2d[i].Flattened();
				for(int v=0;v<nodes2d.length;v++){
					nodelist.add(nodes2d[v]);
				}
			}
			Nodes2D[] returnval = new Nodes2D[nodelist.size()];
			nodelist.toArray(returnval);
			return returnval;
		}
		public void PrintValue(){
			for(int i=0;i<image2d.length;i++){
				image2d[i].PrintValue();
				System.out.println("");
			}
		}
		public void Evaluate(){
			for(int i=0;i<image2d.length;i++){
				image2d[i].Evaluate();
			}
		}
		public void GetChanges(){
			for(int i=0;i<image2d.length;i++){
				image2d[i].GetChanges();
			}
		}
		public void StoreChanges(){
			for(int i=0;i<image2d.length;i++){
				image2d[i].StoreChanges();
			}
		}
		public void ApplyChanges(float learningrate){
			for(int i=0;i<image2d.length;i++){
				image2d[i].ApplyChanges(learningrate);
			}
		}
		public String GetData(){
			String Layer_Packet = "";
			Layer_Packet += layer_type+"::::";
			if(layer_type == POOL_LAYER){
				Layer_Packet += this.pool_size;
			}else if(layer_type == CONV_LAYER){
				Layer_Packet += this.no_kernel+","+this.filter_size+"::::";
				String Kernels_Data_Packet = "";
				boolean buffbool = false;
				for(Image2D image:this.image2d){
					if(buffbool){
						Kernels_Data_Packet += ":::";
					}else{
						buffbool = true;
					}
					Kernels_Data_Packet += image.GetData();
				}
				Layer_Packet += Kernels_Data_Packet;
			}
			return Layer_Packet;
		}
	}
	public static class Image2D{
		public Nodes2D[][] nodes2d;
		public Layer2D prev_layer;
		public Kernel kernels;
		public int poolsize;
		public int type_pooling;
		public Image2D(int nofilter,int kernelsize,Layer2D prev_layer){
			this.prev_layer = prev_layer;
			int prevsize = prev_layer.image2d[0].nodes2d.length;
			int size = prevsize-kernelsize+1;
			nodes2d = new Nodes2D[size][size];
			kernels = new Kernel(prev_layer.image2d.length,kernelsize,this);
			for(int x=0;x<size;x++){
				for(int y=0;y<size;y++){
					Nodes2D[][][] node_connected = GetInputSpaceNodes(prev_layer.image2d,x,y,kernelsize);
					nodes2d[x][y] = new Nodes2D(kernels,node_connected);
				}
			}
		}
		public void Reset() {
			if(kernels != null){
				kernels.Reset();
			}
		}
		public void printKernels() {
			kernels.printFilters();
		}
		public Image2D(int size){
			nodes2d = new Nodes2D[size][size];
			
			for(int x=0;x<size;x++){
				for(int y=0;y<size;y++){
					nodes2d[x][y] = new Nodes2D(0);
				}
			}
		}
		public Image2D(int poolsize,int type_pooling,Image2D image_connected){
			int imagesize = (int)image_connected.nodes2d.length/poolsize;
			this.poolsize = poolsize;
			this.type_pooling = type_pooling;
			this.nodes2d = new Nodes2D[imagesize][imagesize];
			for(int x=0;x<nodes2d.length;x++){
				for(int y=0;y<nodes2d[x].length;y++){
					nodes2d[x][y] = new Nodes2D(GetPoolSpaceNodes(image_connected,x,y,poolsize),type_pooling);
				}
			}
		}
		public Nodes2D[] Flattened(){
			ArrayList<Nodes2D> node2dlist = new ArrayList<Nodes2D>();
			for(int x=0;x<nodes2d.length;x++){
				for(int y=0;y<nodes2d.length;y++){
					node2dlist.add(nodes2d[x][y]);
				}
			}
			Nodes2D[] returnval = new Nodes2D[node2dlist.size()];
			node2dlist.toArray(returnval);
			return returnval;
		}
		public void Evaluate() {
			for(int x=0;x<nodes2d.length;x++){
				for(int y=0;y<nodes2d[0].length;y++){
					nodes2d[x][y].Evaluate();
				}
			}
		}
		public void GetChanges() {
			for(int x=0;x<nodes2d.length;x++){
				for(int y=0;y<nodes2d[0].length;y++){
					nodes2d[x][y].GetChanges();
				}
			}
		}
		public void StoreChanges(){
			for(int x=0;x<nodes2d.length;x++){
				for(int y=0;y<nodes2d[x].length;y++){
					nodes2d[x][y].StoreChanges();
				}
			}
		}
		public void ApplyChanges(float learningrate){
			for(int x=0;x<nodes2d.length;x++){
				for(int y=0;y<nodes2d[x].length;y++){
					nodes2d[x][y].ApplyChanges(learningrate);
				}
			}
		}
		public void PrintValue(){
			for(int y=0;y<nodes2d[0].length;y++){
				for(int x=0;x<nodes2d.length;x++){
					System.out.print(nodes2d[x][y].yn+" ,");
				}
				System.out.println();
			}
		}
		public void SetNodes(float[][] image_data){
			for(int x=0;x<image_data.length;x++){
				for(int y=0;y<image_data.length;y++){
					nodes2d[x][y].yn = image_data[x][y];
					nodes2d[x][y].xn = image_data[x][y];
				}
			}
		}
		public void LoadData(String Kernel_Packet){
			kernels.LoadData(Kernel_Packet);
		}
		public String GetData(){
			return kernels.GetData();
		}
	}
	public static class Filter{
		public float[][] weights;
		public float bias;
		public Kernel kernel;
		public float[][] delta_node_weights;
		public float[][] cum_delta_node_weight;
		public float cum_delta_node_bias = 0;
		public float delta_node_bias = 0;
		public Filter(int size,Kernel kernel){
			this.kernel = kernel;
			weights = new float[size][size];
			delta_node_weights = new float[size][size];
			cum_delta_node_weight = new float[size][size];
			for(int x=0;x<size;x++){
				for(int y=0;y<size;y++){
					weights[x][y] = GetRandomFloat(0,0);
				}
			}
			bias = 0;
		}
		public void StoreChanges(){
			for(int x=0;x<delta_node_weights.length;x++){
				for(int y=0;y<delta_node_weights[0].length;y++){
					cum_delta_node_weight[x][y] += delta_node_weights[x][y];
					delta_node_weights[x][y] = 0;
				}
			}
			cum_delta_node_bias += delta_node_bias;
			delta_node_bias=0;
		}
		public void ApplyChanges(float learningrate){
			for(int x=0;x<delta_node_weights.length;x++){
				for(int y=0;y<delta_node_weights[0].length;y++){
					weights[x][y] += cum_delta_node_weight[x][y]*learningrate;
					cum_delta_node_weight[x][y] = 0;
				}
			}
			bias += cum_delta_node_bias*learningrate;
			cum_delta_node_bias = 0;
		}
		public void printWeights() {
			System.out.print("{");
			for(int y=0;y<weights.length;y++){
				for(int x=0;x<weights[0].length;x++){
					System.out.print(weights[x][y]+", ");
				}
				System.out.println("");
			}
			System.out.print("}");
		}
		public String GetData(){
			String Filter_Data = "";
			//TODO: YOU ADDED SOMETHING HERE 
			Filter_Data += GJMath.GetFloatString(bias,NO_FIGURES);
			for(int x=0;x<weights.length;x++){
				Filter_Data += ":";
				boolean buffbool = false;
				for(int y=0;y<weights[x].length;y++){
					if(buffbool){
						Filter_Data += ",";
					}else{
						buffbool = true;
					}
					//TODO: YOU ADDED SOMETHING HERE
					Filter_Data += GJMath.GetFloatString(weights[x][y],NO_FIGURES);
				}
			}
			return Filter_Data;
		}
		public void LoadData(String Filter_Data){
			String[] Filter_Data_Array = Filter_Data.split(":");
			float bias = Float.parseFloat(Filter_Data_Array[0]);
			for(int x=0;x<weights.length;x++){
				String RowValues = Filter_Data_Array[x+1];
				String[] RowValues_Array = RowValues.split(",");
				for(int y=0;y<weights[x].length;y++){
					weights[x][y] = Float.parseFloat(RowValues_Array[y]);
				}
			}
			this.bias = bias;
		}
		public void Reset() {
			for(int x=0;x<weights.length;x++){
				for(int y=0;y<weights[x].length;y++){
					weights[x][y] = GetRandomFloat(0,0);
				}
			}
			for(int x=0;x<delta_node_weights.length;x++){
				for(int y=0;y<delta_node_weights[0].length;y++){
					cum_delta_node_weight[x][y] = 0;
				}
			}
			cum_delta_node_bias = 0;
			bias = 0;
		}
	}
	public static class Kernel{
		public Filter[] filters;
		public Image2D image2d;
		public Kernel(int nofilter,int kernelsize,Image2D image2d){
			this.image2d = image2d;
			filters = new Filter[nofilter];
			for(int i=0;i<nofilter;i++){
				filters[i] = new Filter(kernelsize,this);
			}
		}
		public void Reset() {
			for(int i=0;i<filters.length;i++){
				filters[i].Reset();
			}
		}
		public void printFilters() {
			System.out.println("Kernel");
			for(int i=0;i<filters.length;i++){
				filters[i].printWeights();
			}
			System.out.println("END Kernel");
		}
		public void StoreChanges(){
			for(int i=0;i<filters.length;i++){
				filters[i].StoreChanges();
			}
		}
		public void ApplyChanges(float learningrate){
			for(int i=0;i<filters.length;i++){
				filters[i].ApplyChanges(learningrate);
			}
		}
		public void LoadData(String Kernel_Packet){
			String[] Filter_Data_Array = Kernel_Packet.split("::");
			for(int i=0;i<filters.length;i++){
				filters[i].LoadData(Filter_Data_Array[i]);
			}
		}
		public String GetData(){
			String Kernel_Packet = "";
			boolean buffbool = false;
			for(Filter filter:filters){
				if(buffbool){
					Kernel_Packet += "::";
				}else{
					buffbool = true;
				}
				Kernel_Packet += filter.GetData();
			}
			return Kernel_Packet;
		}
	}
	public static class Nodes2D{
		public Nodes2D[][][] node_connected;
		public Nodes2D[][] pool_connected;
		public Kernel kernels;
		public float delta_node_yn = 0;
		public float delta_node_xn = 0;
		public float yn=0,xn=0;
		public int type_pooling;
		public int[] maxvalcoord;
		public Nodes2D(Kernel kernels,Nodes2D[][][] node_connected){
			this.kernels = kernels;
			this.node_connected = node_connected;
		}
		public Nodes2D(Nodes2D[][] pool_connected,int type_pooling){
			this.pool_connected = pool_connected;
			this.type_pooling = type_pooling;
		}
		public Nodes2D(float val){
			this.yn = val;
		}
		public void Evaluate(){
			if(kernels != null){//CONVOLUTIONAL
				this.xn = DotProduct3D(node_connected,kernels);
				//this.yn = xn;
				this.yn = RELU(xn);
			}else{//POOLING
				this.xn = PoolValue(pool_connected,type_pooling);
				if(type_pooling == MAXPOOLING){
					maxvalcoord = GetMaxCoord(pool_connected);
				}
				this.yn = this.xn;
			}
		}
		public void GetChanges(){
			if(kernels != null){//CONVOLUTIONAL Change
				GetChangesConv();
			}else{//POOLING Change
				GetChangesPool();
			}
		}
		public void GetChangesConv(){
			for(int i=0;i<node_connected.length;i++){
				for(int x=0;x<node_connected[i].length;x++){
					for(int y=0;y<node_connected[i][x].length;y++){
						node_connected[i][x][y].delta_node_yn += delta_node_xn*kernels.filters[i].weights[x][y];
						//node_connected[i][x][y].delta_node_xn += node_connected[i][x][y].delta_node_yn;
						node_connected[i][x][y].delta_node_xn += node_connected[i][x][y].delta_node_yn*RELUDERIVATIVE(node_connected[i][x][y].xn);
						kernels.filters[i].delta_node_weights[x][y] += delta_node_xn*node_connected[i][x][y].yn;
					}
				}
				//TODO : THE BIAS HERE ARE CHANGED
				kernels.filters[i].delta_node_bias += delta_node_xn;
			}
			//TODO : REMOVE THIS SHT IF IT FUCKED UP
			//ClipChangesConv();
		}
		public void GetChangesPool(){
			if(type_pooling == MEANPOOLING){
				for(int x=0;x<pool_connected.length;x++){
					for(int y=0;y<pool_connected[x].length;y++){
						pool_connected[x][y].delta_node_yn += delta_node_xn/(pool_connected.length*pool_connected.length);
						pool_connected[x][y].delta_node_xn += pool_connected[x][y].delta_node_yn;
					}
				}
			}else if(type_pooling == MAXPOOLING){
				for(int x=0;x<pool_connected.length;x++){
					for(int y=0;y<pool_connected[x].length;y++){
						pool_connected[x][y].delta_node_yn += 0;
						pool_connected[x][y].delta_node_xn += 0;
					}
				}
				pool_connected[maxvalcoord[0]][maxvalcoord[1]].delta_node_yn += delta_node_xn;
				pool_connected[maxvalcoord[0]][maxvalcoord[1]].delta_node_xn += delta_node_xn;
			}
			//TODO : REMOVE THIS SHT IF IT FUCKED UP
			//ClipChangesPool();
		}
		public void StoreChanges(){
			if(this.kernels != null){
				kernels.StoreChanges();
			}
			delta_node_xn=0;
			delta_node_yn=0;
		}
		public void ApplyChanges(float learningrate){
			if(this.kernels != null){
				kernels.ApplyChanges(learningrate);
			}
		}
	}
	public static class Input_Layer2D extends Layer2D{
		public Input_Layer2D(int noofimage,int size){
			super(noofimage,size);
		}
		public void SetInput(float[][][] image){
			for(int i=0;i<image.length;i++){
				image2d[i].SetNodes(image[i]);
			}
		}
	}
	public static class Conv_Layer2D extends Layer2D{
		public Conv_Layer2D(int nokernel,int kernelsize,Layer2D prev_layer){
			super(nokernel,kernelsize,prev_layer);
		}
	}
	public static class Pool_Layer2D extends Layer2D{
		public Pool_Layer2D(int poolsize,Layer2D prev_layer,int type_pooling){
			super(poolsize,prev_layer,type_pooling);
		}
	}
	
	
	
	
	
	
	
	
	
	public static class Network{
		//ACITVATION
		public static final int RELU = 1;
		public static final int TANH = 2;
		public static final int SOFTMAX = 1;
		public static final int NONE = 0;
		//CREATE THE LAYER OBJECTS
		public Input_Layer input_layer;
		public Hidden_Layer[] hidden_layers;
		public Output_Layer output_layer;
		public Softmax_Layer softmax_layer;
		public int no_input_nodes = 0;
		public int no_output_nodes = 0;
		public int no_hidden_layers = 0;
		public int output_type = 0;
		public int output_activation = 0;
		public Layer2D layer2Dout;
		ArrayList<Integer> no_node_layer = new ArrayList<Integer>();
		ArrayList<Integer> layer_activation = new ArrayList<Integer>();
		public Network(Layer2D layer2Dout,int no_hidden_layers,int no_output_nodes,int[] no_node_layer,int output_type){
			//CREATE INPUT LAYER AND INDEX TO -1
			input_layer = new Input_Layer(layer2Dout.Flattened(),this);
			input_layer.index = -1;
			//CREATE HIDDEN AND OUTPUT LAYER
			hidden_layers = new Hidden_Layer[no_hidden_layers];
			output_layer = new Output_Layer(no_output_nodes,NONE,output_type,this);
			softmax_layer = new Softmax_Layer(no_output_nodes,this);
			softmax_layer.index = no_hidden_layers+1;
			softmax_layer.Init();
			//SET NETWORK TO THIS OBJECT
			input_layer.Init();
			this.output_type = output_type;
			//INITIALIZE HIDDEN LAYER AND INDEX
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i] = new Hidden_Layer(no_node_layer[i],RELU,this,DROPOUTRATE);
				hidden_layers[i].index = i;
				hidden_layers[i].network = this;
				hidden_layers[i].Init();
			}
			//INITIALIZE OUTPUT LAYER
			output_layer.index = no_hidden_layers;
			output_layer.Init();
		}
		public void Reset() {
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Reset();
			}
			output_layer.Reset();
		}
		public Network(int no_input_nodes,int no_hidden_layers,int no_output_nodes,int[] no_node_layer,int output_type){
			//CREATE INPUT LAYER AND INDEX TO -1
			input_layer = new Input_Layer(no_input_nodes,this);
			input_layer.index = -1;
			//CREATE HIDDEN AND OUTPUT LAYER
			hidden_layers = new Hidden_Layer[no_hidden_layers];
			output_layer = new Output_Layer(no_output_nodes,NONE,output_type,this);
			softmax_layer = new Softmax_Layer(no_output_nodes,this);
			softmax_layer.index = no_hidden_layers+1;
			softmax_layer.Init();
			//SET NETWORK TO THIS OBJECT
			input_layer.Init();
			this.output_type = output_type;
			//INITIALIZE HIDDEN LAYER AND INDEX
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i] = new Hidden_Layer(no_node_layer[i],RELU,this,DROPOUTRATE);
				hidden_layers[i].index = i;
				hidden_layers[i].network = this;
				hidden_layers[i].Init();
			}
			//INITIALIZE OUTPUT LAYER
			output_layer.index = no_hidden_layers;
			output_layer.Init();
		}
		public Network(String networkdata,Layer2D prev_layer){
			this.layer2Dout = prev_layer;
			this.LoadData(networkdata);
		}
		public Network(String networkdata,int input_size){
			this.no_input_nodes = input_size;
			this.LoadData(networkdata);
		}
		public void ChangeDropout(float dropout){
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].ChangeDropout(dropout);

			}
		}
		//DATA FORMAT
//		WHOLE PACKET
//		NETWORK_DATA:::::HIDDEN_LAYER DATA:::::OUTPUT_LAYER DATA
//
//		NETWORK_DATA PACKET
//		no_input_nodes,no_output_nodes,output_activation,output_type
//
//		HIDDEN_LAYER DATA PACKET
//		HIDDEN_LAYER DATA 1::::HIDDEN_LAYER DATA 2::::HIDDEN_LAYER DATA 3 ...
//
//		LAYER DATA PACKET
//		activation:::NODE1_DATA::NODE2_DATA::NODE3_DATA...
//
//		NODE_DATA PACKET
//		bias:weight1,weight2,weight3,weight4...
		
		public String GetData(){
			String data = "";
			data += no_input_nodes+","+no_output_nodes+","+output_activation+","+output_type+":::::";
			for(int i=0;i<hidden_layers.length;i++){
				if(i!=0){
					data += "::::";
				}
				data += hidden_layers[i].GetData();
			}
			data += ":::::";
			data += output_layer.GetData();
			return data;
		}
		public void LoadData(String data){
			//ORGANIZE THE DATA FIRST AND INITIALIZE A NEW NETWORK
			String[] whole_packet = data.split(":::::");
			String network_data = whole_packet[0];
			String hidden_layer_data = whole_packet[1];
			String output_layer_data = whole_packet[2];
			String[] hidden_layer_data_packet = hidden_layer_data.split("::::");
			String[] network_data_packet = network_data.split(",");
			
			this.no_hidden_layers = hidden_layer_data_packet.length;
			this.no_input_nodes = Integer.parseInt(network_data_packet[0]);
			this.no_output_nodes =  Integer.parseInt(network_data_packet[1]);
			this.output_activation = Integer.parseInt(network_data_packet[2]);
			this.output_type = Integer.parseInt(network_data_packet[3]);
			
			no_node_layer.clear();
			layer_activation.clear();
			for(int i=0;i<hidden_layer_data_packet.length;i++){
				String[] layer_packet = hidden_layer_data_packet[i].split(":::");
				String[] nodes_data = layer_packet[1].split("::");
				layer_activation.add(Integer.parseInt(layer_packet[0]));
				no_node_layer.add(nodes_data.length);
			}
			this.init();
			//LOAD THE ACTUAL DATA
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].LoadData(hidden_layer_data_packet[i]);
			}
			output_layer.LoadData(output_layer_data);
		}
		public void addHiddenLayer(int no_nodes,int activation){
			no_hidden_layers++;
			no_node_layer.add(no_nodes);
			layer_activation.add(activation);
		}
		public Network(int no_input_nodes,int no_output_nodes,int output_activation,int output_type){
			this.no_input_nodes = no_input_nodes;
			this.no_output_nodes = no_output_nodes;
			this.output_type = output_type;
			this.output_activation = output_activation;
		}
		public Network(Layer2D layer2Dout,int no_output_nodes,int output_activation,int output_type){
			this.layer2Dout = layer2Dout;
			this.no_output_nodes = no_output_nodes;
			this.output_type = output_type;
			this.output_activation = output_activation;
		}
		public Network(){
			
		}
		public void init(){
			//CREATE INPUT LAYER AND INDEX TO -1
			if(this.layer2Dout != null){
				input_layer = new Input_Layer(this.layer2Dout.Flattened(),this);
			}else{
				input_layer = new Input_Layer(this.no_input_nodes,this);
			}
			input_layer.index = -1;
			//CREATE HIDDEN AND OUTPUT LAYER
			hidden_layers = new Hidden_Layer[no_hidden_layers];
			output_layer = new Output_Layer(no_output_nodes,output_activation,output_type,this);
			softmax_layer = new Softmax_Layer(no_output_nodes,this);
			softmax_layer.index = no_hidden_layers+1;
			softmax_layer.Init();
			//SET NETWORK TO THIS OBJECT
			input_layer.Init();
			//INITIALIZE HIDDEN LAYER AND INDEX
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i] = new Hidden_Layer(no_node_layer.get(i),layer_activation.get(i),this,DROPOUTRATE);
				hidden_layers[i].index = i;
				hidden_layers[i].network = this;
				hidden_layers[i].Init();
			}
			//INITIALIZE OUTPUT LAYER
			output_layer.index = no_hidden_layers;
			output_layer.Init();
		}
		//GET LAYER
		public Layer GetLayer(int index){
			if(index == -1){
				return input_layer;
			}else if(index >= 0 && index < hidden_layers.length){
				return hidden_layers[index];
			}else if(index == hidden_layers.length){
				return output_layer;
			}else if(index == hidden_layers.length+1){
				return softmax_layer;
			}
			return null;
		}
		public float[] GetOutput(float[] inputs){
			float[] returnval=null;
			input_layer.setValue(inputs);
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate(false);
			}
			output_layer.Evaluate();
			softmax_layer.Evaluate();
			if(output_type == 1){//SOFTMAX
				returnval = new float[softmax_layer.nodes.length];
				for(int i=0;i<softmax_layer.nodes.length;i++){
					returnval[i] = softmax_layer.nodes[i].yn;
				}
			}else if(output_type == 0){
				returnval = new float[output_layer.nodes.length];
				for(int i=0;i<output_layer.nodes.length;i++){
					returnval[i] = output_layer.nodes[i].yn;
				}
			}
			return returnval;
		}
		public float[] GetOutputConv(){
			float[] returnval=null;
			input_layer.GetConvValue();
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate(false);
			}
			output_layer.Evaluate();
			softmax_layer.Evaluate();
			if(output_type == 1){//SOFTMAX+
				returnval = new float[softmax_layer.nodes.length];
				for(int i=0;i<softmax_layer.nodes.length;i++){
					returnval[i] = softmax_layer.nodes[i].yn;
				}
			}else if(output_type == 0){
				System.out.println("OUTPUT TEST");
				returnval = new float[output_layer.nodes.length];
				for(int i=0;i<output_layer.nodes.length;i++){
					returnval[i] = output_layer.nodes[i].yn;
				}
			}
			return returnval;
		}
		public void TrainConv(float[] expected_output){
			input_layer.GetConvValue();
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate(true);
			}
			output_layer.Evaluate();
			softmax_layer.Evaluate();
			BackPropagate(expected_output);
		}
		public void EvaluateCONV(float[] expected_output) {
			input_layer.GetConvValue();
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate(false);
			}
			output_layer.Evaluate();
			softmax_layer.Evaluate();
			System.out.println("OUTPUT VALUE : "+output_layer);
			if(output_type == 1){
				System.out.println("SOFTMAX VAL : "+softmax_layer);
				float sum = 0;
				for(int i=0;i<expected_output.length;i++){
					sum += -Math.log(softmax_layer.nodes[i].yn)*expected_output[i];
				}
				System.out.println("CROSS ENTROPHY : "+sum);
			}else if(output_type == 0){
				float sum = 0;
				for(int i=0;i<expected_output.length;i++){
					sum += (expected_output[i]-output_layer.nodes[i].yn)*(expected_output[i]-output_layer.nodes[i].yn);
				}
				System.out.println("SSR : "+sum);
			}
		}
		//EVALUATE
		public void Evaluate(float[] inputs,float[] expected_output){
			input_layer.setValue(inputs);
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate(false);
			}
			output_layer.Evaluate();
			softmax_layer.Evaluate();
			System.out.println("OUTPUT VALUE : "+output_layer);
			if(output_type == 1){
				System.out.println("SOFTMAX VAL : "+softmax_layer);
				float sum = 0;
				for(int i=0;i<expected_output.length;i++){
					sum += -Math.log(softmax_layer.nodes[i].yn)*expected_output[i];
				}
				System.out.println("CROSS ENTROPHY : "+sum);
			}else if(output_type == 0){
				float sum = 0;
				for(int i=0;i<expected_output.length;i++){
					sum += (expected_output[i]-output_layer.nodes[i].yn)*(expected_output[i]-output_layer.nodes[i].yn);
				}
				System.out.println("SSR : "+sum);
			}
		}
		public float GetScoreConv(float[] expected_output){
			float sum = 0;
			input_layer.GetConvValue();
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate(false);
			}
			output_layer.Evaluate();
			softmax_layer.Evaluate();
			if(output_type == 1){
				for(int i=0;i<expected_output.length;i++){
					sum += -Math.log(softmax_layer.nodes[i].yn)*expected_output[i];
				}
			}else if(output_type == 0){
				for(int i=0;i<expected_output.length;i++){
					sum += (expected_output[i]-output_layer.nodes[i].yn)*(expected_output[i]-output_layer.nodes[i].yn);
				}
			}
			return sum;
		}
		public float GetScore(float[] inputs,float[] expected_output){
			float sum = 0;
			input_layer.setValue(inputs);
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate(false);
			}
			output_layer.Evaluate();
			softmax_layer.Evaluate();
			if(output_type == 1){
				for(int i=0;i<expected_output.length;i++){
					sum += -Math.log(softmax_layer.nodes[i].yn)*expected_output[i];
				}
			}else if(output_type == 0){
				for(int i=0;i<expected_output.length;i++){
					sum += (expected_output[i]-output_layer.nodes[i].yn)*(expected_output[i]-output_layer.nodes[i].yn);
				}
			}
			return sum;
		}
		public void Train(float[] inputs,float[] expected_output){
			input_layer.setValue(inputs);
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate(true);
			}
			output_layer.Evaluate();
			softmax_layer.Evaluate();
			BackPropagate(expected_output);
		}
		//BACKPROPAGATE
		public void BackPropagate(float[] expected_output){
			output_layer.GetChanges(expected_output);//dCE/dOn
			for(int i=hidden_layers.length-1;i>=0;i--){
				hidden_layers[i].GetChanges();
			}
			input_layer.GetChanges();
			output_layer.StoreChanges();
			for(int i=hidden_layers.length-1;i>=0;i--){
				hidden_layers[i].StoreChanges();
			}
		}
		//APPLY CHANGES
		public void ApplyChanges(float learningrate){
			output_layer.ApplyChanges(-learningrate);
			for(int i=hidden_layers.length-1;i>=0;i--){
				hidden_layers[i].ApplyChanges(-learningrate);
			}
		}
	}
	
	public static class Layer{
		public float dropout_rate;
		public int activation = 0;
		Network network;
		int index=-2;
		public Nodes[] nodes;
		public Layer(int no_nodes,Network network,float dropout){
			this.network = network;
			this.dropout_rate = dropout;
			nodes = new Nodes[no_nodes];
		}
		public void Init(){
			int initializer = XAV_INIT;
			if(activation == 1){
				initializer = HE_INIT;
			}
			for(int i=0;i<nodes.length;i++){
				//CHECK IF INDEX IS NOT EQUAL TO INPUT LAYER
				if(index >= 0 && index <= network.hidden_layers.length){
					nodes[i] = new Nodes(nodes.length,network.GetLayer(index-1).nodes.length,initializer);
				}else{
					nodes[i] = new Nodes(nodes.length,0,initializer);
				}
				nodes[i].layer = this;
				nodes[i].Randomize();
			}
		}
		public void LoadData(String layer_data){
			String[] layer_data_packet = layer_data.split(":::");
			this.activation = Integer.parseInt(layer_data_packet[0]);
			String[] nodes_data = layer_data_packet[1].split("::");
			for(int i=0;i<nodes_data.length;i++){
				nodes[i].LoadData(nodes_data[i]);
			}
		}
		public String GetData(){
			String data = "";
			data += activation+":::";
			for(int i=0;i<nodes.length;i++){
				if(i!=0){
					data += "::";
				}
				data+=nodes[i].GetData();
			}
			return data;
		}
		public void Evaluate(boolean training){
			if(training && dropout_rate > 0.01f){
				ArrayList<Nodes> nodes_list = new ArrayList<Nodes>();
				for(Nodes node:nodes){
					nodes_list.add(node);
				}
				Collections.shuffle(nodes_list);
				int portion = (int)(dropout_rate*nodes.length);
				for(int i=0;i<portion;i++){
					nodes_list.get(i).droped_out = true;
				}
				for(int i=0;i<nodes.length;i++){
					nodes[i].Evaluate(activation);
				}
			}else{
				for(int i=0;i<nodes.length;i++){
					nodes[i].Evaluate(activation);
				}
			}
			
		}
		public String toString(){
			String output = "";
			for(int i=0;i<nodes.length;i++){
				output += nodes[i].yn + ", ";
			}
			return output;
		}
		public void StoreChanges(){
			for(int i=0;i<nodes.length;i++){
				nodes[i].StoreChanges();
			}
		}
		public void ApplyChanges(float learningrate){
			for(int i=0;i<nodes.length;i++){
				nodes[i].ApplyChanges(learningrate);
			}
		}
	}
	public static class Input_Layer extends Layer{
		public Nodes2D[] nodes2d;
		public Input_Layer(int no_nodes,Network network) {
			super(no_nodes, network,0);
		}
		public Input_Layer(Nodes2D[] nodes2d,Network network) {
			super(nodes2d.length, network,0);
			this.nodes2d = nodes2d;
		}
		public void Init(){
			int initializer = XAV_INIT;
			if(activation == 1){
				initializer = HE_INIT;
			}
			for(int i=0;i<nodes.length;i++){
				//CHECK IF INDEX IS NOT EQUAL TO INPUT LAYER
				if(index >= 0 && index <= network.hidden_layers.length){
					nodes[i] = new Nodes(nodes.length,network.GetLayer(index-1).nodes.length,initializer);
				}else{
					if(nodes2d != null){
						nodes[i] = new Nodes(nodes2d[i],nodes.length,initializer);
					}else{
						nodes[i] = new Nodes(nodes.length,0,initializer);
					}
					
				}
				nodes[i].layer = this;
				nodes[i].Randomize();
			}
		}
		public void setValue(float[] value){
			if(value.length == nodes.length){
				for(int i=0;i<value.length;i++){
					nodes[i].yn = value[i];
				}
			}else{
				System.out.println("ERROR INPUT VALUE LENGTH AND INPUT NODES LENGTH IS NOT EQUAL!");
			}
		}
		public void GetConvValue(){
			for(int i=0;i<nodes.length;i++){
				nodes[i].xn = nodes2d[i].xn;
				nodes[i].yn = nodes2d[i].yn;
			}
		}
		public void GetChanges(){
			Nodes[] next_nodes = network.GetLayer(index+1).nodes;
			//TODO: YOU CHANGE THIS SHT
			if(nodes[0].nodes2d != null){
				for(int i=0;i<nodes.length;i++){
					float sum=0;
					for(int v=0;v<next_nodes.length;v++){
						sum += next_nodes[v].delta_node_xn*next_nodes[v].weights[i];
					}
					nodes[i].delta_node_yn = sum;
					nodes[i].nodes2d.delta_node_yn = sum;
					nodes[i].delta_node_xn = sum;
					nodes[i].nodes2d.delta_node_xn = sum;
				}
			}
		}
	}
	public static class Hidden_Layer extends Layer{
		public Hidden_Layer(int no_nodes,int activation,Network network,float dropout) {
			super(no_nodes, network,dropout);
			this.activation = activation;
			//
		}
		public void Reset() {
			for(int i=0;i<nodes.length;i++){
				nodes[i].Reset();
			}
			
		}
		public void ChangeDropout(float dropout) {
			this.dropout_rate = dropout;
		}
		public void GetChanges(){
			//dCE/dyn
			Nodes[] next_nodes = network.GetLayer(index+1).nodes;
			Nodes[] prev_nodes = network.GetLayer(index-1).nodes;
			for(int i=0;i<nodes.length;i++){
				if(!nodes[i].droped_out){
					float sum=0;
					for(int v=0;v<next_nodes.length;v++){
						sum += next_nodes[v].delta_node_xn*next_nodes[v].weights[i];
					}
					nodes[i].delta_node_yn = sum;
					float derivative = 1;
					if(activation == 1){ //RELU
						derivative = RELUDERIVATIVE(nodes[i].xn);
					}else if(activation == 2){ //TANH
						derivative = TANHDERIVATIVE(nodes[i].xn);
					}
					nodes[i].delta_node_xn = sum*derivative;
					nodes[i].delta_node_bias = nodes[i].delta_node_xn;
					for(int v=0;v<nodes[i].weights.length;v++){
						nodes[i].delta_node_weight[v] = nodes[i].delta_node_xn*prev_nodes[v].yn;
					}
				}else{
					nodes[i].delta_node_xn = 0;
					nodes[i].delta_node_bias = 0;
					for(int v=0;v<nodes[i].weights.length;v++){
						nodes[i].delta_node_weight[v] = 0;
					}
					nodes[i].droped_out = false;
				}
			}
		}
	}
	public static class Output_Layer extends Layer{
		public int output_type = 0;
		public Output_Layer(int no_nodes,int activation,int output_type,Network network) {
			super(no_nodes, network,0);
			this.activation = activation;
			this.output_type = output_type;
			// 
		}
		public void Reset() {
			for(int i=0;i<nodes.length;i++){
				nodes[i].Reset();
			}
		}
		public void Evaluate(){
			for(int i=0;i<nodes.length;i++){
				nodes[i].Evaluate(activation);
			}
		}
		public void GetChanges(float[] expected_output){
			//GET dCE/dOn
			Nodes[] next_node = network.GetLayer(index+1).nodes;
			Nodes[] prev_nodes = network.GetLayer(index-1).nodes;
			if(output_type == 0){ // NONE SSR
				for(int i=0;i<nodes.length;i++){
					nodes[i].delta_node_yn = -2*(expected_output[i]-nodes[i].yn); 
					float derivative = 1;
					if(activation == 1){ //RELU
						derivative = RELUDERIVATIVE(nodes[i].xn);
					}else if(activation == 2){ //TANH
						derivative = TANHDERIVATIVE(nodes[i].xn);
					}
					nodes[i].delta_node_xn = nodes[i].delta_node_yn*derivative;
					nodes[i].delta_node_bias = nodes[i].delta_node_xn;
					for(int v=0;v<nodes[i].weights.length;v++){
						nodes[i].delta_node_weight[v] = nodes[i].delta_node_xn*prev_nodes[v].yn;
					}
				}
			}else if(output_type == 1){ // SOFTMAX
				for(int i=0;i<nodes.length;i++){
					float sum = 0;
					for(int v=0;v<nodes.length;v++){
						if(v == i){
							sum += expected_output[v]*(next_node[i].yn-1);
						}else{
							sum += expected_output[v]*(next_node[i].yn);
						}
					}
					nodes[i].delta_node_yn = sum;
					float derivative = 1;
					if(activation == 1){ //RELU
						derivative = RELUDERIVATIVE(nodes[i].xn);
					}else if(activation == 2){ //TANH
						derivative = TANHDERIVATIVE(nodes[i].xn);
					}
					nodes[i].delta_node_xn = sum*derivative;
				}
				for(int i=0;i<nodes.length;i++){
					nodes[i].delta_node_bias = nodes[i].delta_node_xn;
					for(int v=0;v<nodes[i].weights.length;v++){
						nodes[i].delta_node_weight[v] =  nodes[i].delta_node_xn*prev_nodes[v].yn;
					}
				}
			}
		}
	}
	public static class Softmax_Layer extends Layer{

		public Softmax_Layer(int no_nodes,Network network) {
			super(no_nodes, network,0);
		}
		public void Evaluate(){
			float sum = 0;
			Nodes[] output_nodes = network.output_layer.nodes;
			for(int i=0;i<output_nodes.length;i++){
				sum += (float)Math.exp(output_nodes[i].yn);
			}
			for(int i=0;i<output_nodes.length;i++){
				this.nodes[i].yn = (float)Math.exp(output_nodes[i].yn)/sum;
			}
		}
	}
	public static class Nodes{
		public Nodes2D nodes2d;
		public float cum_delta_node_bias = 0;
		public float[] cum_delta_node_weight;
		
		public float delta_node_yn = 0;
		public float delta_node_xn = 0;
		public float delta_node_bias = 0;
		public float[] delta_node_weight;
		
		public Layer layer;
		public float xn=0,yn=0,bias=0;
		public float[] weights;
		public boolean droped_out = false;
		int layer_no_nodes,prev_layer_no_nodes;
		int initialization = 0;
		public Nodes(int layer_no_nodes,int prev_layer_no_nodes,int initialization){
			weights = new float[prev_layer_no_nodes];
			delta_node_weight = new float[prev_layer_no_nodes];
			cum_delta_node_weight = new float[prev_layer_no_nodes];
			this.layer_no_nodes = layer_no_nodes;
			this.prev_layer_no_nodes = prev_layer_no_nodes;
			this.initialization = initialization;
			if(initialization == HE_INIT){
				for(int i=0;i<prev_layer_no_nodes;i++){
					weights[i] = GetHEFloat(layer_no_nodes,prev_layer_no_nodes);
				}
			}else if(initialization == XAV_INIT){
				for(int i=0;i<prev_layer_no_nodes;i++){
					weights[i] = GetXAVFloat(prev_layer_no_nodes);
				}
			}
			
		}
		public void Reset() {
				if(initialization == HE_INIT){
					for(int i=0;i<prev_layer_no_nodes;i++){
						weights[i] = GetHEFloat(layer_no_nodes,prev_layer_no_nodes);
					}
				}else if(initialization == XAV_INIT){
					for(int i=0;i<prev_layer_no_nodes;i++){
						weights[i] = GetXAVFloat(prev_layer_no_nodes);
					}
				}
				cum_delta_node_bias = 0;
				for(int i=0;i<cum_delta_node_weight.length;i++){
					cum_delta_node_weight[i] = 0;
				}
				bias=0;
		}
		public Nodes(Nodes2D nodes2d,int layer_no_nodes,int prev_layer_no_nodes){
			this.nodes2d = nodes2d;
			weights = new float[prev_layer_no_nodes];
			delta_node_weight = new float[prev_layer_no_nodes];
			cum_delta_node_weight = new float[prev_layer_no_nodes];
			for(int i=0;i<prev_layer_no_nodes;i++){
				weights[i] = GetHEFloat(layer_no_nodes,prev_layer_no_nodes);
			}
		}
		public void LoadData(String node_data){
			String[] node_data_packet = node_data.split(":");
			this.bias = Float.parseFloat(node_data_packet[0]);
			String[] weights_data = node_data_packet[1].split(",");
			for(int i=0;i<weights.length;i++){
				weights[i] = Float.parseFloat(weights_data[i]);
			}
		}
		public String GetData(){
			String data = "";
			//TODO: YOU ADDED SOMETHING HERE
			data += GJMath.GetFloatString(bias,NO_FIGURES)+":";
			for(int i=0;i<weights.length;i++){
				if(i!=0){
					data += ",";
				}
				//TODO: YOU ADDED SOMETHING HERE
				data += GJMath.GetFloatString(weights[i],NO_FIGURES);
			}
			return data;
		}
		public void Randomize(){
			bias = 0;
		}
		public void Evaluate(int activation){
			//GET PREVIOUS NODES
			if(!droped_out){
				Nodes[] prev_nodes = layer.network.GetLayer(layer.index-1).nodes;
				//COMPUTE FOR Xn
				float xn=0;
				for(int i=0;i<prev_nodes.length;i++){
					xn += prev_nodes[i].yn*weights[i];
				}
				xn+=bias;
				this.xn = xn;
				if(activation == 1){
					this.yn = RELU(xn);
				}else if(activation == 2){
					this.yn = TANH(xn);
				}else if(activation == 0){
					this.yn = xn;
				}
			}else{
				this.yn = 0;
				this.xn = 0;
			}
		}
		public void StoreChanges(){
			cum_delta_node_bias += delta_node_bias;
			for(int i=0;i<cum_delta_node_weight.length;i++){
				cum_delta_node_weight[i] += delta_node_weight[i];
			}
		}
		public void ApplyChanges(float learningrate){
			bias += cum_delta_node_bias*learningrate;
			for(int i=0;i<weights.length;i++){
				weights[i] += cum_delta_node_weight[i]*learningrate;
			}
			cum_delta_node_bias = 0;
			for(int i=0;i<cum_delta_node_weight.length;i++){
				cum_delta_node_weight[i] = 0;
			}
		}
	}
}
