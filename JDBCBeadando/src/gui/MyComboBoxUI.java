package gui;

import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class MyComboBoxUI extends BasicComboBoxUI {
	
	@Override
    protected ComboPopup createPopup() {
        return new BasicComboPopup(comboBox) {
			private static final long serialVersionUID = 1L;

			@Override
            protected JScrollPane createScroller() {
                JScrollPane scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroller.getVerticalScrollBar().setUI(new MyScrollBarUI());
                return scroller;
            }
        };
    }
}
