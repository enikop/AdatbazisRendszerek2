package jdbc;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Training implements Verifiable {
	private int tid;
	private LocalDate day;
	private int length;
	private int trainerId;
	private ArrayList<Integer> participants;
	

	public Training(int tid, LocalDate day, int length, int trainerId) throws Exception {
		super();
		this.setTid(tid);
		this.setDay(day);
		this.setLength(length);
		this.setTrainerId(trainerId);
		this.participants = new ArrayList<Integer>();
	}
	public Training(ArrayList<String> inputs) throws Exception {
		super();
		int trainerId;
		int length;
		LocalDate day;
		try {
			day = LocalDate.parse(inputs.get(0));
		} catch(DateTimeParseException dtpe) {
			throw new Exception("Hibás dátum vagy dátumformátum.");
		}
		try {
			length = Integer.parseInt(inputs.get(1).trim());
		} catch(NumberFormatException nfe) {
			throw new Exception("Az időtartam egész szám");
		}
		try {
			trainerId = Integer.parseInt(inputs.get(2).trim());
		} catch(NumberFormatException nfe) {
			throw new Exception("A kiképző azonosítója egész szám");
		}
		this.participants = new ArrayList<Integer>();
		String[] participants = inputs.get(3).split(",");
		for(int i=0; i<participants.length; i++) {
			this.addParticipant(participants[i]);
		}
		this.setTid(1);
		this.setDay(day);
		this.setLength(length);
		this.setTrainerId(trainerId);
	}

	public Training() throws Exception {
		this(999, LocalDate.now(), 999, 999);
	}
	
	@Override
	public String toString() {
		return "Training [tid=" + tid + ", day=" + day + ", length=" + length + ", trainerId=" + trainerId
				+ ", participants=" + participants + "]";
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) throws Exception {
		if(tid>0) {
			this.tid = tid;
		} else throw new Exception("Az azonosító csak nullánál nagyobb egész érték lehet.");
	}

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) throws Exception {
		if(day.isAfter(LocalDate.now()) || day.isBefore(LocalDate.of(2023, 1, 1)))
			throw new Exception("A foglalkozás dátuma 2023. 01. 01. és a mai nap közé eső dátum.");
		this.day = day;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) throws Exception {
		if(length>0) {
			this.length = length;
		} else throw new Exception("Az időtartam csak nullánál nagyobb egész érték lehet.");
	}
	public int getTrainerId() {
		return trainerId;
	}

	public void setTrainerId(int trainerId) throws Exception {
		if(trainerId>0) {
			this.trainerId = trainerId;
		} else throw new Exception("A kiképző azonosítója csak nullánál nagyobb egész érték lehet.");
	}

	public ArrayList<Integer> getParticipants() {
		return participants;
	}

	public void setParticipants(ArrayList<Integer> participants) {
		this.participants = participants;
	}
	public void addParticipant(String part) throws Exception {
		//Ha nem adok meg, ne csinaljon semmit
		if(part.equals("")) return;
		try {
			Integer n=Integer.parseInt(part.trim());
			if(n<=0) throw new Exception("A kutyák azonosítói nullánál nagyobb egész számok.");
			if(!this.participants.contains(n)){
				this.participants.add(n);
			}
		}catch(NumberFormatException e) {
			throw new Exception("A kutyák azonosítói számok.");
		}
	}
	
	public void checkValidity(String tableField, String data) throws Exception {
		tableField = tableField.trim();
		data = data.trim();
		try {
			switch(tableField.toUpperCase()) {
			case "DAY":
				this.setDay(LocalDate.parse(data));
				break;
			case "LENGTH":	
				this.setLength(Integer.parseInt(data));
				break;
			case "TRAINER_ID":
				this.setTrainerId(Integer.parseInt(data));
				break;
			default: throw new Exception("A foglalkozás táblában csak a következő mezők módosíthatók: DAY, LENGTH, TRAINER_ID");
			}
		}catch(NumberFormatException nfe) {
			throw new Exception(tableField+": számértéket adjon meg!");
		}catch(DateTimeParseException dte) {
			throw new Exception(tableField+": helytelen dátumformátum (yyyy-MM-dd)");
		}	
	}	
}
