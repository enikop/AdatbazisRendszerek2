package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class MyScrollBarUI extends BasicScrollBarUI {
	protected void configureScrollBarColors() {
        this.thumbColor = new Color(210,228,228);
    }
	private JButton createZeroButton() {
		JButton button = new JButton("");
        Dimension zero = new Dimension(0,0);
        button.setPreferredSize(zero);
        button.setMinimumSize(zero);
        button.setMaximumSize(zero);
        return button;
	}
	@Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
    	return createZeroButton();
    }
	
}
