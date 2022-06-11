package GJNodeEditor;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import GJFile.GJFileLoader;
import GJNeural.ConvTrainer;
import GJNeural.Conv2DNet.ConvNetwork;
import GJNodeEditor.ConvTrainer_Blocks.DataLoad_Runnable;
import GJObject.GJObject;
import GJSWING.GJLoader;
import GJUtil.GJMath;
import GJUtil.GJTutorialFrame;
import GJUtil.LogBackAction;

public class NodeEditor extends GJObject{
	static final int SPACING=20;
	
	public Point start_pressed = new Point(0,0);
	public ArrayList<Blocks> blocks = new ArrayList<Blocks>();
	public ArrayList<Connection> connections = new ArrayList<Connection>();
	
	Point current_dragged_pos;
	
	int block_pressed = -1;
	int block_node_pressed = -1;
	int node_pressed = -1;
	boolean cutting_mode = false;
	LogBackAction consolelogback = null;
	public GJTutorialFrame tutorialframe;
	public NodeEditor(int x, int y,int width, int height,LogBackAction consolelogback) {
		super(new Point(x,y),width,height);
		this.consolelogback = consolelogback;
	}
	public String GetNodeEditorData(String path){
		return GetConnectionsData()+"\n"+GetBlocksData(path);
	}
	public void DeleteAllBlocks(){
		while(blocks.size()>0){
			blocks.get(0).delete();
			blocks.remove(0);
		}
		while(connections.size()>0){
			connections.remove(0);
		}
	}
	public void LoadNodeEditor(String[] data,String path){
		Thread editor_thread = new Thread(new LoadEditor_Runnable(data,path));
		editor_thread.start();
	}
	public class LoadEditor_Runnable implements Runnable{
		String[] data;
		String path;
		public LoadEditor_Runnable(String[] data,String path){
			this.data = data;
			this.path = path;
		}
		@Override
		public void run() {
			GJLoader loader = new GJLoader(null);
			loader.showLoader("Loading Blocks");
			DeleteAllBlocks();
			String connections_data = data[0];
			for(int i=1;i<data.length;i++){
				LoadBlockData(data[i],path);
				loader.setValue((float)i/data.length);
			}
			loader.showLoader("Loading Connections");
			LoadConnectionData(connections_data);
			loader.exitLoader();
			render();
		}
	}
	public void LoadConnectionData(String data){
		String[] node_connection_packet = data.split(":");
		for(int i=0;i<node_connection_packet.length;i++){
			String node_data_packet = node_connection_packet[i];
			String[] node_datas = node_data_packet.split(",");
			int block_out_index = Integer.parseInt(node_datas[0]);
			int block_in_index = Integer.parseInt(node_datas[1]);
			int out_node_index = Integer.parseInt(node_datas[2]);
			int in_node_index = Integer.parseInt(node_datas[3]);
			Node output_node = blocks.get(block_out_index).output_nodes.get(out_node_index);
			Node input_node = blocks.get(block_in_index).input_nodes.get(in_node_index);
			if(output_node.output_type == input_node.output_type && input_node.connection_node == null){
				input_node.Connect(output_node);
				connections.add(new Connection(output_node,input_node));
				input_node.block.BlockConnectionUpdate();
			}
		}
	}
	public void LoadBlockData(String data,String path){
		String[] block_packet = data.split(":::");
		String native_block_packet = block_packet[0];
		String specific_block_packet = block_packet[1];
		Blocks block = Blocks.LoadBlock(native_block_packet,path,specific_block_packet,consolelogback);
		this.AddBlock(block);
		block.LoadBlocksData(specific_block_packet);
	}
	public String GetBlocksData(String path){
		String returnval = "";
		boolean test=false;
		for(Blocks b:blocks){
			if(test){
				returnval+="\n";
			}else{
				test = true;
			}
			returnval += b.GetNativeData()+":::"+b.GetBlocksData(path);
		}
		return returnval;
	}
	public String GetConnectionsData(){
		String returnval = "";
		boolean test=false;
		for(Connection c:connections){
			if(test){
				returnval+=":";
			}else{
				test = true;
			}
			Blocks input_block = c.input_node.block;
			Blocks output_block = c.output_node.block;
			int input_block_index = GetBlockIndex(c.input_node.block);
			int output_block_index = GetBlockIndex(c.output_node.block);
			int input_node_index = input_block.GetInputNodeIndex(c.input_node);
			int output_node_index = output_block.GetOutputNodeIndex(c.output_node);
			returnval += output_block_index+","+input_block_index+","+output_node_index+","+input_node_index;
		}
		return returnval;
	}
	public int GetBlockIndex(Blocks b){
		for(int i=0;i<blocks.size();i++){
			if(b.equals(blocks.get(i))){
				return i;
			}
		}
		return -1;
	}
	public void DeleteBlock(Blocks b){
		for(int i=0;i<blocks.size();i++){
			if(blocks.get(i) == b){
				ArrayList<Connection> ConnectionToRemove = new ArrayList<Connection>();
				for(Connection c:connections){
					if(c.input_node.block == b || c.output_node.block == b){
						ConnectionToRemove.add(c);
					}
				}
				for(Connection c:ConnectionToRemove){
					c.input_node.Disconnect();
					connections.remove(c);
					c.input_node.block.BlockConnectionUpdate();
				}
				blocks.remove(i);
				break;
			}
		}
		this.render();
	}
	public void DeleteBlock_NOUPDATE(Blocks b){
		for(int i=0;i<blocks.size();i++){
			if(blocks.get(i) == b){
				ArrayList<Connection> ConnectionToRemove = new ArrayList<Connection>();
				for(Connection c:connections){
					if(c.input_node.block == b || c.output_node.block == b){
						ConnectionToRemove.add(c);
					}
				}
				for(Connection c:ConnectionToRemove){
					c.input_node.Disconnect();
					connections.remove(c);
				}
				blocks.remove(i);
				break;
			}
		}
		this.render();
	}
	public void AddBlock(int x,int y,Blocks b){
		b.point0.x = x;
		b.point0.y = y;
		b.nodeEditor = this;
		b.panel_container = this.panel_container;
		this.panel_container.addObject(b);
		blocks.add(b);
		this.render();
	}
	public void AddBlock(Blocks b){
		b.nodeEditor = this;
		b.panel_container = this.panel_container;
		this.panel_container.addObject(b);
		blocks.add(b);
		this.render();
	}
	public class Connection{
		Node output_node;
		Node input_node;
		public Connection(Node output_node,Node input_node){
			this.output_node = output_node;
			this.input_node = input_node;
		}
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
		if(button == 1){
			current_dragged_pos = pos;
			if(block_pressed >= 0){ // DRAG BLOCKS
				Point p = blocks.get(block_pressed).point0;
				Point original_pos = blocks.get(block_pressed).blocks_original_pos;
				p.x = pos.x-start_pressed.x+original_pos.x;
				p.y = pos.y-start_pressed.y+original_pos.y;
				this.render();
			}
			if(block_node_pressed >= 0){ // RENDER LINE OF CONNECTION
				this.render();
			}
			if(block_pressed == -1 && block_node_pressed == -1){ // DRAG SCREEN
				for(int i=0;i<blocks.size();i++){
					Point p = blocks.get(i).point0;
					Point original_pos = blocks.get(i).blocks_original_pos;
					p.x = pos.x-start_pressed.x+original_pos.x;
					p.y = pos.y-start_pressed.y+original_pos.y;
				}
				this.render();
			}
		}else if(button == 3){
			current_dragged_pos = pos;
			this.render();
		}
	}

	@Override
	public void mouseReleased(int button, Point pos) {
		// TODO Auto-generated method stub
		if(button == 1){
			if(block_node_pressed>=0){
				for(int i=blocks.size()-1;i>=0;i--){
					if(i != block_node_pressed){
						for(int v=blocks.get(i).input_nodes.size()-1;v>=0;v--){
							if(blocks.get(i).input_nodes.get(v).MouseInside){
								Node output_node = blocks.get(block_node_pressed).output_nodes.get(node_pressed);
								Node input_node = blocks.get(i).input_nodes.get(v);
								if(output_node.output_type == input_node.output_type && input_node.connection_node == null){
									input_node.Connect(output_node);
									connections.add(new Connection(output_node,input_node));
									input_node.block.BlockConnectionUpdate();
								}
								break;
							}
						}
					}
				}
			}
			block_node_pressed = -1;
			node_pressed = -1;
			this.render();
		}else if(button == 3){
			if(cutting_mode){
				boolean intersectionfound = true;
				int removeindex=0;
				while(intersectionfound){
					intersectionfound = false;
					for(int i=0;i<connections.size();i++){
						Connection c = connections.get(i);
						Point p1 = GJMath.Add(c.input_node.GetWorldPoint(),new Point(5,5));
						Point p2 = GJMath.Add(c.output_node.GetWorldPoint(),new Point(5,5));
						if(GJMath.CheckLineIntersection(p1, p2, start_pressed, current_dragged_pos)){
							intersectionfound = true;
							removeindex = i;
						}
					}
					if(intersectionfound){
						Node n = connections.get(removeindex).input_node;
						n.Disconnect();
						connections.remove(removeindex);
						n.block.BlockConnectionUpdate();
					}
				}
				
			}
			cutting_mode = false;
			this.render();
		}
	}

	@Override
	public void mousePressed(int button, Point pos) {
		if(button == 1){
			boolean BLOCKCHOSEN = false;
			for(int i=blocks.size()-1;i>=0;i--){
				if(blocks.get(i).MouseInside){
					this.block_pressed = i;
					blocks.get(block_pressed).SetOriginalPos();
					BLOCKCHOSEN=true;
					break;
				}
			}
			for(int i=blocks.size()-1;i>=0;i--){
				for(int v=blocks.get(i).output_nodes.size()-1;v>=0;v--){
					if(blocks.get(i).output_nodes.get(v).MouseInside){
						this.block_node_pressed = i;
						this.node_pressed = v;
						break;
					}
				}
			}
			start_pressed.x = pos.x;
			start_pressed.y = pos.y;
			if(!BLOCKCHOSEN){
				for(Blocks b:blocks){
					b.SetOriginalPos();
				}
				block_pressed = -1;
			}
		}else if(button == 3){
			start_pressed.x = pos.x;
			start_pressed.y = pos.y;
			cutting_mode = true;
		}
		
	}
	public Node[] GetOutputConnection(Node output_node){
		ArrayList<Node> input_nodes = new ArrayList<Node>();
		for(Connection connection:this.connections){
			if(connection.output_node == output_node){
				input_nodes.add(connection.input_node);
			}
		}
		Node[] returnval = new Node[input_nodes.size()];
		input_nodes.toArray(returnval);
		return returnval;
	}
	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		gj2d.setColor(new Color(20,20,20));
		gj2d.fillRect(0, 0, width, height);
		drawGrid(gj2d);
		drawNodeConnection(gj2d);
		drawCutLine(gj2d);
	}
	public void drawGrid(GJGraphics2D gj2d){
		gj2d.setColor(new Color(40,40,40));
		int no_linex = width/SPACING;
		int no_liney = height/SPACING;
		for(int i=0;i<no_linex;i++){
			gj2d.drawLine(i*SPACING, 0, i*SPACING, height);
		}
		for(int i=0;i<no_liney;i++){
			gj2d.drawLine(0,i*SPACING, width, i*SPACING);
		}
	}
	public void drawNodeConnection(GJGraphics2D gj2d){
		if(node_pressed != -1){
			Blocks b = blocks.get(block_node_pressed);
			Node n = b.output_nodes.get(node_pressed);
			Point p = GJMath.Add(GJMath.Add(n.point0,n.GetParentOffset()),new Point(5,5));
			//gj2d.drawLine(p.x, p.y, this.current_dragged_pos.x, this.current_dragged_pos.y);
			Point p1 = new Point(this.current_dragged_pos.x,this.current_dragged_pos.y);
			int dist = Math.round(GJMath.GetDistance(p,p1)/2);
			if(dist > 100) {
				dist = 100;
			}
			gj2d.setColor(Color.white);
			gj2d.drawCubicBezier(p,new Point(p.x+dist,p.y),new Point(p1.x-dist,p1.y),p1,2f);
			gj2d.setColor(n.color);
			gj2d.drawCubicBezier(p,new Point(p.x+dist,p.y),new Point(p1.x-dist,p1.y),p1,1f);
		}
		for(int i=0;i<connections.size();i++){
			Node output_node = connections.get(i).output_node;
			Node input_node = connections.get(i).input_node;
			Point p = GJMath.Add(GJMath.Add(output_node.point0,output_node.GetParentOffset()),new Point(5,5));
			Point p1 = GJMath.Add(GJMath.Add(input_node.point0,input_node.GetParentOffset()),new Point(5,5));
			int dist = Math.round(GJMath.GetDistance(p,p1)/2);
			if(dist > 100) {
				dist = 100;
			}
			//gj2d.drawLine(p.x, p.y, p1.x, p1.y);
			gj2d.setColor(Color.white);
			gj2d.drawCubicBezier(p,new Point(p.x+dist,p.y),new Point(p1.x-dist,p1.y),p1,2f);
			gj2d.setColor(output_node.color);
			gj2d.drawCubicBezier(p,new Point(p.x+dist,p.y),new Point(p1.x-dist,p1.y),p1,1f);
		}
	}
	public void drawCutLine(GJGraphics2D gj2d){
		if(cutting_mode){
			gj2d.setColor(Color.red);
			gj2d.drawLine(this.start_pressed.x, this.start_pressed.y, this.current_dragged_pos.x, this.current_dragged_pos.y);
		}
	}
	@Override
	public void mouseDoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}
}
