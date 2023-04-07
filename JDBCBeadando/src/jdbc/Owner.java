package jdbc;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Owner implements Verifiable {
	private int code;
	private String name; //max 30 char
	private String email; //max 30 char
	private LocalDate dateOfBirth;
	private int hourlyFee;
	private static final int nameLength = 30;
	private static final int emailLength = 30;
	
	public Owner() throws Exception {
		this(9999, "Name", "e@e.hu", LocalDate.now(), 0);
	}
	public Owner(int code, String name, String email, LocalDate dateOfBirth, int hourlyFee) throws Exception {
		super();
		this.setCode(code);
		this.setName(name);
		this.setEmail(email);
		this.setDateOfBirth(dateOfBirth);
		this.setHourlyFee(hourlyFee);
	}
	public Owner(ArrayList<String> inputs) throws Exception {
		super();
		int fee;
		LocalDate birth;
		try {
			if(inputs.get(2).equals("") || inputs.get(2).equals("-")) birth = null;
			else birth = LocalDate.parse(inputs.get(2));
		} catch(DateTimeParseException dtpe) {
			throw new Exception("Hibás dátum vagy dátumformátum.");
		}
		try {
			fee = Integer.parseInt(inputs.get(3).trim());
		} catch(NumberFormatException nfe) {
			throw new Exception("Az óradíj egész szám");
		}
		this.setCode(1);
		this.setName(inputs.get(0));
		this.setEmail(inputs.get(1));
		this.setDateOfBirth(birth);
		this.setHourlyFee(fee);
	}
	@Override
	public String toString() {
		return "Owner [code=" + code + ", name=" + name + ", email=" + email + ", dateOfBirth=" + dateOfBirth
				+ ", hourlyFee=" + hourlyFee + "]";
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) throws Exception {
		if(code>0) {
			this.code = code;
		} else throw new Exception("Az azonosító csak nullánál nagyobb egész érték lehet.");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) throws Exception {
		if(name.length()<=nameLength && !name.equals("-") && !name.equals("") ) {
			this.name = name;
		} else throw new Exception("A név hossza legfeljebb "+nameLength+" karakter. A név mező nem lehet üres.");
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) throws Exception {
		if(email.length()<=emailLength && !email.equals("-") && !email.equals("")) {
			this.email = email;
			if(!EmailChecker.isValid(email)) throw new Exception("Érvénytelen email formátum.");
		} else throw new Exception("Az email hossza legfeljebb "+emailLength+" karakter. Az email mező nem lehet üres.");
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) throws Exception {
		if(dateOfBirth == null) {
			this.dateOfBirth = dateOfBirth;
			return;
		}
		if(dateOfBirth.isAfter(LocalDate.now()) || dateOfBirth.isBefore(LocalDate.of(1900, 1, 1)))
			throw new Exception("A születési dátum 1900. 01. 01. és a mai nap közé eső dátum.");
		this.dateOfBirth = dateOfBirth;
	}
	public int getHourlyFee() {
		return hourlyFee;
	}
	public void setHourlyFee(int hourlyFee) throws Exception {
		if(hourlyFee>=0) {
			this.hourlyFee = hourlyFee;
		} else throw new Exception("Az óradíj nem negatív egész szám.");
	}
	public void checkValidity(String tableField, String data) throws Exception {
		tableField = tableField.trim();
		data = data.trim();
		try {
			switch(tableField.toUpperCase()) {
			case "NAME":
				this.setName(data);
				break;
			case "EMAIL":
				this.setEmail(data);
				break;
			case "DATE_OF_BIRTH":
				this.setDateOfBirth(data.equals("") || data.equals("-") ? null : LocalDate.parse(data));
				break;
			case "HOURLY_FEE":	
				this.setHourlyFee(Integer.parseInt(data));
				break;
			default: throw new Exception("A tulajdonos táblában csak a következő mezők módosíthatók: NAME, EMAIL, DATE_OF_BIRTH*, HOURLY_FEE");
			}
		}catch(NumberFormatException nfe) {
			throw new Exception(tableField+": számértéket adjon meg!");
		}catch(DateTimeParseException dte) {
			throw new Exception(tableField+": helytelen dátumformátum (yyyy-MM-dd)");
		}	
	}
	
}
