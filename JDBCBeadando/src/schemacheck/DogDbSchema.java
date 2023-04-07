package schemacheck;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DogDbSchema {
	private ArrayList<TableSchema> dbSchema;
	
	public DogDbSchema() {
		this.dbSchema=new ArrayList<TableSchema>();
		TableSchema ts = new TableSchema("OWNERS");
		ts.addColumn(new FieldSchema("CODE", "INTEGER"));
		ts.addColumn(new FieldSchema("NAME", "CHAR"));
		ts.addColumn(new FieldSchema("EMAIL", "CHAR"));
		ts.addColumn(new FieldSchema("DATE_OF_BIRTH", "DATE"));
		ts.addColumn(new FieldSchema("HOURLY_FEE", "INTEGER"));
		dbSchema.add(ts);
		ts = new TableSchema("TRAINERS");
		ts.addColumn(new FieldSchema("TCODE", "INTEGER"));
		ts.addColumn(new FieldSchema("NAME", "CHAR"));
		ts.addColumn(new FieldSchema("EMAIL", "CHAR"));
		ts.addColumn(new FieldSchema("EXPERTISE", "CHAR"));
		dbSchema.add(ts);
		ts = new TableSchema("DOGS");
		ts.addColumn(new FieldSchema("ID", "INTEGER"));
		ts.addColumn(new FieldSchema("NAME", "CHAR"));
		ts.addColumn(new FieldSchema("BREED", "CHAR"));
		ts.addColumn(new FieldSchema("DATE_OF_BIRTH", "DATE"));
		ts.addColumn(new FieldSchema("WEIGHT", "FLOAT"));
		ts.addColumn(new FieldSchema("GENDER", "CHAR"));
		ts.addColumn(new FieldSchema("OWNER_ID", "INTEGER"));
		dbSchema.add(ts);
		ts = new TableSchema("TRAININGS");
		ts.addColumn(new FieldSchema("TID", "INTEGER"));
		ts.addColumn(new FieldSchema("DAY", "DATE"));
		ts.addColumn(new FieldSchema("LENGTH", "INTEGER"));
		ts.addColumn(new FieldSchema("TRAINER_ID", "INTEGER"));
		dbSchema.add(ts);
		ts = new TableSchema("PRESENCE");
		ts.addColumn(new FieldSchema("TRAINING_ID", "INTEGER"));
		ts.addColumn(new FieldSchema("DOG_ID", "INTEGER"));
		dbSchema.add(ts);
	}
	public void checkMetaData(Connection connection) throws SQLException{
		ArrayList<String> tableList = new ArrayList<String>();
		DatabaseMetaData dbmd = connection.getMetaData();
		String[] types = { "TABLE" };
		ResultSet tables = dbmd.getTables(null, null, "%", types);
		while (tables.next()) {
			tableList.add(tables.getString("TABLE_NAME"));
		}
		tables.close();
		boolean ok = true;
		Statement stmt = connection.createStatement();
		ResultSet rs;
		schemacheck:
		for(int i=0; i<this.dbSchema.size() && ok; i++) {
			if (!tableList.contains(this.dbSchema.get(i).getName())) {
				ok = false; 
				break;
			}
			rs = stmt.executeQuery("SELECT * FROM "+this.dbSchema.get(i).getName());
			ResultSetMetaData rsmd = rs.getMetaData();
			if(rsmd.getColumnCount()!=this.dbSchema.get(i).getColumnCount()) {
				ok = false; 
				break;
			}
			for(int j=0; j<this.dbSchema.get(i).getColumnCount(); j++) {
				if(!(this.dbSchema.get(i).getColumns()).contains(new FieldSchema(rsmd.getColumnName(j+1), rsmd.getColumnTypeName(j+1)))){
					ok = false; 
					break schemacheck;
				}
				
			}
			rs.close();
		}
		stmt.close();
		if(!ok) {
			throw new SQLException("Nem megfelelő az adatbázisséma. Az elkészítéséhez adja ki a schema.txt-ben található CREATE parancsokat!");
		}
	}

}
