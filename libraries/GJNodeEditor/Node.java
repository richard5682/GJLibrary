package GJNodeEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Stroke;

import GJNeural.TrainingData;
import GJObject.GJObject;
import GJObject.GJText;

public class Node extends GJObject{
	GJText label;
	boolean isInput;
	Blocks block;
	public NodeData nodedata;
	Node connection_node=null;
	Class output_type;
	Color color = Color.gray;
	public Node(Point point0, int width, int height,String label,Blocks parent,boolean isInput,NodeData nodedata,Class output_type) {
		super(point0, width, height,parent);
		this.isInput = isInput;
		this.nodedata = nodedata;
		this.label = new GJText(new Point(10,0), label.length()*6, 10, label,Color.white,10);
		this.label.parent = this;
		this.block = parent;
		this.output_type = output_type;
		GetColor();
		this.addComponent(this.label);
		// TODO Auto-generated constructor stub
	}
	public Node(Point point0, int width, int height,String label,Blocks parent,boolean isInput,Class output_type) {
		super(point0, width, height,parent);
		this.isInput = isInput;
		this.output_type = output_type;
		this.label = new GJText(new Point(10,0), label.length()*6, 10, label,Color.white,10);
		this.label.parent = this;
		this.block = parent;
		GetColor();
		this.addComponent(this.label);
		// TODO Auto-generated constructor stub
	}
	public void GetColor(){
		if(output_type == TrainingData[].class){
			color = Color.orange;
		}else if(output_type == Double.class || output_type == Integer.class){
			color = Color.red;
		}else if(output_type == Double[].class || output_type == Integer[].class){
			color = Color.MAGENTA;
		}
	}
	public boolean isConnected(){
		if(connection_node != null){
			return true;
		}else{
			return false;
		}
	}
	public void Connect(Node n){
		connection_node = n;
	}
	public void Disconnect(){
		connection_node = null;
	}
	@Override
	public void mouseEntered() {
		// TODO Auto-generated method stub
		this.panel_container.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		this.render();
	}

	@Override
	public void mouseLeave() {
		// TODO Auto-generated method stub
		this.panel_container.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
	public void mouseDoubleClicked(Point pos){
		
	}
	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
//		if(isInput){
//			gj2d.setColor(Color.green);
//		}else{
//			gj2d.setColor(Color.red);
//		}
	
		if(this.MouseInside){
			gj2d.setColor(Color.red);
			gj2d.fillOval(-2, -2, width+4, height+4);
		}else{
			gj2d.setColor(Color.white);
		}
		gj2d.fillOval(-1, -1, width+2, height+2);
		gj2d.setColor(color);
		gj2d.fillOval(0, 0, width, height);
	}

}
