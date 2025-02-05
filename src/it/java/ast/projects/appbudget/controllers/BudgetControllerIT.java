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
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.BudgetRepositorySqlImplementation;
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;
import ast.projects.appbudget.views.BudgetAppView;

public class BudgetControllerIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
            DockerImageName.parse("mariadb:10.5.5"));
    private UserRepositorySqlImplementation userRepository;
    private BudgetRepositorySqlImplementation budgetRepository;

    @Mock
    private BudgetAppView view;

    @InjectMocks
    private BudgetController budgetController;

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

        userRepository = new UserRepositorySqlImplementation(factory);
        budgetRepository = new BudgetRepositorySqlImplementation(factory);
        budgetController = new BudgetController(view, budgetRepository);
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
    public void testNewBudget() {
    	User user = new User("name","surname");
    	userRepository.save(user);
    	Budget budgetToSave = new Budget("testtitle", 2000);
    	budgetToSave.setUserId(user.getId());
        budgetController.addBudget(budgetToSave);
        
        Budget budget = budgetRepository.findAll().get(0);
        
        assertThat(budget.getTitle()).isEqualTo("testtitle");
        assertThat(budget.getIncomes()).isEqualTo(2000);
        verify(view).refreshBudgetsList(
                argThat(budgets -> budgets.size() == 1 &&
                        budgets.get(0).getId() == 1 &&
                        budgets.get(0).getTitle().equals("testtitle") &&
                        budgets.get(0).getIncomes() == 2000
                ));
    }
    
    @Test
    public void testUpdateBudget() {
    	User user = new User("name","surname");
    	userRepository.save(user);
        Budget budgetToUpdate = new Budget("testtitle", 2000);
        budgetToUpdate.setUserId(user.getId());
        budgetRepository.save(budgetToUpdate);
        budgetToUpdate.setTitle("testtitle2");
        budgetToUpdate.setIncomes(1000);
        
        budgetController.updateBudget(budgetToUpdate);
        Budget budget = budgetRepository.findAll().get(0);
        
        assertThat(budget.getTitle()).isEqualTo("testtitle2");
        assertThat(budget.getIncomes()).isEqualTo(1000);
        
        verify(view).refreshBudgetsList(
                argThat(budgets -> budgets.size() == 1 &&
                        budgets.get(0).getId() == 1 &&
                        budgets.get(0).getTitle().equals("testtitle2") &&
                        budgets.get(0).getIncomes() == 1000
                ));
    }

    @Test
    public void testDeleteBudget() {
    	User user = new User("name","surname");
    	userRepository.save(user);
        Budget budgetToDelete = new Budget("testtitle", 2000);
        budgetRepository.save(budgetToDelete);
        budgetToDelete.setUserId(user.getId());

        budgetController.deleteBudget(budgetToDelete);

        assertThat(budgetRepository.findAll()).isEmpty();
        verify(view).refreshBudgetsList(argThat(Collection::isEmpty));
    }
    
    @Test
    public void testAllBudgetsByUser() {
    	User user = new User("name","surname");
    	userRepository.save(user);
    	Budget budget = new Budget("testtitle", 1000);
    	budget.setUserId(user.getId());
        budgetRepository.save(budget);
        budgetController.allBudgetsByUser(user);
        verify(view).refreshBudgetsList(
                argThat(budgets -> budgets.size() == 1 &&
                        budgets.get(0).getId() == 1 &&
                        budgets.get(0).getTitle().equals("testtitle") &&
                        budgets.get(0).getIncomes() == 1000
                ));
    }
}
