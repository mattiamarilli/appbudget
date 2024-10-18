package ast.projects.appbudget.views;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ast.projects.appbudget.controllers.UserController;
import ast.projects.appbudget.models.User;

import javax.swing.JLabel;
import java.awt.CardLayout;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ListSelectionModel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class BudgetAppSwingView extends JFrame implements BudgetAppView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField txtName;
	private JTextField txtSurname;
	
	private JButton btnOpen;
	private JButton btnDelete;
	private JButton btnAdd;
	
	JLabel lblErrorMessage;
	
	private JList<User> listUsers;
	DefaultListModel<User> listUsersModel = new DefaultListModel<>();
	
	private transient UserController userController;
	
	public void setUserController(UserController userController) {
		this.userController = userController;
		userController.allUsers(); 
	}
	
	DefaultListModel<User> getListUsersModel() {
		return listUsersModel;
	}

	public BudgetAppSwingView() {
		setTitle("AppBudget");
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 430, 280);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, "name_141374767786875");
		GridBagLayout gblPanel = new GridBagLayout();
		gblPanel.columnWidths = new int[]{41, 100, 61, 120, 95, 0};
		gblPanel.rowHeights = new int[]{27, 168, 29, 16, 0};
		gblPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gblPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gblPanel);
		
		JLabel lblNewLabel = new JLabel("Name");
		GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
		gbcLblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbcLblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbcLblNewLabel.gridx = 0;
		gbcLblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbcLblNewLabel);
		
		txtName = new JTextField();
		txtName.setName("nameTextBox");
		GridBagConstraints gbcTxtName = new GridBagConstraints();
		gbcTxtName.anchor = GridBagConstraints.NORTH;
		gbcTxtName.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtName.insets = new Insets(0, 0, 5, 5);
		gbcTxtName.gridx = 1;
		gbcTxtName.gridy = 0;
		panel.add(txtName, gbcTxtName);
		txtName.setColumns(10);
		
		JLabel lblNewLabel1 = new JLabel("Surname");
		GridBagConstraints gbcLblNewLabel1 = new GridBagConstraints();
		gbcLblNewLabel1.fill = GridBagConstraints.HORIZONTAL;
		gbcLblNewLabel1.insets = new Insets(0, 0, 5, 5);
		gbcLblNewLabel1.gridx = 2;
		gbcLblNewLabel1.gridy = 0;
		panel.add(lblNewLabel1, gbcLblNewLabel1);
		
		txtSurname = new JTextField();
		txtSurname.setName("surnameTextBox");
		GridBagConstraints gbcTxtSurname = new GridBagConstraints();
		gbcTxtSurname.anchor = GridBagConstraints.NORTH;
		gbcTxtSurname.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtSurname.insets = new Insets(0, 0, 5, 5);
		gbcTxtSurname.gridx = 3;
		gbcTxtSurname.gridy = 0;
		panel.add(txtSurname, gbcTxtSurname);
		txtSurname.setColumns(10);
		
		btnAdd = new JButton("Add");
		btnAdd.setName("addButton");
		btnAdd.setEnabled(false);
		GridBagConstraints gbcBtnAdd = new GridBagConstraints();
		gbcBtnAdd.fill = GridBagConstraints.BOTH;
		gbcBtnAdd.insets = new Insets(0, 0, 5, 0);
		gbcBtnAdd.gridx = 4;
		gbcBtnAdd.gridy = 0;
		panel.add(btnAdd, gbcBtnAdd);
		
		KeyAdapter btnAddEnabler = new KeyAdapter() {
		    @Override
		    public void keyReleased(KeyEvent e) {
		        btnAdd.setEnabled(
		            !txtName.getText().trim().isEmpty() &&
		            !txtSurname.getText().trim().isEmpty()
		        );
		    }
		};
		txtName.addKeyListener(btnAddEnabler);
		txtSurname.addKeyListener(btnAddEnabler);
		
		btnAdd.addActionListener(
				e -> userController.addUser(txtName.getText(), txtSurname.getText()));
		
		listUsers = new JList<>(listUsersModel);
		listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listUsers.setName("usersList");
		GridBagConstraints gbcListUsers = new GridBagConstraints();
		gbcListUsers.fill = GridBagConstraints.BOTH;
		gbcListUsers.insets = new Insets(0, 0, 5, 0);
		gbcListUsers.gridwidth = 5;
		gbcListUsers.gridx = 0;
		gbcListUsers.gridy = 1;
		panel.add(listUsers, gbcListUsers);
		
		btnOpen = new JButton("Open Budgets");
		btnOpen.setName("openBudgetsButton");
		btnOpen.setEnabled(false);
		GridBagConstraints gbcBtnOpen = new GridBagConstraints();
		gbcBtnOpen.anchor = GridBagConstraints.NORTHEAST;
		gbcBtnOpen.insets = new Insets(0, 0, 5, 5);
		gbcBtnOpen.gridwidth = 2;
		gbcBtnOpen.gridx = 1;
		gbcBtnOpen.gridy = 2;
		panel.add(btnOpen, gbcBtnOpen);
		
		btnDelete = new JButton("Delete User");
		btnDelete.setName("deleteUserButton");
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(
				   e -> userController.deleteUser(listUsers.getSelectedValue())
				);
		GridBagConstraints gbcBtnDelete = new GridBagConstraints();
		gbcBtnDelete.anchor = GridBagConstraints.NORTHWEST;
		gbcBtnDelete.insets = new Insets(0, 0, 5, 5);
		gbcBtnDelete.gridx = 3;
		gbcBtnDelete.gridy = 2;
		panel.add(btnDelete, gbcBtnDelete);
		
		listUsers.addListSelectionListener(e -> {
		    btnOpen.setEnabled(listUsers.getSelectedIndex() != -1);
		    btnDelete.setEnabled(listUsers.getSelectedIndex() != -1);
		});

		
		lblErrorMessage = new JLabel("");
		lblErrorMessage.setName("errorMessageLabel");
		GridBagConstraints gbcLblErrorMessage = new GridBagConstraints();
		gbcLblErrorMessage.fill = GridBagConstraints.BOTH;
		gbcLblErrorMessage.gridwidth = 5;
		gbcLblErrorMessage.gridx = 0;
		gbcLblErrorMessage.gridy = 3;
		panel.add(lblErrorMessage, gbcLblErrorMessage);
	}

	@Override
	public void refreshUsersList(List<User> users) {
		listUsersModel.clear();
	    users.forEach(listUsersModel::addElement);
	}

	@Override
	public void showErrorMessage(String message) {
		lblErrorMessage.setText(message);		
	}
	
	@Override
	public void resetErrorMessage() {
		lblErrorMessage.setText("");		
	}
}
