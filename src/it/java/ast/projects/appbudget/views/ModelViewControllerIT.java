package ast.projects.appbudget.views;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

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
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {
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
                .setProperty("hibernate.show_sql","true")
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
    public void testAddUser() {
        window.textBox("textFieldUserName").enterText("testname");
        window.textBox("textFieldUserSurname").enterText("testsurname");
        window.button(JButtonMatcher.withText("Add")).click();

        List<User> users = userRepository.findAll();
        assertThat(users.size()).isOne();
        assertThat(users.get(0).getName()).isEqualTo("testname");
        assertThat(users.get(0).getSurname()).isEqualTo("testsurname");
    }
    
    @Test
    public void testAddBudget() {
    	
    	User user = new User(1, "testname", "testsurname");
		
		GuiActionRunner.execute(() -> {
			GuiActionRunner.execute(() -> userController.addUser(user));
		});
		window.list("listUsers").selectItem(0);
		window.button(JButtonMatcher.withText("Open Budgets")).click();
		window.textBox("textFieldBudgetTitle").enterText("testtitle2");
		window.textBox("textFieldBudgetIncomes").enterText("2000");
		window.button(JButtonMatcher.withText("Add Budget")).click();
		
		List<Budget> budgets = budgetRepository.findAll();
		assertThat(budgets.size()).isOne();
		assertThat(budgets.get(0).getTitle()).isEqualTo("testtitle2");
		assertThat(budgets.get(0).getIncomes()).isEqualTo(2000);
		assertThat(budgets.get(0).getUserId()).isOne();
    }
    
    @Test
    public void testAddExpense() {
    	
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
		
		List<ExpenseItem> expenseItems = expenseItemRepository.findAll();
		
		assertThat(expenseItems.size()).isOne();
		assertThat(expenseItems.get(0).getTitle()).isEqualTo("testtitle");
        assertThat(expenseItems.get(0).getAmount()).isEqualTo(10);
        assertThat(expenseItems.get(0).getType()).isEqualTo(Type.NEEDS);
        assertThat(expenseItems.get(0).getBudgetId()).isOne();
    	
    }
    
    
    @Test
    public void testModifyBudget() {
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
		window.textBox("textFieldBudgetTitle").enterText("testtitle2");
		window.textBox("textFieldBudgetIncomes").enterText("2000");

		window.button(JButtonMatcher.withText("Modify Budget")).click();
		
		List<Budget> budgets = budgetRepository.findAll();
		assertThat(budgets.size()).isOne();
		assertThat(budgets.get(0).getTitle()).isEqualTo("testtitle2");
		assertThat(budgets.get(0).getIncomes()).isEqualTo(2000);
		assertThat(budgets.get(0).getUserId()).isOne();
    }
    
    @Test
    public void testModifyExpense() {
    	
    	User user = new User("testname", "testsurname");
		Budget budget = new Budget("testtitle", 1000);
		budget.setUserId(1);
		ExpenseItem expense = new ExpenseItem("testtitle1", Type.NEEDS,10);
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
		window.textBox("textFieldExpenseItemTitle").enterText("testtitlemod");
		window.textBox("textFieldExpenseItemAmount").enterText("100");
		window.comboBox("comboBoxExpenseItemType").selectItem(1);
		window.button(JButtonMatcher.withText("Modify Expense")).click();
		
		List<ExpenseItem> expenseItems = expenseItemRepository.findAll();
		
		assertThat(expenseItems.size()).isOne();
		assertThat(expenseItems.get(0).getTitle()).isEqualTo("testtitlemod");
		
		assertThat(expenseItems.get(0).getAmount()).isEqualTo(100);
		assertThat(expenseItems.get(0).getType()).isEqualTo(Type.WANTS);
		assertThat(expenseItems.get(0).getBudgetId()).isOne();    	
    }

    @Test
    public void testDeleteUser() {
        userRepository.save(new User("testname", "testsurname"));
        GuiActionRunner.execute(() -> userController.allUsers());
        window.list().selectItem(0);
        window.button(JButtonMatcher.withText("Delete User")).click();
        
        assertThat(userRepository.findAll()).isEmpty();
    }
    
    
    @Test
    public void testDeleteBudget() {
    	
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
		
		assertThat(budgetRepository.findAll()).isEmpty();
    	
    }
   
    @Test
    public void testDeleteExpense() {
    	
    	User user = new User(1, "testname", "testsurname");
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
		assertThat(expenseItemRepository.findAll()).isEmpty();
    	
    }
}
