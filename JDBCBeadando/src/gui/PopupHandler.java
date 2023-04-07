package gui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PopupHandler {
	JPanel panel;
	
	public PopupHandler(JPanel panel) {
		super();
		this.panel = panel;
	}
	public void showException(String message) {
		JOptionPane.showMessageDialog(panel, message, "Hiba", 2);
	}
	public void showInformation(String title, String message) {
		JOptionPane.showMessageDialog(panel, message, title, 1);
	}
}
