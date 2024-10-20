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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Color;

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
	private JPanel budgetPanel;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JLabel needsLabel;
	private JLabel labelSavings;
	private JLabel lblWants;
	private JPanel panel;
	private JPanel panel_1;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JButton btnNewButton_2;
	private JButton btnNewButton_3;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_6;
	private JPanel panel_2;
	private JButton btnNewButton_4;
	
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
		setBounds(100, 100, 1023, 641);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel userPanel = new JPanel();
		contentPane.add(userPanel, "name_141374767786875");
		GridBagLayout gbl_userPanel = new GridBagLayout();
		gbl_userPanel.columnWidths = new int[]{41, 100, 61, 120, 95, 0};
		gbl_userPanel.rowHeights = new int[]{27, 168, 29, 16, 0};
		gbl_userPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_userPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		userPanel.setLayout(gbl_userPanel);
		
		JLabel lblNewLabel = new JLabel("Name");
		GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
		gbcLblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbcLblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbcLblNewLabel.gridx = 0;
		gbcLblNewLabel.gridy = 0;
		userPanel.add(lblNewLabel, gbcLblNewLabel);
		
		txtName = new JTextField();
		txtName.setName("nameTextBox");
		GridBagConstraints gbcTxtName = new GridBagConstraints();
		gbcTxtName.anchor = GridBagConstraints.NORTH;
		gbcTxtName.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtName.insets = new Insets(0, 0, 5, 5);
		gbcTxtName.gridx = 1;
		gbcTxtName.gridy = 0;
		userPanel.add(txtName, gbcTxtName);
		txtName.setColumns(10);
		
		JLabel lblNewLabel1 = new JLabel("Surname");
		GridBagConstraints gbcLblNewLabel1 = new GridBagConstraints();
		gbcLblNewLabel1.fill = GridBagConstraints.HORIZONTAL;
		gbcLblNewLabel1.insets = new Insets(0, 0, 5, 5);
		gbcLblNewLabel1.gridx = 2;
		gbcLblNewLabel1.gridy = 0;
		userPanel.add(lblNewLabel1, gbcLblNewLabel1);
		
		txtSurname = new JTextField();
		txtSurname.setName("surnameTextBox");
		GridBagConstraints gbcTxtSurname = new GridBagConstraints();
		gbcTxtSurname.anchor = GridBagConstraints.NORTH;
		gbcTxtSurname.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtSurname.insets = new Insets(0, 0, 5, 5);
		gbcTxtSurname.gridx = 3;
		gbcTxtSurname.gridy = 0;
		userPanel.add(txtSurname, gbcTxtSurname);
		txtSurname.setColumns(10);
		
		btnAdd = new JButton("Add");
		btnAdd.setName("addButton");
		btnAdd.setEnabled(false);
		GridBagConstraints gbcBtnAdd = new GridBagConstraints();
		gbcBtnAdd.fill = GridBagConstraints.BOTH;
		gbcBtnAdd.insets = new Insets(0, 0, 5, 0);
		gbcBtnAdd.gridx = 4;
		gbcBtnAdd.gridy = 0;
		userPanel.add(btnAdd, gbcBtnAdd);
		
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
		userPanel.add(listUsers, gbcListUsers);
		
		btnOpen = new JButton("Open Budgets");
		btnOpen.setName("openBudgetsButton");
		btnOpen.setEnabled(false);
		GridBagConstraints gbcBtnOpen = new GridBagConstraints();
		gbcBtnOpen.anchor = GridBagConstraints.NORTHEAST;
		gbcBtnOpen.insets = new Insets(0, 0, 5, 5);
		gbcBtnOpen.gridwidth = 2;
		gbcBtnOpen.gridx = 1;
		gbcBtnOpen.gridy = 2;
		userPanel.add(btnOpen, gbcBtnOpen);
		
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
		userPanel.add(btnDelete, gbcBtnDelete);
		
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
		userPanel.add(lblErrorMessage, gbcLblErrorMessage);
		
		budgetPanel = new JPanel();
		contentPane.add(budgetPanel, "name_344315617073333");
		GridBagLayout gbl_budgetPanel = new GridBagLayout();
		gbl_budgetPanel.columnWidths = new int[]{0, 260, 44, 210, 21, 210, 0, 210, 0, 0};
		gbl_budgetPanel.rowHeights = new int[]{47, 0, 352, 62, 0};
		gbl_budgetPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_budgetPanel.rowWeights = new double[]{1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		budgetPanel.setLayout(gbl_budgetPanel);
		
		panel_2 = new JPanel();
		panel_2.setLayout(null);
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridheight = 2;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 0;
		budgetPanel.add(panel_2, gbc_panel_2);
		
		btnNewButton_4 = new JButton("Exit");
		btnNewButton_4.setForeground(Color.RED);
		btnNewButton_4.setBackground(Color.BLACK);
		btnNewButton_4.setBounds(129, 41, 100, 29);
		panel_2.add(btnNewButton_4);
		
		
		needsLabel = new JLabel("Needs - 50%");
		GridBagConstraints gbc_needsLabel = new GridBagConstraints();
		gbc_needsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_needsLabel.gridx = 3;
		gbc_needsLabel.gridy = 1;
		budgetPanel.add(needsLabel, gbc_needsLabel);
		
		lblWants = new JLabel("Wants - 30%");
		GridBagConstraints gbc_lblWants = new GridBagConstraints();
		gbc_lblWants.insets = new Insets(0, 0, 5, 5);
		gbc_lblWants.gridx = 5;
		gbc_lblWants.gridy = 1;
		budgetPanel.add(lblWants, gbc_lblWants);
		
		labelSavings = new JLabel("Savings - 20%");
		GridBagConstraints gbc_labelSavings = new GridBagConstraints();
		gbc_labelSavings.insets = new Insets(0, 0, 5, 5);
		gbc_labelSavings.gridx = 7;
		gbc_labelSavings.gridy = 1;
		budgetPanel.add(labelSavings, gbc_labelSavings);
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 2;
		budgetPanel.add(scrollPane_1, gbc_scrollPane_1);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setEnabled(false);
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 3;
		gbc_scrollPane_2.gridy = 2;
		budgetPanel.add(scrollPane_2, gbc_scrollPane_2);
		
		scrollPane = new JScrollPane();
		scrollPane.setEnabled(false);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 5;
		gbc_scrollPane.gridy = 2;
		budgetPanel.add(scrollPane, gbc_scrollPane);
		
		scrollPane_3 = new JScrollPane();
		scrollPane_3.setEnabled(false);
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.gridx = 7;
		gbc_scrollPane_3.gridy = 2;
		budgetPanel.add(scrollPane_3, gbc_scrollPane_3);
		
		panel = new JPanel();
		panel.setLayout(null);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 3;
		budgetPanel.add(panel, gbc_panel);
		
		textField_3 = new JTextField();
		textField_3.setBounds(99, 6, 130, 26);
		panel.add(textField_3);
		textField_3.setColumns(10);
		
		textField_4 = new JTextField();
		textField_4.setBounds(99, 29, 130, 26);
		panel.add(textField_4);
		textField_4.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Title");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(26, 11, 61, 16);
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Total Incomes");
		lblNewLabel_2.setBounds(6, 34, 95, 16);
		panel.add(lblNewLabel_2);
		
		btnNewButton_2 = new JButton("Add");
		btnNewButton_2.setBounds(112, 60, 117, 29);
		panel.add(btnNewButton_2);
		
		btnNewButton_3 = new JButton("Exit Edit");
		btnNewButton_3.setBounds(0, 60, 117, 29);
		panel.add(btnNewButton_3);
		
		panel_1 = new JPanel();
		panel_1.setLayout(null);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 5;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 3;
		gbc_panel_1.gridy = 3;
		budgetPanel.add(panel_1, gbc_panel_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(83, 25, 154, 26);
		panel_1.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(83, 6, 154, 26);
		panel_1.add(textField_2);
		textField_2.setColumns(10);
		
		JButton btnNewButton = new JButton("Add");
		btnNewButton.setBounds(120, 82, 117, 29);
		panel_1.add(btnNewButton);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(83, 56, 162, 27);
		panel_1.add(comboBox);
		
		JButton btnNewButton_1 = new JButton("Exit edit");
		btnNewButton_1.setBounds(6, 82, 117, 29);
		panel_1.add(btnNewButton_1);
		
		lblNewLabel_3 = new JLabel("Title");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(6, 11, 61, 16);
		panel_1.add(lblNewLabel_3);
		
		lblNewLabel_4 = new JLabel("Amount");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setBounds(6, 30, 61, 16);
		panel_1.add(lblNewLabel_4);
		
		lblNewLabel_5 = new JLabel("");
		lblNewLabel_5.setBounds(176, 57, 61, 16);
		panel_1.add(lblNewLabel_5);
		
		lblNewLabel_6 = new JLabel("Type");
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_6.setBounds(6, 60, 61, 16);
		panel_1.add(lblNewLabel_6);
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
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
