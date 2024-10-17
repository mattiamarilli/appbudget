package ast.projects.appbudget.views;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.assertj.swing.annotation.GUITest;
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
public class BudgetAppSwingViewIT extends AssertJSwingJUnitTestCase {
	private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(
			DockerImageName.parse("mariadb:10.5.5"));

	private UserRepositorySqlImplementation userRepository;
	private BudgetAppSwingView budgetAppView;
	private UserController userController;
	private FrameFixture window;

	private static SessionFactory factory;

	// Costanti per la configurazione del database e Hibernate
	private static final String JDBC_PREFIX = "jdbc:";
	private static final String JDBC_URL_FORMAT = "jdbc:mariadb://%s:%s/appbudget";
	private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.MariaDBDialect";
	private static final String HIBERNATE_USERNAME = "testuser";
	private static final String HIBERNATE_PASSWORD = "testpassword";
	private static final String HIBERNATE_HBM2DDL_AUTO = "create-drop";
	private static final String HIBERNATE_SHOW_SQL = "true";
	private static final String INIT_SCRIPT = "initializer.sql";

	// Costanti per test
	private static final String BUTTON_ADD_TEXT = "Add";
	private static final String BUTTON_DELETE_USER_TEXT = "Delete User";
	private static final String TEXTBOX_NAME = "nameTextBox";
	private static final String TEXTBOX_SURNAME = "surnameTextBox";
	private static final String LABEL_ERROR_MESSAGE = "errorMessageLabel";
	private static final String ERROR_DELETING_USER = "Error deleting user";

	private static final String USER_NAME_1 = "test1name";
	private static final String USER_SURNAME_1 = "test1surname";
	private static final String USER_NAME_2 = "test2name";
	private static final String USER_SURNAME_2 = "test2surname";
	private static final String USER_TO_REMOVE_NAME = "toremoveName";
	private static final String USER_TO_REMOVE_SURNAME = "toremoveSurname";

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
	@GUITest
	public void testAllUsers() {
		User user1 = new User(USER_NAME_1, USER_SURNAME_1);
		User user2 = new User(USER_NAME_2, USER_SURNAME_2);
		userRepository.save(user1);
		userRepository.save(user2);

		GuiActionRunner.execute(() -> userController.allUsers());

		assertThat(window.list().contents()).containsExactly(user1.toString(), user2.toString());
	}

	@Test
	@GUITest
	public void testAddButtonSuccess() {
		window.textBox(TEXTBOX_NAME).enterText(USER_NAME_1);
		window.textBox(TEXTBOX_SURNAME).enterText(USER_SURNAME_1);
		window.button(JButtonMatcher.withText(BUTTON_ADD_TEXT)).click();
		
		assertThat(window.list().contents()).containsExactly(new User(USER_NAME_1, USER_SURNAME_1).toString());
	}
	
	//TODO: testAddButtonError()

	@Test
	@GUITest
	public void testDeleteButtonSuccess() {
		GuiActionRunner.execute(() -> userController.addUser(USER_TO_REMOVE_NAME, USER_TO_REMOVE_SURNAME));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText(BUTTON_DELETE_USER_TEXT)).click();
		assertThat(window.list().contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteButtonError() {
		User user = new User(1, USER_NAME_1, USER_SURNAME_1);
		GuiActionRunner.execute(() -> budgetAppView.getListUsersModel().addElement(user));

		window.list().selectItem(0);
		window.button(JButtonMatcher.withText(BUTTON_DELETE_USER_TEXT)).click();

		assertThat(window.list().contents()).containsExactly(user.toString());

		window.label(LABEL_ERROR_MESSAGE).requireText(ERROR_DELETING_USER);
	}
	
	
}
