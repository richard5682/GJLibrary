package GJSWING;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JFrame;

import GJObject.GJButton;
import GJObject.GJText;

public class GJAlert{
	public static GJFrame default_frame;
	
	GJFrame alert_yesnoframe = new GJFrame("Alert",300,200,0,JFrame.HIDE_ON_CLOSE,false,false);
	GJPanel alert_yesnopanel = new GJPanel(300,200,0,0,null);
	GJFrame alert_okframe = new GJFrame("Alert",300,200,0,JFrame.HIDE_ON_CLOSE,false,false);
	GJPanel alert_okpanel = new GJPanel(300,200,0,0,null);
	GJText ok_text = new GJText(new Point(0,0),300,150,"ALERT");
	GJButton ok_button = new GJButton(new Point(100,130),100,50,Color.black,"OK",new ok_button_action());
	
	GJText yesno_text = new GJText(new Point(0,0),300,150,"ALERT");
	GJButton yes_button = new GJButton(new Point(55,140),80,30,Color.black,"YES",new yes_button_action());
	GJButton no_button = new GJButton(new Point(155,140),80,30,Color.black,"NO",new no_button_action());
	
	GJAlertAction current_action;
	
	GJFrame parent_frame;
	GJFrame[] other_frame;
	public GJAlert(GJFrame frame){
		if(frame == null) {
			parent_frame = default_frame;
		}else {
			parent_frame = frame;
		}
		yesno_text.changeFontSize(10);
		alert_yesnoframe.setAlwaysOnTop(true);
		alert_yesnoframe.setAutoRequestFocus(true);
		alert_yesnoframe.addComponent(alert_yesnopanel);
		alert_yesnopanel.setBackground(new Color(85,85,85));
		yesno_text.changeColor(Color.WHITE);
		yesno_text.changeFontSize(15);
		alert_yesnopanel.addObject(yesno_text);
		alert_yesnopanel.addObject(yes_button);
		alert_yesnopanel.addObject(no_button);
		
		alert_okframe.addComponent(alert_okpanel);
		alert_okpanel.addObject(ok_text);
		alert_okpanel.addObject(ok_button);
		alert_okframe.setAlwaysOnTop(true);
		alert_okframe.setAutoRequestFocus(true);
		
		alert_yesnoframe.hide();
		alert_okframe.hide();
	}
	public void disableFrames(){
		if(parent_frame != null) {
			parent_frame.setEnabled(false);
		}
		if(other_frame != null)
		for(GJFrame frame:other_frame){
			frame.setEnabled(false);
		}
	}
	public void enableFrames(){
		if(parent_frame != null) {
			parent_frame.setEnabled(true);
		}
		if(other_frame != null)
		for(GJFrame frame:other_frame){
			frame.setEnabled(true);
		}
		other_frame = null;
	}
	public void ShowAlert_OK(String text){
		disableFrames();
		ok_text.changeText(text);
		alert_okpanel.repaint();
		alert_okframe.show();
	}
	public void ShowAlert_OK(String text,GJFrame frame){
		other_frame = new GJFrame[1];
		other_frame[0] = frame;
		disableFrames();
		ok_text.changeText(text);
		alert_okpanel.repaint();
		alert_okframe.show();
	}
	public void ShowAlert_YESNO(String text,GJFrame frame,GJAlertAction action){
		other_frame = new GJFrame[1];
		other_frame[0] = frame;
		disableFrames();
		yesno_text.changeText(text);
		this.current_action = action;
		alert_yesnoframe.show();
	}
	public void ShowAlert_OK(String text,GJFrame[] frame){
		other_frame = frame;
		disableFrames();
		ok_text.changeText(text);
		alert_okpanel.repaint();
		alert_okframe.show();
	}
	public void ShowAlert_YESNO(String text,GJFrame[] frame,GJAlertAction action){
		other_frame = frame;
		disableFrames();
		yesno_text.changeText(text);
		this.current_action = action;
		alert_yesnoframe.show();
	}
	public class ok_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			enableFrames();
			alert_okframe.hide();
		}
	}
	public class yes_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			enableFrames();
			current_action.Result(true);
			current_action = null;
			alert_yesnoframe.hide();
		}
	}
	public class no_button_action extends GJButtonAction{
		@Override
		public void mouseClick(int button, Point pos) {
			enableFrames();
			current_action.Result(false);
			current_action = null;
			alert_yesnoframe.hide();
		}
	}
}
