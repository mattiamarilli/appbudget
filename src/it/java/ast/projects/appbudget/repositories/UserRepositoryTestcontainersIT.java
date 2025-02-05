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

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.User;

public class UserRepositoryTestcontainersIT {

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));

    @ClassRule
    public static final MariaDBContainer<?> mariaDB = MARIA_DB_CONTAINER.withUsername("root").withPassword("")
            .withInitScript("initializer.sql");

    private static UserRepositorySqlImplementation userRepository;
    private static SessionFactory factory;

    @Before
    public void setupRepo() {
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
    
    private User addTestUserToDatabase(String name, String surname) {
        User user = new User(name, surname);
        userRepository.save(user);
        return user;
    }

    @Test
    public void testFindAll() {
        User u1 = addTestUserToDatabase("Mario", "Rossi");
        User u2 = addTestUserToDatabase("Luigi", "Verdi");

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo(u1.getName());
        assertThat(users.get(0).getSurname()).isEqualTo(u1.getSurname());
        assertThat(users.get(1).getName()).isEqualTo(u2.getName());
        assertThat(users.get(1).getSurname()).isEqualTo(u2.getSurname());
    }

    @Test
    public void testSaveUser() {
        User u1 = addTestUserToDatabase("Mario", "Rossi");

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo(u1.getName());
        assertThat(users.get(0).getSurname()).isEqualTo(u1.getSurname());
    }

    @Test
    public void testDeleteUser() {
        User user = addTestUserToDatabase("Mario", "Rossi");

        userRepository.delete(user);

        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }
}
