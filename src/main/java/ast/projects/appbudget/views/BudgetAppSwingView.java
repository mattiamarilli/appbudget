package ast.projects.appbudget.views;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

import java.util.Collections;
import java.util.List;

import javax.swing.ListSelectionModel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import ast.project.appbudget.utils.TextFieldsValidatorUtils;

/**
 * This class represents the Swing-based graphical user interface (GUI) for the budget application. 
 * It extends JFrame to provide a window for the application and implements the BudgetAppView interface 
 * to define the methods for interacting with the application's data and displaying information to the user.
 */
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

	private transient User currentUser = null;
	private transient Budget currentBudget = null;
	private transient ExpenseItem currentExpenseItem = null;
	
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
	
	private JLabel labelNeedsInfo;
	private JLabel lblWantsInfo;
	private JLabel lblSavingsInfo;
	
	// Combobox
	private JComboBox<ast.projects.appbudget.models.Type> comboBoxExpenseItemType;

	/**
	 * Default constructor for BudgetAppSwingView. 
	 * 
	 * This constructor initializes the GUI components of the application, 
	 * such as panels, buttons, text fields, and tables, and sets up the initial layout 
	 * and appearance of the application window. 
	 * Establish event listeners for user interactions with the GUI elements.
	 */

	public BudgetAppSwingView() {
		super.setTitle("AppBudget");
		super.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		super.setBounds(100, 100, 855, 499);
		super.setSize(855, 499);
		super.setResizable(false);
		
		//Panel
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new CardLayout(0, 0));
		setContentPane(contentPane);

		panelUsers = new JPanel();
		panelUsers.setName("panelUsers");
		contentPane.add(panelUsers, "usersCard");
		GridBagLayout gblPanelUsers = new GridBagLayout();
		gblPanelUsers.columnWidths = new int[] { 76, 323, 38, 350, 86, 0 };
		gblPanelUsers.rowHeights = new int[] { 30, 29, 328, 29, 92, 0 };
		gblPanelUsers.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gblPanelUsers.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panelUsers.setLayout(gblPanelUsers);
		
		panelBudgets = new JPanel();
		panelBudgets.setName("panelBudgets");
		contentPane.add(panelBudgets, "budgetsCard");
		GridBagLayout gblPanelBudgets = new GridBagLayout();
		gblPanelBudgets.columnWidths = new int[] { 140, 106, 36, 140, 140, 140, 0 };
		gblPanelBudgets.rowHeights = new int[] { 0, 283, 154, 0 };
		gblPanelBudgets.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gblPanelBudgets.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelBudgets.setLayout(gblPanelBudgets);
		
		panelBudgetForm = new JPanel();
		panelBudgetForm.setLayout(null);
		GridBagConstraints gbcPanelBudgetForm = new GridBagConstraints();
		gbcPanelBudgetForm.gridwidth = 2;
		gbcPanelBudgetForm.insets = new Insets(0, 0, 0, 5);
		gbcPanelBudgetForm.fill = GridBagConstraints.BOTH;
		gbcPanelBudgetForm.gridx = 0;
		gbcPanelBudgetForm.gridy = 2;
		panelBudgets.add(panelBudgetForm, gbcPanelBudgetForm);
		
		panelExpenseForm = new JPanel();
		panelExpenseForm.setLayout(null);
		GridBagConstraints gbcPanelExpenseForm = new GridBagConstraints();
		gbcPanelExpenseForm.gridwidth = 3;
		gbcPanelExpenseForm.fill = GridBagConstraints.BOTH;
		gbcPanelExpenseForm.gridx = 3;
		gbcPanelExpenseForm.gridy = 2;
		panelBudgets.add(panelExpenseForm, gbcPanelExpenseForm);
		
		//Labels
		lblUserName = new JLabel("Name");
		GridBagConstraints gbcLblUserName = new GridBagConstraints();
		gbcLblUserName.fill = GridBagConstraints.HORIZONTAL;
		gbcLblUserName.insets = new Insets(0, 0, 5, 5);
		gbcLblUserName.gridx = 1;
		gbcLblUserName.gridy = 0;
		panelUsers.add(lblUserName, gbcLblUserName);
		
		lblUserSurname = new JLabel("Surname");
		GridBagConstraints gbcLblUserSurname = new GridBagConstraints();
		gbcLblUserSurname.fill = GridBagConstraints.HORIZONTAL;
		gbcLblUserSurname.insets = new Insets(0, 0, 5, 5);
		gbcLblUserSurname.gridx = 3;
		gbcLblUserSurname.gridy = 0;
		panelUsers.add(lblUserSurname, gbcLblUserSurname);
		
		lblUserError = new JLabel("");
		lblUserError.setName("lblUserError");
		GridBagConstraints gbcLblUserError = new GridBagConstraints();
		gbcLblUserError.fill = GridBagConstraints.BOTH;
		gbcLblUserError.gridwidth = 5;
		gbcLblUserError.gridx = 0;
		gbcLblUserError.gridy = 4;
		panelUsers.add(lblUserError, gbcLblUserError);
		
		lblUserDetails = new JLabel("");
		lblUserDetails.setName("lblUserDetails");
		GridBagConstraints gbcLblUserDetails = new GridBagConstraints();
		gbcLblUserDetails.insets = new Insets(0, 0, 5, 5);
		gbcLblUserDetails.gridx = 0;
		gbcLblUserDetails.gridy = 0;
		panelBudgets.add(lblUserDetails, gbcLblUserDetails);
		
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
		
		labelNeedsInfo = new JLabel("");
		labelNeedsInfo.setName("labelNeedsInfo");
		GridBagConstraints gbcLabelNeedsInfo = new GridBagConstraints();
		gbcLabelNeedsInfo.insets = new Insets(0, 0, 5, 5);
		gbcLabelNeedsInfo.gridx = 3;
		gbcLabelNeedsInfo.gridy = 0;
		panelBudgets.add(labelNeedsInfo, gbcLabelNeedsInfo);
		
		lblWantsInfo = new JLabel("");
		lblWantsInfo.setName("lblWantsInfo");
		GridBagConstraints gbcLblWantsInfo = new GridBagConstraints();
		gbcLblWantsInfo.insets = new Insets(0, 0, 5, 5);
		gbcLblWantsInfo.gridx = 4;
		gbcLblWantsInfo.gridy = 0;
		panelBudgets.add(lblWantsInfo, gbcLblWantsInfo);
		
		lblSavingsInfo = new JLabel("");
		lblSavingsInfo.setName("lblSavingsInfo");
		GridBagConstraints gbcLblSavingsInfo = new GridBagConstraints();
		gbcLblSavingsInfo.insets = new Insets(0, 0, 5, 0);
		gbcLblSavingsInfo.gridx = 5;
		gbcLblSavingsInfo.gridy = 0;
		panelBudgets.add(lblSavingsInfo, gbcLblSavingsInfo);
		
		//Text fields
		// DocumentListener for enabling btnUserAdd
		DocumentListener btnUserAddEnabler = new DocumentListener() {
		    private void updateButtonState() {
		        btnUserAdd.setEnabled(
		            TextFieldsValidatorUtils.validateUserNameTextField(textFieldUserName.getText()) &&
		            TextFieldsValidatorUtils.validateUserSurnameTextField(textFieldUserSurname.getText())
		        );
		    }

		    @Override
		    public void insertUpdate(DocumentEvent e) {
		        updateButtonState();
		    }

		    @Override
		    public void removeUpdate(DocumentEvent e) {
		        updateButtonState();
		    }

		    @Override
		    @ExcludeGeneratedFromJacocoReport
		    public void changedUpdate(DocumentEvent e) {
		    	// This method is intentionally left empty because this implementation does not handle
		        // attribute changes (e.g., font style or color) in the document. It is required to 
		        // override this method as part of the DocumentListener interface.
		    }
		};

		// DocumentListener for enabling btnBudgetAdd and btnBudgetModify
		DocumentListener btnBudgetEnabler = new DocumentListener() {
		    private void updateButtonState() {
		        btnBudgetAdd.setEnabled(
		            TextFieldsValidatorUtils.validateBudgetTitleTextField(textFieldBudgetTitle.getText()) &&
		            TextFieldsValidatorUtils.validateBudgetIncomesTextField(textFieldBudgetIncomes.getText())
		        );

		        btnBudgetModify.setEnabled(
		            listBudgets.getSelectedIndex() != -1 &&
		            TextFieldsValidatorUtils.validateBudgetTitleTextField(textFieldBudgetTitle.getText()) &&
		            TextFieldsValidatorUtils.validateBudgetIncomesTextField(textFieldBudgetIncomes.getText())
		        );
		    }

		    @Override
		    public void insertUpdate(DocumentEvent e) {
		        updateButtonState();
		    }

		    @Override
		    public void removeUpdate(DocumentEvent e) {
		        updateButtonState();
		    }

		    @Override
		    @ExcludeGeneratedFromJacocoReport
		    public void changedUpdate(DocumentEvent e) {
		    	// This method is intentionally left empty because this implementation does not handle
		        // attribute changes (e.g., font style or color) in the document. It is required to 
		        // override this method as part of the DocumentListener interface.
		    }
		};

		// DocumentListener for enabling btnExpenseAdd
		DocumentListener btnExpenseEnabler = new DocumentListener() {
		    private void updateButtonState() {
		        btnExpenseAdd.setEnabled(
		            TextFieldsValidatorUtils.validateExpenseItemTitleTextField(textFieldExpenseItemTitle.getText()) &&
		            TextFieldsValidatorUtils.validateExpenseItemAmountTextField(textFieldExpenseItemAmount.getText())
		        );
		        btnModifyExpenseEnabler();
		    }

		    @Override
		    public void insertUpdate(DocumentEvent e) {
		        updateButtonState();
		    }

		    @Override
		    public void removeUpdate(DocumentEvent e) {
		        updateButtonState();
		    }

		    @Override
		    @ExcludeGeneratedFromJacocoReport
		    public void changedUpdate(DocumentEvent e) {
		    	// This method is intentionally left empty because this implementation does not handle
		        // attribute changes (e.g., font style or color) in the document. It is required to 
		        // override this method as part of the DocumentListener interface.
		    }
		};

		
		textFieldUserName = new JTextField();
		textFieldUserName.setName("textFieldUserName");
		GridBagConstraints gbcTextFieldUserName = new GridBagConstraints();
		gbcTextFieldUserName.anchor = GridBagConstraints.NORTH;
		gbcTextFieldUserName.fill = GridBagConstraints.HORIZONTAL;
		gbcTextFieldUserName.insets = new Insets(0, 0, 5, 5);
		gbcTextFieldUserName.gridx = 1;
		gbcTextFieldUserName.gridy = 1;
		panelUsers.add(textFieldUserName, gbcTextFieldUserName);
		textFieldUserName.setColumns(10);
		textFieldUserName.getDocument().addDocumentListener(btnUserAddEnabler);

		textFieldUserSurname = new JTextField();
		textFieldUserSurname.setName("textFieldUserSurname");
		GridBagConstraints gbcTextFieldUserSurname = new GridBagConstraints();
		gbcTextFieldUserSurname.anchor = GridBagConstraints.NORTH;
		gbcTextFieldUserSurname.fill = GridBagConstraints.HORIZONTAL;
		gbcTextFieldUserSurname.insets = new Insets(0, 0, 5, 5);
		gbcTextFieldUserSurname.gridx = 3;
		gbcTextFieldUserSurname.gridy = 1;
		panelUsers.add(textFieldUserSurname, gbcTextFieldUserSurname);
		textFieldUserSurname.setColumns(10);
		textFieldUserSurname.getDocument().addDocumentListener(btnUserAddEnabler);

		textFieldBudgetTitle = new JTextField();
		textFieldBudgetTitle.setName("textFieldBudgetTitle");
		textFieldBudgetTitle.setBounds(38, 6, 130, 26);
		panelBudgetForm.add(textFieldBudgetTitle);
		textFieldBudgetTitle.setColumns(10);
		textFieldBudgetTitle.getDocument().addDocumentListener(btnBudgetEnabler);

		textFieldBudgetIncomes = new JTextField();
		textFieldBudgetIncomes.setName("textFieldBudgetIncomes");
		textFieldBudgetIncomes.setBounds(62, 33, 130, 26);
		panelBudgetForm.add(textFieldBudgetIncomes);
		textFieldBudgetIncomes.setColumns(10);
		textFieldBudgetIncomes.getDocument().addDocumentListener(btnBudgetEnabler);
		
		textFieldExpenseItemTitle = new JTextField();
		textFieldExpenseItemTitle.setName("textFieldExpenseItemTitle");
		textFieldExpenseItemTitle.setEnabled(false);
		textFieldExpenseItemTitle.setBounds(35, 1, 130, 26);
		panelExpenseForm.add(textFieldExpenseItemTitle);
		textFieldExpenseItemTitle.setColumns(10);
		textFieldExpenseItemTitle.getDocument().addDocumentListener(btnExpenseEnabler);

		textFieldExpenseItemAmount = new JTextField();
		textFieldExpenseItemAmount.setName("textFieldExpenseItemAmount");
		textFieldExpenseItemAmount.setEnabled(false);
		textFieldExpenseItemAmount.setBounds(55, 28, 130, 26);
		panelExpenseForm.add(textFieldExpenseItemAmount);
		textFieldExpenseItemAmount.setColumns(10);
		textFieldExpenseItemAmount.getDocument().addDocumentListener(btnExpenseEnabler);
		
		//ComboBox
		comboBoxExpenseItemType = new JComboBox<>();
		comboBoxExpenseItemType.setName("comboBoxExpenseItemType");
		comboBoxExpenseItemType.setModel(new DefaultComboBoxModel<>(
				ast.projects.appbudget.models.Type.values()));
		comboBoxExpenseItemType.setEnabled(false);
		comboBoxExpenseItemType.setBounds(35, 57, 135, 27);
		panelExpenseForm.add(comboBoxExpenseItemType);
		 
		//Buttons
		btnUserAdd = new JButton("Add");
		btnUserAdd.setName("btnUserAdd");
		btnUserAdd.setEnabled(false);
		GridBagConstraints gbcBtnUserAdd = new GridBagConstraints();
		gbcBtnUserAdd.fill = GridBagConstraints.BOTH;
		gbcBtnUserAdd.insets = new Insets(0, 0, 5, 0);
		gbcBtnUserAdd.gridx = 4;
		gbcBtnUserAdd.gridy = 1;
		panelUsers.add(btnUserAdd, gbcBtnUserAdd);
		btnUserAdd.addActionListener(e -> userController.addUser(new User(textFieldUserName.getText(), textFieldUserSurname.getText())));
		
		btnUserDelete = new JButton("Delete User");
		btnUserDelete.setName("btnUserDelete");
		btnUserDelete.setEnabled(false);
		GridBagConstraints gbcBtnUserDelete = new GridBagConstraints();
		gbcBtnUserDelete.anchor = GridBagConstraints.NORTHWEST;
		gbcBtnUserDelete.insets = new Insets(0, 0, 5, 5);
		gbcBtnUserDelete.gridx = 3;
		gbcBtnUserDelete.gridy = 3;
		panelUsers.add(btnUserDelete, gbcBtnUserDelete);
		btnUserDelete.addActionListener(e -> userController.deleteUser(listUsers.getSelectedValue()));
		
		btnBudgetsOpen = new JButton("Open Budgets");
		btnBudgetsOpen.setName("btnBudgetsOpen");
		btnBudgetsOpen.setEnabled(false);
		GridBagConstraints gbcBtnBudgetsOpen = new GridBagConstraints();
		gbcBtnBudgetsOpen.anchor = GridBagConstraints.NORTHEAST;
		gbcBtnBudgetsOpen.insets = new Insets(0, 0, 5, 5);
		gbcBtnBudgetsOpen.gridx = 1;
		gbcBtnBudgetsOpen.gridy = 3;
		panelUsers.add(btnBudgetsOpen, gbcBtnBudgetsOpen);
		btnBudgetsOpen.addActionListener(e -> openUserBudgets(listUsers.getSelectedValue()));
		
		btnUserExit = new JButton("Exit user");
		btnUserExit.setName("btnUserExit");
		GridBagConstraints gbcBtnUserExit = new GridBagConstraints();
		gbcBtnUserExit.anchor = GridBagConstraints.EAST;
		gbcBtnUserExit.insets = new Insets(0, 0, 5, 5);
		gbcBtnUserExit.gridx = 1;
		gbcBtnUserExit.gridy = 0;
		panelBudgets.add(btnUserExit, gbcBtnUserExit);
		btnUserExit.addActionListener(e -> exitUser());
		
		btnBudgetModify = new JButton("Modify Budget");
		btnBudgetModify.setName("btnBudgetModify");
		btnBudgetModify.setEnabled(false);
		btnBudgetModify.setBounds(0, 58, 117, 29);
		panelBudgetForm.add(btnBudgetModify);
		btnBudgetModify.addActionListener(e -> {
		    Budget budgetToModify = listBudgets.getSelectedValue();
		    budgetToModify.setTitle(textFieldBudgetTitle.getText());
		    budgetToModify.setIncomes(Double.parseDouble(textFieldBudgetIncomes.getText()));
		    budgetController.updateBudget(budgetToModify);
		});
		
		btnBudgetAdd = new JButton("Add Budget");
		btnBudgetAdd.setName("btnBudgetAdd");
		btnBudgetAdd.setEnabled(false);
		btnBudgetAdd.setBounds(118, 58, 117, 29);
		panelBudgetForm.add(btnBudgetAdd);
		btnBudgetAdd.addActionListener(e -> {
		    Budget budget = new Budget(textFieldBudgetTitle.getText(), Double.parseDouble(textFieldBudgetIncomes.getText()));
		    budget.setUserId(currentUser.getId());
		    budgetController.addBudget(budget);
		});


		btnBudgetDelete = new JButton("Delete Budget");
		btnBudgetDelete.setName("btnBudgetDelete");
		btnBudgetDelete.setEnabled(false);
		btnBudgetDelete.setBounds(62, 88, 117, 29);
		panelBudgetForm.add(btnBudgetDelete);
		btnBudgetDelete.addActionListener(e -> budgetController.deleteBudget(listBudgets.getSelectedValue()));

		
		btnExpenseAdd = new JButton("Add Expense");
		btnExpenseAdd.setEnabled(false);
		btnExpenseAdd.setBounds(0, 83, 117, 29);
		panelExpenseForm.add(btnExpenseAdd);
		btnExpenseAdd.addActionListener(e -> {
		    ExpenseItem expense = new ExpenseItem(
		        textFieldExpenseItemTitle.getText(),
		        (ast.projects.appbudget.models.Type) comboBoxExpenseItemType.getSelectedItem(),
		        Double.parseDouble(textFieldExpenseItemAmount.getText())
		    );
		    expense.setBudgetId(currentBudget.getId());
		    expenseItemController.addExpenseItem(expense);
		});


		btnExpenseDelete = new JButton("Delete Expense");
		btnExpenseDelete.setEnabled(false);
		btnExpenseDelete.setBounds(231, 83, 130, 29);
		panelExpenseForm.add(btnExpenseDelete);
		btnExpenseDelete.addActionListener(e -> expenseItemController.deleteExpenseItem(currentExpenseItem));

		
		btnExpenseModify = new JButton("Modify Expense");
		btnExpenseModify.setEnabled(false);
		btnExpenseModify.setBounds(111, 83, 130, 29);
		panelExpenseForm.add(btnExpenseModify);
		btnExpenseModify.addActionListener(e -> {
		    currentExpenseItem.setTitle(textFieldExpenseItemTitle.getText());
		    currentExpenseItem.setAmount(Double.parseDouble(textFieldExpenseItemAmount.getText()));
		    currentExpenseItem.setType((ast.projects.appbudget.models.Type) comboBoxExpenseItemType.getSelectedItem());
		    expenseItemController.updateExpenseItem(currentExpenseItem);
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
					currentUser = listUsers.getSelectedValue();
		});

		listBudgets = new JList<>(listBudgetsModel);
		listBudgets.setName("listBudgets");
		GridBagConstraints gbcListBudgets = new GridBagConstraints();
		gbcListBudgets.gridwidth = 2;
		listBudgets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gbcListBudgets.insets = new Insets(0, 0, 5, 5);
		gbcListBudgets.fill = GridBagConstraints.BOTH;
		gbcListBudgets.gridx = 0;
		gbcListBudgets.gridy = 1;
		panelBudgets.add(listBudgets, gbcListBudgets);
		listBudgets.addListSelectionListener(e -> {
			
			if (!e.getValueIsAdjusting()) {
			
			btnBudgetModify.setEnabled(
					listBudgets.getSelectedIndex() != -1
					&& TextFieldsValidatorUtils.validateBudgetIncomesTextField(textFieldBudgetIncomes.getText())
					&& TextFieldsValidatorUtils.validateBudgetTitleTextField(textFieldBudgetTitle.getText())
					);
			
			currentBudget = listBudgets.getSelectedValue();
			
			btnBudgetDelete.setEnabled(listBudgets.getSelectedIndex() != -1);
			openBudget(listBudgets.getSelectedValue());
			}
		});

		listNeeds = new JList<>(listNeedsModel);
		listNeeds.setName("listNeeds");
		GridBagConstraints gbcListNeeds = new GridBagConstraints();
		gbcListNeeds.insets = new Insets(0, 0, 5, 5);
		gbcListNeeds.fill = GridBagConstraints.BOTH;
		gbcListNeeds.gridx = 3;
		gbcListNeeds.gridy = 1;
		panelBudgets.add(listNeeds, gbcListNeeds);
		listNeeds.addListSelectionListener(e -> {
			listWants.clearSelection();
			listSavings.clearSelection();

			currentExpenseItem = listNeeds.getSelectedValue();
			
			if(currentExpenseItem != null)
			{
				textFieldExpenseItemTitle.setText(currentExpenseItem.getTitle());
				textFieldExpenseItemAmount.setText(String.valueOf(currentExpenseItem.getAmount()));
				comboBoxExpenseItemType.setSelectedIndex(0);
			}

			btnModifyExpenseEnabler();
			btnExpenseDelete.setEnabled(listNeeds.getSelectedIndex() != -1);
		});

		listWants = new JList<>(listWantsModel);
		listWants.setName("listWants");
		GridBagConstraints gbcListWants = new GridBagConstraints();
		gbcListWants.insets = new Insets(0, 0, 5, 5);
		gbcListWants.fill = GridBagConstraints.BOTH;
		gbcListWants.gridx = 4;
		gbcListWants.gridy = 1;
		panelBudgets.add(listWants, gbcListWants);
		listWants.addListSelectionListener(e -> {
			listNeeds.clearSelection();
			listSavings.clearSelection();

			currentExpenseItem = listWants.getSelectedValue();
			
			if(currentExpenseItem != null)
			{
				textFieldExpenseItemTitle.setText(currentExpenseItem.getTitle());
				textFieldExpenseItemAmount.setText(String.valueOf(currentExpenseItem.getAmount()));
				comboBoxExpenseItemType.setSelectedIndex(1);
			}

			btnModifyExpenseEnabler();
			btnExpenseDelete.setEnabled(listWants.getSelectedIndex() != -1);
		});

		listSavings = new JList<>(listSavingsModel);
		listSavings.setName("listSavings");
		GridBagConstraints gbcListSavings = new GridBagConstraints();
		gbcListSavings.insets = new Insets(0, 0, 5, 0);
		gbcListSavings.fill = GridBagConstraints.BOTH;
		gbcListSavings.gridx = 5;
		gbcListSavings.gridy = 1;
		panelBudgets.add(listSavings, gbcListSavings);

		listSavings.addListSelectionListener(e -> {
			listNeeds.clearSelection();
			listWants.clearSelection();

			currentExpenseItem = listSavings.getSelectedValue();
			
			if(currentExpenseItem != null)
			{
				textFieldExpenseItemTitle.setText(currentExpenseItem.getTitle());
				textFieldExpenseItemAmount.setText(String.valueOf(currentExpenseItem.getAmount()));
				comboBoxExpenseItemType.setSelectedIndex(2);
			}
			
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
	
	JLabel getLblUserDetails() {
		return lblUserDetails;
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

	JButton getBtnExpenseAdd() {
		return btnExpenseAdd;
	}

	JButton getBtnExpenseDelete() {
		return btnExpenseDelete;
	}

	JButton getBtnExpenseModify() {
		return btnExpenseModify;
	}

	JButton getBtnBudgetDelete() {
		return btnBudgetDelete;
	}

	JButton getBtnBudgetModify() {
		return btnBudgetModify;
	}
	
	JButton getBtnBudgetAdd() {
		return btnBudgetAdd;
	}

	void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	void setCurrentBudget(Budget currentBudget) {
		this.currentBudget = currentBudget;
	}
	
	//View methods implementation

	/**
	 * Updates the user list in the view with the provided list of users.
	 * 
	 * @param users The list of users to be displayed.
	 */
	@Override
	public void refreshUsersList(List<User> users) {
		textFieldUserName.setText("");
		textFieldUserSurname.setText("");
		listUsersModel.clear();
		users.forEach(listUsersModel::addElement);
	}

	/**
	 * Displays an error message related to users.
	 * 
	 * @param message The error message to be shown.
	 */
	@Override
	public void showUserErrorMessage(String message) {
		lblUserError.setText(message);
	}

	/**
	 * Clears any previously displayed error messages related to users.
	 */
	@Override
	public void resetUserErrorMessage() {
		lblUserError.setText("");
	}

	/**
	 * Updates the budget list in the view with the provided list of budgets.
	 * 
	 * @param budgets The list of budgets to be displayed.
	 */
	@Override
	public void refreshBudgetsList(List<Budget> budgets) {
		listBudgetsModel.clear();
		budgets.forEach(listBudgetsModel::addElement);
	}

	/**
	 * Displays an error message related to budgets.
	 * 
	 * @param message The error message to be shown.
	 */
	@Override
	public void showBudgetErrorMessage(String message) {
		lblBudgetError.setText(message);
	}

	/**
	 * Clears any previously displayed error messages related to budgets.
	 */
	@Override
	public void resetBudgetErrorMessage() {
		lblBudgetError.setText("");
	}

	/**
	 * Updates the expense item lists (Needs, Wants, Savings) in the view with the provided list of expense items.
	 * 
	 * @param expenseItems The list of expense items to be displayed.
	 */
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
		
		updateInfoLabel(currentBudget);
		
	}

	/**
	 * Displays an error message related to expense items.
	 * 
	 * @param message The error message to be displayed.
	 */
	@Override
	public void showExpenseItemErrorMessage(String message) {
		lblExpenseError.setText(message);
	}

	/**
	 * Clears any previously displayed error messages related to expense items.
	 */
	@Override
	public void resetExpenseItemErrorMessage() {
		lblExpenseError.setText("");
	}
	
	/**
	 * Clears the input fields used for entering budget information.
	 */
	@Override
	public void clearBudgetInputs() {
		textFieldBudgetTitle.setText("");
		textFieldBudgetIncomes.setText("");
	}

	/**
	 * Clears the input fields used for entering expense item information.
	 */
	@Override
	public void clearExpenseItemInputs() {
		textFieldExpenseItemTitle.setText("");
		textFieldExpenseItemAmount.setText("");
		comboBoxExpenseItemType.setSelectedIndex(0);
	}
	
	//Private methods for managing user behavior
	
	/**
	 * Enables the "Modify Expense" button only if an expense item is selected, 
	 * and both the title and amount fields contain valid input.
	 */
	private void btnModifyExpenseEnabler() {
		btnExpenseModify.setEnabled((listSavings.getSelectedIndex() != -1 || listWants.getSelectedIndex() != -1
				|| listNeeds.getSelectedIndex() != -1)
				&& TextFieldsValidatorUtils.validateExpenseItemTitleTextField(textFieldExpenseItemTitle.getText())
				&& TextFieldsValidatorUtils.validateExpenseItemAmountTextField(textFieldExpenseItemAmount.getText()));
	}
	
	/**
	 * Displays the user's budgets and updates the view with user information.
	 * 
	 * @param user The user object representing the currently logged-in user.
	 */
	private void openUserBudgets(User user) {
		lblUserDetails.setText("Current user: " + user.toString());
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
		cardLayout.show(contentPane, "budgetsCard");
		budgetController.allBudgetsByUser(user);
	}
	
	/**
	 * Calculates and displays the total expenses for Needs, Wants, and Savings categories.
	 * Also displays the recommended budget allocation based on the given budget's income.
	 *
	 * @param budget The budget object containing income information.
	 */
	private void updateInfoLabel(Budget budget) {
		
		double sumNeeds = 0;
		double sumWants = 0;
		double sumSavings = 0;
		
		List<ExpenseItem> needs = Collections.list(listNeedsModel.elements());
		for (ExpenseItem expenseItem : needs) {
			sumNeeds+=expenseItem.getAmount();
		}
		
		List<ExpenseItem> wants = Collections.list(listWantsModel.elements());
		for (ExpenseItem expenseItem : wants) {
			sumWants+=expenseItem.getAmount();
		}
		
		List<ExpenseItem> savings = Collections.list(listSavingsModel.elements());
		for (ExpenseItem expenseItem : savings) {
			sumSavings+=expenseItem.getAmount();
		}
		
		labelNeedsInfo.setText((Math.round(sumNeeds * 100.0) / 100.0) + "$/" + (Math.round(budget.getIncomes()*0.5 * 100.0) / 100.0) + "$");
		lblWantsInfo.setText((Math.round(sumWants * 100.0) / 100.0) + "$/" + (Math.round(budget.getIncomes()*0.3 * 100.0) / 100.0) + "$");
		lblSavingsInfo.setText((Math.round(sumSavings * 100.0) / 100.0) + "$/" + (Math.round(budget.getIncomes()*0.2 * 100.0) / 100.0) + "$");

	}

	/**
	 * Displays expense items for a given budget and updates the view with budget information.
	 * Handles the case where no budget is selected by clearing relevant data.
	 * 
	 * @param budget The budget selected by the user. If `null`, the user interface is reset.
	 */
	private void openBudget(Budget budget) {

		if (budget != null) {			
			expenseItemController.allExpenseItemsByBudget(budget);

			textFieldExpenseItemTitle.setEnabled(true);
			textFieldExpenseItemAmount.setEnabled(true);
			comboBoxExpenseItemType.setEnabled(true);
			
			textFieldBudgetTitle.setText(currentBudget.getTitle());
			textFieldBudgetIncomes.setText(String.valueOf(currentBudget.getIncomes()));
			textFieldExpenseItemTitle.setText("");
			textFieldExpenseItemAmount.setText("");
			comboBoxExpenseItemType.setSelectedIndex(0);
			updateInfoLabel(budget);
		} else {
			listNeedsModel.clear();
			listWantsModel.clear();
			listSavingsModel.clear();
			textFieldExpenseItemTitle.setEnabled(false);
			textFieldExpenseItemAmount.setEnabled(false);
			comboBoxExpenseItemType.setEnabled(false);
			
			textFieldBudgetTitle.setText("");
			textFieldBudgetIncomes.setText("");
			textFieldExpenseItemTitle.setText("");
			textFieldExpenseItemAmount.setText("");
			labelNeedsInfo.setText("");
			lblWantsInfo.setText("");
			lblSavingsInfo.setText("");
			comboBoxExpenseItemType.setSelectedIndex(0);
			
		}

	}
	
	/**
	 * Resets the application view to its initial state after a user logs out.
	 * Clears data displays, disables buttons, and resets text fields.
	 */

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
		
		this.textFieldBudgetTitle.setText("");
		this.textFieldBudgetIncomes.setText("");
		this.textFieldExpenseItemTitle.setText("");
		this.textFieldExpenseItemAmount.setText("");
		this.comboBoxExpenseItemType.setSelectedIndex(0);
		
		this.textFieldExpenseItemTitle.setEnabled(false);
		this.textFieldExpenseItemAmount.setEnabled(false);
		this.comboBoxExpenseItemType.setEnabled(false);
		
		this.textFieldUserName.setText("");
		this.textFieldUserSurname.setText("");

		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
		cardLayout.show(contentPane, "usersCard");
	}


}
