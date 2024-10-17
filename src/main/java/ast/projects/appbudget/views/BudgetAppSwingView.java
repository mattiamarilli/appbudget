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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
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
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{41, 100, 61, 120, 95, 0};
		gbl_panel.rowHeights = new int[]{27, 168, 29, 16, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("Name");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		txtName = new JTextField();
		txtName.setName("nameTextBox");
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.anchor = GridBagConstraints.NORTH;
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.insets = new Insets(0, 0, 5, 5);
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 0;
		panel.add(txtName, gbc_txtName);
		txtName.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Surname");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 2;
		gbc_lblNewLabel_1.gridy = 0;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		txtSurname = new JTextField();
		txtSurname.setName("surnameTextBox");
		GridBagConstraints gbc_txtSurname = new GridBagConstraints();
		gbc_txtSurname.anchor = GridBagConstraints.NORTH;
		gbc_txtSurname.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSurname.insets = new Insets(0, 0, 5, 5);
		gbc_txtSurname.gridx = 3;
		gbc_txtSurname.gridy = 0;
		panel.add(txtSurname, gbc_txtSurname);
		txtSurname.setColumns(10);
		
		btnAdd = new JButton("Add");
		btnAdd.setName("addButton");
		btnAdd.setEnabled(false);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.BOTH;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 4;
		gbc_btnAdd.gridy = 0;
		panel.add(btnAdd, gbc_btnAdd);
		
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
		GridBagConstraints gbc_listUsers = new GridBagConstraints();
		gbc_listUsers.fill = GridBagConstraints.BOTH;
		gbc_listUsers.insets = new Insets(0, 0, 5, 0);
		gbc_listUsers.gridwidth = 5;
		gbc_listUsers.gridx = 0;
		gbc_listUsers.gridy = 1;
		panel.add(listUsers, gbc_listUsers);
		
		btnOpen = new JButton("Open Budgets");
		btnOpen.setName("openBudgetsButton");
		btnOpen.setEnabled(false);
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnOpen.insets = new Insets(0, 0, 5, 5);
		gbc_btnOpen.gridwidth = 2;
		gbc_btnOpen.gridx = 1;
		gbc_btnOpen.gridy = 2;
		panel.add(btnOpen, gbc_btnOpen);
		
		btnDelete = new JButton("Delete User");
		btnDelete.setName("deleteUserButton");
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(
				   e -> userController.deleteUser((User) listUsers.getSelectedValue())
				);
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnDelete.gridx = 3;
		gbc_btnDelete.gridy = 2;
		panel.add(btnDelete, gbc_btnDelete);
		
		listUsers.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				btnOpen.setEnabled(listUsers.getSelectedIndex() != -1);
				btnDelete.setEnabled(listUsers.getSelectedIndex() != -1);
			}
		});
		
		lblErrorMessage = new JLabel("");
		lblErrorMessage.setName("errorMessageLabel");
		GridBagConstraints gbc_lblErrorMessage = new GridBagConstraints();
		gbc_lblErrorMessage.fill = GridBagConstraints.BOTH;
		gbc_lblErrorMessage.gridwidth = 5;
		gbc_lblErrorMessage.gridx = 0;
		gbc_lblErrorMessage.gridy = 3;
		panel.add(lblErrorMessage, gbc_lblErrorMessage);
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
