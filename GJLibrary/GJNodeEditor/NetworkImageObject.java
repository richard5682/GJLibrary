package GJNodeEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import GJNeural.Conv2DNet;
import GJNeural.Conv2DNet.ConvNetwork;
import GJNeural.Conv2DNet.Conv_Layer2D;
import GJNeural.Conv2DNet.Hidden_Layer;
import GJNeural.Conv2DNet.Image2D;
import GJNeural.Conv2DNet.Input_Layer;
import GJNeural.Conv2DNet.Input_Layer2D;
import GJNeural.Conv2DNet.Layer;
import GJNeural.Conv2DNet.Layer2D;
import GJNeural.Conv2DNet.Network;
import GJNeural.Conv2DNet.Output_Layer;
import GJObject.GJObject;

public class NetworkImageObject extends GJObject{
	ConvNetwork network;
	final int HEIGHT_INPUT_LAYER2D = 200;
	final int HEIGHT_CONV_LAYER2D = 200;
	final int HEIGHT_INPUT_LAYER = 200;
	final int HEIGHT_HIDDEN_LAYER = 200;
	final int HEIGHT_OUTPUT_LAYER = 200;
	final int MAX_IMAGE2D = 5;
	final int SPACING = 7;
	final int IMAGE_SIZE = 150;
	public NetworkImageObject(Point point0, int width, int height,ConvNetwork network) {
		super(point0, width, height);
		this.network = network;
		// TODO Auto-generated constructor stub
	}
	public void drawImage2d(GJGraphics2D gk2d,Image2D[] image2d,String desc,int yoffset) {
		int no_image2d = image2d.length;
		if(no_image2d > MAX_IMAGE2D) {
			no_image2d = MAX_IMAGE2D;
			gj2d.setColor(Color.WHITE);
			gj2d.fillRect(-5, -5+yoffset, IMAGE_SIZE, IMAGE_SIZE);
			gj2d.setColor(Color.BLACK);
			gj2d.drawRect(-5, -5+yoffset, IMAGE_SIZE, IMAGE_SIZE);
			gj2d.setColor(Color.WHITE);
			gj2d.fillRect(-2, -2+yoffset, IMAGE_SIZE, IMAGE_SIZE);
			gj2d.setColor(Color.BLACK);
			gj2d.drawRect(-2, -2+yoffset, IMAGE_SIZE, IMAGE_SIZE);
		}
		for(int i=0;i<no_image2d;i++) {
			gj2d.setColor(Color.WHITE);
			gj2d.fillRect(i*SPACING, i*SPACING+yoffset, IMAGE_SIZE, IMAGE_SIZE);
			gj2d.setColor(Color.BLACK);
			gj2d.drawRect(i*SPACING, i*SPACING+yoffset, IMAGE_SIZE, IMAGE_SIZE);
		}
		int position = (no_image2d-1)*SPACING;
		gj2d.drawCenteredString(desc, new Rectangle(position,position+yoffset-50,IMAGE_SIZE,IMAGE_SIZE));
		int yarrow = yoffset+150;
		int xarrow = 50;
		DrawArrow(gj2d,xarrow,yarrow);
	}
	public void DrawArrow(GJGraphics2D gj2d,int xarrow,int yarrow) {
		gj2d.setColor(Color.white);
		gj2d.fillRect(0+xarrow, 0+yarrow, 50, 50);
		gj2d.setColor(Color.black);
		gj2d.drawLine(0+xarrow, 0+yarrow, 50+xarrow, 0+yarrow);
		gj2d.drawLine(50+xarrow, 0+yarrow, 50+xarrow, 50+yarrow);
		gj2d.drawLine(50+xarrow, 50+yarrow, 70+xarrow, 50+yarrow);
		gj2d.drawLine(70+xarrow, 50+yarrow, 25+xarrow, 90+yarrow);
		gj2d.drawLine(25+xarrow, 90+yarrow, -20+xarrow, 50+yarrow);
		gj2d.drawLine(-20+xarrow, 50+yarrow, 0+xarrow, 50+yarrow);
		gj2d.drawLine(0+xarrow, 50+yarrow, 0+xarrow, 0+yarrow);
	}
	public void DrawDenseLayer(GJGraphics2D gj2d,int yoffset,Input_Layer input_layer,Hidden_Layer[] hidden_layers,Output_Layer output_layer) {
		gj2d.setColor(Color.WHITE);
		gj2d.fillRect(0, yoffset, IMAGE_SIZE*2, IMAGE_SIZE/2);
		gj2d.setColor(Color.black);
		gj2d.drawRect(0, yoffset, IMAGE_SIZE*2, IMAGE_SIZE/2);
		gj2d.drawCenteredString("Flattened\nNo. Nodes: "+input_layer.nodes.length, new Rectangle(0, yoffset-20, IMAGE_SIZE*2, IMAGE_SIZE/2));
		DrawArrow(gj2d,50,yoffset+65);
		yoffset+=IMAGE_SIZE/2 + 100;
		for(int i=0;i<hidden_layers.length;i++) {
			gj2d.setColor(Color.WHITE);
			gj2d.fillRect(0, yoffset, IMAGE_SIZE*2, IMAGE_SIZE/2);
			gj2d.setColor(Color.black);
			gj2d.drawRect(0, yoffset, IMAGE_SIZE*2, IMAGE_SIZE/2);
			gj2d.drawCenteredString("Dense Layer "+i+"\nNo. Nodes: "+hidden_layers[i].nodes.length+"\n Activation Type: "+GetStringAct(hidden_layers[i].activation), new Rectangle(0, yoffset-20, IMAGE_SIZE*2, IMAGE_SIZE/2));
			DrawArrow(gj2d,50,yoffset+65);
			yoffset+=IMAGE_SIZE/2 + 100;
		}
		gj2d.setColor(Color.WHITE);
		gj2d.fillRect(0, yoffset, IMAGE_SIZE*2, IMAGE_SIZE);
		gj2d.setColor(Color.black);
		gj2d.drawRect(0, yoffset, IMAGE_SIZE*2, IMAGE_SIZE);
		gj2d.drawCenteredString("Output Layer\nNo. Nodes: "+output_layer.nodes.length+"\n Activation Type: "+GetStringAct(output_layer.activation), new Rectangle(0, yoffset-20, IMAGE_SIZE*2, IMAGE_SIZE));
		yoffset+=IMAGE_SIZE + 100;
	}
	public String GetStringAct(int act) {
		switch(act) {
			case(Conv2DNet.Network.RELU):
			return "RELU";
			case(Conv2DNet.Network.TANH):
			return "TANH";
			case(Conv2DNet.Network.NONE):
			return "NONE";
		}
		System.err.println("Activation ERROR");
		return null;
	}
	public void DrawImage(GJGraphics2D gj2d) {
		Input_Layer2D input_layer2d = network.input_layer2d; //200
		ArrayList<Layer2D> layers2d = network.layer_order; //200*n
		Network densenetwork = network.densenetwork; 
		Input_Layer input_layer = densenetwork.input_layer;// 200
		Hidden_Layer[] hidden_layers = densenetwork.hidden_layers;// 200*n
		Output_Layer output_layer = densenetwork.output_layer;// 200
		int lasty=0;
		String text = "Input Layer2D\n Channel size: "+input_layer2d.image2d.length+"\n"+input_layer2d.image2d[0].nodes2d.length+"x"+input_layer2d.image2d[0].nodes2d.length;
		drawImage2d(gj2d,input_layer2d.image2d,text,lasty);
		lasty+=250;
		for(int i=0;i<layers2d.size();i++) {
			Layer2D layer2d = layers2d.get(i);
			if(layer2d.image2d[0].kernels != null) {
				text = "Conv Layer "+(i+1)+"\n Filter Size: "+layer2d.image2d[0].kernels.filters[0].weights.length+"\n Filter Number: "+layer2d.image2d.length;
			}else { // POOL
				text = "Pool Layer "+(i+1)+"\n Pool Size: "+layer2d.image2d[0].poolsize;
			}
			
			drawImage2d(gj2d,layer2d.image2d,text,lasty);
			lasty+=250;
		}
		DrawDenseLayer(gj2d,lasty,input_layer,hidden_layers,output_layer);
//		int image_height = 600+(conv_layer2d.size()+hidden_layers.length)*200;
//		image = new BufferedImage(200,image_height,BufferedImage.TYPE_INT_RGB);
//		Graphics2D g2d = (Graphics2D) image.getGraphics();
//		g2d.setColor(Color.white);
//		g2d.fillRect(0, 0, 200, image.getHeight());
//		drawInput2d(g2d,input_layer2d);
//		g2d.dispose();
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

	@Override
	public void mouseDoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		//gj2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
		DrawImage(gj2d);
	}

}
