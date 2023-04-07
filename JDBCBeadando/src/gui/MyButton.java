package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

public class MyButton extends JButton{
	private static final long serialVersionUID = 1L;
	private Color focusColor = new Color(210,228,228);
    
    MyButton(String s){
        super(s);
        setBackground(Color.WHITE); // set initial background color
        setUI(new BasicButtonUI() {
            public void installDefaults(AbstractButton button) {
                super.installDefaults(button);
                button.setBackground(Color.WHITE);
            }

            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                if (model.isPressed()) {
                    b.setBackground(focusColor);
                    b.setBorder(BorderFactory.createCompoundBorder(
                        	BorderFactory.createLineBorder(Color.GRAY,2),
                        	BorderFactory.createEmptyBorder(8,15,8,15)));
            
                } else if(model.isRollover()){
                	b.setBackground(Color.WHITE);
                	b.setBorder(BorderFactory.createCompoundBorder(
                			BorderFactory.createCompoundBorder(
                					BorderFactory.createEmptyBorder(1,1,1,1),
                					BorderFactory.createLineBorder(Color.GRAY,1)),
                			BorderFactory.createCompoundBorder(
                        			BorderFactory.createLineBorder(focusColor,1),
                        			BorderFactory.createEmptyBorder(6,14,6,14))));
                }
                else {
                    b.setBackground(Color.WHITE);
                    b.setBorder(BorderFactory.createCompoundBorder(
                        	BorderFactory.createEmptyBorder(1,1,1,1),
                			BorderFactory.createCompoundBorder(
                        			BorderFactory.createLineBorder(Color.GRAY, 1),
                        			BorderFactory.createEmptyBorder(7,15,7,15))));
                }
                super.paint(g, c);
            }
        });
        setFocusPainted(false); 
        
    }
}
