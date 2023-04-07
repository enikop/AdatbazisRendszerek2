package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jdbc.*;

import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.CardLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import java.awt.Insets;

public class DogSchoolMain extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private DataBaseHandler database;
	
	private JPanel contentPane;
	private PopupHandler popupHandler;
	
	private JTextField textFieldConn;
	private JTextField textFieldUname;
	private JPasswordField passwordField;
	
	private JPanel panelUpper;
	private LowerPanel panelLower;
	private JPanel panelOption2;
	
	private JComboBox<String> comboBoxL1;
	private JComboBox<String> comboBoxL2;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DogSchoolMain frame = new DogSchoolMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public DogSchoolMain() {
		setTitle("Kutyaiskola Adatbázis Kezelő");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 1000, 750);
		setLocationRelativeTo(null);
		setResizable(false);
		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                if(database!=null) {
                	database.disconnect();
                }
                System.exit(0);
            }
        });
		UIManager.put("ComboBox.selectionBackground", new Color(0, 128, 128,60));
		UIManager.put("List.selectionBackground", new Color(0, 128, 128, 60));
		
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 128, 128));
		contentPane.setForeground(new Color(230, 230, 250));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		popupHandler = new PopupHandler(contentPane);
		
		JLabel lblTitle = new JLabel("Kutyaiskola Adatbázis Kezelő");
		lblTitle.setForeground(new Color(211, 211, 211));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(0, 20, 980, 70);
		lblTitle.setFont(new Font("Verdana", Font.PLAIN, 36));
		contentPane.add(lblTitle);
		
		panelUpper = new JPanel();
		panelUpper.setBackground(new Color(211, 211, 211));
		panelUpper.setFont(new Font("Arial", Font.PLAIN, 18));
		panelUpper.setBounds(20, 100, 950, 90);
		contentPane.add(panelUpper);
		panelUpper.setLayout(new CardLayout(0,0));
		
		JPanel panelLogin = new JPanel();
		panelLogin.setBackground(new Color(211, 211, 211));
		panelLogin.setPreferredSize(new Dimension(950, 90));
		panelUpper.add(panelLogin, "upperLogin");
		panelLogin.setLayout(null);
		
		JPanel panelConn = new JPanel();
		panelConn.setBounds(110, 5, 733, 33);
		panelLogin.add(panelConn);
		
		JLabel lblConn = new JLabel("Adatbázis elérési út");
		lblConn.setAlignmentX(0.5f);
		lblConn.setFont(new Font("Arial", Font.PLAIN, 18));
		panelConn.add(lblConn);
		
		textFieldConn = new JTextField();
		textFieldConn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldConn.setColumns(46);
		panelConn.add(textFieldConn);
		
		MyButton btnConnect = new MyButton("Bejelentkezés");
		btnConnect.setBounds(693, 46, 150, 30);
		btnConnect.setMargin(new Insets(0, 14, 0, 14));
		btnConnect.setBackground(new Color(248, 248, 255));
		btnConnect.setFont(new Font("Arial", Font.PLAIN, 18));
		btnConnect.setActionCommand("Connect");
		btnConnect.addActionListener(this);
		
		JPanel panelPassword = new JPanel();
		panelPassword.setBounds(110, 44, 575, 32);
		panelLogin.add(panelPassword);
		
		JLabel lblUname = new JLabel("Felhasználónév");
		lblUname.setFont(new Font("Arial", Font.PLAIN, 18));
		panelPassword.add(lblUname);
		
		textFieldUname = new JTextField();
		textFieldUname.setMinimumSize(new Dimension(200, 19));
		textFieldUname.setPreferredSize(new Dimension(200, 19));
		textFieldUname.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelPassword.add(textFieldUname);
		textFieldUname.setColumns(15);
		
		JLabel lblNewLabel = new JLabel("Jelszó");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		panelPassword.add(lblNewLabel);
		
		passwordField = new JPasswordField();
		passwordField.setColumns(15);
		passwordField.setPreferredSize(new Dimension(120, 19));
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelPassword.add(passwordField);
		
		panelLogin.add(btnConnect);
		
		JPanel panelChooseOp = new JPanel();
		panelChooseOp.setBackground(new Color(211, 211, 211));
		panelUpper.add(panelChooseOp, "upperMain");
		
		comboBoxL1 = new JComboBox<String>();
		comboBoxL1.setBounds(265, 5, 350, 30);
		comboBoxL1.setBackground(new Color(255, 255, 255));
		comboBoxL1.setFont(new Font("Arial", Font.PLAIN, 18));
		comboBoxL1.setUI(new MyComboBoxUI());
		comboBoxL1.addItem("Metaadat összesítő");
		comboBoxL1.addItem("Adatfelvitel");
		comboBoxL1.addItem("Törlés");
		comboBoxL1.addItem("Adatmódosítás");
		comboBoxL1.addItem("Lekérdezés");
		comboBoxL1.addItem("Mentés txt-be");
		panelChooseOp.setLayout(null);
		panelChooseOp.add(comboBoxL1);
		
		comboBoxL2 = new JComboBox<String>();
		comboBoxL2.setBounds(265, 5, 350, 30);
		comboBoxL2.setBackground(new Color(255, 255, 255));
		comboBoxL2.setFont(new Font("Arial", Font.PLAIN, 18));
		comboBoxL2.setUI(new MyComboBoxUI());
		
		MyButton btnOp1 = new MyButton("OK");
		btnOp1.setBounds(625, 5, 60, 30);
		btnOp1.setActionCommand("ChoosePrimary");
		btnOp1.addActionListener(this);
		btnOp1.setFont(new Font("Arial", Font.PLAIN, 18));
		btnOp1.setBackground(new Color(248, 248, 255));
		btnOp1.setActionCommand("ChoosePrimary");
		panelChooseOp.add(btnOp1);
		
		panelOption2 = new JPanel();
		panelOption2.setBounds(0, 40, 950, 50);
		panelOption2.setBackground(new Color(211, 211, 211));
		panelOption2.setPreferredSize(new Dimension(950, 50));
		panelOption2.setVisible(false);
		panelOption2.setLayout(null);
		panelOption2.add(comboBoxL2);
		panelChooseOp.add(panelOption2);
		
		
		MyButton btnOp2 = new MyButton("OK");
		btnOp2.setBounds(625, 5, 60, 30);
		btnOp2.addActionListener(this);
		btnOp2.setActionCommand("ChooseSecondary");
		btnOp2.setBackground(new Color(248, 248, 255));
		btnOp2.setFont(new Font("Arial", Font.PLAIN, 18));
		panelOption2.add(btnOp2);
		
		panelLower = new LowerPanel(popupHandler, database);
		contentPane.add(panelLower);
	}

	private void connectToDatabase() {
		try{
			String dbPath = textFieldConn.getText();
			String username = textFieldUname.getText();
			String password = String.valueOf(passwordField.getPassword());
			
			database = new DataBaseHandler("jdbc:sqlite:"+dbPath, username, password);
			panelLower.setDataBaseHandler(database);
			CardLayout upper = (CardLayout)(panelUpper.getLayout());
			upper.show(panelUpper, "upperMain");
		} catch(Exception exc) {popupHandler.showException("Sikertelen DB kapcsolódás. "+exc.getMessage());};
		
	}
	
	
	private void handlePrimaryChoice(String choice, JComboBox<String> comboOp2) {
		comboOp2.removeAllItems();
		panelLower.emptyPanel();
		switch(choice) {
		case "Metaadat összesítő": 
			panelLower.metaDataSummary();
			panelOption2.setVisible(false);
			break;
		case "Adatfelvitel": 
			comboOp2.addItem("Tulajdonos");
			comboOp2.addItem("Kutya");
			comboOp2.addItem("Kutyakiképző");
			comboOp2.addItem("Foglalkozás");
			comboOp2.addItem("Részvétel");
			panelOption2.setVisible(true); 
			break;
		case "Törlés":
			comboOp2.addItem("Tulajdonos");
			comboOp2.addItem("Kutya");
			comboOp2.addItem("Kutyakiképző");
			comboOp2.addItem("Foglalkozás");
			comboOp2.addItem("Részvétel");
			panelOption2.setVisible(true); 
			break;
		case "Adatmódosítás":
			comboOp2.addItem("Tulajdonos");
			comboOp2.addItem("Kutya");
			comboOp2.addItem("Kutyakiképző");
			comboOp2.addItem("Foglalkozás");
			panelOption2.setVisible(true); 
			break;
		case "Lekérdezés": 
			comboOp2.addItem("Tulajdonos");
			comboOp2.addItem("Kutya");
			comboOp2.addItem("Kutyakiképző");
			comboOp2.addItem("Foglalkozás");
			comboOp2.addItem("Részvétel");
			comboOp2.addItem("Gazdik tartozásai");
			comboOp2.addItem("Gazdik kutyáinak száma");
			comboOp2.addItem("Adott kutya részvétele foglalkozásokon");
			comboOp2.addItem("Adott nemű nehéz kutyák");
			comboOp2.addItem("Adott gazdi kutyái");
			comboOp2.addItem("X hónapnál fiatalabb kutyák");
			panelOption2.setVisible(true);
			break;
		case "Mentés txt-be": 
			panelOption2.setVisible(false); 
			panelLower.saveDataTxt();
			break;
		}
		
	}
	
	private void handleSecondaryChoice(String secondaryOp) {
		panelLower.emptyPanel();
		panelLower.setCurrentSubOperation(secondaryOp);
		switch(panelLower.getCurrentOperation()) {
		case "Adatfelvitel": panelLower.insertData(secondaryOp); break;
		case "Törlés": panelLower.deleteData(secondaryOp); break;
		case "Adatmódosítás": panelLower.updateData(secondaryOp); break;
		case "Lekérdezés": panelLower.queryData(secondaryOp);break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "Connect":
			connectToDatabase();
			break;
		case "ChoosePrimary":
			String current = (String)comboBoxL1.getSelectedItem();
			panelLower.setCurrentOperation(current);
			panelLower.emptyPanel();
			handlePrimaryChoice(current, comboBoxL2);
			break;
		case "ChooseSecondary":
			handleSecondaryChoice((String)comboBoxL2.getSelectedItem());
		}
		
	}
}
