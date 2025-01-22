package ast.projects.appbudget.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.Collection;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.Type;
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.BudgetRepositorySqlImplementation;
import ast.projects.appbudget.repositories.ExpenseItemRepositorySqlImplementation;
import ast.projects.appbudget.views.BudgetAppView;

public class ExpenseItemControllerIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
            DockerImageName.parse("mariadb:10.5.5"));

    private ExpenseItemRepositorySqlImplementation expenseItemRepository;
    private BudgetRepositorySqlImplementation budgetRepository;

    @Mock
    private BudgetAppView view;

    @InjectMocks
    private ExpenseItemController expenseItemController;

    private AutoCloseable closeable;
    private static SessionFactory factory;

    @ClassRule
    public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
            .withInitScript("initializer.sql");

    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
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

        budgetRepository = new BudgetRepositorySqlImplementation(factory);
        expenseItemRepository = new ExpenseItemRepositorySqlImplementation(factory);
        expenseItemController = new ExpenseItemController(view, expenseItemRepository);
    }

    @After
    public void releaseMocks() throws Exception {
        if (factory != null) {
            factory.close();
        }
        if (mariaDB != null && mariaDB.isRunning()) {
            mariaDB.close();
        }
        closeable.close();
    }

    @Test
    public void testNewExpenseItem() {
    	Budget b = new Budget("testtitle",100);
        new BudgetRepositorySqlImplementation(factory).save(b);
        ExpenseItem expenseItemToSave = new ExpenseItem("testtitle", Type.NEEDS ,10);
        expenseItemToSave.setBudgetId(b.getId());
        expenseItemController.addExpenseItem(expenseItemToSave);
        
        ExpenseItem expenseItem = expenseItemRepository.findAll().get(0);
        assertThat(expenseItem.getTitle()).isEqualTo("testtitle");
        assertThat(expenseItem.getAmount()).isEqualTo(10);
        assertThat(expenseItem.getType()).isEqualTo(Type.NEEDS);
        verify(view).refreshExpenseItemsLists(
                argThat(expenseItems -> expenseItems.size() == 1 &&
                		expenseItems.get(0).getId() == 1 &&
                		expenseItems.get(0).getTitle().equals("testtitle") &&
                		expenseItems.get(0).getAmount() == 10 && 
                		expenseItem.getType() == Type.NEEDS
                ));
    }
    
    @Test
    public void testUpdateExpenseItem() {
    	Budget b = new Budget("testtitle",100);
        new BudgetRepositorySqlImplementation(factory).save(b);
        ExpenseItem expenseToUpdate = new ExpenseItem("testtitle", Type.NEEDS ,10);
        expenseToUpdate.setBudgetId(b.getId());
        expenseItemController.addExpenseItem(expenseToUpdate);

        expenseToUpdate.setTitle("testtitle2");
        expenseToUpdate.setType(Type.WANTS);
        expenseToUpdate.setAmount(20);
        
        expenseItemController.updateExpenseItem(expenseToUpdate);

        ExpenseItem expenseItem = expenseItemRepository.findAll().get(0);
        
        assertThat(expenseItem.getTitle()).isEqualTo("testtitle2");
        assertThat(expenseItem.getAmount()).isEqualTo(20);
        assertThat(expenseItem.getType()).isEqualTo(Type.WANTS);
        
        verify(view).refreshExpenseItemsLists(
                argThat(expenseItems -> expenseItems.size() == 1 &&
                		expenseItems.get(0).getId() == 1 &&
                		expenseItems.get(0).getTitle().equals("testtitle2") &&
                		expenseItems.get(0).getAmount() == 20 && 
                		expenseItem.getType() == Type.WANTS
                ));
    }

    @Test
    public void testDeleteBudget() {
    	Budget b = new Budget("testtitle",100);
        new BudgetRepositorySqlImplementation(factory).save(b);
    	ExpenseItem expenseToDelete = new ExpenseItem("testtitle", Type.NEEDS ,10);
    	expenseToDelete.setBudgetId(b.getId());
    	expenseItemController.addExpenseItem(expenseToDelete);

    	expenseItemController.deleteExpenseItem(expenseToDelete);

        assertThat(expenseItemRepository.findAll()).isEmpty();
        verify(view).refreshExpenseItemsLists(argThat(Collection::isEmpty));
    }
    
    @Test
    public void testAllExpenseItemsByBudget() {
    	Budget b = new Budget("testtitle", 1000);
    	budgetRepository.save(b);
    	ExpenseItem e = new ExpenseItem("testtitle",Type.NEEDS, 10);
    	e.setBudgetId(b.getId());
        expenseItemRepository.save(e);
        expenseItemController.allExpenseItemsByBudget(b);
        verify(view).refreshExpenseItemsLists(
                argThat(expenseItems -> expenseItems.size() == 1 &&
                		expenseItems.get(0).getId() == 1 &&
                		expenseItems.get(0).getType().equals(Type.NEEDS) &&
                		expenseItems.get(0).getAmount() == 10
                ));
    }
}
