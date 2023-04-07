package jdbc;

import java.util.ArrayList;

public class Trainer implements Verifiable {
	private int tcode;
	private String name;
	private String email;
	private String expertise;
	private static final int nameLength = 30;
	private static final int emailLength = 30;
	private static final int expertiseLength = 30;
	
	public Trainer(int tcode, String name, String email, String expertise) throws Exception {
		super();
		this.setTcode(tcode);
		this.setName(name);
		this.setEmail(email);
		this.setExpertise(expertise);
	}
	
	public Trainer(ArrayList<String> inputs) throws Exception {
		this(1, inputs.get(0), inputs.get(1), inputs.get(2));
	}


	public Trainer() throws Exception {
		this(999, "Name", "e@e.hu", "");
	}

	@Override
	public String toString() {
		return "Trainer [tcode=" + tcode + ", name=" + name + ", email=" + email + ", expertise=" + expertise + "]";
	}
	public int getTcode() {
		return tcode;
	}
	public void setTcode(int tcode) {
		this.tcode = tcode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) throws Exception {
		if(name.length()<=nameLength && !name.equals("-") && !name.equals("")) {
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
	public String getExpertise() {
		return expertise;
	}
	public void setExpertise(String expertise) throws Exception {
		if(expertise.length()<=expertiseLength) {
			this.expertise = expertise.equals("-") || expertise.equals("") ? null : expertise;
		} else throw new Exception("A szakterület hossza legfeljebb "+expertiseLength+" karakter.");
	}
	public void checkValidity(String tableField, String data) throws Exception {
		tableField = tableField.trim();
			switch(tableField.toUpperCase()) {
			case "NAME":
				this.setName(data);
				break;
			case "EMAIL":
				this.setEmail(data);
				break;
			case "EXPERTISE":
				this.setExpertise(data);
				break;
			default: throw new Exception("A kutyakiképző táblában csak a következő mezők módosíthatók: NAME, EMAIL, EXPERTISE*");
			}
	}
	
	
}
