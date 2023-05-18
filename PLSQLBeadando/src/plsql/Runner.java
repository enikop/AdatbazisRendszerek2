package plsql;

import java.util.Scanner;

public class Runner {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		//MentoDB mdb = new MentoDB("jdbc:oracle:thin:@localhost:1521/XEPDB1", "demo", "demo", "yyyy.MM.dd. HH:mm");
		//mdb.delete();
		new MenuHandler().startMenu(input);
		input.close();
	}
	
}
