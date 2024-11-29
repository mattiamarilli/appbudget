package ast.projects.appbudget.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.BudgetRepositorySqlImplementation;
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;
import ast.projects.appbudget.views.BudgetAppView;

public class BudgetControllerRaceContitionIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));
    
    private BudgetRepositorySqlImplementation budgetRepository;
    
    // Costanti per la configurazione del database e Hibernate
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
    private BudgetController budgetController;

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

        budgetRepository = new BudgetRepositorySqlImplementation(factory);
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
	public void testNewBudget() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(10);
		
		User u = new User(1,"name","surname");
		new UserRepositorySqlImplementation(factory).save(u);
		

		IntStream.range(0, 10)
			.mapToObj(i -> new Thread(() -> {
				
				Budget budget = new Budget("testtitle", 1000);
				budget.setUser(u);
				new BudgetController(view, budgetRepository).addBudget(budget); 
				latch.countDown();
			}))
			.forEach(Thread::start);

		latch.await();
		List<Budget> budgetFound = budgetRepository.findAll();
		assertEquals(1, budgetFound.size());
		assertEquals(budgetFound.get(0).getTitle(), "testtitle");
		assertTrue(budgetFound.get(0).getIncomes() == 1000);
	}
	
	@Test
	public void testUpdateBudget() throws InterruptedException {
	    Budget budget = new Budget(1,"testtitle", 1000);
	    budgetRepository.save(budget);

	    CountDownLatch latch = new CountDownLatch(10);

	    for (int i = 0; i < 10; i++) {
	        int increment = 100; 
	        Thread thread = new Thread(() -> {
	            try {
	            	double newIncomes;
	            	synchronized (budget) {
	                    newIncomes = budget.getIncomes() + increment;
	                }
	            	budget.setIncomes(newIncomes);
                    new BudgetController(view, budgetRepository).updateBudget(budget);
	            } finally {
	                latch.countDown();
	            }
	        });
	        thread.start();
	    }

	    latch.await();
	    Budget updatedBudget = budgetRepository.findAll().get(0);
	    assertEquals(2000, updatedBudget.getIncomes(), 0.01);
	}
}
