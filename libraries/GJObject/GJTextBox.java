package GJObject;

import java.awt.Color;
import java.awt.Point;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.NumberFormatter;

import GJSWING.GJButtonAction;

public class GJTextBox extends GJObject{
	public NumberFormat numberformat = NumberFormat.getInstance();
	public NumberFormatter numberformatter = new NumberFormatter(numberformat);
	public JLabel label = new JLabel();
	public JFormattedTextField textfield;
	public GJButton add_button;
	public GJButton decrease_button;
	public GJTextBox(int x,int y,int width,int height,String label,int min,int max){
		super(new Point(x,y),width,height);
		numberformatter.setValueClass(Integer.class);
		numberformatter.setMinimum(min);
		numberformatter.setMaximum(max);
		numberformatter.setAllowsInvalid(false);
		textfield  = new JFormattedTextField(numberformatter);
		textfield.setBounds(x+150, y, width, height);
		this.label.setText(label);
		this.label.setBounds(x, y, 150, height);
		add_button = new GJButton(new Point(width+150,0), height/2, height/2, Color.black, "/\\", new add_button_action(this));
		decrease_button = new GJButton(new Point(width+150,height/2), height/2, height/2, Color.black, "\\/", new decrease_button_action(this));
		textfield.setText("0");
		this.addComponent(textfield);
		this.addComponent(decrease_button);
		this.addComponent(add_button);
		this.addComponent(this.label);
	}
	public GJTextBox(int x,int y,int width,int height,String label,Class type,String initial_text){
		super(new Point(x,y),width,height);
		numberformatter.setValueClass(type);
		numberformatter.setAllowsInvalid(false);
		textfield  = new JFormattedTextField(numberformatter);
		textfield.setBounds(x+150, y, width, height);
		this.label.setText(label);
		textfield.setText(initial_text);
		this.label.setBounds(x, y, 150, height);
		add_button = new GJButton(new Point(width+150,0), height/2, height/2, Color.black, "/\\", new add_button_action(this));
		decrease_button = new GJButton(new Point(width+150,height/2), height/2, height/2, Color.black, "\\/", new decrease_button_action(this));
		this.addComponent(textfield);
		this.addComponent(decrease_button);
		this.addComponent(add_button);
		this.addComponent(this.label);
	}
	public GJTextBox(int x,int y,int width,int height,String label){
		super(new Point(x,y),width,height);
		textfield  = new JFormattedTextField();
		textfield.setBounds(x+150, y, width, height);
		this.label.setText(label);
		this.label.setBounds(x, y, 150, height);
		this.addComponent(textfield);
		this.addComponent(this.label);
	}
	public GJTextBox(int x,int y,int width,int height,String label,String initial_text){
		super(new Point(x,y),width,height);
		textfield  = new JFormattedTextField();
		textfield.setBounds(x+150, y, width, height);
		this.label.setText(label);
		this.label.setBounds(x, y, 150, height);
		textfield.setText(initial_text);
		this.addComponent(textfield);
		this.addComponent(this.label);
	}
	public GJTextBox(int x,int y,int width,int height){
		super(new Point(x,y),width,height);
		textfield  = new JFormattedTextField();
		textfield.setBounds(x, y, width, height);
		this.addComponent(textfield);
	}
	public GJTextBox(int x,int y,int width,int height,int min,int max){
		super(new Point(x,y),width,height);
		numberformatter.setValueClass(Integer.class);
		numberformatter.setMinimum(min);
		numberformatter.setMaximum(max);
		numberformatter.setAllowsInvalid(false);
		textfield  = new JFormattedTextField(numberformatter);
		textfield.setBounds(x, y, width, height);
		add_button = new GJButton(new Point(width+150,0), height/2, height/2, Color.black, "/\\", new add_button_action(this));
		decrease_button = new GJButton(new Point(width+150,height/2), height/2, height/2, Color.black, "\\/", new decrease_button_action(this));
		this.addComponent(textfield);
		this.addComponent(decrease_button);
		this.addComponent(add_button);
	}
	
	public static class decrease_button_action extends GJButtonAction{
		GJTextBox textbox;
		public decrease_button_action(GJTextBox textbox){
			this.textbox = textbox;
		}
		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
			textbox.textfield.setText(String.valueOf(Integer.parseInt(textbox.textfield.getText().replace(",", ""))-1));
		}
	}
	public static class add_button_action extends GJButtonAction{
		GJTextBox textbox;
		public add_button_action(GJTextBox textbox){
			this.textbox = textbox;
		}
		@Override
		public void mouseClick(int button, Point pos) {
			// TODO Auto-generated method stub
		
			textbox.textfield.setText(String.valueOf(Integer.parseInt(textbox.textfield.getText().replace(",", ""))+1));
		}
	}
	public int getInt(){
		return (int)getDouble();
	}
	public double getDouble(){
		if(this.textfield.getText() == null) {
			return 0;
		}
		return Double.parseDouble(this.textfield.getText().replace(",", ""));
	}
	public String getText(){
		return this.textfield.getText();
	}
	public void setText(String text){
		this.textfield.setText(text);
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
	public void drawLocal(GJGraphics2D gj2d) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseDoubleClicked(Point pos) {
		// TODO Auto-generated method stub
		
	}
}
