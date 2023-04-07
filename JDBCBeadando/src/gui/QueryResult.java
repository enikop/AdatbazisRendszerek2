package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;

public class QueryResult extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public QueryResult(JTable table) {
		setTitle("Lekérdezés eredménye");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout());
		Dimension size = new Dimension(Math.min(table.getColumnCount()*150+25, 800), Math.min((table.getRowCount()+2)*table.getRowHeight()+12+table.getTableHeader().getHeight(),200));
		contentPanel.setPreferredSize(size);
		setBounds(100, 100, 900, (int)(size.getHeight()+70));
		setLocationRelativeTo(null);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
		
		contentPanel.setLayout(new BorderLayout(0, 0));
		for(int i=0; i<table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(150);
		}
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		JScrollPane sp = new JScrollPane();
		sp.getVerticalScrollBar().setUI(new MyScrollBarUI());
		sp.getHorizontalScrollBar().setUI(new MyScrollBarUI());
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
		sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));
		sp.setViewportView(table);
		contentPanel.add(sp, BorderLayout.CENTER);
		getContentPane().add(contentPanel);
		
	}

}
