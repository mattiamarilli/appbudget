package ast.project.appbudget.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.net.URI;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import ast.projects.appbudget.models.User;

@RunWith(GUITestRunner.class)
public class BudgetSwingAppE2E extends AssertJSwingJUnitTestCase {

    private static final String DB_USERNAME = "testuser";
    private static final String DB_PASSWORD = "testpassword";
    private static final String APP_NAME = "AppBudget";
    private static final String INITIALIZER_SCRIPT = "initializer.sql";
    private static final String JDBC_URL_FORMAT = "jdbc:mariadb://%s:%s/appbudget";
    private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.MariaDBDialect";
    private static final String HIBERNATE_HBM2DDL_AUTO = "update";
    private static final String HIBERNATE_SHOW_SQL = "true";
    private static final String JDBC_PREFIX = "jdbc:";

    // Test constants
    private static final String USER1_NAME = "Mario";
    private static final String USER1_SURNAME = "Rossi";
    private static final String USER2_NAME = "Luigi";
    private static final String USER2_SURNAME = "Bianchi";
    private static final String USER3_NAME = "Vittorio";
    private static final String USER3_SURNAME = "Verdi";
    private static final String DELETE_USER_PATTERN = ".*" + USER2_NAME + " " + USER2_SURNAME + ".*";
    private static final String ERROR_MESSAGE_DELETE = "Error deleting user";

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
            DockerImageName.parse("mariadb:10.5.5"));

    private FrameFixture window;

    private SessionFactory sessionFactory;

    @ClassRule
    public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root")
            .withPassword("").withInitScript(INITIALIZER_SCRIPT);

    @Override
    protected void onSetUp() throws Exception {
    	mariaDB.start();
        String jdbcUrl = mariaDB.getJdbcUrl();
        URI uri = URI.create(jdbcUrl.replace(JDBC_PREFIX, ""));

        System.getProperties().setProperty("app.db_host", uri.getHost());
        System.getProperties().setProperty("app.db_port", Integer.toString(uri.getPort()));

        sessionFactory = new Configuration()
                .setProperty("hibernate.dialect", HIBERNATE_DIALECT)
                .setProperty("hibernate.connection.url", String.format(JDBC_URL_FORMAT, uri.getHost(), uri.getPort()))
                .setProperty("hibernate.connection.username", DB_USERNAME)
                .setProperty("hibernate.connection.password", DB_PASSWORD)
                .setProperty("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO)
                .setProperty("hibernate.show_sql", HIBERNATE_SHOW_SQL)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        saveUserManually(USER1_NAME, USER1_SURNAME);
        saveUserManually(USER2_NAME, USER2_SURNAME);

        application("ast.projects.appbudget.app.BudgetSwingApp").start();

        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return APP_NAME.equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(robot());
    }

    @Override
    protected void onTearDown() {
        sessionFactory.close();
        mariaDB.close();
    }

    @Test
    @GUITest
    public void testOnStartAllDatabaseElementsAreShown() {
        assertThat(window.list().contents()).anySatisfy(e -> assertThat(e).contains(USER1_NAME, USER1_SURNAME))
                .anySatisfy(e -> assertThat(e).contains(USER2_NAME, USER2_SURNAME));
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        window.textBox("nameTextBox").enterText(USER3_NAME);
        window.textBox("surnameTextBox").enterText(USER3_SURNAME);
        window.button(JButtonMatcher.withText("Add")).click();
        assertThat(window.list().contents())
                .anySatisfy(e -> assertThat(e).contains(USER3_NAME, USER3_SURNAME));
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        window.list("usersList")
                .selectItem(Pattern.compile(DELETE_USER_PATTERN));
        window.button(JButtonMatcher.withText("Delete User")).click();
        assertThat(window.list().contents())
                .noneMatch(e -> e.contains(USER2_NAME + " " + USER2_SURNAME));
    }

    @Test
    @GUITest
    public void testDeleteButtonError() {
        window.list("usersList")
                .selectItem(Pattern.compile(DELETE_USER_PATTERN));

        deleteUserFromDatabase((long) 2);

        window.button(JButtonMatcher.withText("Delete User")).click();

        assertThat(window.label("errorMessageLabel").text())
                .contains(ERROR_MESSAGE_DELETE);
    }

    private void saveUserManually(String name, String username) {
        User user = new User(name, username);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    private void deleteUserFromDatabase(Long userId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        User user = session.get(User.class, userId);
        session.delete(user); 
        transaction.commit();
    }
}
