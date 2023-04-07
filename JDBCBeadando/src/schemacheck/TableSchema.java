package schemacheck;

import java.util.ArrayList;

public class TableSchema {
	String name;
	ArrayList<FieldSchema> columns;
	
	public TableSchema(String name) {
		super();
		this.name = name;
		this.columns = new ArrayList<FieldSchema>();
	}
	public void addColumn(FieldSchema f) {
		if(!columns.contains(f)) columns.add(f);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getColumnCount() {
		return columns.size();
	}
	public FieldSchema getColumn(int i) {
		return columns.get(i);
	}
	public ArrayList<FieldSchema> getColumns() {
		return columns;
	}
	
}
