package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.FlowLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

public class LabeledInput extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JComboBox<String> comboBox;
	private String fieldLabel;
	private JScrollPane scrollPane;
	private JList<String> list;
	private int activeType = 1; //1 ha textfield, 2 ha combobox, 3 ha lista
	
	public LabeledInput(String label) {
		fieldLabel=label;
		setSize(new Dimension(600, 32));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel(label);
		lblNewLabel.setPreferredSize(new Dimension(250, 19));
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		add(lblNewLabel);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(textField);
		textField.setColumns(20);
		
		comboBox = new JComboBox<String>();
	    scrollPane = new JScrollPane(list);
	}
	//listat hoz letre a szovegmezo helyen vagy listat aktualizal
	public void switchToList(String[] values) {
		this.remove(scrollPane);
		this.remove(textField);
		this.remove(comboBox);
	    list = new JList<String>(values);
	    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    int rowsVisible = Math.max(1, Math.min(values.length, 6));
	    list.setVisibleRowCount(rowsVisible);
	    list.setFixedCellWidth(250);
	    list.setFixedCellHeight(16);
	    list.setSize(new Dimension(240,Math.max(32, 16*rowsVisible+2*6)));
	    scrollPane = new JScrollPane(list);
	    scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
	    scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10,0));
		this.setSize(new Dimension(600,list.getHeight()));
		this.add(scrollPane);
		activeType=3;
	}
	//comboboxot hoz letre a szovegmezo helyen vagy comboboxot aktualizal
	public void switchToComboBox(String[] values) {
		this.remove(textField);
		this.remove(comboBox);
		this.remove(scrollPane);
		comboBox = new JComboBox<String>(values);
		comboBox.setUI(new MyComboBoxUI());
		comboBox.setBackground(Color.WHITE);
		comboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBox.setPreferredSize(new Dimension(250, 18));
		this.add(comboBox);
		activeType=2;
	}
	public String getFieldLabel() {
		return fieldLabel;
	}
	public JTextField getTextField() {
		return textField;
	}
	public JComboBox<String> getComboBox() {
		return comboBox;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public JList<String> getList() {
		return list;
	}

	public int getActiveType() {
		return activeType;
	}
}
