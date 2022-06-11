package GJSWING;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class GJMenuBar extends JMenuBar implements ActionListener{
	ArrayList<MenuIndex> menu_index = new ArrayList<MenuIndex>();
	GJMenuAction listener;
	public GJMenuBar(GJMenuAction listener){
		this.listener = listener;
	}
	public class MenuIndex{
		int index;
		JMenuItem menu_item;
		public MenuIndex(int index,JMenuItem menu_item){
			this.index = index;
			this.menu_item = menu_item;
		}
	}
	public JMenu AddMenu(String text){
		JMenu menu = new JMenu(text);
		this.add(menu);
		return menu;
	}
	public void AddMenuItem(JMenu menu,int index,String text){
		JMenuItem menu_item = new JMenuItem(text);
		menu_index.add(new MenuIndex(index,menu_item));
		menu_item.setPreferredSize(new Dimension(200,30));
		menu_item.addActionListener(this);
		menu.add(menu_item);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		for(MenuIndex menuindex:menu_index){
			if(menuindex.menu_item == e.getSource()){
				listener.actionPerformed(menuindex.index);
				break;
			}
		}
	}
}
