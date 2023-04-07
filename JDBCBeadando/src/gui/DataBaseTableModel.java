package gui;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DataBaseTableModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;
	public DataBaseTableModel(ResultSet rs) throws Exception {
			if(rs==null) throw new SQLException("ResultSet nem elérhető.");
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for(int i=1; i<=columnCount; i++) {
				addColumn(rsmd.getColumnName(i));
			}
			Vector<String> row = new Vector<String>();
			while(rs.next()) {
				for(int i=1; i<=columnCount; i++) {
					String s = rs.getString(i);
					row.add(s);
					if(rs.wasNull()) row.set(i-1, "nincs megadva");
				}
				addRow(row);
				row = new Vector<String>();
			}
			rs.close();
			if(!(rs.getStatement() instanceof PreparedStatement)) {
				rs.getStatement().close();
			}
			if(this.getRowCount()==0) {
				throw new Exception("Nincs a specifikációknak megfelelő adat");
			}
	}
	
	
	public boolean 	isCellEditable(int row, int column) {
		return false;
	};
	public Class<?> getColumnClass(int columnIndex){
		return String.class;
	}
}
