package GJNodeEditor;

import java.awt.Color;
import java.awt.Point;

import GJObject.GJButton;
import GJObject.GJTextBox;
import GJObject.GJTextBox.add_button_action;
import GJObject.GJTextBox.decrease_button_action;
import GJSWING.GJButtonAction;
import GJObject.GJText;

public class Number_Blocks extends Blocks {
	Node index_output;
	GJTextBox textbox1 = new GJTextBox(10,10,100,50,"Output","0");
	GJText number_label;
	double number=0;
	public GJButton add_button;
	public GJButton decrease_button;
	
	public Number_Blocks() {
		super(new Point(0,0), 120, 40, "Number Blocks",300,200,true,Blocks.NUMBER);
		this.body_trigger = false;
		this.AddSettingsComponent(textbox1);
		add_button = new GJButton(new Point(5,20), 15, 15,  new Color(50,50,50), "/\\", new add_button_action());
		decrease_button = new GJButton(new Point(5,40), 15, 15,  new Color(50,50,50), "\\/", new decrease_button_action());
		index_output = this.AddOutputNode("Number", new index_output_nodedata(), Double.class);
		number_label = new GJText(new Point(0,10), 100, 70, "0", Color.white, 11);
		this.addComponent(number_label);
		this.addComponent(add_button);
		this.addComponent(decrease_button);
//		panel.setBackground(new Color(200,200,200));
//		frame.addComponent(panel);
		// TODO Auto-generated constructor stub
	}
	public class add_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			number++;
			number_label.changeText(String.valueOf(number));
			textbox1.setText(String.valueOf(number));
			BlockConnectionUpdate();
		}
		
	}
	public class decrease_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			number--;
			number_label.changeText(String.valueOf(number));
			textbox1.setText(String.valueOf(number));
			BlockConnectionUpdate();
		}
		
	}
	public class index_output_nodedata extends NodeData<Double>{

		@Override
		public <T> T getValue() {
			// TODO Auto-generated method stub
			return (T)Double.valueOf(number);
		}
	
		
	}
	@Override
	public void BlockUpdate() {
		// TODO Auto-generated method stub
		number = textbox1.getDouble();
		number_label.changeText(String.valueOf(number));
	}

	@Override
	public void drawBlocks(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String GetBlocksData(String path) {
		return String.valueOf(number);
	}

	@Override
	public void LoadBlocksData(String data) {
		textbox1.setText(data);
		number = textbox1.getDouble();
		number_label.changeText(String.valueOf(number));
	}
}
