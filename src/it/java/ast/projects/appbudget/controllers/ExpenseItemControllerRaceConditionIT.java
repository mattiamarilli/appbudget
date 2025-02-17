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
    public void setupRepo() {
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
