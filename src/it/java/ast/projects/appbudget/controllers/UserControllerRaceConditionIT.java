package ast.projects.appbudget.controllers;

import static org.junit.Assert.*;

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
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;
import ast.projects.appbudget.views.BudgetAppView;

public class UserControllerRaceConditionIT {

	private UserRepositorySqlImplementation userRepository;
	
	@Mock
	private BudgetAppView view;
  
	@InjectMocks
	private UserController userController;
	
	private AutoCloseable closeable;
	private static SessionFactory factory;
	
	private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
			DockerImageName.parse("mariadb:10.5.5"));
	
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
				.setProperty("hibernate.connection.url", String.format("jdbc:mariadb://%s:%d/appbudget", uri.getHost(), uri.getPort()))
				.setProperty("hibernate.connection.username", "testuser")
				.setProperty("hibernate.connection.password", "testpassword")
				.setProperty("hibernate.hbm2ddl.auto", "create-drop")
				.setProperty("hibernate.show_sql", "true")
				.setProperty("hibernate.hikari.maximumPoolSize", "10")
				.addAnnotatedClass(User.class)
				.addAnnotatedClass(Budget.class)
				.addAnnotatedClass(ExpenseItem.class)
				.buildSessionFactory();

		userRepository = new UserRepositorySqlImplementation(factory);
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
	public void testNewUser() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(10);

		IntStream.range(0, 10)
			.mapToObj(i -> new Thread(() -> {
				
				User user = new User("Mario", "Rossi");
			
				new UserController(view, userRepository).addUser(user); 
				latch.countDown();
			}))
			.forEach(Thread::start);

		latch.await();
		List<User> userFound = userRepository.findAll();
		assertEquals(1, userFound.size());
		assertEquals(userFound.get(0).getName(), "Mario");
		assertEquals(userFound.get(0).getSurname(), "Rossi");
	}
}
