package gui;

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class InputPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<LabeledInput> inputFields;
	private MyButton btnInput;
	private JLabel panelTitle;
	
	public InputPanel(String[] values, String title) {
		setLayout(null);
		setBackground(new Color(211,211,211));
		setPreferredSize(new Dimension(950, 478));
		inputFields = new ArrayList<LabeledInput>();
		btnInput = new MyButton("OK");
		panelTitle = new JLabel(title, SwingConstants.CENTER);
		panelTitle.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panelTitle.setBounds(175, 30, 600, 32);
		btnInput.setBounds(800, 430, 60, 30);
		btnInput.setBackground(new Color(248, 248, 255));
		btnInput.setFont(new Font("Arial", Font.PLAIN, 18));
		for(int i=0; i<values.length; i++) {
			inputFields.add(new LabeledInput(values[i]));
			//this.add(inputFields.get(i));
		};
		renderInputPanel();
		
	}
	private void renderInputPanel() {
		int x = 175;
		int y=80;
		this.add(panelTitle);
		for(int i=0; i<inputFields.size(); i++) {
			inputFields.get(i).setBounds(x, y, inputFields.get(i).getWidth() , inputFields.get(i).getHeight());
			this.add(inputFields.get(i));
			y+=50;
		};
		this.add(btnInput);
	}
	public ArrayList<String> getInputData() {
		ArrayList<String> inputValues = new ArrayList<String>();
		for(int i=0; i<inputFields.size(); i++) {
			if(inputFields.get(i).getActiveType()==1) {
				inputValues.add((inputFields.get(i).getTextField().getText()));
			}
			else if(inputFields.get(i).getActiveType()==2) {
				if(inputFields.get(i).getComboBox().getItemCount()>0) inputValues.add((String)inputFields.get(i).getComboBox().getSelectedItem());
				else inputValues.add("");
			}
			else if(inputFields.get(i).getActiveType()==3) {
				List<String> values = inputFields.get(i).getList().getSelectedValuesList();
				String valuesCommaSep = "";
				for(int j=0; j<values.size(); j++) {
					valuesCommaSep+=values.get(j)+",";
				}
				//vegerol plusz vesszot leszedni
				if(!valuesCommaSep.equals("")) valuesCommaSep = valuesCommaSep.substring(0, valuesCommaSep.length()-1);
				inputValues.add(valuesCommaSep);
			}
		};
		return inputValues;
	}
	public void clearInputFields() {
		for(int i=0; i<inputFields.size(); i++) {
			if(inputFields.get(i).getActiveType()==1) {
				inputFields.get(i).getTextField().setText("");
			}
			else if(inputFields.get(i).getActiveType()==2 && inputFields.get(i).getComboBox().getItemCount()>0) {
				inputFields.get(i).getComboBox().setSelectedIndex(0);
			}
			else if(inputFields.get(i).getActiveType()==3) {
				inputFields.get(i).getList().clearSelection();
			}
		};
	}
	public void switchToComboBox(int index, String[] values) {
		this.removeAll();
		inputFields.get(index).switchToComboBox(values);
		renderInputPanel();
	}
	public void switchLastToList(String[] values) {
		this.removeAll();
		inputFields.get(inputFields.size()-1).switchToList(values);
		renderInputPanel();
	}
	public ArrayList<LabeledInput> getInputFields() {
		return inputFields;
	}
	public void setInputFields(ArrayList<LabeledInput> inputFields) {
		this.inputFields = inputFields;
	}
	public MyButton getBtnInput() {
		return btnInput;
	}
	public JLabel getPanelTitle() {
		return panelTitle;
	}
	public void setPanelTitle(String title) {
		this.panelTitle.setText(title);
		renderInputPanel();
	}

}
