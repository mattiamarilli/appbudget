package ast.projects.appbudget.views;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.awt.CardLayout;
import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ast.projects.appbudget.controllers.BudgetController;
import ast.projects.appbudget.controllers.ExpenseItemController;
import ast.projects.appbudget.controllers.UserController;
import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.Type;
import ast.projects.appbudget.models.User;

import org.junit.Test;

@RunWith(GUITestRunner.class)
public class BudgetAppSwingViewTest extends AssertJSwingJUnitTestCase {


	private FrameFixture window;

	@Mock
	private UserController userController;
	
	@Mock
	private BudgetController budgetController;
	
	@Mock
	private ExpenseItemController expenseItemController;
	
	private BudgetAppSwingView budgetAppView;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			budgetAppView = new BudgetAppSwingView();
			budgetAppView.setUserController(userController);
			budgetAppView.setBudgetController(budgetController);
			budgetAppView.setExpenseItemController(expenseItemController);
			return budgetAppView;
		});
		window = new FrameFixture(robot(), budgetAppView);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	
	// Initial state 
	
	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Name"));
		window.textBox("textFieldUserName").requireEnabled();
		window.label(JLabelMatcher.withText("Surname"));
		window.textBox("textFieldUserSurname").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.list("listUsers");
		window.button(JButtonMatcher.withText("Open Budgets")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete User")).requireDisabled();
		window.label("lblUserError").requireText("");
	}

	// View behavior test

	@Test
	public void testAddButtonShouldBeEnabledOnlyWhenNameAndSurnameAreValid() {
		
		JTextComponentFixture nameTextBox = window.textBox("textFieldUserName");
		JTextComponentFixture surnameTextBox = window.textBox("textFieldUserSurname");

		nameTextBox.enterText("Mario");
		surnameTextBox.enterText("");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		
		nameTextBox.setText("");
		surnameTextBox.setText("");

		nameTextBox.enterText("");
		surnameTextBox.enterText("Rossi");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		
		nameTextBox.setText("");
		surnameTextBox.setText("");
		
		nameTextBox.enterText("Mario");
		surnameTextBox.enterText("Rossi");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	public void testOpenBudgetsAndDeleteUserButtonShouldBeEnabledOnlyWhenAUserIsSelected() {
		GuiActionRunner
			.execute(() -> budgetAppView.getListUsersModel().addElement(new User(1, "testname", "testsurname")));
		window.list("listUsers").selectItem(0);

		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete User"));
		JButtonFixture openButton = window.button(JButtonMatcher.withText("Open Budgets"));

		deleteButton.requireEnabled();
		openButton.requireEnabled();

		window.list("listUsers").clearSelection();
		deleteButton.requireDisabled();
		openButton.requireDisabled();
	}

	@Test
	public void testAddBudgetButtonShouldBeEnabledOnlyWhenTitleAndIncomesAreValid() {

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});
		
		JTextComponentFixture budgetTitleTextBox = window.textBox("textFieldBudgetTitle");
		JTextComponentFixture budgetIncomesTextBox = window.textBox("textFieldBudgetIncomes");

		budgetTitleTextBox.enterText("testtitle");
		budgetIncomesTextBox.enterText("2000");
		window.button(JButtonMatcher.withText("Add Budget")).requireEnabled();
		
		budgetTitleTextBox.setText("");
		budgetIncomesTextBox.setText("");
		
		budgetTitleTextBox.enterText("testtitle");
		budgetIncomesTextBox.enterText("");
		window.button(JButtonMatcher.withText("Add Budget")).requireDisabled();
		
		budgetTitleTextBox.setText("");
		budgetIncomesTextBox.enterText("");

		budgetTitleTextBox.enterText("");
		budgetIncomesTextBox.enterText("2000");
		window.button(JButtonMatcher.withText("Add Budget")).requireDisabled();
	}

	@Test
	public void testModifyBudgetButtonShouldBeEnabledOnlyWhenTitleAndIncomesAreValidAndABudgetIsSelected() {
		Budget budget = new Budget("testtitle", 1000);
		budget.setExpenseItems(Arrays.asList());
		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.getListBudgetsModel().addElement(budget);
		});

		window.textBox("textFieldBudgetTitle").enterText("testtitle");
		window.textBox("textFieldBudgetIncomes").enterText("2000");
		window.button(JButtonMatcher.withText("Modify Budget")).requireDisabled();
		
		window.textBox("textFieldBudgetTitle").setText("");
		window.textBox("textFieldBudgetIncomes").setText("");
		
		window.textBox("textFieldBudgetTitle").enterText("");
		window.textBox("textFieldBudgetIncomes").enterText("2000");
		window.list("listBudgets").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Budget")).requireDisabled();
		
		window.textBox("textFieldBudgetTitle").setText("");
		window.textBox("textFieldBudgetIncomes").setText("");
		
		window.textBox("textFieldBudgetTitle").enterText("testtitle");
		window.textBox("textFieldBudgetIncomes").enterText(" ");
		window.list("listBudgets").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Budget")).requireDisabled();
		
		window.textBox("textFieldBudgetTitle").setText("");
		window.textBox("textFieldBudgetIncomes").setText("");
		
		window.textBox("textFieldBudgetTitle").enterText("testtitle");
		window.textBox("textFieldBudgetIncomes").enterText("2000");
		window.list("listBudgets").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Budget")).requireEnabled();
	}

	@Test
	public void testDeleteBudgetButtonShouldBeEnabledOnlyWhenABudgetIsSelected() {
		Budget budget = new Budget("testtitle", 1000);
		budget.setExpenseItems(Arrays.asList());
		
		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.getListBudgetsModel().addElement(budget);
		});

		window.list("listBudgets").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Budget")).requireEnabled();
		
		window.list("listBudgets").clearSelection();
		window.button(JButtonMatcher.withText("Delete Budget")).requireDisabled();
	}

	@Test
	public void testAddExpenseButtonShouldBeEnabledOnlyWhenExpenseItemTitleAndAmountAreValid() {

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");

			budgetAppView.getTextFieldExpenseItemTitle().setEnabled(true);
			budgetAppView.getTextFieldExpenseItemAmount().setEnabled(true);
			budgetAppView.getComboBoxExpenseItemType().setEnabled(true);
		});
		
		JTextComponentFixture expenseItemTitleTextBox = window.textBox("textFieldExpenseItemTitle");
		JTextComponentFixture expenseItemAmountTextBox = window.textBox("textFieldExpenseItemAmount");

		expenseItemTitleTextBox.enterText("testtitle");
		expenseItemAmountTextBox .enterText("10");
		window.button(JButtonMatcher.withText("Add Expense")).requireEnabled();
		
		expenseItemTitleTextBox.setText("");
		expenseItemAmountTextBox.setText("");
		
		expenseItemTitleTextBox.enterText("");
		expenseItemAmountTextBox.enterText("10");
		window.button(JButtonMatcher.withText("Add Expense")).requireDisabled();
		
		expenseItemTitleTextBox.setText("");
		expenseItemAmountTextBox.setText("");

		expenseItemTitleTextBox.setText("testtitle");
		expenseItemAmountTextBox.setText("");
		window.button(JButtonMatcher.withText("Add Expense")).requireDisabled();
	}

	@Test
	public void testDeleteExpenseButtonShouldBeEnabledOnlyWhenAnExpenseIsSelected() {
	    ExpenseItem expenseItem1 = new ExpenseItem("testtitle", Type.NEEDS, 10);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle", Type.WANTS, 10);
	    ExpenseItem expenseItem3 = new ExpenseItem("testtitle", Type.SAVINGS, 10);

	    GuiActionRunner.execute(() -> {
	        CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
	        cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
	        budgetAppView.getListNeedsModel().addElement(expenseItem1);
	        budgetAppView.getListWantsModel().addElement(expenseItem2);
	        budgetAppView.getListSavingsModel().addElement(expenseItem3);
	    });

	    window.list("listNeeds").selectItem(0); 
	    window.button(JButtonMatcher.withText("Delete Expense")).requireEnabled();
	    window.list("listNeeds").clearSelection();
	    window.button(JButtonMatcher.withText("Delete Expense")).requireDisabled();
	    
	    window.list("listWants").selectItem(0);
	    window.button(JButtonMatcher.withText("Delete Expense")).requireEnabled();
	    window.list("listWants").clearSelection();
	    window.button(JButtonMatcher.withText("Delete Expense")).requireDisabled();
	   
	    window.list("listSavings").selectItem(0);
	    window.button(JButtonMatcher.withText("Delete Expense")).requireEnabled();
	    window.list("listSavings").clearSelection();
	    window.button(JButtonMatcher.withText("Delete Expense")).requireDisabled();
	}

	@Test
	public void testModifyExpenseButtonShouldBeEnabledOnlyWhenExpenseItemTitleAndAmountAreValidAndAnExpenseIsSelected() {
		ExpenseItem expenseItem1 = new ExpenseItem("testtitle", Type.NEEDS, 10);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle", Type.WANTS, 10);
	    ExpenseItem expenseItem3 = new ExpenseItem("testtitle", Type.SAVINGS, 10);

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.getListNeedsModel().addElement(expenseItem1);
	        budgetAppView.getListWantsModel().addElement(expenseItem2);
	        budgetAppView.getListSavingsModel().addElement(expenseItem3);
			budgetAppView.getTextFieldExpenseItemTitle().setEnabled(true);
			budgetAppView.getTextFieldExpenseItemAmount().setEnabled(true);
		});
		
		window.list("listNeeds").selectItem(0);
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("10");
		window.button(JButtonMatcher.withText("Modify Expense")).requireEnabled();
		
		window.textBox("textFieldExpenseItemTitle").setText("");
		window.textBox("textFieldExpenseItemAmount").setText("");
		window.list("listNeeds").clearSelection();
		
		window.list("listWants").selectItem(0);
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("10");
		window.button(JButtonMatcher.withText("Modify Expense")).requireEnabled();
		
		window.textBox("textFieldExpenseItemTitle").setText("");
		window.textBox("textFieldExpenseItemAmount").setText("");
		window.list("listWants").clearSelection();
		
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("20");
		window.list("listSavings").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).requireEnabled();
	}

	@Test
	public void testWhenEitherExpenseItemTitleOrAmountAreNotValidOrNoExpenseItemIsSelectedThenModifyExpenseButtonShouldBeDisabled() {
		
		ExpenseItem expenseItem1 = new ExpenseItem("testtitle", Type.NEEDS, 10);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle", Type.WANTS, 10);
	    ExpenseItem expenseItem3 = new ExpenseItem("testtitle", Type.SAVINGS, 10);

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.getListNeedsModel().addElement(expenseItem1);
	        budgetAppView.getListWantsModel().addElement(expenseItem2);
	        budgetAppView.getListSavingsModel().addElement(expenseItem3);
			budgetAppView.getTextFieldExpenseItemTitle().setEnabled(true);
			budgetAppView.getTextFieldExpenseItemAmount().setEnabled(true);
		});
		
		window.textBox("textFieldExpenseItemTitle").enterText("");
		window.textBox("textFieldExpenseItemAmount").enterText("20");
		window.list("listNeeds").selectItem(0);

		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();
		window.list("listNeeds").clearSelection();
		
		window.list("listWants").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();
		window.list("listWants").clearSelection();
		
		window.list("listSavings").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();
		window.list("listSavings").clearSelection();
		
		window.textBox("textFieldExpenseItemTitle").setText("testtitle");
		window.textBox("textFieldExpenseItemAmount").setText("");
		
		window.list("listNeeds").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();
		window.list("listNeeds").clearSelection();
		
		window.list("listWants").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();
		window.list("listWants").clearSelection();
		
		window.list("listSavings").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();
		window.list("listSavings").clearSelection();
		
		window.textBox("textFieldExpenseItemTitle").setText("testtitle");
		window.textBox("textFieldExpenseItemAmount").setText("10");
		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();

	}
	
	@Test
	public void testSelectANeedsExpenseItemWillDeselectWantsListOrSavingsList() {

		ExpenseItem expenseItem1 = new ExpenseItem("testtitle", Type.NEEDS, 10);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle", Type.WANTS, 10);
	    ExpenseItem expenseItem3 = new ExpenseItem("testtitle", Type.SAVINGS, 10);

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.getListNeedsModel().addElement(expenseItem1);
			budgetAppView.getListWantsModel().addElement(expenseItem2);
			budgetAppView.getListSavingsModel().addElement(expenseItem3);
		});

		window.list("listWants").selectItem(0);
		window.list("listNeeds").selectItem(0);
		window.list("listWants").requireNoSelection();

		window.list("listSavings").selectItem(0);
		window.list("listNeeds").selectItem(0);
		window.list("listSavings").requireNoSelection();

	}

	@Test
	public void testSelectAWantsExpenseItemWillDeselectNeedsListOrSavingsList() {

		ExpenseItem expenseItem1 = new ExpenseItem("testtitle", Type.NEEDS, 10);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle", Type.WANTS, 10);
	    ExpenseItem expenseItem3 = new ExpenseItem("testtitle", Type.SAVINGS, 10);

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.getListNeedsModel().addElement(expenseItem1);
			budgetAppView.getListWantsModel().addElement(expenseItem2);
			budgetAppView.getListSavingsModel().addElement(expenseItem3);
		});

		window.list("listNeeds").selectItem(0);
		window.list("listWants").selectItem(0);
		window.list("listNeeds").requireNoSelection();

		window.list("listSavings").selectItem(0);
		window.list("listWants").selectItem(0);
		window.list("listSavings").requireNoSelection();

	}

	@Test
	public void testSelectASavingExpenseItemWillDeselectNeedsListOrWantsList() {

		ExpenseItem expenseItem1 = new ExpenseItem("testtitle", Type.NEEDS, 10);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle", Type.WANTS, 10);
	    ExpenseItem expenseItem3 = new ExpenseItem("testtitle", Type.SAVINGS, 10);

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.getListNeedsModel().addElement(expenseItem1);
			budgetAppView.getListWantsModel().addElement(expenseItem2);
			budgetAppView.getListSavingsModel().addElement(expenseItem3);
		});

		window.list("listNeeds").selectItem(0);
		window.list("listSavings").selectItem(0);
		window.list("listNeeds").requireNoSelection();

		window.list("listWants").selectItem(0);
		window.list("listSavings").selectItem(0);
		window.list("listWants").requireNoSelection();

	}

	// View methods test

	@Test
	public void testsRefreshUsersListShouldAddUsersToTheUsersList() {
		User user1 = new User(1, "testname1", "testsurname1");
		User user2 = new User(2, "testname2", "testsurname2");

		GuiActionRunner.execute(() -> budgetAppView.refreshUsersList(Arrays.asList(user1, user2)));

		String[] listContents = window.list("listUsers").contents();

		assertThat(listContents).containsExactly(user1.toString(), user2.toString());
	}
	
	@Test
	public void testRefreshExpenseItemsListsShouldAddExpenseItemsToTheCorrectLists() {
		
		ExpenseItem expenseItem1 = new ExpenseItem("testtitle", Type.NEEDS, 10);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle", Type.WANTS, 10);
	    ExpenseItem expenseItem3 = new ExpenseItem("testtitle", Type.SAVINGS, 10);
	    
	    GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.refreshExpenseItemsLists(Arrays.asList(expenseItem1, expenseItem2, expenseItem3));
		});

	    String[] needsListContents = window.list("listNeeds").contents();
	    String[] wantsListContents = window.list("listWants").contents();
	    String[] savingsListContents = window.list("listSavings").contents();

	    assertThat(needsListContents).containsExactly(expenseItem1.toString());
	    assertThat(wantsListContents).containsExactly(expenseItem2.toString());
	    assertThat(savingsListContents).containsExactly(expenseItem3.toString());
	}
	
	@Test
	public void testRefreshBudgetsListShouldAddBudgetsToTheList() {
	    Budget budget1 = new Budget("testtitle1", 1000);
	    Budget budget2 = new Budget("testtitle2", 1000);
	    
	    GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.refreshBudgetsList(Arrays.asList(budget1, budget2));
		});

	    String[] listContents = window.list("listBudgets").contents();

	    assertThat(listContents).containsExactly(budget1.toString(), budget2.toString());
	}

	@Test
	public void testShowUserErrorShouldShowTheMessageInTheBudgetErrorLabel() {
		GuiActionRunner.execute(() -> budgetAppView.showUserErrorMessage("User Error"));
		window.label("lblUserError").requireText("User Error");
	}
	@Test
	public void testShowBudgetErrorShouldShowTheMessageInTheUserErrorLabel() {
	    GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});
		GuiActionRunner.execute(() -> budgetAppView.showBudgetErrorMessage("Budget Error"));
		window.label("lblBudgetError").requireText("Budget Error");
	}
	
	@Test
	public void testShowExpenceItemErrorShouldShowTheMessageInTheExpenceItemErrorLabel() {
	    GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});
		GuiActionRunner.execute(() -> budgetAppView.showExpenseItemErrorMessage("Expence Error"));
		window.label("lblExpenseError").requireText("Expence Error");
	}

	@Test
	public void testResetUserErrorMessageShouldResetTheUserErrorLabel() {
		GuiActionRunner.execute(() -> budgetAppView.getLblErrorMessage().setText("test"));
		GuiActionRunner.execute(() -> budgetAppView.resetUserErrorMessage());
		window.label("lblUserError").requireText("");
	}
	
	@Test
	public void testResetBudgetErrorMessageShouldResetTheBudgetErrorLabel() {
	    GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});
		GuiActionRunner.execute(() -> budgetAppView.getLblBudgetError().setText("test"));
		GuiActionRunner.execute(() -> budgetAppView.resetBudgetErrorMessage());
		window.label("lblBudgetError").requireText("");
	}
	
	@Test
	public void testResetExpenceItemErrorMessageShouldResetTheExpenceItemErrorLabel() {
	    GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});
		GuiActionRunner.execute(() -> budgetAppView.getLblExpenseError().setText("test"));
		GuiActionRunner.execute(() -> budgetAppView.resetExpenseItemErrorMessage());
		window.label("lblExpenseError").requireText("");
	}

	// Test view methods
	@Test
	public void testOpenBudgetsButtonShouldInitBudgetsView() {
		User user = new User("testname", "testsurname");
		Budget budget = new Budget("testtitle", 2000);
		user.setBudgets(Arrays.asList(budget));
		
		GuiActionRunner.execute(() -> {
			budgetAppView.getListUsersModel().addElement(user);
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();

		window.panel("panelBudgets").requireVisible();
		window.label("lblUserDetails").requireText("Current user: " + user.toString());

		String[] listBudgetsContents = window.list("listBudgets").contents();
		assertThat(listBudgetsContents).containsExactly(budget.toString());
		String[] listNeedsContents = window.list("listNeeds").contents();
		assertThat(listNeedsContents).isEmpty();
		String[] listWantsContents = window.list("listWants").contents();
		assertThat(listWantsContents).isEmpty();
		String[] listSavingsContents = window.list("listSavings").contents();
		assertThat(listSavingsContents).isEmpty();

		window.label("lblBudgetTitle").requireText("Title");
		window.label("lblBudgetIncomes").requireText("Incomes");
		window.label("lblExpenseItemTitle").requireText("Title");
		window.label("lblExpenseItemAmount").requireText("Amount");
		window.label("lblExpenseItemType").requireText("Type");
		window.label("lblBudgetError").requireText("");
		window.label("lblExpenseError").requireText("");

		window.textBox("textFieldBudgetTitle").requireEnabled();
		window.textBox("textFieldBudgetIncomes").requireEnabled();
		window.textBox("textFieldExpenseItemTitle").requireDisabled();
		window.textBox("textFieldExpenseItemAmount").requireDisabled();

		window.comboBox("comboBoxExpenseItemType").requireDisabled();

		window.button(JButtonMatcher.withText("Exit user")).requireEnabled();
		window.button(JButtonMatcher.withText("Add Budget")).requireDisabled();
		window.button(JButtonMatcher.withText("Modify Budget")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete Budget")).requireDisabled();
		window.button(JButtonMatcher.withText("Add Expense")).requireDisabled();
		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete Expense")).requireDisabled();
	}

	@Test
	public void testBudgetListSelectionShouldSetUpExpenceItemsLists() {

		Budget budget = new Budget("testtitle", 2000);
		ExpenseItem expenseItem1 = new ExpenseItem("testtitle", Type.NEEDS, 10);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle", Type.WANTS, 10);
	    ExpenseItem expenseItem3 = new ExpenseItem("testtitle", Type.SAVINGS, 10);

		budget.setExpenseItems(Arrays.asList(expenseItem1, expenseItem2, expenseItem3));

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			budgetAppView.getListBudgetsModel().addElement(budget);
		});
		
		window.list("listBudgets").selectItem(0);

		String[] listNeedsContents = window.list("listNeeds").contents();
		assertThat(listNeedsContents).containsExactly(expenseItem1.toString());
		String[] listWantsContents = window.list("listWants").contents();
		assertThat(listWantsContents).containsExactly(expenseItem2.toString());
		String[] listSavingsContents = window.list("listSavings").contents();
		assertThat(listSavingsContents).containsExactly(expenseItem3.toString());

		window.textBox("textFieldExpenseItemTitle").requireEnabled();
		window.textBox("textFieldExpenseItemAmount").requireEnabled();
		window.comboBox("comboBoxExpenseItemType").requireEnabled();
		
		window.list("listBudgets").clearSelection();
		
		listNeedsContents = window.list("listNeeds").contents();
		assertThat(listNeedsContents).isEmpty();;
		listWantsContents = window.list("listWants").contents();
		assertThat(listWantsContents).isEmpty();;
		listSavingsContents = window.list("listSavings").contents();
		assertThat(listSavingsContents).isEmpty();;

		window.textBox("textFieldExpenseItemTitle").requireDisabled();
		window.textBox("textFieldExpenseItemAmount").requireDisabled();
		window.comboBox("comboBoxExpenseItemType").requireDisabled();

	}

	@Test
	public void testExitUserShouldResetTheBudgetViewAndComeBackToUserView() {

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});

		window.button(JButtonMatcher.withText("Exit user")).click();

		window.panel("panelUsers").requireVisible();

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});

		String[] listNeedsContents = window.list("listNeeds").contents();
		assertThat(listNeedsContents).isEmpty();
		String[] listWantsContents = window.list("listWants").contents();
		assertThat(listWantsContents).isEmpty();
		String[] listSavingsContents = window.list("listSavings").contents();
		assertThat(listSavingsContents).isEmpty();

		window.button(JButtonMatcher.withText("Add Budget")).requireDisabled();
		window.button(JButtonMatcher.withText("Modify Budget")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete Budget")).requireDisabled();
		window.button(JButtonMatcher.withText("Add Expense")).requireDisabled();
		window.button(JButtonMatcher.withText("Modify Expense")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete Expense")).requireDisabled();

		window.label("lblBudgetError").requireText("");
		window.label("lblExpenseError").requireText("");
		window.label("lblUserDetails").requireText("");
	}
	
	
	// Test button delegation to controller
	@Test
	public void testAddUserButtonShouldDelegateToUserControllerNewUser() {
		window.textBox("textFieldUserName").enterText("testname");
		window.textBox("textFieldUserSurname").enterText("testsurname");

		window.button(JButtonMatcher.withText("Add")).click();
		
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userController).addUser(userCaptor.capture());

        assertEquals("testname", userCaptor.getValue().getName());
        assertEquals("testsurname", userCaptor.getValue().getSurname());
	}

	@Test
	public void testDeleteUserButtonShouldDelegateToUserControllerDeleteUser() {
		User user = new User(1, "testname", "testsurname");

		GuiActionRunner.execute(() -> {
			DefaultListModel<User> listUsersModel = budgetAppView.getListUsersModel();
			listUsersModel.addElement(user);
		});

		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Delete User")).click();

		verify(userController).deleteUser(user);
	}
	
	@Test
	public void testAddBudgetButtonShouldDelegateBudgetControllerNewBudget() {
		
		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});
		window.textBox("textFieldBudgetTitle").enterText("testtitle");
		window.textBox("textFieldBudgetIncomes").enterText("2000");

		window.button(JButtonMatcher.withText("Add Budget")).click();
		
		ArgumentCaptor<Budget> budgetCaptor = ArgumentCaptor.forClass(Budget.class);
		verify(budgetController).addBudget(budgetCaptor.capture());

        assertEquals("testtitle", budgetCaptor.getValue().getTitle());
        assertEquals("2000.0", String.valueOf(budgetCaptor.getValue().getIncomes()));
        
	}

	@Test
	public void testDeleteBudgetButtonShouldDelegateToBudgetControllerDeleteBudget() {
		Budget budget = new Budget("testtitle", 2000);
		budget.setExpenseItems(Arrays.asList());

		GuiActionRunner.execute(() -> {

			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			DefaultListModel<Budget> listBudgetsModel = budgetAppView.getListBudgetsModel();
			listBudgetsModel.addElement(budget);
		});

		window.list("listBudgets").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Budget")).click();

		verify(budgetController).deleteBudget(budget);
	}
	
	@Test
	public void testModifyBudgetButtonShouldDelegateToBudgetControllerUpdateBudget() {
		Budget budget = new Budget("testtitle", 2000);
		budget.setExpenseItems(Arrays.asList());
		
		GuiActionRunner.execute(() -> {
			
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			DefaultListModel<Budget> listBudgetsModel = budgetAppView.getListBudgetsModel();
			listBudgetsModel.addElement(budget);
		});
		
		window.textBox("textFieldBudgetTitle").enterText("testtitle");
		window.textBox("textFieldBudgetIncomes").enterText("2000");

		window.list("listBudgets").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Budget")).click();
		
		ArgumentCaptor<Budget> budgetCaptor = ArgumentCaptor.forClass(Budget.class);
		verify(budgetController).updateBudget(budgetCaptor.capture());

		 assertEquals("testtitle", budgetCaptor.getValue().getTitle());
	     assertEquals("2000.0", String.valueOf(budgetCaptor.getValue().getIncomes()));
	}
	
	@Test
	public void testAddExpenseButtonShouldDelegateExpenseItemControllerNewExpence() {
		
		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			
			budgetAppView.getTextFieldExpenseItemTitle().setEnabled(true);
			budgetAppView.getTextFieldExpenseItemAmount().setEnabled(true);
			budgetAppView.getComboBoxExpenseItemType().setEnabled(true);
		});
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("10");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);

		window.button(JButtonMatcher.withText("Add Expense")).click();
		
		ArgumentCaptor<ExpenseItem> expenseItemCaptor = ArgumentCaptor.forClass(ExpenseItem.class);
		verify(expenseItemController).addExpenseItem(expenseItemCaptor.capture());

        assertEquals("testtitle", expenseItemCaptor.getValue().getTitle());
        assertEquals("10.0", String.valueOf(expenseItemCaptor.getValue().getAmount()));
        assertEquals(Type.NEEDS, expenseItemCaptor.getValue().getType());
        
	}

	@Test
	public void testDeleteExpenseItemButtonShouldDelegateToExpenseControllerDeleteExpence() {
		ExpenseItem expenseItem = new ExpenseItem("testtitle", Type.NEEDS, 10);

		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			DefaultListModel<ExpenseItem> listNeedsModel = budgetAppView.getListNeedsModel();
			listNeedsModel.addElement(expenseItem);
			
			budgetAppView.getTextFieldExpenseItemTitle().setEnabled(true);
			budgetAppView.getTextFieldExpenseItemAmount().setEnabled(true);
			budgetAppView.getComboBoxExpenseItemType().setEnabled(true);
		});

		window.list("listNeeds").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Expense")).click();

		verify(expenseItemController).deleteExpenseItem(expenseItem);
	}
	
	@Test
	public void testModifyExpenseButtonShouldDelegateToExpenceItemControllerUpdateExpense() {
		ExpenseItem expenseItem = new ExpenseItem("testtitle", Type.NEEDS, 10);
		
		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
			DefaultListModel<ExpenseItem> listNeedsModel = budgetAppView.getListNeedsModel();
			listNeedsModel.addElement(expenseItem);
			
			budgetAppView.getTextFieldExpenseItemTitle().setEnabled(true);
			budgetAppView.getTextFieldExpenseItemAmount().setEnabled(true);
			budgetAppView.getComboBoxExpenseItemType().setEnabled(true);
		});
		
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("10");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);

		window.list("listNeeds").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).click();

		verify(expenseItemController).updateExpenseItem(expenseItem);
		
		ArgumentCaptor<ExpenseItem> expenseItemCaptor = ArgumentCaptor.forClass(ExpenseItem.class);
		verify(expenseItemController).updateExpenseItem(expenseItemCaptor.capture());
		
		assertEquals("testtitle", expenseItemCaptor.getValue().getTitle());
        assertEquals("10.0", String.valueOf(expenseItemCaptor.getValue().getAmount()));
        assertEquals(Type.NEEDS, expenseItemCaptor.getValue().getType());
	}

}
