package ast.projects.appbudget.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.User;

public class BudgetRepositoryTestcontainersIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));

    @ClassRule
    public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
            .withInitScript("initializer.sql");

    private static BudgetRepositorySqlImplementation budgetRepository;
    private static SessionFactory factory;

    @Before
    public void setupRepo() {
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
    }

    @After
    public void tearDown() {
        if (factory != null) {
            factory.close();
        }
        if (mariaDB != null && mariaDB.isRunning()) {
            mariaDB.close();
        }
    }
    
    private Budget addTestBudgetToDatabase(String title, double amount) {
        Budget budget = new Budget(title, amount);
        budgetRepository.save(budget);
        return budget;
    }

    @Test
    public void testFindAll() {
        Budget budget1 = addTestBudgetToDatabase("testtitle1", 2000);
        Budget budget2 = addTestBudgetToDatabase("testtitle2", 1000);

        List<Budget> budgets = budgetRepository.findAll();

        assertThat(budgets).hasSize(2);
        assertThat(budgets.get(0).getTitle()).isEqualTo(budget1.getTitle());
        assertThat(budgets.get(0).getIncomes()).isEqualTo(budget1.getIncomes());
        assertThat(budgets.get(1).getTitle()).isEqualTo(budget2.getTitle());
        assertThat(budgets.get(1).getIncomes()).isEqualTo(budget2.getIncomes());
    }

    @Test
    public void testSaveBudget() {
        Budget budget = addTestBudgetToDatabase("testtitle1", 2000);

        List<Budget> budgets = budgetRepository.findAll();

        assertThat(budgets).hasSize(1);
        assertThat(budgets.get(0).getTitle()).isEqualTo(budget.getTitle());
        assertThat(budgets.get(0).getIncomes()).isEqualTo(budget.getIncomes());
    }
    
    @Test
    public void testModifyBudget() {
    	Budget budget = addTestBudgetToDatabase("testtitle1", 2000);
        budget.setTitle("testtitle2");
        budget.setIncomes(1000);

        budgetRepository.update(budget);
        List<Budget> budgets = budgetRepository.findAll();

        assertThat(budgets).hasSize(1);
        assertThat(budgets.get(0).getTitle()).isEqualTo("testtitle2");
        assertThat(budgets.get(0).getIncomes()).isEqualTo(1000);
    }

    @Test
    public void testDeleteBudget() {
    	Budget budget = addTestBudgetToDatabase("testtitle1", 2000);

        budgetRepository.delete(budget);

        List<Budget> budgets = budgetRepository.findAll();
        assertThat(budgets).isEmpty();
    }
}
