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
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;
import ast.projects.appbudget.views.BudgetAppView;

public class UserControllerIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
            DockerImageName.parse("mariadb:10.5.5"));
    private UserRepositorySqlImplementation userRepository;

    @Mock
    private BudgetAppView view;

    @InjectMocks
    private UserController userController;

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
        userController.addUser(new User("test1name", "test1surname"));
        User user = userRepository.findAll().get(0);
        
        assertThat(user.getName()).isEqualTo("test1name");
        assertThat(user.getSurname()).isEqualTo("test1surname");
        
        verify(view).refreshUsersList(
                argThat(users -> users.size() == 1 &&
                        users.get(0).getId() == 1 &&
                        users.get(0).getName().equals("test1name") &&
                        users.get(0).getSurname().equals("test1surname")
                ));
    }

    @Test
    public void testDeleteUser() {
        User userToDelete = new User(1, "test1name", "test1surname");
        userController.addUser(new User("test1name", "test1surname"));

        userController.deleteUser(userToDelete);

        assertThat(userRepository.findAll()).isEmpty();
        verify(view).refreshUsersList(argThat(Collection::isEmpty));
    }

    @Test
    public void testAllUsers() {
        userRepository.save(new User("test1name", "test1surname"));
        userController.allUsers();
        verify(view).refreshUsersList(
                argThat(users -> users.size() == 1 &&
                        users.get(0).getId() == 1 &&
                        users.get(0).getName().equals("test1name") &&
                        users.get(0).getSurname().equals("test1surname")
                ));
    }
}
