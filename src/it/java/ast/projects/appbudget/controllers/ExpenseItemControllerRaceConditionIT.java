package ast.projects.appbudget.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

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

public class ExpenseItemControllerRaceConditionIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));
    
    private ExpenseItemRepositorySqlImplementation expenseItemRepository;
    
    private static final String JDBC_PREFIX = "jdbc:";
    private static final String JDBC_URL_FORMAT = "jdbc:mariadb://%s:%s/appbudget";
    private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.MariaDBDialect";
    private static final String HIBERNATE_USERNAME = "testuser";
    private static final String HIBERNATE_PASSWORD = "testpassword";
    private static final String HIBERNATE_HBM2DDL_AUTO = "create-drop";
    private static final String HIBERNATE_SHOW_SQL = "true";
    private static final String INIT_SCRIPT = "initializer.sql";
    
    @Mock
    private BudgetAppView view;

    @InjectMocks
    private ExpenseItemController expenseItemController;

    private AutoCloseable closeable;
    private static SessionFactory factory;
    
    @ClassRule
    public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
            .withInitScript(INIT_SCRIPT);

    @Before
    public void setupRepo() {
    	closeable = MockitoAnnotations.openMocks(this);
        mariaDB.start();
        String jdbcUrl = mariaDB.getJdbcUrl();
        URI uri = URI.create(jdbcUrl.replace(JDBC_PREFIX, ""));
        factory = new Configuration()
                .setProperty("hibernate.dialect", HIBERNATE_DIALECT)
                .setProperty("hibernate.connection.url", String.format(JDBC_URL_FORMAT, uri.getHost(), uri.getPort()))
                .setProperty("hibernate.connection.username", HIBERNATE_USERNAME)
                .setProperty("hibernate.connection.password", HIBERNATE_PASSWORD)
                .setProperty("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO)
                .setProperty("hibernate.show_sql", HIBERNATE_SHOW_SQL)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Budget.class)
                .addAnnotatedClass(ExpenseItem.class)
                .buildSessionFactory();

        expenseItemRepository = new ExpenseItemRepositorySqlImplementation(factory);
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
	public void testNewExpense() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(10);
		
		Budget b = new Budget(1,"title", 1000);
		new BudgetRepositorySqlImplementation(factory).save(b);

		IntStream.range(0, 10)
			.mapToObj(i -> new Thread(() -> {
				
				ExpenseItem expenseItem = new ExpenseItem("testtitle",Type.NEEDS, 10);
				expenseItem.setBudgetId(b.getId());
			
				new ExpenseItemController(view, expenseItemRepository).addExpenseItem(expenseItem); 
				latch.countDown();
			}))
			.forEach(Thread::start);

		latch.await();
		List<ExpenseItem> expenseItemFound = expenseItemRepository.findAll();
		assertThat(expenseItemFound.size()).isOne();
		assertThat(expenseItemFound.get(0).getTitle()).isEqualTo("testtitle");
		assertThat(expenseItemFound.get(0).getType()).isEqualTo(Type.NEEDS);
		assertThat(expenseItemFound.get(0).getAmount()).isEqualTo(10.0);
	}
	
	@Test
	public void testUpdateExpenseItem() throws InterruptedException {
		ExpenseItem expenseItem = new ExpenseItem("testtitle",Type.NEEDS, 10);
		expenseItemRepository.save(expenseItem);

	    CountDownLatch latch = new CountDownLatch(10);

	    for (int i = 0; i < 10; i++) {
	        int increment = 1; 
	        Thread thread = new Thread(() -> {
	            try {
	            	double newAmount;
	            	synchronized (expenseItem) {
	            		newAmount = expenseItem.getAmount() + increment;
	                }
	            	expenseItem.setAmount(newAmount);
                    new ExpenseItemController(view, expenseItemRepository).updateExpenseItem(expenseItem);
	            } finally {
	                latch.countDown();
	            }
	        });
	        thread.start();
	    }

	    latch.await();
	    ExpenseItem updatedExpenseItem = expenseItemRepository.findAll().get(0);
	    assertThat(updatedExpenseItem.getAmount()).isEqualTo(20.0);
	}
}
