package GJNeural;

import java.util.ArrayList;
import java.util.Random;

public class NeuralNet {
	public static float GetRandomFloat(int lb,int hb){
		Random r = new Random();
		return (float)r.nextGaussian();
	}
	public static float RELU(float val){
		float returnval = (float)(Math.log(1+Math.exp(val)));
		return returnval;
	}
	public static float TANH(float val){
		return (float)Math.tanh(val);
	}
	public static float TANHDERIVATIVE(float val){
		return (float)(1-(Math.tanh(val)*Math.tanh(val)));
	}
	public static float RELUDERIVATIVE(float val){
		return (float) (Math.exp(val)/(1+Math.exp(val)));
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
		ArrayList<Integer> no_node_layer = new ArrayList<Integer>();
		ArrayList<Integer> layer_activation = new ArrayList<Integer>();
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
				hidden_layers[i] = new Hidden_Layer(no_node_layer[i],RELU,this);
				hidden_layers[i].index = i;
				hidden_layers[i].network = this;
				hidden_layers[i].Init();
			}
			//INITIALIZE OUTPUT LAYER
			output_layer.index = no_hidden_layers;
			output_layer.Init();
		}
		public Network(String networkdata){
			this.LoadData(networkdata);
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
		public Network(){
			
		}
		public void init(){
			//CREATE INPUT LAYER AND INDEX TO -1
			input_layer = new Input_Layer(no_input_nodes,this);
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
				hidden_layers[i] = new Hidden_Layer(no_node_layer.get(i),layer_activation.get(i),this);
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
				hidden_layers[i].Evaluate();
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
		//EVALUATE
		public void Evaluate(float[] inputs,float[] expected_output){
			input_layer.setValue(inputs);
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate();
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
		public float GetScore(float[] inputs,float[] expected_output){
			float sum = 0;
			input_layer.setValue(inputs);
			for(int i=0;i<hidden_layers.length;i++){
				hidden_layers[i].Evaluate();
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
				hidden_layers[i].Evaluate();
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
		public int activation = 0;
		Network network;
		int index=-2;
		public Nodes[] nodes;
		public Layer(int no_nodes,Network network){
			this.network = network;
			nodes = new Nodes[no_nodes];
		}
		public void Init(){
			for(int i=0;i<nodes.length;i++){
				//CHECK IF INDEX IS NOT EQUAL TO INPUT LAYER
				if(index >= 0 && index <= network.hidden_layers.length){
					nodes[i] = new Nodes(network.GetLayer(index-1).nodes.length);
				}else{
					nodes[i] = new Nodes(0);
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
		public void Evaluate(){
			for(int i=0;i<nodes.length;i++){
				nodes[i].Evaluate(activation);
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
		public Input_Layer(int no_nodes,Network network) {
			super(no_nodes, network);
			// TODO Auto-generated constructor stub
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
	}
	public static class Hidden_Layer extends Layer{
		public Hidden_Layer(int no_nodes,int activation,Network network) {
			super(no_nodes, network);
			this.activation = activation;
			// TODO Auto-generated constructor stub
		}
		public void GetChanges(){
			//dCE/dyn
			Nodes[] next_nodes = network.GetLayer(index+1).nodes;
			Nodes[] prev_nodes = network.GetLayer(index-1).nodes;
			for(int i=0;i<nodes.length;i++){
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
			}
		}
	}
	public static class Output_Layer extends Layer{
		int output_type = 0;
		public Output_Layer(int no_nodes,int activation,int output_type,Network network) {
			super(no_nodes, network);
			this.activation = activation;
			this.output_type = output_type;
			// TODO Auto-generated constructor stub
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
			super(no_nodes, network);
			// TODO Auto-generated constructor stub
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
		public float cum_delta_node_bias = 0;
		public float[] cum_delta_node_weight;
		
		public float delta_node_yn = 0;
		public float delta_node_xn = 0;
		public float delta_node_bias = 0;
		public float[] delta_node_weight;
		
		public Layer layer;
		public float xn=0,yn=0,bias=0;
		public float[] weights;
		public Nodes(int prev_layer_no_nodes){
			weights = new float[prev_layer_no_nodes];
			delta_node_weight = new float[prev_layer_no_nodes];
			cum_delta_node_weight = new float[prev_layer_no_nodes];
			for(int i=0;i<prev_layer_no_nodes;i++){
				weights[i] = GetRandomFloat(0,1);
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
			data += bias+":";
			for(int i=0;i<weights.length;i++){
				if(i!=0){
					data += ",";
				}
				data += weights[i];
			}
			return data;
		}
		public void Randomize(){
			bias = GetRandomFloat(0,1);
		}
		public void Evaluate(int activation){
			//GET PREVIOUS NODES
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
