package plsql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class InputHandler {
	public static LocalDate readDate(Scanner input, DateTimeFormatter dtf) {
		boolean ok = false;
		LocalDate output = null;
		do {
			System.out.println("Dátum (pl:"+LocalDate.now().format(dtf)+"):");
			String s = input.nextLine();
			try {
				output = LocalDate.parse(s, dtf);
				ok=true;
			} catch(Exception e){
				System.out.println("Hiba. Dátumformátum: "+LocalDate.now().format(dtf));
				ok=false;
			}
		} while(!ok);
		return output;
	}
	
	public static LocalDateTime readDateTime(Scanner input, DateTimeFormatter dtf) {
		boolean ok = false;
		LocalDateTime output = null;
		do {
			System.out.println("Dátum (pl:"+LocalDateTime.now().format(dtf)+"):");
			String s = input.nextLine();
			try {
				output = LocalDateTime.parse(s, dtf);
				ok=true;
			} catch(Exception e){
				System.out.println("Hiba. Dátumformátum: "+LocalDateTime.now().format(dtf));
				ok=false;
			}
		} while(!ok);
		return output;
	}
	
	public static int readInt(int lower, int upper, Scanner input) {
		int num;
		do {
			System.out.println(lower+" és "+upper+" közötti egész:");
			while(!input.hasNextInt()) {
				input.next();
				System.out.println("Egész számot írjon be!");
			}
			num=input.nextInt();
			input.nextLine();
		} while(num<lower || num>upper);
		return num;
	}
	public static int readInt(Scanner input) {
		int num;
		do {
			System.out.println("Pozitív egész:");
			while(!input.hasNextInt()) {
				input.next();
				System.out.println("Egész számot írjon be!");
			}
			num=input.nextInt();
			input.nextLine();
		} while(num<0);
		return num;
	}
}
