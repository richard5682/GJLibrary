package GJSWING;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class GJTextArea extends JTextArea{
	public GJTextArea(int x,int y,int width,int height){
		this.setBounds(x, y, width, height);
		DefaultCaret caret = (DefaultCaret)this.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	public JScrollPane CreateScrollPane(int x,int y,int width,int height){
		JScrollPane console_scroll = new JScrollPane();
		console_scroll.setViewportView(this);
		console_scroll.setBounds(x,y,width,height);
		return console_scroll;
	}
	public void SetText(String text){
		
	}
}
