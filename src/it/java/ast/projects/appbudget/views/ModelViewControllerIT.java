package ast.projects.appbudget.views;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

import ast.projects.appbudget.controllers.UserController;
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {
    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
            DockerImageName.parse("mariadb:10.5.5"));

    private UserRepositorySqlImplementation userRepository;
    private BudgetAppSwingView budgetAppView;
    private UserController userController;
    private FrameFixture window;

    private static SessionFactory factory;

    // Constants for database configuration and Hibernate
    private static final String JDBC_PREFIX = "jdbc:";
    private static final String JDBC_URL_FORMAT = "jdbc:mariadb://%s:%s/appbudget";
    private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.MariaDBDialect";
    private static final String HIBERNATE_USERNAME = "testuser";
    private static final String HIBERNATE_PASSWORD = "testpassword";
    private static final String HIBERNATE_HBM2DDL_AUTO = "create-drop";
    private static final String HIBERNATE_SHOW_SQL = "true";
    private static final String INIT_SCRIPT = "initializer.sql";

    // Constants for test values
    private static final String BUTTON_ADD_TEXT = "Add";
    private static final String BUTTON_DELETE_USER_TEXT = "Delete User";
    private static final String TEXTBOX_NAME = "nameTextBox";
    private static final String TEXTBOX_SURNAME = "surnameTextBox";

    // Test user constants
    private static final String TEST_USER_NAME = "test1name";
    private static final String TEST_USER_SURNAME = "test1surname";

    @ClassRule
    public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
            .withInitScript(INIT_SCRIPT);

    @Override
    protected void onSetUp() throws Exception {
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

        GuiActionRunner.execute(() -> {
            budgetAppView = new BudgetAppSwingView();
            userController = new UserController(budgetAppView, userRepository);
            budgetAppView.setUserController(userController);
            budgetAppView.maximizeWindow();
            return budgetAppView;
        });

        window = new FrameFixture(robot(), budgetAppView);
        window.show();
    }

    @Override
    public void onTearDown() {
        if (factory != null) {
            factory.close();
        }
        if (mariaDB != null && mariaDB.isRunning()) {
            mariaDB.close();
        }
    }

    @Test
    public void testAddUser() {
        window.textBox(TEXTBOX_NAME).enterText(TEST_USER_NAME);
        window.textBox(TEXTBOX_SURNAME).enterText(TEST_USER_SURNAME);
        window.button(JButtonMatcher.withText(BUTTON_ADD_TEXT)).click();

        List<User> users = userRepository.findAll();
        assertThat(users.size()).isOne();
        assertThat(users.get(0).getName()).isEqualTo(TEST_USER_NAME);
        assertThat(users.get(0).getSurname()).isEqualTo(TEST_USER_SURNAME);
    }

    @Test
    public void testDeleteUser() {
        userRepository.save(new User(TEST_USER_NAME, TEST_USER_SURNAME));
        GuiActionRunner.execute(() -> userController.allUsers());
        window.list().selectItem(0);
        window.button(JButtonMatcher.withText(BUTTON_DELETE_USER_TEXT)).click();
        
        assertThat(userRepository.findAll().size()).isZero();
    }
}
