package ast.project.appbudget.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.net.URI;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.Type;
import ast.projects.appbudget.models.User;

@RunWith(GUITestRunner.class)
public class BudgetSwingAppE2E extends AssertJSwingJUnitTestCase {

	private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
			DockerImageName.parse("mariadb:10.5.5"));
	
	@ClassRule
	public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
			.withInitScript("initializer.sql");

	private FrameFixture window;

	private SessionFactory sessionFactory;

	@Override
	protected void onSetUp() throws Exception {
		mariaDB.start();
		String jdbcUrl = mariaDB.getJdbcUrl();
		URI uri = URI.create(jdbcUrl.replace("jdbc:", ""));

		System.getProperties().setProperty("app.db_host", uri.getHost());
		System.getProperties().setProperty("app.db_port", Integer.toString(uri.getPort()));

		sessionFactory = new Configuration()
				.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
				.setProperty("hibernate.connection.url", String.format("jdbc:mariadb://%s:%s/appbudget", uri.getHost(), uri.getPort()))
				.setProperty("hibernate.connection.username", "testuser")
				.setProperty("hibernate.connection.password", "testpassword")
				.setProperty("hibernate.hbm2ddl.auto", "update")
				.setProperty("hibernate.show_sql", "true")
				.addAnnotatedClass(User.class)
				.addAnnotatedClass(Budget.class)
				.addAnnotatedClass(ExpenseItem.class)
				.buildSessionFactory();

		User user1 = new User("Mario", "Rossi");
		User user2 = new User("Luigi", "Bianchi");

		saveUserManually(user1);
		saveUserManually(user2);

		Budget budget1 = new Budget("Maggio 2024", 2000);
		budget1.setUser(user1);
		Budget budget2 = new Budget("Giugno 2024", 1000);
		budget2.setUser(user2);

		saveBudgetManually(budget1);
		saveBudgetManually(budget2);

		ExpenseItem expense1 = new ExpenseItem("Benzina", Type.NEEDS, 10);
		expense1.setBudget(budget1);
		ExpenseItem expense2 = new ExpenseItem("Cinema", Type.WANTS, 20);
		expense2.setBudget(budget1);
		
		saveExpenseItemManually(expense1);
		saveExpenseItemManually(expense2);


		application("ast.projects.appbudget.app.BudgetSwingApp").start();

		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "AppBudget".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}
	

	@Override
	protected void onTearDown() {
		sessionFactory.close();
		mariaDB.close();
	}

	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("listUsers").contents()).anySatisfy(e -> assertThat(e).contains("Mario", "Rossi"))
				.anySatisfy(e -> assertThat(e).contains("Luigi", "Bianchi"));
	}

	@Test
	@GUITest
	public void testAddUserButtonSuccess() {
		window.textBox("textFieldUserName").enterText("Vittorio");
		window.textBox("textFieldUserSurname").enterText("Verdi");

		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("listUsers").contents()).anySatisfy(e -> assertThat(e).contains("Vittorio", "Verdi"));

	}
	
	@Test
	@GUITest
	public void testAddUserButtonError() {
		window.textBox("textFieldUserName").enterText("Mario");
		window.textBox("textFieldUserSurname").enterText("Rossi");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("lblUserError").text()).contains("Error adding new user");
	}

	@Test
	@GUITest
	public void testAddBudgetButtonSuccess() {

		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.textBox("textFieldBudgetTitle").enterText("Luglio 2024");
		window.textBox("textFieldBudgetIncomes").enterText("3000");
		window.button(JButtonMatcher.withText("Add Budget")).click();

		assertThat(window.list("listBudgets").contents())
				.anySatisfy(e -> assertThat(e).contains("Luglio 2024 - 3000.0$"));

	}
	
	@Test
	@GUITest
	public void testAddBudgetButtonError() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.textBox("textFieldBudgetTitle").enterText("Maggio 2024");
		window.textBox("textFieldBudgetIncomes").enterText("3000");
		window.button(JButtonMatcher.withText("Add Budget")).click();
		assertThat(window.label("lblBudgetError").text()).contains("Error adding new budget");

	}

	@Test
	@GUITest
	public void testAddExpenseItemButtonSucess() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		
		window.textBox("textFieldExpenseItemTitle").enterText("Spesa");
		window.textBox("textFieldExpenseItemAmount").enterText("50");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);
		window.button(JButtonMatcher.withText("Add Expense")).click();
		
		window.textBox("textFieldExpenseItemTitle").enterText("Palestra");
		window.textBox("textFieldExpenseItemAmount").enterText("200");
		window.comboBox("comboBoxExpenseItemType").selectItem(1);
		window.button(JButtonMatcher.withText("Add Expense")).click();
		
		window.textBox("textFieldExpenseItemTitle").enterText("Viaggio");
		window.textBox("textFieldExpenseItemAmount").enterText("30");
		window.comboBox("comboBoxExpenseItemType").selectItem(2);
		window.button(JButtonMatcher.withText("Add Expense")).click();
		
		assertThat(window.list("listNeeds").contents())
		.anySatisfy(e -> assertThat(e).contains("Spesa - 50.0 - NEEDS"));
		
		assertThat(window.list("listWants").contents())
		.anySatisfy(e -> assertThat(e).contains("Palestra - 200.0 - WANTS"));
		
		assertThat(window.list("listSavings").contents())
		.anySatisfy(e -> assertThat(e).contains("Viaggio - 30.0 - SAVINGS"));
	}


	@Test
	@GUITest
	public void testAddExpenseItemButtonError() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		
		window.textBox("textFieldExpenseItemTitle").enterText("Benzina");
		window.textBox("textFieldExpenseItemAmount").enterText("50");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);
		window.button(JButtonMatcher.withText("Add Expense")).click();

	}
	
	
	@Test
	@GUITest
	public void testModifyBudgetButtonSuccess() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(Pattern.compile(".*" + "Maggio 2024" + ".*"));
		window.textBox("textFieldBudgetTitle").enterText("Luglio 2024");
		window.textBox("textFieldBudgetIncomes").enterText("3000");
		window.button(JButtonMatcher.withText("Modify Budget")).click();
		
		assertThat(window.list("listBudgets").contents())
		.anySatisfy(e -> assertThat(e).contains("Luglio 2024 - 3000.0$"));

	}

	@Test
	@GUITest
	public void testModifyBudgetButtonError() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(Pattern.compile(".*" + "Maggio 2024" + ".*"));
		window.textBox("textFieldBudgetTitle").enterText("Luglio 2024");
		window.textBox("textFieldBudgetIncomes").enterText("3000");
		
		deleteBudgetFromDatabase((long) 1);
		
		window.button(JButtonMatcher.withText("Modify Budget")).click();
		
		assertThat(window.list("listBudgets").contents())
		.anySatisfy(e -> assertThat(e).contains("Luglio 2024 - 3000.0$"));
		
		assertThat(window.label("lblBudgetError").text()).contains("Error updating budget");
		
	}

	@Test
	@GUITest
	public void testModifyExpenseItemButtonSuccess() {
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		
		window.list("listNeeds").selectItem(Pattern.compile(".*" + "Benzina" + ".*"));
		window.textBox("textFieldExpenseItemTitle").enterText("Spesa");
		window.textBox("textFieldExpenseItemAmount").enterText("50");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);
		
		window.button(JButtonMatcher.withText("Modify Expense")).click();
		
		assertThat(window.list("listNeeds").contents())
		.anySatisfy(e -> assertThat(e).contains("Spesa - 50.0 - NEEDS"));
		


	}

	@Test
	@GUITest
	public void testModifyExpenseItemButtonError() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		
		window.list("listNeeds").selectItem(Pattern.compile(".*" + "Benzina" + ".*"));
		window.textBox("textFieldExpenseItemTitle").enterText("Spesa");
		window.textBox("textFieldExpenseItemAmount").enterText("50");
		window.comboBox("comboBoxExpenseItemType").selectItem(0);
		
		deleteBudgetFromDatabase((long) 1);
		
		window.button(JButtonMatcher.withText("Modify Expense")).click();
		
		assertThat(window.label("lblExpenseError").text()).contains("Error updating expense");

	}
	

	@Test
	@GUITest
	public void testDeleteUserButtonSuccess() {
		window.list("listUsers").selectItem(Pattern.compile(".*" + "Luigi" + " " + "Bianchi" + ".*"));
		window.button(JButtonMatcher.withText("Delete User")).click();
		assertThat(window.list("listUsers").contents()).noneMatch(e -> e.contains("Luigi" + " " + "Bianchi"));
	}

	@Test
	@GUITest
	public void testDeleteUserButtonError() {
		window.list("listUsers").selectItem(Pattern.compile(".*" + "Luigi" + " " + "Bianchi" + ".*"));

		deleteUserFromDatabase((long) 2);

		window.button(JButtonMatcher.withText("Delete User")).click();

		assertThat(window.label("lblUserError").text()).contains("Error deleting user");
	}

	@Test
	@GUITest
	public void testDeleteBudgetButtonSuccess() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(Pattern.compile(".*" + "Maggio 2024" + ".*"));
		window.button(JButtonMatcher.withText("Delete Budget")).click();
		assertThat(window.list("listBudgets").contents()).noneMatch(e -> e.contains("Maggio 2024 - 2000.0$"));
		
		

	}

	@Test
	@GUITest
	public void testDeleteBudgetButtonError() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(Pattern.compile(".*" + "Maggio 2024" + ".*"));
		
		deleteBudgetFromDatabase((long) 1);
		
		window.button(JButtonMatcher.withText("Delete Budget")).click();

		assertThat(window.label("lblBudgetError").text()).contains("Error deleting budget");
	}

	@Test
	@GUITest
	public void testDeleteExpenseItemButtonSuccess() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		window.list("listNeeds").selectItem(Pattern.compile(".*" + "Benzina" + ".*"));
		window.button(JButtonMatcher.withText("Delete Expense")).click();
		
		assertThat(window.list("listBudgets").contents()).noneMatch(e -> e.contains("Benzina - 10.0 - NEEDS"));


	}

	@Test
	@GUITest
	public void testDeleteExpenseItemButtonError() {
		
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.list("listBudgets").selectItem(0);
		window.list("listNeeds").selectItem(Pattern.compile(".*" + "Benzina" + ".*"));
		
		deleteExpenseItemFromDatabase((long) 1);
		
		window.button(JButtonMatcher.withText("Delete Expense")).click();

	}

	private void saveUserManually(User user) {
	    Session session = sessionFactory.openSession();
	    session.beginTransaction();
	    session.save(user);
	    session.getTransaction().commit();
	    session.close();
	}

	private void deleteUserFromDatabase(Long userId) {
	    Session session = sessionFactory.openSession();
	    Transaction transaction = session.beginTransaction();
	    User user = session.get(User.class, userId);
	    if (user != null) {
	        session.delete(user);
	    }
	    transaction.commit();
	    session.close();
	}

	private void saveBudgetManually(Budget budget) {
	    Session session = sessionFactory.openSession();
	    session.beginTransaction();
	    session.save(budget);
	    session.getTransaction().commit();
	    session.close();
	}

	private void deleteBudgetFromDatabase(Long budgetId) {
	    Session session = sessionFactory.openSession();
	    Transaction transaction = session.beginTransaction();
	    Budget budget = session.get(Budget.class, budgetId);
	    if (budget != null) {
	        session.delete(budget);
	    }
	    transaction.commit();
	    session.close();
	}

	private void saveExpenseItemManually(ExpenseItem expenseItem) {
	    Session session = sessionFactory.openSession();
	    Transaction transaction = session.beginTransaction();
	    session.save(expenseItem);
	    transaction.commit();
	    session.close();
	}

	private void deleteExpenseItemFromDatabase(Long expenseItemId) {
	    Session session = sessionFactory.openSession();
	    Transaction transaction = session.beginTransaction();
	    ExpenseItem expenseItem = session.get(ExpenseItem.class, expenseItemId);
	    if (expenseItem != null) {
	        session.delete(expenseItem);
	    }
	    transaction.commit();
	    session.close();
	}
}
