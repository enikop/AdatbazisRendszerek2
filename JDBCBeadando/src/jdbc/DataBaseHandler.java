package jdbc;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import org.sqlite.SQLiteConfig;

import schemacheck.*;

public class DataBaseHandler {
	private String connectionString;
	private Connection connection;
	private PreparedStatement insertDogsPrepared;
	private PreparedStatement deleteDogsPrepared;
	private PreparedStatement insertOwnersPrepared;
	private PreparedStatement deleteOwnersPrepared;
	private PreparedStatement insertTrainersPrepared;
	private PreparedStatement deleteTrainersPrepared;
	private PreparedStatement insertTrainingsPrepared;
	private PreparedStatement deleteTrainingsPrepared;
	private PreparedStatement insertPresencePrepared;
	private PreparedStatement deletePresencePrepared;
	private PreparedStatement selectDogsYoungerThan;
	private PreparedStatement selectDogsOwnedBy;
	private PreparedStatement selectHeavyDogs;
	private PreparedStatement selectTrainingDataOfDog;

	public DataBaseHandler(String connectionString, String username, String password) throws Exception {
		super();
		this.connectionString = connectionString;
		connection = null;
			//specialis konfig keszitese, hogy az sqlite elvegezze a foreign key ellenorzeseket
			Class.forName("org.sqlite.JDBC");
			SQLiteConfig config = new SQLiteConfig();
			config.enforceForeignKeys(true);
			connection = DriverManager.getConnection(connectionString, config.toProperties());
			if(!logIn(username, password)) {
				connection.close(); 
				throw new Exception("Hibás felhasználónév vagy jelszó.");
			}
			new DogDbSchema().checkMetaData(connection);
			System.out.println("Sikeres adatbázis-kapcsolódás.");
			prepareStatements();
	}
	
	private boolean logIn(String username, String password) throws SQLException {
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(USERNAME) NUM FROM USERS WHERE USERNAME=? AND PASSWORD=?");
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if (rs.getInt(1)==1) return true;
			else return false;
		} catch(SQLException e) {
			throw new SQLException(e.getMessage()+"\nUsers tábla létrehozási parancsok: CREATE TABLE USERS(USERNAME CHAR(30) primary key, PASSWORD CHAR(30) not null)");
		}
		
	}
	
	public void disconnect() {
		try {
			if (connection != null) {
				insertDogsPrepared.close();
				deleteDogsPrepared.close();
				insertOwnersPrepared.close();
				deleteOwnersPrepared.close();
				insertTrainersPrepared.close();
				deleteTrainersPrepared.close();
				insertTrainingsPrepared.close();
				deleteTrainingsPrepared.close();
				insertPresencePrepared.close();
				deletePresencePrepared.close();
				selectDogsOwnedBy.close();
				selectDogsYoungerThan.close();
				selectHeavyDogs.close();
				selectTrainingDataOfDog.close();
				connection.close();
			}
			System.out.println("Sikeres kapcsolatbontás.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void insertDog(Dog d) throws SQLException{
		try {
			insertDogsPrepared.setString(1, d.getName());
			insertDogsPrepared.setNull(2, Types.CHAR);
			if(d.getBreed()!=null)insertDogsPrepared.setString(2, d.getBreed());
			insertDogsPrepared.setNull(3, Types.DATE);
			if(d.getDateOfBirth()!=null) insertDogsPrepared.setString(3, d.getDateOfBirth().toString());
			insertDogsPrepared.setDouble(4, d.getWeight());
			insertDogsPrepared.setString(5, d.getGender());
			insertDogsPrepared.setInt(6, d.getOwnerId());
			insertDogsPrepared.executeUpdate();
		} catch (SQLException e) {
			String message ="Sikertelen adatfelvitel. ";
			String type = e.getMessage().split("]")[0];
			if (type.equals("[SQLITE_CONSTRAINT_PRIMARYKEY")) {
				message+="Már létezik ilyen kódú kutya.";
			}else if (type.equals("[SQLITE_CONSTRAINT_FOREIGNKEY")) {
				message+="Nem létezik tulajdonos a megadott kóddal. Előbb vegye fel a kutyatulajdonos adatait!";
			} else {
				message+=e.getMessage();
			}
			throw new SQLException(message);
		}
	}

	public void insertOwner(Owner o) throws SQLException {
		try {
			insertOwnersPrepared.setString(1, o.getName());
			insertOwnersPrepared.setString(2, o.getEmail());
			insertOwnersPrepared.setNull(3, Types.DATE);
			if(o.getDateOfBirth()!=null)insertOwnersPrepared.setString(3, o.getDateOfBirth().toString());
			insertOwnersPrepared.setInt(4, o.getHourlyFee());
			insertOwnersPrepared.executeUpdate();
		} catch (SQLException e) {
			String message="Sikertelen adatfelvitel. ";
			String type = e.getMessage().split("]")[0];
			if (type.equals("[SQLITE_CONSTRAINT_PRIMARYKEY")) {
				message+="Már létezik ilyen kódú tulajdonos.";
			} else {
				message+=e.getMessage();
			}
			throw new SQLException(message);
		}
	}
	public void insertTrainer(Trainer t) throws SQLException {
		try {
			insertTrainersPrepared.setString(1, t.getName());
			insertTrainersPrepared.setString(2, t.getEmail());
			insertTrainersPrepared.setNull(3, Types.CHAR);
			if(t.getExpertise()!=null)insertTrainersPrepared.setString(3, t.getExpertise());
			insertTrainersPrepared.executeUpdate();
		} catch (SQLException e) {
			String message="Sikertelen adatfelvitel. ";
			String type = e.getMessage().split("]")[0];
			if (type.equals("[SQLITE_CONSTRAINT_PRIMARYKEY")) {
				message+="Már létezik ilyen kódú kiképző.";
			} else {
				message+=e.getMessage();
			}
			throw new SQLException(message);
		}
	}
	//Foglalkozas es resztvevo kutyak felvetele tranzakciokezelessel. Ha a resztvevo kutyaknal (vagy hamarabb) hiba van, nem hajtodik vegre az adatfelvitel
	public void insertTraining(Training t) throws SQLException {
			try {
				connection.setAutoCommit(false);
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT IFNULL(MAX(TID), 99) FROM TRAININGS");
				rs.next();
				int trainingId = rs.getInt(1)+1; 
				rs.close();
				stmt.close();
				insertTrainingsPrepared.setInt(1, trainingId);
				insertTrainingsPrepared.setString(2, t.getDay().toString());
				insertTrainingsPrepared.setInt(3, t.getLength());
				insertTrainingsPrepared.setInt(4, t.getTrainerId());
				insertTrainingsPrepared.executeUpdate();
				int dogId = -1;
				ArrayList<Integer> participants = t.getParticipants();
				for (int i = 0; i < participants.size(); i++) {
					dogId = participants.get(i);
					insertPresencePrepared.setInt(1, trainingId);
					insertPresencePrepared.setInt(2, dogId);
					insertPresencePrepared.executeUpdate();
				}
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				String type = e.getMessage().split("]")[0];
				String message ="Sikertelen adatfelvitel. ";
				if (type.equals("[SQLITE_CONSTRAINT_PRIMARYKEY")) {
					message+="Már létezik ilyen kódú foglalkozás.";
				} else if (type.equals("[SQLITE_CONSTRAINT_FOREIGNKEY")) {
					message+="Nem létezik ilyen kódú kiképző.";
				} else if (type.equals("[SQLITE_CONSTRAINT_UNIQUE")) {
					message+="Már létezik foglalkozás ilyen dátummal.";
				} else {
					message+=e.getMessage();
				}
				throw new SQLException(message);

			} finally {
				connection.setAutoCommit(true);
			}
	}
	public void insertPresence(int trainingId, int dogId) throws SQLException {
		try {
			insertPresencePrepared.setInt(1, trainingId);
			insertPresencePrepared.setInt(2, dogId);
			insertPresencePrepared.executeUpdate();
		} catch(SQLException e) {
			String message="Sikertelen adatfelvitel. ";
			String type = e.getMessage().split("]")[0];
			if (type.equals("[SQLITE_CONSTRAINT_PRIMARYKEY")) {
				message+="Már fel van véve a jelenlét.";
			} else if (type.equals("[SQLITE_CONSTRAINT_FOREIGNKEY")) {
				message+="Nem létező kutya vagy foglalkozás azonosító.";
			} else {
				message+=e.getMessage();
			}
			throw new SQLException(message);
		}
	}

	public void deleteDog(int id) throws SQLException {
		try {
			deleteDogsPrepared.setInt(1, id);
			if (deleteDogsPrepared.executeUpdate() == 0)
				throw new SQLException("Nincs ilyen azonosítójú kutya.");
		} catch (SQLException e) {
			String type = e.getMessage().split("]")[0];
			if (type.equals("[SQLITE_CONSTRAINT_FOREIGNKEY")) {
				throw new SQLException("Előbb törölje a foglalkozásokat, melyeken részt vett a kutya!");
			} else {
				throw new SQLException(e.getMessage());
			}
		}
	}

	public void deleteOwner(int id) throws SQLException {
		try {
			deleteOwnersPrepared.setInt(1, id);
			if (deleteOwnersPrepared.executeUpdate() == 0)
				throw new SQLException("Nincs ilyen azonosítójú tulajdonos.");
		} catch (SQLException e) {
			String type = e.getMessage().split("]")[0];
			if (type.equals("[SQLITE_CONSTRAINT_FOREIGNKEY")) {
				throw new SQLException("Előbb törölje a tulajdonos kutyáit!");
			} else {
				throw new SQLException(e.getMessage());
			}
		}
	}
	public void deleteTrainer(int tcode) throws SQLException{
		try {
			deleteTrainersPrepared.setInt(1, tcode);
			if (deleteTrainersPrepared.executeUpdate() == 0)
				throw new SQLException("Nincs ilyen azonosítójú kiképző.");
		} catch (SQLException e) {
			String type = e.getMessage().split("]")[0];
			if (type.equals("[SQLITE_CONSTRAINT_FOREIGNKEY")) {
				throw new SQLException("Előbb törölje a kiképző foglalkozásait!");
			} else {
				throw new SQLException(e.getMessage());
			}
		}
	}
	//Foglalkozas es a resztvevok torlese tranzakciokent
	public int deleteTraining(int tid) throws SQLException {
		Statement stmt;
		int count=-1;
			try {
				connection.setAutoCommit(false);
				stmt = connection.createStatement();
				int particNum = stmt.executeUpdate("DELETE FROM PRESENCE WHERE TRAINING_ID = "+tid);
				deleteTrainingsPrepared.setInt(1, tid);
				if (deleteTrainingsPrepared.executeUpdate() == 0)
					throw new SQLException("Nincs ilyen azonosítójú foglalkozás.");
				connection.commit();
				count=particNum;
			} catch(SQLException e) {
				connection.rollback();
				throw new SQLException(e.getMessage());
			} finally {
				connection.setAutoCommit(true);
			}
		return count;
	}
	//Adott kutya adott alkalmon resztvetelenek torlese
	public void deletePresence(int training, int dog) throws SQLException {
		try {
			deletePresencePrepared.setInt(1, training);
			deletePresencePrepared.setInt(2, dog);
			if (deletePresencePrepared.executeUpdate() == 0)
				throw new SQLException("Nem volt ilyen kutya az adott foglalkozáson.");
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}
	
	public String queryMetaData() {
		Statement stmt;
		String out ="";
		try {
			DatabaseMetaData dbmd = connection.getMetaData();
			String[] types = { "TABLE" };
			ResultSet tables = dbmd.getTables(null, null, "%", types);
			while(tables.next()) {
				String tablename = tables.getString("TABLE_NAME");
				ResultSet primaryKeys = dbmd.getPrimaryKeys(null, null, tablename);
				out+="Táblanév: "+tablename+"\nPK: ";
				while(primaryKeys.next()) {
					out+=primaryKeys.getString(4);
				}
				out+="\n";
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("select * from "+tablename);
				ResultSetMetaData rsmd = rs.getMetaData();
				out+=String.format( "%-20s%-20s%-20s\n", "Mezőnév", "Mezőtípus", "Lehet null (ha nem PK)");
				out+="-".repeat(60)+"\n";
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					out+=String.format("%-20s%-20s%-20s\n", rsmd.getColumnName(i), rsmd.getColumnTypeName(i), rsmd.isNullable(i));
				}
				out+="\n";
				rs.close();
				primaryKeys.close();
				stmt.close();
			}
			tables.close();
			return out;
			
			
		}catch(SQLException e) {
			return e.getMessage();
		}
	}
	//Megadott fajlba nyomtat resultsetet, 25 karakter/oszlop
	public void printResultSetToFile(ResultSet rs, File file) throws Exception {
		try(FileWriter fw = new FileWriter(file)){
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for(int i=1; i<=columnCount; i++) {
				fw.write(String.format("%-25s", rsmd.getColumnName(i)));
			}
			fw.write("\n");
			fw.write("-".repeat(25*columnCount)+"\n");
			while(rs.next()) {
				for(int i=1; i<=columnCount; i++) {
					String s = rs.getString(i);
					if(rs.wasNull()) s = "nincs megadva";
					fw.write(String.format("%-25s", s));
				}
				fw.write("\n");
			}
		}catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	private void saveTableToTxt(String tablename) throws Exception{
		Statement stmt;
		File file = new File(tablename+".txt");
		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM "+tablename);
		printResultSetToFile(rs, file);
		rs.close();
		stmt.close();
	}
	public void saveDBToTxt() throws Exception {
		try {
			DatabaseMetaData dbmd = connection.getMetaData();
			String[] types = { "TABLE" };
			ResultSet tables = dbmd.getTables(null, null, "%", types);
			while(tables.next()) {
				if(!tables.getString("TABLE_NAME").equals("USERS")) saveTableToTxt(tables.getString("TABLE_NAME"));
			}
			tables.close();
			
		}catch(Exception e) {
			throw new Exception("A DB fájlba mentése sikertelen. "+e.getMessage());
		} 
		
	}
	public ResultSet listAll(String tablename) throws SQLException {
		Statement stmt;
		ResultSet rs = null;
		stmt = connection.createStatement();
		rs = stmt.executeQuery("SELECT * FROM "+tablename);
		return rs;
	}

	// Gazdik altal fizetendo osszegek
	public ResultSet listSumToPay() throws SQLException {
		Statement stmt;
		ResultSet rs = null;
		stmt = connection.createStatement();
		rs = stmt.executeQuery(
				"SELECT CODE, OWNERS.NAME, IFNULL(MAX(HOURLY_FEE)*SUM(LENGTH),0) AMOUNT_DUE, IFNULL(SUM(LENGTH),0) TOTAL_HOURS, MAX(HOURLY_FEE) PERHOUR FROM OWNERS LEFT OUTER JOIN DOGS ON OWNER_ID=CODE  LEFT OUTER JOIN PRESENCE ON ID=DOG_ID LEFT OUTER JOIN TRAININGS ON TRAINING_ID=TID GROUP BY OWNER_ID");
		return rs;
	}

	public ResultSet listOwnersByDogNumber() throws SQLException {
		Statement stmt;
		ResultSet rs = null;
		stmt = connection.createStatement();
		rs = stmt.executeQuery(
				"SELECT OWNERS.NAME, COUNT(DOGS.ID) DOG_NUMBER FROM OWNERS LEFT OUTER JOIN DOGS ON CODE=OWNER_ID GROUP BY OWNERS.CODE ORDER BY DOG_NUMBER DESC");
		return rs;
	}

	public ResultSet listDogsByGenderHeavierThan(String gender, double minweight) throws SQLException {
		ResultSet rs = null;
		selectHeavyDogs.setString(1, gender);
		selectHeavyDogs.setDouble(2, minweight);
		rs = selectHeavyDogs.executeQuery();
		return rs;
	}
	public ResultSet listTrainingDetails(String dogName) throws SQLException {
		ResultSet rs = null;
		selectTrainingDataOfDog.setString(1, dogName);
		rs = selectTrainingDataOfDog.executeQuery();
		return rs;
	}

	public ResultSet listDogsOwnedBy(String name) throws SQLException {
		ResultSet rs = null;
		selectDogsOwnedBy.setString(1, name);
		rs = selectDogsOwnedBy.executeQuery();
		return rs;
	}

	public ResultSet listDogsYoungerThan(int months) throws SQLException {
		ResultSet rs = null;
		selectDogsYoungerThan.setInt(1, months);
		rs = selectDogsYoungerThan.executeQuery();
		return rs;
	}
	//kiirja a resultset adatait a standard outputra, dinamikusan; sorszelesseg 30 karakter, null ertek helyett "nincs megadva"
	public void printResultSet(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for(int i=1; i<=columnCount; i++) {
				System.out.format("%-30s", rsmd.getColumnName(i));
			}
			System.out.format("\n");
			System.out.println("-".repeat(30*columnCount));
			while(rs.next()) {
				for(int i=1; i<=columnCount; i++) {
					String s = rs.getString(i);
					if(rs.wasNull()) s = "nincs megadva";
					System.out.format("%-30s", s);
				}
				System.out.format("\n");
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	public int updateById(String table, String field, String newvalue, int id) throws Exception {
		int count = -1;
		switch(table) {
		case "OWNERS": count=updateOwnerById(field, newvalue, id); break;
		case "DOGS": count=updateDogById(field, newvalue, id); break;
		case "TRAINERS": count=updateTrainerById(field, newvalue, id); break;
		case "TRAININGS": count=updateTrainingById(field, newvalue, id); break;
		}
		return count;
	}
	public int updateTrainerById(String field, String newvalue, int id) throws Exception  {
		new Trainer().checkValidity(field, newvalue);
		PreparedStatement stmt;
		int count=0;
			stmt = connection.prepareStatement("UPDATE TRAINERS SET "+field+"=? WHERE tcode=?");
			stmt.setInt(2, id);
			switch(field.toUpperCase()) {
			case "EXPERTISE":
				if(newvalue.equals("") || newvalue.equals("-")) {
					stmt.setNull(1, Types.CHAR);
					break;
				}
			case "EMAIL":
			case "NAME":
				stmt.setString(1, newvalue);
				break;
			}
			count= stmt.executeUpdate();
			stmt.close();
		return count;
	}
	public int updateTrainingById(String field, String newvalue, int id) throws Exception  {
		new Training().checkValidity(field, newvalue);
		PreparedStatement stmt;
		int count =0;
			stmt = connection.prepareStatement("UPDATE TRAININGS SET "+field+"=? WHERE tid=?");
			stmt.setInt(2, id);
			switch(field.toUpperCase()) {
			case "LENGTH":	
			case "TRAINER_ID":
				stmt.setInt(1, Integer.parseInt(newvalue));
				break;
			case "DAY":
				stmt.setString(1, newvalue);
				break;
			}
			count = stmt.executeUpdate();
			stmt.close();
		return count;
	}
	public int updateDogById(String field, String newvalue, int id) throws Exception  {
		new Dog().checkValidity(field, newvalue);
		PreparedStatement stmt;
		int count = 0;
		stmt = connection.prepareStatement("UPDATE DOGS SET " + field + "=? WHERE id=?");
		stmt.setInt(2, id);
		switch (field.toUpperCase()) {
		case "OWNER_ID":
			stmt.setInt(1, Integer.parseInt(newvalue));
			break;
		case "BREED":
			if (newvalue.equals("") || newvalue.equals("-")) {
				stmt.setNull(1, Types.CHAR);
				break;
			}
		case "NAME":
		case "GENDER":
			stmt.setString(1, newvalue);
			break;
		case "DATE_OF_BIRTH":
			if (newvalue.equals("") || newvalue.equals("-")) {
				stmt.setNull(1, Types.DATE);
			} else
				stmt.setString(1, newvalue);
			break;
		case "WEIGHT":
			stmt.setDouble(1, Double.parseDouble(newvalue));
			break;
		}
		count = stmt.executeUpdate();
		stmt.close();
		return count;
	}
	public int updateOwnerById(String field, String newvalue, int code) throws Exception {
		new Owner().checkValidity(field, newvalue);
		PreparedStatement stmt;
		int count = 0;
			stmt = connection.prepareStatement("UPDATE OWNERS SET "+field+"=? WHERE code = ?");
			stmt.setInt(2, code);
			switch(field.toUpperCase()) {
			case "HOURLY_FEE":	
				stmt.setInt(1, Integer.parseInt(newvalue));
				break;
			case "DATE_OF_BIRTH":
				if(newvalue.equals("") || newvalue.equals("-")) {
					stmt.setNull(1,  Types.DATE);
					break;
				}
			case "NAME":
			case "EMAIL":
				stmt.setString(1, newvalue);
				break;
			}
			count= stmt.executeUpdate();
			stmt.close();
		return count;
	}
	
	public String[] getValidIds(String tablename) throws SQLException{
		ArrayList<String> idList = new ArrayList<>();
		try {
			ResultSet rs = this.listAll(tablename);
			while (rs.next()) {
				idList.add(rs.getString(1));
			}
			rs.close();
			rs.getStatement().close();
		} catch (Exception e) {
			throw new SQLException("Hiba az azonosítók listázásakor.");
		}
		return idList.toArray(new String[idList.size()]);
		
	}

	private void prepareStatements() throws SQLException {
		insertDogsPrepared = connection.prepareStatement("INSERT INTO DOGS (NAME, BREED, DATE_OF_BIRTH, WEIGHT, GENDER, OWNER_ID) VALUES(?, ?, ?, ?, ?, ?)");
		deleteDogsPrepared = connection.prepareStatement("DELETE FROM DOGS WHERE ID = ?");
		insertOwnersPrepared = connection.prepareStatement("INSERT INTO OWNERS (NAME, EMAIL, DATE_OF_BIRTH, HOURLY_FEE) VALUES(?, ?, ?, ?)");
		deleteOwnersPrepared = connection.prepareStatement("DELETE FROM OWNERS WHERE CODE = ?");
		insertTrainersPrepared = connection.prepareStatement("INSERT INTO TRAINERS (NAME, EMAIL, EXPERTISE) VALUES(?, ?, ?)");
		deleteTrainersPrepared = connection.prepareStatement("DELETE FROM TRAINERS WHERE TCODE = ?");
		//itt id is kell, szamolunk hogy egyszerre lehessen resztvevot es foglalkozast felvenni
		insertTrainingsPrepared = connection.prepareStatement("INSERT INTO TRAININGS (TID, DAY, LENGTH, TRAINER_ID) VALUES(?,?, ?, ?)");
		deleteTrainingsPrepared = connection.prepareStatement("DELETE FROM TRAININGS WHERE TID = ?");
		insertPresencePrepared = connection.prepareStatement("INSERT INTO PRESENCE VALUES(?, ?)");
		deletePresencePrepared = connection.prepareStatement("DELETE FROM PRESENCE WHERE TRAINING_ID = ? AND DOG_ID = ?");
		selectDogsOwnedBy = connection.prepareStatement("SELECT DOGS.NAME, DOGS.BREED FROM DOGS INNER JOIN OWNERS ON CODE = OWNER_ID WHERE OWNERS.NAME=?");
		selectDogsYoungerThan =connection.prepareStatement("SELECT NAME, BREED, DATE_OF_BIRTH, (strftime('%Y', DATE())-strftime('%Y', DATE_OF_BIRTH))*12+(strftime('%m', DATE())-strftime('%m', DATE_OF_BIRTH)) AGE_IN_MONTHS FROM DOGS WHERE AGE_IN_MONTHS < ?");
		selectHeavyDogs = connection.prepareStatement("SELECT ID, NAME, WEIGHT FROM DOGS WHERE GENDER=? AND WEIGHT>=?");
		selectTrainingDataOfDog = connection.prepareStatement("SELECT DAY, LENGTH LENGHT_IN_HOURS, TRAINERS.NAME TRAINER, EXPERTISE TOPIC  FROM TRAININGS INNER JOIN TRAINERS ON TCODE=TRAINER_ID INNER JOIN PRESENCE ON TID=TRAINING_ID INNER JOIN DOGS ON ID=DOG_ID WHERE DOGS.NAME=?");
	}

	public String getConnectionString() {
		return connectionString;
	}

}
