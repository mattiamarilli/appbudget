package ast.projects.appbudget.controllers;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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

import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;
import ast.projects.appbudget.views.BudgetAppView;

public class UserControllerRaceConditionIT {

	private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));
	private static final String JDBC_PREFIX = "jdbc:";
	private static final String DB_URL_FORMAT = "jdbc:mariadb://%s:%d/appbudget";
	private static final String DB_USERNAME = "testuser";
	private static final String DB_PASSWORD = "testpassword";
	private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.MariaDBDialect";
	private static final String HIBERNATE_HBM2DDL_AUTO = "create-drop";
	private static final String HIBERNATE_SHOW_SQL = "true";
	private static final String INIT_SCRIPT = "initializer.sql";

	private static final String TEST_USER_NAME = "test1name";
	private static final String TEST_USER_SURNAME = "test1surname";
	private static final int THREAD_COUNT = 10;

	private UserRepositorySqlImplementation userRepository;
	
	@Mock
	private BudgetAppView view;
  
	@InjectMocks
	private UserController userController;
	
	private AutoCloseable closeable;
	private static SessionFactory factory;
	
	@ClassRule
	public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
			.withInitScript(INIT_SCRIPT);

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		mariaDB.start();
		String jdbcUrl = mariaDB.getJdbcUrl();
		URI uri = URI.create(jdbcUrl.replace(JDBC_PREFIX, ""));
		factory = new Configuration()
				.setProperty("hibernate.dialect", HIBERNATE_DIALECT)
				.setProperty("hibernate.connection.url", String.format(DB_URL_FORMAT, uri.getHost(), uri.getPort()))
				.setProperty("hibernate.connection.username", DB_USERNAME)
				.setProperty("hibernate.connection.password", DB_PASSWORD)
				.setProperty("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO)
				.setProperty("hibernate.show_sql", HIBERNATE_SHOW_SQL)
				.addAnnotatedClass(User.class)
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
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

		// Use a single UserController instance
		UserController sharedUserController = new UserController(view, userRepository);

		IntStream.range(0, THREAD_COUNT)
			.mapToObj(i -> new Thread(() -> {
				sharedUserController.addUser(TEST_USER_NAME, TEST_USER_SURNAME);  // Use the shared instance
				latch.countDown();
			}))
			.forEach(Thread::start);

		// Wait for all the threads to finish
		latch.await(10, TimeUnit.SECONDS);

		// There should be a single element in the list
		List<User> userFound = userRepository.findAll();
		assertEquals(1, userFound.size());  // Expect only 1 user
		User u = userFound.get(0);
		assertTrue(u.getName().equals(TEST_USER_NAME) && u.getSurname().equals(TEST_USER_SURNAME));
	}
}
