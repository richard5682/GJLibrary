package GJObject;

import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class GJSelector extends GJObject{
	JLabel label = new JLabel();
	JComboBox<String> combobox = new JComboBox<String>();
	public GJSelector(int x,int y,int width,int height,String label,String[] choices){
		super(new Point(x,y),width,height);
		combobox.setBounds(x+150, y, width, height);
		this.label.setText(label);
		this.label.setBounds(x, y, 150, height);
		for(int i=0;i<choices.length;i++){
			combobox.addItem(choices[i]);
		}
		this.addComponent(combobox);
		this.addComponent(this.label);
	}
	public int getSelectedIndex(){
		return combobox.getSelectedIndex();
	}
	public void setSelectedIndex(int index){
		combobox.setSelectedIndex(index);
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
