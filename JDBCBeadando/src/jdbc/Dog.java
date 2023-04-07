package jdbc;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Dog implements Verifiable{
	private int id;
	private String name;
	private String breed;
	private LocalDate dateOfBirth;
	private double weight;
	private String gender;
	private int ownerId;
	private static final int nameLength = 30;
	private static final int breedLength = 30;
	
	public Dog() throws Exception {
		this(999, "Name", "", LocalDate.now(), 999, "K", 999);
	}
	public Dog(int id, String name, String breed, LocalDate dateOfBirth, double weight, String gender, int ownerId) throws Exception {
		super();
		this.setId(id);
		this.setName(name);
		this.setBreed(breed);
		this.setDateOfBirth(dateOfBirth);
		this.setWeight(weight);
		this.setGender(gender);
		this.setOwnerId(ownerId);
	}
	public Dog(ArrayList<String> inputs) throws Exception {
		int ownerId;
		double weight;
		LocalDate birth;
		try {
			if(inputs.get(2).equals("") || inputs.get(2).equals("-")) birth = null;
			else birth = LocalDate.parse(inputs.get(2));
		} catch(DateTimeParseException dtpe) {
			throw new Exception("Hibás dátum vagy dátumformátum.");
		}
		try {
			weight = Double.parseDouble(inputs.get(3).trim());
		} catch(NumberFormatException nfe) {
			throw new Exception("A tömeg lebegőpontos szám.");
		}
		try {
			ownerId = Integer.parseInt(inputs.get(5).trim());
		} catch(NumberFormatException nfe) {
			throw new Exception("A tulajdonos azonosítója egész szám.");
		}
		
		this.setId(1);
		this.setName(inputs.get(0));
		this.setBreed(inputs.get(1));
		this.setDateOfBirth(birth);
		this.setWeight(weight);
		this.setGender(inputs.get(4).split(" ")[0]);
		this.setOwnerId(ownerId);
	}
	
	@Override
	public String toString() {
		return "Dog [id=" + id + ", name=" + name + ", breed=" + breed + ", dateOfBirth=" + dateOfBirth + ", weight="
				+ weight + ", gender=" + gender + ", ownerId=" + ownerId + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) throws Exception {
		if(id>0) {
			this.id = id;
		} else throw new Exception("Az azonosító csak nullánál nagyobb egész érték lehet.");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) throws Exception {
		if(name.length()<=nameLength && !name.equals("-") && !name.equals("")) {
			this.name = name;
		} else throw new Exception("A név hossza legfeljebb "+nameLength+" karakter. A név mező nem lehet üres.");
	}
	public String getBreed() {
		return breed;
	}
	public void setBreed(String breed) throws Exception {
		if(breed.length()<=breedLength) {
			this.breed = breed.equals("-") ? null : breed;
		} else throw new Exception("A fajta hossza legfeljebb "+breedLength+" karakter.");
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) throws Exception {
		if(dateOfBirth == null) {
			this.dateOfBirth = dateOfBirth;
			return;
		}
		if(dateOfBirth.isAfter(LocalDate.now()) || dateOfBirth.isBefore(LocalDate.of(1990, 1, 1)))
				throw new Exception("A születési dátum 1990. 01. 01. és a mai nap közé eső dátum.");
		this.dateOfBirth = dateOfBirth;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) throws Exception {
		if(weight > 0) {
			this.weight = weight;
		} else throw new Exception("A tömeg nullánál nagyobb.");
	}
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) throws Exception {
		if(ownerId > 0) {
			this.ownerId = ownerId;
		} else throw new Exception("A tulajdonos azonosítója nullánál nagyobb egész szám.");
	}
	
	public void checkValidity(String tableField, String data) throws Exception {
		tableField = tableField.trim();
		data = data.trim();
		try {
			switch(tableField.toUpperCase()) {
			case "NAME":
				this.setName(data);
				break;
			case "BREED":
				this.setBreed(data);
				break;
			case "DATE_OF_BIRTH":
				this.setDateOfBirth(data.equals("") || data.equals("-")? null : LocalDate.parse(data));
				break;
			case "WEIGHT":
				this.setWeight(Double.parseDouble(data));
				break;
			case "GENDER":
				this.setGender(data);
				break;
			case "OWNER_ID":
				this.setOwnerId(Integer.parseInt(data));
				break;
			default: throw new Exception("A kutya táblában csak a következő mezők módosíthatók: NAME, BREED*, DATE_OF_BIRTH*, WEIGHT, GENDER, OWNER_ID");
			}
		}catch(NumberFormatException nfe) {
			throw new Exception(tableField+": számértéket adjon meg!");
		}catch(DateTimeParseException dte) {
			throw new Exception(tableField+": helytelen dátumformátum (yyyy-MM-dd)");
		}	
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) throws Exception {
		if (gender.equals("IK") || gender.equals("ISz") || gender.equals("K") || gender.equals("Sz"))
			this.gender = gender;
		else throw new Exception("A kutya neme csak IK, ISz, K vagy Sz lehet.");
	}
	
}
