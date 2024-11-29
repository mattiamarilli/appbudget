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
import ast.projects.appbudget.models.Type;
import ast.projects.appbudget.models.User;

public class ExpenseItemRepositoryTestcontainersIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));

    @ClassRule
    public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
            .withInitScript("initializer.sql");

    private static ExpenseItemRepositorySqlImplementation expenseItemRepository;
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

        expenseItemRepository = new ExpenseItemRepositorySqlImplementation(factory);
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

    @Test
    public void testFindAll() {
        ExpenseItem item1 = addTestExpenseItemToDatabase("testtitle", Type.NEEDS, 10);
        ExpenseItem item2 = addTestExpenseItemToDatabase("testtitle2", Type.WANTS, 20);

        List<ExpenseItem> expenseItems = expenseItemRepository.findAll();

        assertThat(expenseItems).hasSize(2);
        assertThat(expenseItems.get(0).getTitle()).isEqualTo(item1.getTitle());
        assertThat(expenseItems.get(0).getAmount()).isEqualTo(item1.getAmount());
        assertThat(expenseItems.get(0).getType()).isEqualTo(item1.getType());
        
        assertThat(expenseItems.get(1).getTitle()).isEqualTo(item2.getTitle());
        assertThat(expenseItems.get(1).getAmount()).isEqualTo(item2.getAmount());
        assertThat(expenseItems.get(1).getType()).isEqualTo(item2.getType());
    }

    @Test
    public void testSaveExpenseItem() {
        ExpenseItem item1 = addTestExpenseItemToDatabase("testtitle", Type.NEEDS, 10);

        List<ExpenseItem> expenseItems = expenseItemRepository.findAll();

        assertThat(expenseItems).hasSize(1);
        assertThat(expenseItems.get(0).getTitle()).isEqualTo(item1.getTitle());
        assertThat(expenseItems.get(0).getAmount()).isEqualTo(item1.getAmount());
        assertThat(expenseItems.get(0).getType()).isEqualTo(item1.getType());
    }

    @Test
    public void testDeleteExpenseItem() {
        ExpenseItem item1 = addTestExpenseItemToDatabase("testtitle", Type.NEEDS, 150.0);

        expenseItemRepository.delete(item1);

        List<ExpenseItem> expenseItems = expenseItemRepository.findAll();
        assertThat(expenseItems).isEmpty();
    }

    private ExpenseItem addTestExpenseItemToDatabase(String title, Type type, double amount) {
        ExpenseItem expenseItem = new ExpenseItem(title, type, amount);
        expenseItemRepository.save(expenseItem);
        return expenseItem;
    }
}
