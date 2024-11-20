package ast.projects.appbudget.views;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ast.projects.appbudget.controllers.BudgetController;
import ast.projects.appbudget.controllers.ExpenseItemController;
import ast.projects.appbudget.controllers.UserController;
import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
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
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import ast.project.appbudget.utils.TextFieldsValidatorUtils;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BudgetAppSwingView extends JFrame implements BudgetAppView {

	private static final long serialVersionUID = 1L;
	

	// JList
	private JList<User> listUsers;
	DefaultListModel<User> listUsersModel = new DefaultListModel<>();
	private JList<Budget> listBudgets;
	DefaultListModel<Budget> listBudgetsModel = new DefaultListModel<>();
	private JList<ExpenseItem> listNeeds;
	DefaultListModel<ExpenseItem> listNeedsModel = new DefaultListModel<>();
	private JList<ExpenseItem> listWants;
	DefaultListModel<ExpenseItem> listWantsModel = new DefaultListModel<>();
	private JList<ExpenseItem> listSavings;
	DefaultListModel<ExpenseItem> listSavingsModel = new DefaultListModel<>();

	// Controller
	private transient UserController userController;
	private transient BudgetController budgetController;
	private transient ExpenseItemController expenseItemController;

	ExpenseItem currentExpenseItem = null;

	// JPanel
	private JPanel contentPane;
	private JPanel panelBudgets;
	private JPanel panelBudgetForm;
	private JPanel panelUsers;
	private JPanel panelExpenseForm;

	// JButton
	private JButton btnUserExit;
	private JButton btnBudgetsOpen;
	private JButton btnUserDelete;
	private JButton btnUserAdd;
	private JButton btnExpenseAdd;
	private JButton btnExpenseDelete;
	private JButton btnExpenseModify;
	private JButton btnBudgetDelete;
	private JButton btnBudgetAdd;
	private JButton btnBudgetModify;

	// JTextField
	private JTextField textFieldBudgetTitle;
	private JTextField textFieldBudgetIncomes;
	private JTextField textFieldExpenseItemTitle;
	private JTextField textFieldExpenseItemAmount;
	private JTextField textFieldUserName;
	private JTextField textFieldUserSurname;

	// JLabel
	private JLabel lblUserDetails;
	private JLabel lblExpenseError;
	private JLabel lblBudgetError;
	private JLabel lblUserError;
	private JLabel lblUserName;
	private JLabel lblUserSurname;
	private JLabel lblBudgetTitle;
	private JLabel lblBudgetIncomes;
	private JLabel lblExpenseItemTitle;
	private JLabel lblExpenseItemAmount;
	private JLabel lblExpenseItemType;
	
	// Combobox
	private JComboBox<ast.projects.appbudget.models.Type> comboBoxExpenseItemType;

	public BudgetAppSwingView() {
		setTitle("AppBudget");
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 746, 509);
		
		//Panel
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new CardLayout(0, 0));
		setContentPane(contentPane);

		panelUsers = new JPanel();
		panelUsers.setName("panelUsers");
		contentPane.add(panelUsers, "usersCard");
		GridBagLayout gbl_panelUsers = new GridBagLayout();
		gbl_panelUsers.columnWidths = new int[] { 72, 100, 61, 120, 86, 0 };
		gbl_panelUsers.rowHeights = new int[] { 107, 64, 168, 29, 239, 0 };
		gbl_panelUsers.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelUsers.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelUsers.setLayout(gbl_panelUsers);
		
		panelBudgets = new JPanel();
		panelBudgets.setName("panelBudgets");
		contentPane.add(panelBudgets, "budgetsCard");
		GridBagLayout gbl_panelBudgets = new GridBagLayout();
		gbl_panelBudgets.columnWidths = new int[] { 140, 106, 36, 140, 140, 140, 0 };
		gbl_panelBudgets.rowHeights = new int[] { 0, 283, 154, 0 };
		gbl_panelBudgets.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelBudgets.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelBudgets.setLayout(gbl_panelBudgets);
		
		panelBudgetForm = new JPanel();
		panelBudgetForm.setLayout(null);
		GridBagConstraints gbc_panelBudgetForm = new GridBagConstraints();
		gbc_panelBudgetForm.gridwidth = 2;
		gbc_panelBudgetForm.insets = new Insets(0, 0, 0, 5);
		gbc_panelBudgetForm.fill = GridBagConstraints.BOTH;
		gbc_panelBudgetForm.gridx = 0;
		gbc_panelBudgetForm.gridy = 2;
		panelBudgets.add(panelBudgetForm, gbc_panelBudgetForm);
		
		panelExpenseForm = new JPanel();
		panelExpenseForm.setLayout(null);
		GridBagConstraints gbc_panelExpenseForm = new GridBagConstraints();
		gbc_panelExpenseForm.gridwidth = 3;
		gbc_panelExpenseForm.insets = new Insets(0, 0, 0, 5);
		gbc_panelExpenseForm.fill = GridBagConstraints.BOTH;
		gbc_panelExpenseForm.gridx = 3;
		gbc_panelExpenseForm.gridy = 2;
		panelBudgets.add(panelExpenseForm, gbc_panelExpenseForm);
		
		//Labels
		lblUserName = new JLabel("Name");
		GridBagConstraints gbc_lblUserName = new GridBagConstraints();
		gbc_lblUserName.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblUserName.insets = new Insets(0, 0, 5, 5);
		gbc_lblUserName.gridx = 0;
		gbc_lblUserName.gridy = 1;
		panelUsers.add(lblUserName, gbc_lblUserName);
		
		lblUserSurname = new JLabel("Surname");
		GridBagConstraints gbc_lblUserSurname = new GridBagConstraints();
		gbc_lblUserSurname.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblUserSurname.insets = new Insets(0, 0, 5, 5);
		gbc_lblUserSurname.gridx = 2;
		gbc_lblUserSurname.gridy = 1;
		panelUsers.add(lblUserSurname, gbc_lblUserSurname);
		
		lblUserError = new JLabel("");
		lblUserError.setName("lblUserError");
		GridBagConstraints gbc_lblUserError = new GridBagConstraints();
		gbc_lblUserError.fill = GridBagConstraints.BOTH;
		gbc_lblUserError.gridwidth = 5;
		gbc_lblUserError.gridx = 0;
		gbc_lblUserError.gridy = 4;
		panelUsers.add(lblUserError, gbc_lblUserError);
		
		lblUserDetails = new JLabel("");
		lblUserDetails.setName("lblUserDetails");
		GridBagConstraints gbc_lblUserDetails = new GridBagConstraints();
		gbc_lblUserDetails.insets = new Insets(0, 0, 5, 5);
		gbc_lblUserDetails.gridx = 0;
		gbc_lblUserDetails.gridy = 0;
		panelBudgets.add(lblUserDetails, gbc_lblUserDetails);
		
		lblBudgetTitle = new JLabel("Title");
		lblBudgetTitle.setName("lblBudgetTitle");
		lblBudgetTitle.setHorizontalAlignment(SwingConstants.LEFT);
		lblBudgetTitle.setBounds(6, 11, 61, 16);
		panelBudgetForm.add(lblBudgetTitle);

		lblBudgetIncomes = new JLabel("Incomes");
		lblBudgetIncomes.setName("lblBudgetIncomes");
		lblBudgetIncomes.setHorizontalAlignment(SwingConstants.LEFT);
		lblBudgetIncomes.setBounds(6, 38, 61, 16);
		panelBudgetForm.add(lblBudgetIncomes);
		
		lblBudgetError = new JLabel("");
		lblBudgetError.setName("lblBudgetError");
		lblBudgetError.setBounds(6, 121, 229, 16);
		panelBudgetForm.add(lblBudgetError);
		
		lblExpenseItemTitle = new JLabel("Title");
		lblExpenseItemTitle.setName("lblExpenseItemTitle");
		lblExpenseItemTitle.setBounds(0, 6, 33, 16);
		panelExpenseForm.add(lblExpenseItemTitle);

		lblExpenseItemAmount = new JLabel("Amount");
		lblExpenseItemAmount.setName("lblExpenseItemAmount");
		lblExpenseItemAmount.setBounds(0, 33, 61, 16);
		panelExpenseForm.add(lblExpenseItemAmount);

		lblExpenseItemType = new JLabel("Type");
		lblExpenseItemType.setName("lblExpenseItemType");
		lblExpenseItemType.setBounds(0, 61, 61, 16);
		panelExpenseForm.add(lblExpenseItemType);

		lblExpenseError = new JLabel("");
		lblExpenseError.setName("lblExpenseError");
		lblExpenseError.setBounds(0, 124, 443, 16);
		panelExpenseForm.add(lblExpenseError);
		
		//Text fields
		KeyAdapter btnUserAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnUserAdd.setEnabled(TextFieldsValidatorUtils.validateUserNameTextField(textFieldUserName.getText())
						&& TextFieldsValidatorUtils.validateUserSurnameTextField(textFieldUserSurname.getText()));
			}
		};
		
		KeyAdapter btnBudgetEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnBudgetAdd.setEnabled(
						TextFieldsValidatorUtils.validateBudgetTitleTextField(textFieldBudgetTitle.getText())
						&& TextFieldsValidatorUtils.validateBudgetIncomesTextField(textFieldBudgetIncomes.getText()));

				btnBudgetModify.setEnabled(
						listBudgets.getSelectedIndex() != -1
						&& TextFieldsValidatorUtils.validateBudgetTitleTextField(textFieldBudgetTitle.getText())
						&& TextFieldsValidatorUtils.validateBudgetIncomesTextField(textFieldBudgetIncomes.getText()));
			}
		};
		
		KeyAdapter btnExpenseEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnExpenseAdd.setEnabled(
						TextFieldsValidatorUtils.validateExpenseItemTitleTextField(textFieldExpenseItemTitle.getText())
						&& TextFieldsValidatorUtils.validateExpenseItemTitleTextField(textFieldExpenseItemAmount.getText()));
				btnModifyExpenseEnabler();
			}
		};

		
		textFieldUserName = new JTextField();
		textFieldUserName.setName("textFieldUserName");
		GridBagConstraints gbc_textFieldUserName = new GridBagConstraints();
		gbc_textFieldUserName.anchor = GridBagConstraints.NORTH;
		gbc_textFieldUserName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUserName.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUserName.gridx = 1;
		gbc_textFieldUserName.gridy = 1;
		panelUsers.add(textFieldUserName, gbc_textFieldUserName);
		textFieldUserName.setColumns(10);
		textFieldUserName.addKeyListener(btnUserAddEnabler);

		textFieldUserSurname = new JTextField();
		textFieldUserSurname.setName("textFieldUserSurname");
		GridBagConstraints gbc_textFieldUserSurname = new GridBagConstraints();
		gbc_textFieldUserSurname.anchor = GridBagConstraints.NORTH;
		gbc_textFieldUserSurname.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUserSurname.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUserSurname.gridx = 3;
		gbc_textFieldUserSurname.gridy = 1;
		panelUsers.add(textFieldUserSurname, gbc_textFieldUserSurname);
		textFieldUserSurname.setColumns(10);
		textFieldUserSurname.addKeyListener(btnUserAddEnabler);

		textFieldBudgetTitle = new JTextField();
		textFieldBudgetTitle.setName("textFieldBudgetTitle");
		textFieldBudgetTitle.setBounds(38, 6, 130, 26);
		panelBudgetForm.add(textFieldBudgetTitle);
		textFieldBudgetTitle.setColumns(10);
		textFieldBudgetTitle.addKeyListener(btnBudgetEnabler);

		textFieldBudgetIncomes = new JTextField();
		textFieldBudgetIncomes.setName("textFieldBudgetIncomes");
		textFieldBudgetIncomes.setBounds(62, 33, 130, 26);
		panelBudgetForm.add(textFieldBudgetIncomes);
		textFieldBudgetIncomes.setColumns(10);
		textFieldBudgetIncomes.addKeyListener(btnBudgetEnabler);
		
		textFieldExpenseItemTitle = new JTextField();
		textFieldExpenseItemTitle.setName("textFieldExpenseItemTitle");
		textFieldExpenseItemTitle.setEnabled(false);
		textFieldExpenseItemTitle.setBounds(35, 1, 130, 26);
		panelExpenseForm.add(textFieldExpenseItemTitle);
		textFieldExpenseItemTitle.setColumns(10);
		textFieldExpenseItemTitle.addKeyListener(btnExpenseEnabler);

		textFieldExpenseItemAmount = new JTextField();
		textFieldExpenseItemAmount.setName("textFieldExpenseItemAmount");
		textFieldExpenseItemAmount.setEnabled(false);
		textFieldExpenseItemAmount.setBounds(55, 28, 130, 26);
		panelExpenseForm.add(textFieldExpenseItemAmount);
		textFieldExpenseItemAmount.setColumns(10);
		textFieldExpenseItemAmount.addKeyListener(btnExpenseEnabler);
		
		//ComboBox
		comboBoxExpenseItemType = new JComboBox<ast.projects.appbudget.models.Type>();
		comboBoxExpenseItemType.setName("comboBoxExpenseItemType");
		comboBoxExpenseItemType.setModel(new DefaultComboBoxModel<ast.projects.appbudget.models.Type>(
				ast.projects.appbudget.models.Type.values()));
		comboBoxExpenseItemType.setEnabled(false);
		comboBoxExpenseItemType.setBounds(35, 57, 135, 27);
		panelExpenseForm.add(comboBoxExpenseItemType);
		
		//Buttons
		btnUserAdd = new JButton("Add");
		btnUserAdd.setName("btnUserAdd");
		btnUserAdd.setEnabled(false);
		GridBagConstraints gbc_btnUserAdd = new GridBagConstraints();
		gbc_btnUserAdd.fill = GridBagConstraints.BOTH;
		gbc_btnUserAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnUserAdd.gridx = 4;
		gbc_btnUserAdd.gridy = 1;
		panelUsers.add(btnUserAdd, gbc_btnUserAdd);
		btnUserAdd.addActionListener(e -> userController.addUser(new User(textFieldUserName.getText(), textFieldUserSurname.getText())));
		
		btnUserDelete = new JButton("Delete User");
		btnUserDelete.setName("btnUserDelete");
		btnUserDelete.setEnabled(false);
		GridBagConstraints gbc_btnUserDelete = new GridBagConstraints();
		gbc_btnUserDelete.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnUserDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnUserDelete.gridx = 3;
		gbc_btnUserDelete.gridy = 3;
		panelUsers.add(btnUserDelete, gbc_btnUserDelete);
		btnUserDelete.addActionListener(e -> userController.deleteUser(listUsers.getSelectedValue()));
		
		btnBudgetsOpen = new JButton("Open Budgets");
		btnBudgetsOpen.setName("btnBudgetsOpen");
		btnBudgetsOpen.setEnabled(false);
		GridBagConstraints gbc_btnBudgetsOpen = new GridBagConstraints();
		gbc_btnBudgetsOpen.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnBudgetsOpen.insets = new Insets(0, 0, 5, 5);
		gbc_btnBudgetsOpen.gridx = 1;
		gbc_btnBudgetsOpen.gridy = 3;
		panelUsers.add(btnBudgetsOpen, gbc_btnBudgetsOpen);
		btnBudgetsOpen.addActionListener(e -> openUserBudgets(listUsers.getSelectedValue()));
		
		btnUserExit = new JButton("Exit user");
		btnUserExit.setName("btnUserExit");
		GridBagConstraints gbc_btnUserExit = new GridBagConstraints();
		gbc_btnUserExit.anchor = GridBagConstraints.EAST;
		gbc_btnUserExit.insets = new Insets(0, 0, 5, 5);
		gbc_btnUserExit.gridx = 1;
		gbc_btnUserExit.gridy = 0;
		panelBudgets.add(btnUserExit, gbc_btnUserExit);
		btnUserExit.addActionListener(e -> exitUser());
		
		btnBudgetModify = new JButton("Modify Budget");
		btnBudgetModify.setName("btnBudgetModify");
		btnBudgetModify.setEnabled(false);
		btnBudgetModify.setBounds(0, 58, 117, 29);
		panelBudgetForm.add(btnBudgetModify);
		btnBudgetModify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Budget budgetToModify = listBudgets.getSelectedValue();
				budgetToModify.setTitle(textFieldBudgetTitle.getText());
				budgetToModify.setIncomes(Double.parseDouble(textFieldBudgetIncomes.getText()));
				budgetController.updateBudget(budgetToModify);
			}
		});
		
		btnBudgetAdd = new JButton("Add Budget");
		btnBudgetAdd.setName("btnBudgetAdd");
		btnBudgetAdd.setEnabled(false);
		btnBudgetAdd.setBounds(118, 58, 117, 29);
		panelBudgetForm.add(btnBudgetAdd);
		btnBudgetAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				budgetController.addBudget(new Budget(textFieldBudgetTitle.getText(),
						Double.parseDouble(textFieldBudgetIncomes.getText())));
			}
		});

		btnBudgetDelete = new JButton("Delete Budget");
		btnBudgetDelete.setName("btnBudgetDelete");
		btnBudgetDelete.setEnabled(false);
		btnBudgetDelete.setBounds(62, 88, 117, 29);
		panelBudgetForm.add(btnBudgetDelete);
		btnBudgetDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				budgetController.deleteBudget(listBudgets.getSelectedValue());
			}
		});
		
		btnExpenseAdd = new JButton("Add Expense");
		btnExpenseAdd.setEnabled(false);
		btnExpenseAdd.setBounds(0, 83, 117, 29);
		panelExpenseForm.add(btnExpenseAdd);
		btnExpenseAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				expenseItemController.addExpenseItem(new ExpenseItem(textFieldExpenseItemTitle.getText(),
						(ast.projects.appbudget.models.Type) comboBoxExpenseItemType.getSelectedItem(),
						Double.parseDouble(textFieldExpenseItemAmount.getText())));
			}
		});

		btnExpenseDelete = new JButton("Delete Expense");
		btnExpenseDelete.setEnabled(false);
		btnExpenseDelete.setBounds(231, 83, 130, 29);
		panelExpenseForm.add(btnExpenseDelete);
		btnExpenseDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				expenseItemController.deleteExpenseItem(currentExpenseItem);
			}
		});
		
		btnExpenseModify = new JButton("Modify Expense");
		btnExpenseModify.setEnabled(false);
		btnExpenseModify.setBounds(111, 83, 130, 29);
		panelExpenseForm.add(btnExpenseModify);
		btnExpenseModify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentExpenseItem.setTitle(textFieldExpenseItemTitle.getText());
				currentExpenseItem.setAmount(Double.parseDouble(textFieldExpenseItemAmount.getText()));
				currentExpenseItem
						.setType((ast.projects.appbudget.models.Type) comboBoxExpenseItemType.getSelectedItem());

				expenseItemController.updateExpenseItem(currentExpenseItem);
			}
		});
		
		//Lists
		listUsers = new JList<>(listUsersModel);
		listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listUsers.setName("listUsers");
		GridBagConstraints gbcListUsers = new GridBagConstraints();
		gbcListUsers.fill = GridBagConstraints.BOTH;
		gbcListUsers.insets = new Insets(0, 0, 5, 5);
		gbcListUsers.gridwidth = 3;
		gbcListUsers.gridx = 1;
		gbcListUsers.gridy = 2;
		panelUsers.add(listUsers, gbcListUsers);
		listUsers.addListSelectionListener(
				e -> {
					btnBudgetsOpen.setEnabled(listUsers.getSelectedIndex() != -1);
					btnUserDelete.setEnabled(listUsers.getSelectedIndex() != -1);
		});

		listBudgets = new JList<>(listBudgetsModel);
		listBudgets.setName("listBudgets");
		GridBagConstraints gbc_listBudgets = new GridBagConstraints();
		gbc_listBudgets.gridwidth = 2;
		listBudgets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gbc_listBudgets.insets = new Insets(0, 0, 5, 5);
		gbc_listBudgets.fill = GridBagConstraints.BOTH;
		gbc_listBudgets.gridx = 0;
		gbc_listBudgets.gridy = 1;
		panelBudgets.add(listBudgets, gbc_listBudgets);
		listBudgets.addListSelectionListener(e -> {
			btnBudgetModify.setEnabled(
					listBudgets.getSelectedIndex() != -1
					&& TextFieldsValidatorUtils.validateBudgetTitleTextField(textFieldBudgetTitle.getText())
					&& TextFieldsValidatorUtils.validateBudgetIncomesTextField(textFieldBudgetIncomes.getText()));

			btnBudgetDelete.setEnabled(listBudgets.getSelectedIndex() != -1);
			openBudget(listBudgets.getSelectedValue());
		});

		listNeeds = new JList<>(listNeedsModel);
		listNeeds.setName("listNeeds");
		GridBagConstraints gbc_listNeeds = new GridBagConstraints();
		gbc_listNeeds.insets = new Insets(0, 0, 5, 5);
		gbc_listNeeds.fill = GridBagConstraints.BOTH;
		gbc_listNeeds.gridx = 3;
		gbc_listNeeds.gridy = 1;
		panelBudgets.add(listNeeds, gbc_listNeeds);
		listNeeds.addListSelectionListener(e -> {
			listWants.clearSelection();
			listSavings.clearSelection();

			currentExpenseItem = listNeeds.getSelectedValue();

			btnModifyExpenseEnabler();
			btnExpenseDelete.setEnabled(listNeeds.getSelectedIndex() != -1);
		});

		listWants = new JList<>(listWantsModel);
		listWants.setName("listWants");
		GridBagConstraints gbc_listWants = new GridBagConstraints();
		gbc_listWants.insets = new Insets(0, 0, 5, 5);
		gbc_listWants.fill = GridBagConstraints.BOTH;
		gbc_listWants.gridx = 4;
		gbc_listWants.gridy = 1;
		panelBudgets.add(listWants, gbc_listWants);
		listWants.addListSelectionListener(e -> {
			listNeeds.clearSelection();
			listSavings.clearSelection();

			currentExpenseItem = listWants.getSelectedValue();

			btnModifyExpenseEnabler();
			btnExpenseDelete.setEnabled(listWants.getSelectedIndex() != -1);
		});

		listSavings = new JList<>(listSavingsModel);
		listSavings.setName("listSavings");
		GridBagConstraints gbc_listSavings = new GridBagConstraints();
		gbc_listSavings.insets = new Insets(0, 0, 5, 0);
		gbc_listSavings.fill = GridBagConstraints.BOTH;
		gbc_listSavings.gridx = 5;
		gbc_listSavings.gridy = 1;
		panelBudgets.add(listSavings, gbc_listSavings);

		listSavings.addListSelectionListener(e -> {
			listNeeds.clearSelection();
			listWants.clearSelection();

			currentExpenseItem = listSavings.getSelectedValue();

			btnModifyExpenseEnabler();
			btnExpenseDelete.setEnabled(listSavings.getSelectedIndex() != -1);
		});




	}

	// Budget Controller Setter
	public void setUserController(UserController userController) {
		this.userController = userController;
		userController.allUsers();
	}

	public void setBudgetController(BudgetController budgetController) {
		this.budgetController = budgetController;
	}

	public void setExpenseItemController(ExpenseItemController expenseItemController) {
		this.expenseItemController = expenseItemController;
	}

	// Package private getter for testing purpose
	
	JLabel getLblErrorMessage() {
		return lblUserError;
	}

	JLabel getLblBudgetError() {
		return lblBudgetError;
	}

	JLabel getLblExpenseError() {
		return lblExpenseError;
	}

	DefaultListModel<User> getListUsersModel() {
		return listUsersModel;
	}

	DefaultListModel<Budget> getListBudgetsModel() {
		return listBudgetsModel;
	}

	DefaultListModel<ExpenseItem> getListNeedsModel() {
		return listNeedsModel;
	}

	DefaultListModel<ExpenseItem> getListWantsModel() {
		return listWantsModel;
	}

	DefaultListModel<ExpenseItem> getListSavingsModel() {
		return listSavingsModel;
	}

	JTextField getTextFieldExpenseItemTitle() {
		return textFieldExpenseItemTitle;
	}

	JTextField getTextFieldExpenseItemAmount() {
		return textFieldExpenseItemAmount;
	}

	JComboBox<ast.projects.appbudget.models.Type> getComboBoxExpenseItemType() {
		return comboBoxExpenseItemType;
	}
	
	//View methods implementation

	@Override
	public void refreshUsersList(List<User> users) {
		listUsersModel.clear();
		users.forEach(listUsersModel::addElement);
	}

	@Override
	public void showUserErrorMessage(String message) {
		lblUserError.setText(message);
	}

	@Override
	public void resetUserErrorMessage() {
		lblUserError.setText("");
	}

	@Override
	public void refreshBudgetsList(List<Budget> budgets) {
		listBudgetsModel.clear();
		budgets.forEach(listBudgetsModel::addElement);
	}

	@Override
	public void showBudgetErrorMessage(String message) {
		lblBudgetError.setText(message);
	}

	@Override
	public void resetBudgetErrorMessage() {
		lblBudgetError.setText("");
	}

	@Override
	public void refreshExpenseItemsLists(List<ExpenseItem> expenseItems) {
		listNeedsModel.clear();
		listWantsModel.clear();
		listSavingsModel.clear();

		expenseItems.stream().filter(e -> e.getType() == ast.projects.appbudget.models.Type.NEEDS)
				.forEach(listNeedsModel::addElement);
		expenseItems.stream().filter(e -> e.getType() == ast.projects.appbudget.models.Type.WANTS)
				.forEach(listWantsModel::addElement);
		expenseItems.stream().filter(e -> e.getType() == ast.projects.appbudget.models.Type.SAVINGS)
				.forEach(listSavingsModel::addElement);
	}

	@Override
	public void showExpenseItemErrorMessage(String message) {
		lblExpenseError.setText(message);
	}

	@Override
	public void resetExpenseItemErrorMessage() {
		lblExpenseError.setText("");
	}
	
	//Private methods for managing user behavior
	
	private void btnModifyExpenseEnabler() {
		btnExpenseModify.setEnabled((listSavings.getSelectedIndex() != -1 || listWants.getSelectedIndex() != -1
				|| listNeeds.getSelectedIndex() != -1)
				&& TextFieldsValidatorUtils.validateExpenseItemTitleTextField(textFieldExpenseItemTitle.getText())
				&& TextFieldsValidatorUtils.validateExpenseItemTitleTextField(textFieldExpenseItemAmount.getText()));
	}

	private void openUserBudgets(User user) {
		lblUserDetails.setText("Current user: " + user.toString());
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
		cardLayout.show(contentPane, "budgetsCard");

		user.getBudgets().stream().forEach(listBudgetsModel::addElement);
	}

	private void openBudget(Budget budget) {

		if (budget != null) {
			listNeedsModel.clear();
			listWantsModel.clear();
			listSavingsModel.clear();

			budget.getExpenseItems().stream()
					.filter(expenseItem -> expenseItem.getType() == ast.projects.appbudget.models.Type.NEEDS)
					.forEach(listNeedsModel::addElement);
			budget.getExpenseItems().stream()
					.filter(expenseItem -> expenseItem.getType() == ast.projects.appbudget.models.Type.WANTS)
					.forEach(listWantsModel::addElement);
			budget.getExpenseItems().stream()
					.filter(expenseItem -> expenseItem.getType() == ast.projects.appbudget.models.Type.SAVINGS)
					.forEach(listSavingsModel::addElement);
			textFieldExpenseItemTitle.setEnabled(true);
			textFieldExpenseItemAmount.setEnabled(true);
			comboBoxExpenseItemType.setEnabled(true);
		} else {
			listNeedsModel.clear();
			listWantsModel.clear();
			listSavingsModel.clear();
			textFieldExpenseItemTitle.setEnabled(false);
			textFieldExpenseItemAmount.setEnabled(false);
			comboBoxExpenseItemType.setEnabled(false);
		}

	}

	private void exitUser() {

		listBudgetsModel.clear();
		listNeedsModel.clear();
		listWantsModel.clear();
		listSavingsModel.clear();

		btnExpenseDelete.setEnabled(false);
		btnExpenseModify.setEnabled(false);
		btnExpenseAdd.setEnabled(false);

		btnBudgetDelete.setEnabled(false);
		btnBudgetModify.setEnabled(false);
		btnBudgetAdd.setEnabled(false);

		lblExpenseError.setText("");
		lblBudgetError.setText("");
		lblUserDetails.setText("");

		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
		cardLayout.show(contentPane, "usersCard");

	}

}
