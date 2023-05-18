package plsql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MenuHandler {
	public void startMenu(Scanner input) {
		String dateFormat = "yyyy.MM.dd. HH:mm";
		AmbulanceDB ambulanceDB = login(input, dateFormat);
		if(!ambulanceDB.isValidConnection()) {
			System.out.println("Sikertelen bejelentkezés.");
			return;
		}
		int choice;
		String[] options = {"Kilépés", "Tábla feltöltése fájlból","Tábla feltöltése random módon","Adatfelvitel","Lekérdezés"};
		do {
			listOptions(options);
			choice=InputHandler.readInt(0, options.length-1, input);
			switch(choice) {
			case 0: System.out.println("Kilépés..."); break;
			case 1: fileFill(ambulanceDB, input); break;
			case 2: randomFill(ambulanceDB, input); break;
			case 3: insertData(ambulanceDB, input); break;
			case 4: queryData(ambulanceDB, input); break;
			}
		}while(choice!=0);
	}
	
	private AmbulanceDB login(Scanner input, String dateFormat) {
		System.out.println("Connection string:");
		String conn = input.nextLine();
		System.out.println("Username:");
		String uName = input.nextLine();
		System.out.println("Password:");
		String pswd = input.nextLine();
		return new AmbulanceDB(conn, uName, pswd, dateFormat);
	}
	private void listOptions(String[] opt) {
		for(int i=0; i<opt.length; i++) {
			System.out.println(i+": "+opt[i]);
		}
	}
	private   void fileFill(AmbulanceDB ambulanceDB, Scanner input) {
		String[] options = {"Mentősök felvétele fájlból (hozzáírás)", "Autók felvétele fájlból (hozzáírás)", "Hívások és kivonulások felvétele fájlból (törlés és újraírás)"};
		listOptions(options);
		int choice=InputHandler.readInt(0, options.length-1, input);
		switch(choice) {
		case 0: ambulanceDB.readEmts();break;
		case 1: ambulanceDB.readVehicles();break;
		case 2: ambulanceDB.readCallsAndOnSites();break;
		}
	}
	private   void randomFill(AmbulanceDB ambulanceDB, Scanner input) {
		String[] options = {"Hívások generálása", "Kivonulások generálása (0-2/hívás)", "Osztagok generálása (0-3/kivonulás)"};
		listOptions(options);
		int choice=InputHandler.readInt(0, options.length-1, input);
		int num;
		switch(choice) {
		case 0: 
			System.out.println("Generált hívások száma:");
			num=InputHandler.readInt(1, 100, input);
			ambulanceDB.randomGenerateCall(num);
			break;
		case 1: ambulanceDB.randomGenerateOnSite();break;
		case 2: ambulanceDB.randomGeneratePersonnel();break;
		}
	}
	private   void insertData(AmbulanceDB ambulanceDB, Scanner input) {
		String[] options = {"Hívás napló", "Kivonulás felvétele"};
		listOptions(options);
		int choice=InputHandler.readInt(0, options.length-1, input);
		switch(choice) {
		case 0: 
			LocalDateTime time = LocalDateTime.now();
			System.out.println("Időpont: "+time.format(ambulanceDB.getDtf())+", hívó telefonszám:");
			String phone = input.nextLine();
			int id = ambulanceDB.auditCall(time, phone);
			if(id<=0) {
				System.out.println("Sikertelen művelet.");
				break;
			}
			System.out.println("Hívó neve:");
			String name = input.nextLine();
			System.out.println("Irányítószám:");
			String postCode = input.nextLine();
			System.out.println("Cím:");
			String address = input.nextLine();
			System.out.println("Sérültek vagy betegek száma:");
			int injuredNum = InputHandler.readInt(1, 99, input);
			ambulanceDB.detailCall(id, name, postCode, address, injuredNum);
			System.out.println("Szükséges kivonulás? (0:igen 1:nem)");
			int goOnSite = InputHandler.readInt(0,1,input);
			ambulanceDB.diszpecserDontes(id, goOnSite==0 ? true : false);
			break;
		case 1:
			System.out.println("Hívás id:");
			int cid = InputHandler.readInt(input);
			System.out.println("Autó id:");
			int vid = InputHandler.readInt(input);
			System.out.println("Helyszínre érkezés ideje:");
			LocalDateTime arr = InputHandler.readDateTime(input, ambulanceDB.getDtf());
			System.out.println("Telepre visszatérés ideje:");
			LocalDateTime back = InputHandler.readDateTime(input, ambulanceDB.getDtf());
			System.out.println("Mentősök id-i vesszővel elválasztva:");
			int[] emtId;
			boolean ok=false;
			do {
				String[] emts = input.nextLine().split(",");
				emtId = new int[emts.length];
				try {
					for(int i=0; i<emts.length; i++) {
						emtId[i]=Integer.parseInt(emts[i].trim());
					}
					ok=true;
				} catch(Exception e) {
					ok=false;
					System.out.println("Hiba, írja be újra az azonosítókat vesszővel elválasztva!");
				}
			} while(!ok);
			ambulanceDB.auditOnSite(cid, vid, arr, back, emtId);
			break;
		}
	}
	private  void queryData(AmbulanceDB ambulanceDB, Scanner input) {
		String[] options = {"Hívások szűrése hívó neve szerint", "Hívások szűrése időszakra", "Hívások száma adott napon", "Leghosszabb kivonulás időtartama percben", "Adott mentős telefonszáma", "Adott időszakban adott mentős volt-e kiküldetésen"};
		listOptions(options);
		int choice=InputHandler.readInt(0, options.length-1, input);
		int emtId;
		LocalDateTime beginning, end;
		switch(choice) {
		case 0: 
			System.out.println("Név:");
			String name = input.nextLine();
			ambulanceDB.filterCallsByCaller(name);
			break;
		case 1: 
			System.out.println("Időszak kezdete:");
			beginning = InputHandler.readDateTime(input, ambulanceDB.getDtf());
			System.out.println("Időszak vége:");
			end = InputHandler.readDateTime(input, ambulanceDB.getDtf());
			ambulanceDB.filterCallsByTime(beginning, end);
			break;
		case 2:
			System.out.println("Dátum:");
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd.");
			LocalDate day = InputHandler.readDate(input, dtf);
			System.out.println("A hívások száma "+day.format(dtf)+" napon: "+ambulanceDB.numberOfCallsOn(day));
			break;
		case 3:
			System.out.println("A leghosszabb kivonulás ideje: "+ambulanceDB.maxOnSiteMinutes()+" perc volt.");
			break;
		case 4:
			System.out.println("Mentős azonosítója:");
			emtId = InputHandler.readInt(input);
			System.out.println("Telefonszáma: "+ambulanceDB.getPhoneNumOfEmt(emtId));
			break;
		case 5:
			System.out.println("Mentős azonosítója:");
			emtId = InputHandler.readInt(input);
			System.out.println("Időszak kezdete:");
			beginning = InputHandler.readDateTime(input, ambulanceDB.getDtf());
			System.out.println("Időszak vége:");
			end = InputHandler.readDateTime(input, ambulanceDB.getDtf());
			System.out.println(beginning.format(ambulanceDB.getDtf())+" és "+end.format(ambulanceDB.getDtf())+" között a mentős "+(ambulanceDB.wasOnSite(emtId, beginning, end)?"volt":"nem volt")+" kiküldetésen.");
			break;
			
		}
	}
	
	
}
