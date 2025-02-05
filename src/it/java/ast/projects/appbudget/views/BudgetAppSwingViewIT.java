package ast.projects.appbudget.views;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.CardLayout;
import java.net.URI;
import java.util.Arrays;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

import ast.projects.appbudget.controllers.BudgetController;
import ast.projects.appbudget.controllers.ExpenseItemController;
import ast.projects.appbudget.controllers.UserController;
import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.Type;
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.BudgetRepositorySqlImplementation;
import ast.projects.appbudget.repositories.ExpenseItemRepositorySqlImplementation;
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;

@RunWith(GUITestRunner.class)
public class BudgetAppSwingViewIT extends AssertJSwingJUnitTestCase {
	private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
			DockerImageName.parse("mariadb:10.5.5"));
	
	@ClassRule
	public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
			.withInitScript("initializer.sql");

	private UserRepositorySqlImplementation userRepository;
	private BudgetRepositorySqlImplementation budgetRepository;
	private ExpenseItemRepositorySqlImplementation expenseItemRepository;

	private BudgetAppSwingView budgetAppView;
	private UserController userController;
	private BudgetController budgetController;
	private ExpenseItemController expenseItemController;
	private FrameFixture window;

	private static SessionFactory factory;

	@Override
	protected void onSetUp() throws Exception {
		mariaDB.start();
		String jdbcUrl = mariaDB.getJdbcUrl();
		URI uri = URI.create(jdbcUrl.replace("jdbc:", ""));
		factory = new Configuration()
				.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
				.setProperty("hibernate.connection.url", String.format("jdbc:mariadb://%s:%s/appbudget", uri.getHost(), uri.getPort()))
				.setProperty("hibernate.connection.username", "testuser")
				.setProperty("hibernate.connection.password", "testpassword")
				.setProperty("hibernate.hbm2ddl.auto", "create-drop")
				.setProperty("hibernate.show_sql", "true")
				.addAnnotatedClass(User.class)
				.addAnnotatedClass(Budget.class)
				.addAnnotatedClass(ExpenseItem.class)
				.buildSessionFactory();
		userRepository = new UserRepositorySqlImplementation(factory);
		budgetRepository = new BudgetRepositorySqlImplementation(factory);
		expenseItemRepository = new ExpenseItemRepositorySqlImplementation(factory);

		GuiActionRunner.execute(() -> {
			budgetAppView = new BudgetAppSwingView();
			userController = new UserController(budgetAppView, userRepository);
			budgetController = new BudgetController (budgetAppView,budgetRepository);
			expenseItemController = new ExpenseItemController(budgetAppView,expenseItemRepository);
			budgetAppView.setUserController(userController);
			budgetAppView.setBudgetController(budgetController);
			budgetAppView.setExpenseItemController(expenseItemController);
			return budgetAppView;
		});

		window = new FrameFixture(robot(), budgetAppView);
		window.show();
	}

	@Override
	public void onTearDown() {
		if (factory != null) {
			factory.close();
		}
		if (mariaDB != null && mariaDB.isRunning()) {
			mariaDB.close();
		}
	}

	@Test
	@GUITest
	public void testAllUsers() {
		User user1 = new User("testname1","testsurname1");
		User user2 = new User("testname2","testsurname2");
		userRepository.save(user1);
		userRepository.save(user2);

		GuiActionRunner.execute(() -> userController.allUsers());

		assertThat(window.list().contents()).containsExactly(user1.toString(), user2.toString());
	}
	
	@Test
	@GUITest
	public void testAllBudgetsByUser() {
		User user = new User("testname1","testsurname1");
		userRepository.save(user);
		Budget b = new Budget("testtitle",1000);
		b.setUserId(user.getId());
		budgetRepository.save(b);
		
		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});

		GuiActionRunner.execute(() -> budgetController.allBudgetsByUser(user));

		assertThat(window.list("listBudgets").contents()).containsExactly(b.toString());
	}
	
	@Test
	@GUITest
	public void testAllExpenseItemsByBudget() {
		Budget b = new Budget("testtitle",1000);
		budgetRepository.save(b);
		ExpenseItem e1 = new ExpenseItem("testtitle",Type.NEEDS,10);
		ExpenseItem e2 = new ExpenseItem("testtitle",Type.WANTS,20);
		ExpenseItem e3 = new ExpenseItem("testtitle",Type.SAVINGS,30);
		
		e1.setBudgetId(b.getId());
		e2.setBudgetId(b.getId());
		e3.setBudgetId(b.getId());
		
		expenseItemRepository.save(e1);
		expenseItemRepository.save(e2);
		expenseItemRepository.save(e3);
		
		
		GuiActionRunner.execute(() -> {
			CardLayout cardLayout = (CardLayout) budgetAppView.getContentPane().getLayout();
			cardLayout.show(budgetAppView.getContentPane(), "budgetsCard");
		});

		GuiActionRunner.execute(() -> expenseItemController.allExpenseItemsByBudget(b));

		assertThat(window.list("listNeeds").contents()).containsExactly(e1.toString());
		assertThat(window.list("listWants").contents()).containsExactly(e2.toString());
		assertThat(window.list("listSavings").contents()).containsExactly(e3.toString());

		
	}

	@Test
	@GUITest
	public void testAddUserButtonSuccess() {
		window.textBox("textFieldUserName").enterText("testname1");
		window.textBox("textFieldUserSurname").enterText("testsurname1");
		window.button(JButtonMatcher.withText("Add")).click();
		
		assertThat(window.list().contents()).containsExactly(new User("testname1", "testsurname1").toString());
	}
	
	@Test
	@GUITest
	public void testAddUserButtonError() {
		User user = new User(1, "testname","testusername");
		
		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		window.textBox("textFieldUserName").enterText("testname");
		window.textBox("textFieldUserSurname").enterText("testusername");
		window.button(JButtonMatcher.withText("Add")).click();
		window.label("lblUserError").requireText("Error adding new user");
	}
	
	@Test
	@GUITest
	public void testAddBudgetButtonSuccess() {
		User user = new User(1, "testname", "testsurname");
		
		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.textBox("textFieldBudgetTitle").enterText("testtitle2");
		window.textBox("textFieldBudgetIncomes").enterText("2000");
		window.button(JButtonMatcher.withText("Add Budget")).click();
		
		assertThat(window.list("listBudgets").contents()).containsExactly("testtitle2" + " - " + "2000.0" + "$");
	}
	
	@Test
	@GUITest
	public void testAddBudgetButtonError() {
		User user = new User(1, "testname", "testsurname");
		Budget budget = new Budget("testtitle", 1000);
		budget.setUserId(user.getId());
		user.setBudgets(Arrays.asList(budget));
		
		
		GuiActionRunner.execute(() -> {
			userController.addUser(user);
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.textBox("textFieldBudgetTitle").enterText("testtitle");
		window.textBox("textFieldBudgetIncomes").enterText("1000");
		window.button(JButtonMatcher.withText("Add Budget")).click();
		window.label("lblBudgetError").requireText("Error adding new budget");
	}
	
	@Test
	@GUITest
	public void testAddExpenseButtonSuccess() {
		User user = new User(1, "testname", "testsurname");
		Budget budget = new Budget("testtitle", 1000);
		
		user.setBudgets(Arrays.asList(budget));
		budget.setUserId(user.getId());
		
		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("10");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);
		window.button(JButtonMatcher.withText("Add Expense")).click();
		
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("10");
		window.comboBox("comboBoxExpenseItemType").selectItem(1);
		window.button(JButtonMatcher.withText("Add Expense")).click();
		
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("10");
		window.comboBox("comboBoxExpenseItemType").selectItem(2);
		window.button(JButtonMatcher.withText("Add Expense")).click();
		
		assertThat(window.list("listNeeds").contents()).containsExactly("testtitle" +  " - " + "10.0$" + " - " + "NEEDS");
		assertThat(window.list("listWants").contents()).containsExactly("testtitle" +  " - " + "10.0$" + " - " + "WANTS");
		assertThat(window.list("listSavings").contents()).containsExactly("testtitle" +  " - " + "10.0$" + " - " + "SAVINGS");

	}
	
	@Test
	@GUITest
	public void testAddExpenseButtonError() {
		User user = new User("testname", "testsurname");
		Budget budget = new Budget("testtitle", 1000);
		budget.setUserId(1);
		ExpenseItem expense = new ExpenseItem("testtitle", Type.NEEDS,10);
		expense.setBudgetId(1);
		
		budget.setExpenseItems(Arrays.asList(expense));
		user.setBudgets(Arrays.asList(budget));
		
		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle");
		window.textBox("textFieldExpenseItemAmount").enterText("10");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);
		window.button(JButtonMatcher.withText("Add Expense")).click();
		window.label("lblExpenseError").requireText("Error adding expense item");

	}
	
	@Test
	@GUITest
	public void testModifyUserButtonSuccess() {
		User user = new User(1, "testname1", "testsurname1");

		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		
		window.list("listUsers").selectItem(0);
		window.textBox("textFieldUserName").setText("testname2");
		window.textBox("textFieldUserSurname").setText("testsurname2");
		window.button(JButtonMatcher.withText("Modify")).click();
		assertThat(window.list("listUsers").contents()).containsExactly("testname2 testsurname2");
	}

	@Test
	@GUITest
	public void testModifyUserButtonError() {
		User userFake = new User("testname", "testsurname");
		
		GuiActionRunner.execute(() -> {
			budgetAppView.getListUsersModel().addElement(userFake);

		});
		
		window.list("listUsers").selectItem(0);
		
		window.textBox("textFieldUserName").enterText("testname2");
		window.textBox("textFieldUserSurname").enterText("testsurname2");
		window.button(JButtonMatcher.withText("Modify")).click();
		window.label("lblUserError").requireText("Error updating user");
	}

	@Test
	@GUITest
	public void testModifyBudgetButtonSuccess() {
		User user = new User(1, "testname", "testsurname");
		Budget budget = new Budget("testtitle", 1000);
		budget.setUserId(user.getId());
		user.setBudgets(Arrays.asList(budget));

		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		window.textBox("textFieldBudgetTitle").setText("testtitle2");
		window.textBox("textFieldBudgetIncomes").setText("2000");
		window.button(JButtonMatcher.withText("Modify Budget")).click();
		assertThat(window.list("listBudgets").contents()).containsExactly("testtitle2" + " - " + "2000.0" + "$");
	}

	@Test
	@GUITest
	public void testModifyBudgetButtonError() {
		User user = new User(1, "testname", "testsurname");
		Budget budget = new Budget(1,"testtitle", 1000);
		Budget budgetFake = new Budget("testtitle", 100);
		budget.setUserId(user.getId());
		
		GuiActionRunner.execute(() -> {
			userController.addUser(user);
		});

		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		
		GuiActionRunner.execute(() -> {
			budgetAppView.getListBudgetsModel().addElement(budgetFake);

		});

		window.list("listBudgets").selectItem(0);
		window.textBox("textFieldBudgetTitle").enterText("testtitle2");
		window.textBox("textFieldBudgetIncomes").enterText("2000");
		window.button(JButtonMatcher.withText("Modify Budget")).click();
		window.label("lblBudgetError").requireText("Error updating budget");
	}
	
	@Test
	@GUITest
	public void testModifyExpenseButtonSuccess() {
		User user = new User("testname", "testsurname");
		Budget budget = new Budget("testtitle", 1000);
		budget.setUserId(1);
		ExpenseItem expense1 = new ExpenseItem("testtitle1", Type.NEEDS,10);
		expense1.setBudgetId(1);
		ExpenseItem expense2 = new ExpenseItem("testtitle2", Type.WANTS,20);
		expense2.setBudgetId(1);
		ExpenseItem expense3 = new ExpenseItem("testtitle3", Type.SAVINGS,30);
		expense3.setBudgetId(1);	

		budget.setExpenseItems(Arrays.asList(expense1,expense2,expense3));
		user.setBudgets(Arrays.asList(budget));
		
		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		
		window.list("listNeeds").selectItem(0);
		window.textBox("textFieldExpenseItemTitle").setText("testtitle1mod");
		window.textBox("textFieldExpenseItemAmount").setText("100");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).click();
		
		window.list("listWants").selectItem(0);
		window.textBox("textFieldExpenseItemTitle").setText("testtitle2mod");
		window.textBox("textFieldExpenseItemAmount").setText("100");
		window.comboBox("comboBoxExpenseItemType").selectItem(1);
		window.button(JButtonMatcher.withText("Modify Expense")).click();
		
		window.list("listSavings").selectItem(0);
		window.textBox("textFieldExpenseItemTitle").setText("testtitle3mod");
		window.textBox("textFieldExpenseItemAmount").setText("100");
		window.comboBox("comboBoxExpenseItemType").selectItem(2);
		window.button(JButtonMatcher.withText("Modify Expense")).click();
		
		assertThat(window.list("listNeeds").contents()).containsExactly("testtitle1mod" +  " - " + "100.0$" + " - " + "NEEDS");
		assertThat(window.list("listWants").contents()).containsExactly("testtitle2mod" +  " - " + "100.0$" + " - " + "WANTS");
		assertThat(window.list("listSavings").contents()).containsExactly("testtitle3mod" +  " - " + "100.0$" + " - " + "SAVINGS");
	}

	@Test
	@GUITest
	public void testModifyExpenseButtonError() {
		User user = new User(1, "testname", "testsurname");
		Budget budget = new Budget(1,"testtitle", 1000);
		ExpenseItem expense = new ExpenseItem(1,"testtitle", Type.NEEDS,10);
		budget.setUserId(user.getId());
		expense.setBudgetId(budget.getId());
		
		ExpenseItem expenseFake = new ExpenseItem(2,"testtitle", Type.NEEDS,10);
		
		GuiActionRunner.execute(() -> {
			userController.addUser(user);
			budgetController.addBudget(budget);
			expenseItemController.addExpenseItem(expense);
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		
		GuiActionRunner.execute(() -> {
			budgetAppView.getListNeedsModel().addElement(expenseFake);

		});
		
		window.list("listNeeds").selectItem(1);
		window.textBox("textFieldExpenseItemTitle").enterText("testtitle1mod");
		window.textBox("textFieldExpenseItemAmount").enterText("100");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);
		window.button(JButtonMatcher.withText("Modify Expense")).click();
		window.label("lblExpenseError").requireText("Error updating expense item");
	}
	
	@Test
	@GUITest
	public void testDeleteUserButtonSuccess() {
		GuiActionRunner.execute(() -> userController.addUser(new User("testname1", "testsurname1")));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete User")).click();
		assertThat(window.list().contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteUserButtonError() {
		User user = new User(1, "testname1", "testsurname1");
		GuiActionRunner.execute(() -> budgetAppView.getListUsersModel().addElement(user));

		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete User")).click();
		assertThat(window.list().contents()).containsExactly(user.toString());
		window.label("lblUserError").requireText("Error deleting user");
	}
	
	@Test
	@GUITest
	public void testDeleteBudgetButtonSuccess() {
		User user = new User(1, "testname", "testsurname");
		Budget budget = new Budget("testtitle", 1000);
		budget.setUserId(user.getId());
		user.setBudgets(Arrays.asList(budget));
		
		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		
		window.list("listBudgets").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Budget")).click();
		assertThat(window.list("listBudgets").contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteBudgetButtonError() {
		User user = new User(1, "testname", "testsurname");
		Budget budget = new Budget(1,"testtitle", 1000);
		Budget budgetFake = new Budget(2,"testtitle", 100);
		budget.setUserId(user.getId());
		
		GuiActionRunner.execute(() -> {
			userController.addUser(user);
			budgetController.addBudget(budget);
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		
		GuiActionRunner.execute(() -> {
			budgetAppView.getListBudgetsModel().addElement(budgetFake);

		});

		window.list("listBudgets").selectItem(1);
		window.button(JButtonMatcher.withText("Delete Budget")).click();
		window.label("lblBudgetError").requireText("Error deleting budget");
	}
	
	@Test
	@GUITest
	public void testDeleteButtonExpenseSuccess() {
		User user = new User("testname", "testsurname");
		Budget budget = new Budget("testtitle", 1000);
		budget.setUserId(1);
		ExpenseItem expense = new ExpenseItem("testtitle", Type.NEEDS,10);
		expense.setBudgetId(1);
		
		budget.setExpenseItems(Arrays.asList(expense));
		user.setBudgets(Arrays.asList(budget));
		
		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		window.list("listNeeds").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Expense")).click();
		assertThat(window.list("listNeeds").contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteExpenseButtonError() {
		User user = new User(1, "testname", "testsurname");
		Budget budget = new Budget(1,"testtitle", 1000);
		ExpenseItem expense = new ExpenseItem(1,"testtitle", Type.NEEDS,10);
		budget.setUserId(user.getId());
		expense.setBudgetId(budget.getId());
		ExpenseItem expenseFake = new ExpenseItem(2,"testtitle", Type.NEEDS,10);
		
		GuiActionRunner.execute(() -> {
			userController.addUser(user);
			budgetController.addBudget(budget);
			expenseItemController.addExpenseItem(expense);
		});
		

		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		GuiActionRunner.execute(() -> {
			budgetAppView.getListNeedsModel().addElement(expenseFake);

		});
		window.list("listNeeds").selectItem(1);
		window.button(JButtonMatcher.withText("Delete Expense")).click();
		window.label("lblExpenseError").requireText("Error deleting expense item");
	}
	
}
