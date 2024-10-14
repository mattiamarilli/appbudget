package ast.projects.appbudget.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import ast.projects.appbudget.models.User;

public class UserRepositoryTestcontainersIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));

    // Costanti per la configurazione del database e Hibernate
    private static final String JDBC_PREFIX = "jdbc:";
    private static final String JDBC_URL_FORMAT = "jdbc:mariadb://%s:%s/appbudget";
    private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.MariaDBDialect";
    private static final String HIBERNATE_USERNAME = "testuser";
    private static final String HIBERNATE_PASSWORD = "testpassword";
    private static final String HIBERNATE_HBM2DDL_AUTO = "create-drop";
    private static final String HIBERNATE_SHOW_SQL = "true";
    private static final String INIT_SCRIPT = "initializer.sql";

    // Costanti per i test
    private static final String TEST_USER_NAME_1 = "Mario";
    private static final String TEST_USER_SURNAME_1 = "Rossi";
    private static final String TEST_USER_NAME_2 = "Luigi";
    private static final String TEST_USER_SURNAME_2 = "Verdi";

    @ClassRule
    public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
            .withInitScript(INIT_SCRIPT);

    private static UserRepositorySqlImplementation userRepository;
    private static SessionFactory factory;

    @Before
    public void setupRepo() {
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
    }

    @After
    public void tearDown() {
        if (factory != null) {
            factory.close();
        }
        if (mariaDB != null && mariaDB.isRunning()) {
            mariaDB.close();
        }
    }

    @Test
    public void testFindAll() {
        User u1 = addTestUserToDatabase(TEST_USER_NAME_1, TEST_USER_SURNAME_1);
        User u2 = addTestUserToDatabase(TEST_USER_NAME_2, TEST_USER_SURNAME_2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo(u1.getName());
        assertThat(users.get(1).getName()).isEqualTo(u2.getName());
    }

    @Test
    public void testSaveUser() {
        User u1 = addTestUserToDatabase(TEST_USER_NAME_1, TEST_USER_SURNAME_1);

        User savedUser = userRepository.findAll().get(0);

        assertThat(savedUser.getName()).isEqualTo(u1.getName());
        assertThat(savedUser.getSurname()).isEqualTo(u1.getSurname());
    }

    @Test
    public void testDeleteUser() {
        User user = addTestUserToDatabase(TEST_USER_NAME_1, TEST_USER_SURNAME_1);

        userRepository.delete(user);

        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    private User addTestUserToDatabase(String name, String surname) {
        User user = new User(name, surname);
        userRepository.save(user);
        return user;
    }
}
