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

public class BudgetAppSwingView extends JFrame implements BudgetAppView{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField txtName;
	private JTextField txtSurname;
	
	private JButton btnOpen;
	private JButton btnDelete;
	
	JLabel lblErrorMessage;
	
	private JList<User> listUsers;
	DefaultListModel<User> listUsersModel;
	
	private UserController userController;
	
	public void setUserController(UserController userController) {
		this.userController = userController;
		userController.allUsers();
	}
	
	DefaultListModel<User> getListUsersModel(){
		return listUsersModel;
	}
	
    void maximizeWindow() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

	public BudgetAppSwingView() {
		setTitle("AppBudget");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, "name_141374767786875");
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Name");
		lblNewLabel.setBounds(6, 11, 41, 16);
		panel.add(lblNewLabel);
		
		txtName = new JTextField();
		txtName.setName("nameTextBox");
		txtName.setBounds(47, 6, 100, 26);
		panel.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Surname");
		lblNewLabel_1.setBounds(151, 11, 61, 16);
		panel.add(lblNewLabel_1);
		
		txtSurname = new JTextField();
		txtSurname.setName("surnameTextBox");
		txtSurname.setBounds(213, 6, 120, 26);
		panel.add(txtSurname);
		txtSurname.setColumns(10);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setName("addButton");
		btnAdd.setEnabled(false);
		btnAdd.setBounds(339, 7, 95, 26);
		
		btnAdd.addActionListener(
				e -> userController.addUser(txtName.getText(), txtSurname.getText()));
		panel.add(btnAdd);
		
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
		
		listUsersModel = new DefaultListModel<>();
		listUsers = new JList<>(listUsersModel);
		listUsers.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				btnOpen.setEnabled(listUsers.getSelectedIndex() != -1);
				btnDelete.setEnabled(listUsers.getSelectedIndex() != -1);
			}
		});
		
		
		listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listUsers.setName("usersList");
		listUsers.setBounds(6, 39, 428, 168);
		panel.add(listUsers);
		
		btnOpen = new JButton("Open Budgets");
		btnOpen.setName("openBudgetsButton");
		btnOpen.setEnabled(false);
		btnOpen.setBounds(95, 208, 117, 29);
		panel.add(btnOpen);
		
		btnDelete = new JButton("Delete User");
		btnDelete.setName("deleteUserButton");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(213, 208, 117, 29);
		btnDelete.addActionListener(
				   e -> userController.deleteUser((User) listUsers.getSelectedValue())
				);
		panel.add(btnDelete);
		
		lblErrorMessage = new JLabel("");
		lblErrorMessage.setName("errorMessageLabel");
		lblErrorMessage.setBounds(6, 240, 428, 16);
		panel.add(lblErrorMessage);
		
		
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
