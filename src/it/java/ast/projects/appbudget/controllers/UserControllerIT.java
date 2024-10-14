package ast.projects.appbudget.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.net.URI;

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

public class UserControllerIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
            DockerImageName.parse("mariadb:10.5.5"));

    // Costanti per i test
    private static final String TEST_USER_NAME = "test1name";
    private static final String TEST_USER_SURNAME = "test1surname";
    private static final String JDBC_PREFIX = "jdbc:";
    private static final String JDBC_URL_FORMAT = "jdbc:mariadb://%s:%s/appbudget";
    private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.MariaDBDialect";
    private static final String HIBERNATE_USERNAME = "testuser";
    private static final String HIBERNATE_PASSWORD = "testpassword";
    private static final String HIBERNATE_HBM2DDL_AUTO = "create-drop";
    private static final String HIBERNATE_SHOW_SQL = "true";
    private static final String INIT_SCRIPT = "initializer.sql";

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
                .setProperty("hibernate.connection.url", String.format(JDBC_URL_FORMAT, uri.getHost(), uri.getPort()))
                .setProperty("hibernate.connection.username", HIBERNATE_USERNAME)
                .setProperty("hibernate.connection.password", HIBERNATE_PASSWORD)
                .setProperty("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO)
                .setProperty("hibernate.show_sql", HIBERNATE_SHOW_SQL)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        userRepository = new UserRepositorySqlImplementation(factory);
        userController = new UserController(view, userRepository);
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
    public void testNewUser() {
        userController.addUser(TEST_USER_NAME, TEST_USER_SURNAME);
        User user = userRepository.findAll().get(0);
        assertTrue(user.getName().equals(TEST_USER_NAME) && user.getSurname().equals(TEST_USER_SURNAME));
        verify(view).refreshUsersList(
                argThat(users -> users.size() == 1 &&
                        users.get(0).getId() == 1 &&
                        users.get(0).getName().equals(TEST_USER_NAME) &&
                        users.get(0).getSurname().equals(TEST_USER_SURNAME)
                ));
        verify(view).resetErrorMessage();
    }

    @Test
    public void testDeleteUser() {
        User userToDelete = new User(1, TEST_USER_NAME, TEST_USER_SURNAME);
        userController.addUser(TEST_USER_NAME, TEST_USER_SURNAME);

        userController.deleteUser(userToDelete);

        assertThat(userRepository.findAll().size()).isEqualTo(0);
        verify(view).refreshUsersList(argThat(users -> users.isEmpty()));
    }

    @Test
    public void testAllUsers() {
    	userRepository.save(new User(TEST_USER_NAME, TEST_USER_SURNAME));
        userController.allUsers();
        verify(view).refreshUsersList(
                argThat(users -> users.size() == 1 &&
                        users.get(0).getId() == 1 &&
                        users.get(0).getName().equals(TEST_USER_NAME) &&
                        users.get(0).getSurname().equals(TEST_USER_SURNAME)
                ));
    }
}
