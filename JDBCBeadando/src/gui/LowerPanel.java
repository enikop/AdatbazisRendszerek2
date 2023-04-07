package gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import jdbc.DataBaseHandler;
import jdbc.Dog;
import jdbc.Owner;
import jdbc.Trainer;
import jdbc.Training;

public class LowerPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private DataBaseHandler database;

	private InputPanel panelUpdate;
	private InputPanel panelDelete;
	private InputPanel panelIDog;
	private InputPanel panelIOwner;
	private InputPanel panelITrainer;
	private InputPanel panelITraining;
	private InputPanel panelIPresence;
	private InputPanel panelDeletePresence;
	private InputPanel panelQPresence;
	private InputPanel panelQWeight;
	private InputPanel panelQOwnership;
	private InputPanel panelQAge;
	private PopupHandler popupHandler;
	
	private JTextArea textAreaMeta;

	private String currentOperation="Metaadat összesítő";
	private String currentSubOperation="";
	
	private CardLayout lower;
	
	public void emptyPanel() {
		lower.show(this, "Empty");
	}
	
	public LowerPanel(PopupHandler popupHandler, DataBaseHandler dbh) {
		this.popupHandler = popupHandler;
		this.database = dbh;
		this.lower=new CardLayout(0,0);
		this.setLayout(lower);
		this.setBackground(new Color(211, 211, 211));
		this.setBounds(20, 212, 950, 478);
		initPanels();		
	}
	
	public void initPanels() {
		JPanel panelEmpty = new JPanel();
		panelEmpty.setBackground(new Color(211, 211, 211));
		this.add(panelEmpty, "Empty");
		
		JPanel panelMeta = new JPanel();
		panelMeta.setBackground(new Color(211, 211, 211));
		this.add(panelMeta, "Meta");
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		panelMeta.add(scrollPane);
		textAreaMeta = new JTextArea();
		scrollPane.setViewportView(textAreaMeta);
		textAreaMeta.setColumns(125);
		textAreaMeta.setRows(26);
		textAreaMeta.setEditable(false);
		textAreaMeta.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textAreaMeta.setBorder(BorderFactory.createCompoundBorder(
		        scrollPane.getBorder(), 
		        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		String panelFeeder[] = {"Kutya neve"};
		panelQPresence = new InputPanel(panelFeeder, "Adott kutya foglalkozás részvételeinek listázása");
		panelQPresence.getBtnInput().addActionListener(this);
		panelQPresence.getBtnInput().setActionCommand("Query");
		this.add(panelQPresence, "PresenceQuery");
		
		String genders[] = {"K - Kan", "IK - Ivartalanított kan", "Sz - Szuka", "ISz - Ivartalanított szuka"};
		panelFeeder = new String[] {"Kutya neme", "Minimum tömeg (kg)"};
		panelQWeight = new InputPanel(panelFeeder, "Adott nemű, adott tömegnél nehezebb kutyák listázása");
		panelQWeight.switchToComboBox(0, genders);
		panelQWeight.getBtnInput().addActionListener(this);
		panelQWeight.getBtnInput().setActionCommand("Query");
		this.add(panelQWeight, "WeightQuery");
		
		panelFeeder = new String[] {"Gazdi neve"};
		panelQOwnership = new InputPanel(panelFeeder, "Adott tulajdonos kutyáinak listázása");
		panelQOwnership.getBtnInput().addActionListener(this);
		panelQOwnership.getBtnInput().setActionCommand("Query");
		this.add(panelQOwnership, "OwnershipQuery");
		
		panelFeeder = new String[] {"Kutya max életkora (hónap)"};
		panelQAge = new InputPanel(panelFeeder, "Megadott hónapnál fiatalabb kutyák listázása");
		panelQAge.getBtnInput().addActionListener(this);
		panelQAge.getBtnInput().setActionCommand("Query");
		this.add(panelQAge, "DogAgeQuery");
		
		panelFeeder = new String[] {"Azonosító", "Változtatni kívánt mező neve", "Új érték"};
		panelUpdate = new InputPanel(panelFeeder, "Adatmódosítás");
		panelUpdate.getBtnInput().addActionListener(this);
		panelUpdate.getBtnInput().setActionCommand("Update");
		this.add(panelUpdate, "Update");
		
		panelFeeder = new String[] {"Azonosító"};
		panelDelete = new InputPanel(panelFeeder, "Törlés");
		panelDelete.getBtnInput().setActionCommand("Delete");
		panelDelete.getBtnInput().addActionListener(this);
		this.add(panelDelete, "Delete");
		
		panelFeeder = new String[] {"Foglalkozás azonosító","Kutya azonosító"};
		panelDeletePresence = new InputPanel(panelFeeder, "Foglalkozás részvétel törlése");
		panelDeletePresence.getBtnInput().setActionCommand("Delete");
		panelDeletePresence.getBtnInput().addActionListener(this);
		this.add(panelDeletePresence, "DeletePresence");
		
		panelFeeder = new String[]{"Név", "Email", "Születési dátum (yyyy-MM-dd)*", "Óradíj"};
		panelIOwner = new InputPanel(panelFeeder, "Tulajdonos adatainak felvétele (*-gal jelölt maradhat üresen)");
		panelIOwner.getBtnInput().addActionListener(this);
		panelIOwner.getBtnInput().setActionCommand("Insert");
		this.add(panelIOwner, "OwnerInsert");
		
		panelFeeder = new String[]{"Név", "Fajta*", "Születési dátum (yyyy-MM-dd)*", "Tömeg (kg)", "Nem", "Tulajdonos azonosító"};
		panelIDog = new InputPanel(panelFeeder, "Kutya adatainak felvétele (*-gal jelölt maradhat üresen)");
		panelIDog.switchToComboBox(4, genders);
		panelIDog.getBtnInput().addActionListener(this);
		panelIDog.getBtnInput().setActionCommand("Insert");
		this.add(panelIDog, "DogInsert");
		
		panelFeeder = new String[]{"Név", "Email", "Szakterület*"};
		panelITrainer = new InputPanel(panelFeeder, "Kiképző adatainak felvétele (*-gal jelölt maradhat üresen)");
		panelITrainer.getBtnInput().addActionListener(this);
		panelITrainer.getBtnInput().setActionCommand("Insert");
		this.add(panelITrainer, "TrainerInsert");
		
		panelFeeder = new String[]{"Dátum (yyyy-MM-dd)", "Időtartam (óra)", "Kiképző azonosító", "Résztvevő kutyák azonosítói"};
		panelITraining = new InputPanel(panelFeeder, "Foglalkozás adatainak felvétele (több résztvevő választása: ctrl)");
		panelITraining.getBtnInput().addActionListener(this);
		panelITraining.getBtnInput().setActionCommand("Insert");
		this.add(panelITraining, "TrainingInsert");
		
		panelFeeder = new String[] {"Foglalkozás azonosító","Kutya azonosító"};
		panelIPresence = new InputPanel(panelFeeder, "Foglalkozás részvételi adat hozzáadása");
		panelIPresence.getBtnInput().addActionListener(this);
		panelIPresence.getBtnInput().setActionCommand("Insert");
		this.add(panelIPresence, "PresenceInsert");
	}
	public void setDataBaseHandler(DataBaseHandler dbh) {
		this.database = dbh;
	}
	
	
	private void query() {
		Dog verifyDog;
		String error ="";
		try {
			switch(currentSubOperation) {
			case "Adott kutya részvétele foglalkozásokon": 
				verifyDog = new Dog();
				verifyDog.setName(panelQPresence.getInputData().get(0));
				new QueryResult(new JTable(new DataBaseTableModel(database.listTrainingDetails(verifyDog.getName()))));
				break;
			case "Adott nemű nehéz kutyák": 
				verifyDog = new Dog();
				ArrayList<String> inputs = panelQWeight.getInputData();
				verifyDog.setGender(inputs.get(0).split(" ")[0]);
				error="Tömeg: ";
				double weight = Double.parseDouble(inputs.get(1));
				verifyDog.setWeight(weight);
				new QueryResult(new JTable(new DataBaseTableModel(database.listDogsByGenderHeavierThan(verifyDog.getGender(), verifyDog.getWeight()))));
				break;
			case "Adott gazdi kutyái": 
				Owner verifyOwner = new Owner();
				verifyOwner.setName(panelQOwnership.getInputData().get(0));
				new QueryResult(new JTable(new DataBaseTableModel(database.listDogsOwnedBy(verifyOwner.getName()))));
				break;
			case "X hónapnál fiatalabb kutyák":
				error="Kor: ";
				int age = Integer.parseInt(panelQAge.getInputData().get(0).trim());
				new QueryResult(new JTable(new DataBaseTableModel(database.listDogsYoungerThan(age))));
				break;
			}	
		}catch(NumberFormatException nfe) {
			popupHandler.showException(error+"Számot adjon meg.");
		}catch(Exception exc) {
			popupHandler.showException(exc.getMessage());
		}
		
	}
	private void update() {
		try {
			ArrayList<String> inputs = panelUpdate.getInputData();
			if(inputs.get(0).equals("")) throw new Exception("Nincs azonosító megadva.");
			int id = Integer.parseInt(inputs.get(0).trim());
			int db = 0;
			db = database.updateById(nameToTablename(currentSubOperation), inputs.get(1), inputs.get(2), id);
			popupHandler.showInformation("Sikeres módosítás", db+" rekord módosult.");
			panelUpdate.clearInputFields();					
		}catch(Exception exc) {
			popupHandler.showException(exc.getMessage());
		}
	}
	private String nameToTablename(String hun) {
		switch(hun) {
		case "Tulajdonos": return "OWNERS";
		case "Kutya": return "DOGS";
		case "Kutyakiképző": return "TRAINERS";
		case "Foglalkozás": return "TRAININGS";
		case "Részvétel": return "PRESENCE";
		}
		return null;
	}
	private void delete() {
		try {
			if(currentSubOperation.equals("Részvétel")) {
				ArrayList<String> inputs = panelDeletePresence.getInputData();
				if(inputs.get(0).equals("") || inputs.get(1).equals("")) throw new Exception("Nincs azonosító megadva.");
				database.deletePresence(Integer.parseInt(inputs.get(0)),Integer.parseInt(inputs.get(1)));
				popupHandler.showInformation("Sikeres törlés", "Rekord törölve.");
				return;	
			}
			if(panelDelete.getInputData().get(0).equals("")) throw new Exception("Nincs azonosító megadva.");
			int id = Integer.parseInt(panelDelete.getInputData().get(0));
			int db=-1;
			switch(currentSubOperation) {
			case "Tulajdonos": database.deleteOwner(id); break;
			case "Kutya":  database.deleteDog(id); break;
			case "Kutyakiképző": database.deleteTrainer(id); break; 
			case "Foglalkozás":
				db = database.deleteTraining(id);
				popupHandler.showInformation("Sikeres törlés", "A foglalkozás és "+db+" részvételi adat törölve.");
				break;
			}
			deleteData(currentSubOperation);
			this.revalidate();
			this.repaint();
			if(db==-1)popupHandler.showInformation("Sikeres törlés", "Rekord törölve.");
		} catch(Exception exc) {
			popupHandler.showException(exc.getMessage());
		}
	}
	private void insert() {
		ArrayList<String> inputs = null;
		try {
			switch (currentSubOperation) {
			case "Tulajdonos":
				inputs = panelIOwner.getInputData();
				Owner o = new Owner(inputs);
				database.insertOwner(o);
				break;
			case "Kutya":
				inputs = panelIDog.getInputData();
				Dog d = new Dog(inputs);
				database.insertDog(d);
				break;
			case "Kutyakiképző":
				inputs = panelITrainer.getInputData();
				Trainer ter = new Trainer(inputs);
				database.insertTrainer(ter);
				break;
			case "Foglalkozás":
				inputs = panelITraining.getInputData();
				Training ting = new Training(inputs);
				database.insertTraining(ting);
				break;
			case "Részvétel":
				inputs = panelIPresence.getInputData();
				database.insertPresence(Integer.parseInt(inputs.get(0).trim()), Integer.parseInt(inputs.get(1).trim()));
			}
			popupHandler.showInformation("Sikeres adatfelvitel.", "Rekord hozzáadva: " + inputs);
		} catch (Exception exc) {
			popupHandler.showException(exc.getMessage());
		}
	}
	public void metaDataSummary() {
		lower.show(this, "Meta");
		textAreaMeta.setText(database.queryMetaData());
	}

	public void insertData(String table) {
		try {
			switch (table) {
			case "Tulajdonos":
				panelIOwner.clearInputFields();
				lower.show(this, "OwnerInsert");
				break;
			case "Kutya":
				panelIDog.clearInputFields();
				// mindig ujra kell hivni, hogy az eppen ervenyes id-k legyenek benne a
				// comboboxban
				panelIDog.switchToComboBox(5, database.getValidIds("OWNERS"));
				lower.show(this, "DogInsert");
				break;
			case "Kutyakiképző":
				panelITrainer.clearInputFields();
				lower.show(this, "TrainerInsert");
				break;
			case "Foglalkozás":
				panelITraining.clearInputFields();
				panelITraining.switchToComboBox(2, database.getValidIds("TRAINERS"));
				panelITraining.switchLastToList(database.getValidIds("DOGS"));
				lower.show(this, "TrainingInsert");
				break;
			case "Részvétel":
				panelIPresence.switchToComboBox(1, database.getValidIds("DOGS"));
				panelIPresence.switchToComboBox(0, database.getValidIds("TRAININGS"));
				lower.show(this, "PresenceInsert");
				break;
			}
		} catch (Exception e) {
			popupHandler.showException(e.getMessage());
		}
	}

	public void deleteData(String table) {
		try {
			switch (table) {
			case "Tulajdonos":
				panelDelete.switchToComboBox(0, database.getValidIds("OWNERS"));
				break;
			case "Kutya":
				panelDelete.switchToComboBox(0, database.getValidIds("DOGS"));
				break;
			case "Kutyakiképző":
				panelDelete.switchToComboBox(0, database.getValidIds("TRAINERS"));
				break;
			case "Foglalkozás":
				panelDelete.switchToComboBox(0, database.getValidIds("TRAININGS"));
				break;
			case "Részvétel":
				panelDeletePresence.switchToComboBox(1, database.getValidIds("DOGS"));
				panelDeletePresence.switchToComboBox(0, database.getValidIds("TRAININGS"));
				lower.show(this, "PresenceInsert");
				break;
			}
			panelDelete.clearInputFields();
			panelDelete.setPanelTitle(table + " törlése");
			currentSubOperation = table;
			lower.show(this, "Delete");
			if (table.equals("Részvétel"))
				lower.show(this, "DeletePresence");
		} catch (Exception e) {
			popupHandler.showException(e.getMessage());
		}
	}

	public void updateData(String table) {
		try {
			switch (table) {
			case "Tulajdonos":
				panelUpdate.switchToComboBox(0, database.getValidIds("OWNERS"));
				break;
			case "Kutya":
				panelUpdate.switchToComboBox(0, database.getValidIds("DOGS"));
				break;
			case "Kutyakiképző":
				panelUpdate.switchToComboBox(0, database.getValidIds("TRAINERS"));
				break;
			case "Foglalkozás":
				panelUpdate.switchToComboBox(0, database.getValidIds("TRAININGS"));
				break;
			}
			panelUpdate.clearInputFields();
			panelUpdate.setPanelTitle(table + " adatainak módosítása");
			currentSubOperation = table;
			lower.show(this, "Update");
		} catch (Exception e) {
			popupHandler.showException(e.getMessage());
		}
	}
	public void queryData(String queryName) {
		try {
		switch (queryName) { 
			case "Tulajdonos": new QueryResult(new JTable(new DataBaseTableModel(database.listAll("OWNERS")))); break;
			case "Kutya": new QueryResult(new JTable(new DataBaseTableModel(database.listAll("DOGS")))); break;
			case "Kutyakiképző": new QueryResult(new JTable(new DataBaseTableModel(database.listAll("TRAINERS")))); break;
			case "Foglalkozás": new QueryResult(new JTable(new DataBaseTableModel(database.listAll("TRAININGS")))); break;
			case "Részvétel": new QueryResult(new JTable(new DataBaseTableModel(database.listAll("PRESENCE")))); break;
			case "Gazdik tartozásai": new QueryResult(new JTable(new DataBaseTableModel(database.listSumToPay()))); break;
			case "Gazdik kutyáinak száma": new QueryResult(new JTable(new DataBaseTableModel(database.listOwnersByDogNumber()))); break;
			case "Adott kutya részvétele foglalkozásokon": lower.show(this,"PresenceQuery"); break;
			case "Adott nemű nehéz kutyák": lower.show(this,"WeightQuery"); break;
			case "Adott gazdi kutyái": lower.show(this,"OwnershipQuery"); break;
			case "X hónapnál fiatalabb kutyák": lower.show(this,"DogAgeQuery"); break;
		}
		}catch(Exception e) {
			popupHandler.showException("Sikertelen lekérdezés. "+e.getMessage());
		}
	}
	public void saveDataTxt() {
		try {
			database.saveDBToTxt();
			popupHandler.showInformation("Sikeres művelet", "Táblák kiírva txt-be.");
		} catch(Exception e) {
			popupHandler.showException(e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "Delete": delete(); break;
		case "Update": update(); break;
		case "Insert": insert(); break;
		case "Query": query(); break;
		}
	}
	public String getCurrentOperation() {
		return currentOperation;
	}

	public void setCurrentOperation(String currentOperation) {
		this.currentOperation = currentOperation;
	}

	public String getCurrentSubOperation() {
		return currentSubOperation;
	}

	public void setCurrentSubOperation(String currentSubOperation) {
		this.currentSubOperation = currentSubOperation;
	}

}
