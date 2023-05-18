package plsql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AmbulanceDB {
	private String connectionString;
	private String password;
	private String userName;
	private DateTimeFormatter dtf;

	public AmbulanceDB(String connectionString, String userName, String password, String format){
		this.connectionString = connectionString;
		this.userName=userName;
		this.password=password;
		this.dtf = DateTimeFormatter.ofPattern(format);
	}
	
	public void readEmts() {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.MENTOS_BEOLVAS(?)}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.execute();
			int success = cstmt.getInt(1);
			if(success==-1) {
				throw new SQLException("Mentősök: Beolvasási hiba.");
			} else if(success!=0) {
				throw new SQLException("Mentősök: Hibás fájlstruktúra a szerveren, "+success+" db rekord beolvasása sikertelen.");
			} else {
				System.out.println("Mentősök beolvasása sikeres.");
			}
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	
	public void readVehicles() {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.AUTO_BEOLVAS(?)}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.execute();
			int success = cstmt.getInt(1);
			if(success==-1) {
				throw new SQLException("Autók: Beolvasási hiba.");
			} else if(success!=0) {
				throw new SQLException("Autók: Hibás fájlstruktúra vagy ismétlődő rendszám a szerveren,"+success+" db rekord beolvasása sikertelen.");
			} else {
				System.out.println("Autók beolvasása sikeres.");
			}
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	public void randomGenerateCall(int num) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.HIVAS_FELTOLT_RANDOM(?,?)}");
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.setInt(1, num);
			cstmt.execute();
			int error = cstmt.getInt(2);
			if(error!=0) {
				throw new SQLException("Hívás: random generálás hiba.");
			}
			System.out.println(num+" db random hívás legenerálása sikeres.");
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	public void randomGenerateOnSite() {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.KIVONULAS_FELTOLT_RANDOM(?)}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.execute();
			int error = cstmt.getInt(1);
			if(error==-1) {
				throw new SQLException("Kivonulás: random generálás hiba.");
			} else if(error!=0) {
				System.out.println("Kivonulás: random generálás: "+error+" db kivonulás nem lett legenerálva");		
			} else System.out.println("Random kivonulások (kivonulást igénylő hívásonként 1 vagy 2) legenerálása sikeres.");
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	public void randomGeneratePersonnel() {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.OSZTAG_FELTOLT_RANDOM(?)}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.execute();
			int error = cstmt.getInt(1);
			if(error==-1) {
				throw new SQLException("Osztag: random generálás hiba.");
			} else if(error!=0) {
				System.out.println("Osztag: random generálás: "+error+" db osztag tagság nem lett legenerálva");		
			} else System.out.println("Random osztagok (kivonulásonként 3) legenerálása sikeres.");
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	
	public void readCallsAndOnSites() {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.HIVAS_BEOLVAS(?)}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.execute();
			int error = cstmt.getInt(1);
			if(error!=0) {
				throw new SQLException("Hívás és kivonulás beolvasás hiba.");
			} else System.out.println("Hívások és kivonulások beolvasása sikeres.");
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	public int auditCall(LocalDateTime time, String phoneNum) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.HIVAS_NAPLOZ(?,?,?)}");
			cstmt.registerOutParameter(3, Types.INTEGER);
			cstmt.setTimestamp(1, java.sql.Timestamp.valueOf(time));
			cstmt.setString(2, phoneNum);
			cstmt.execute();
			int id = cstmt.getInt(3);
			if(id<=0) {
				throw new SQLException("Hívás felviteli hiba.");
			} else System.out.println("Hívás naplózás sikeres.");
			cstmt.close();
			disconnect(conn);
			return id;
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
			disconnect(conn);
			return -1;
		}
	}
	public void detailCall(int id, String name, String postCode, String address, int injuredNum) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.HIVAS_RESZLETEK(?, ?, ?, ?, ?, ?)}");
			cstmt.registerOutParameter(6, Types.INTEGER);
			cstmt.setInt(1, id);
			cstmt.setString(2, name);
			cstmt.setString(3, postCode);
			cstmt.setString(4, address);
			cstmt.setInt(5, injuredNum);
			cstmt.execute();
			int error = cstmt.getInt(6);
			if(error!=0) {
				throw new SQLException("Hívás részlet felvételi hiba."+(error==1 ? " Nincs ilyen azonosítójú hívás.":""));
			} else System.out.println("Hívás részletek felvéve.");
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	public void diszpecserDontes(int id, boolean sendingAmbulance) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.DISZPECSER_DONTES(?, ?, ?)}");
			cstmt.registerOutParameter(3, Types.INTEGER);
			cstmt.setInt(1, id);
			cstmt.setString(2, sendingAmbulance ? "I":"N");
			cstmt.execute();
			int error = cstmt.getInt(3);
			if(error!=0) {
				throw new SQLException("Döntés felvételi hiba."+(error==1 ? " Nincs ilyen azonosítójú hívás.":""));
			} else System.out.println("Döntés rögzítve.");
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	public int auditOnSite(int callId, int vehicleId, LocalDateTime arrivalOnSite, LocalDateTime arrivalBack, int[] emtId) {
		Connection conn = connect();
		try {
			//Kivonulas
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.KIVONULAS_FELVESZ(?, ?, ?, ?, ?)}");
			cstmt.registerOutParameter(5, Types.INTEGER);
			cstmt.setInt(1, callId);
			cstmt.setInt(2, vehicleId);
			cstmt.setTimestamp(3, Timestamp.valueOf(arrivalOnSite));
			cstmt.setTimestamp(4, Timestamp.valueOf(arrivalBack));
			cstmt.execute();
			int id = cstmt.getInt(5);
			if(id==-1) {
				throw new SQLException("Kivonulás felvételi hiba, nem létező azonosító.");
			} else System.out.println("Kivonulás rögzítve.");
			//Osztag tagjai
			cstmt = conn.prepareCall("{call MENTOSZOLGALAT.OSZTAG_FELVESZ(?, ?, ?)}");
			cstmt.registerOutParameter(3, Types.INTEGER);
			cstmt.setInt(1, id);
			int success;
			for(int i: emtId) {
				cstmt.setInt(2, i);
				cstmt.execute();
				success = cstmt.getInt(3);
				if(success!=0) System.out.println("Mentős részvétel felvitel sikertelen: "+i);
			}
			cstmt.close();
			disconnect(conn);
			return id;
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
			disconnect(conn);
			return -1;
		}
	}
	public void filterCallsByTime(LocalDateTime beginning, LocalDateTime end) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.HIVAS_SZURES_IDOSZAK(?, ?, ?)}");
			cstmt.registerOutParameter(3, Types.REF_CURSOR);
			cstmt.setTimestamp(1, Timestamp.valueOf(beginning));
			cstmt.setTimestamp(2, Timestamp.valueOf(end));
			cstmt.execute();
			ResultSet rs = (ResultSet)cstmt.getObject(3);
			System.out.format("%4s%20s%20s%15s%6s%30s%10s%10s%n", "id","időpont", "név", "telefon", "irsz", "cím", "sérültek", "kivonul");
			while(rs.next()) {
				System.out.format("%4d%20s%20s%15s%6s%30s%10d%10s%n", rs.getInt(1), rs.getTimestamp(2).toLocalDateTime().format(dtf), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8));
			}
			rs.close();
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	public void filterCallsByCaller(String nev) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{call MENTOSZOLGALAT.HIVAS_SZURES_NEV(?, ?)}");
			cstmt.registerOutParameter(2, Types.REF_CURSOR);
			cstmt.setString(1, nev);
			cstmt.execute();
			ResultSet rs = (ResultSet)cstmt.getObject(2);
			System.out.format("%4s%20s%20s%15s%6s%30s%10s%10s%n", "id","időpont", "név", "telefon", "irsz", "cím", "sérültek", "kivonul");
			while(rs.next()) {
				System.out.format("%4d%20s%20s%15s%6s%30s%10d%10s%n", rs.getInt(1), rs.getTimestamp(2).toLocalDateTime().format(dtf), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8));
			}
			rs.close();
			cstmt.close();
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	
	public boolean wasOnSite(int id, LocalDateTime beginning, LocalDateTime end) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{?=call MENTOSZOLGALAT.KIKULDETESEN_VOLT_E(?, ?, ?)}");
			cstmt.registerOutParameter(1, Types.VARCHAR);
			cstmt.setInt(2, id);
			cstmt.setTimestamp(3, Timestamp.valueOf(beginning));
			cstmt.setTimestamp(4, Timestamp.valueOf(end));
			cstmt.execute();
			String out = cstmt.getString(1);
			cstmt.close();
			disconnect(conn);
			return out.equals("TRUE") ? true : false;
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
			disconnect(conn);
			return false;
		}
	}
	
	public String getPhoneNumOfEmt(int id) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{?=call MENTOSZOLGALAT.MENTOS_TELEFONSZAM(?)}");
			cstmt.registerOutParameter(1, Types.VARCHAR);
			cstmt.setInt(2, id);
			cstmt.execute();
			String out = cstmt.getString(1);
			if(cstmt.wasNull()) throw new SQLException("Nincs ilyen azonosítójú mentős.");
			cstmt.close();
			disconnect(conn);
			return out;
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
			disconnect(conn);
			return null;
		}
	}
	
	public int numberOfCallsOn(LocalDate day) {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{?=call MENTOSZOLGALAT.HIVASOK_SZAMA(?)}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.setDate(2, Date.valueOf(day));
			cstmt.execute();
			int out = cstmt.getInt(1);
			cstmt.close();
			disconnect(conn);
			return out;
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
			disconnect(conn);
			return -1;
		}
	}
	public int maxOnSiteMinutes() {
		Connection conn = connect();
		try {
			CallableStatement cstmt = conn.prepareCall("{?=call MENTOSZOLGALAT.MAX_KIVONULASI_IDO_PERC}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.execute();
			int out = cstmt.getInt(1);
			cstmt.close();
			disconnect(conn);
			return out;
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
			disconnect(conn);
			return -1;
		}
	}
	public void delete() {
		Connection conn = connect();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("delete from osztagok");
			stmt.execute("delete from kivonulasok");
			stmt.execute("delete from hivasok");
			stmt.execute("delete from mentosok");
			stmt.execute("delete from autok");
			stmt.close();
			disconnect(conn);
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		disconnect(conn);
	}
	public boolean isValidConnection() {
		Connection conn = connect();
		if(conn==null)return false;
		else {
			disconnect(conn);
			return true;
		}
	}
	
	private Connection connect() {
		Connection conn=null;
		try {
			conn=DriverManager.getConnection(connectionString, userName, password);
		} catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return conn;
		
	}
	
	private void disconnect(Connection conn) {
		try {
			if(conn!=null) {
				conn.close();
			}
		} catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public DateTimeFormatter getDtf() {
		return dtf;
	}
}
