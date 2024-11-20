package ast.projects.appbudget.repositories;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class UserRepositorySqlImplementationTest {

    private UserRepositorySqlImplementation userRepository;
    private SessionFactory sessionFactory;

    // Constants for test values
    private static final String TEST_USER_1_NAME = "Mario";
    private static final String TEST_USER_1_SURNAME = "Rossi";
    private static final String TEST_USER_2_NAME = "Luigi";
    private static final String TEST_USER_2_SURNAME = "Bianchi";
    
    
    // Constants for hibernate's session factory 
    private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.H2Dialect";
    private static final String HIBERNATE_HBM2DDL_AUTO = "create-drop";
    private static final String HIBERNATE_SHOW_SQL = "true";
    private static final String HIBERNATE_CONNECTION_DRIVER_CLASS = "org.h2.Driver";
    private static final String HIBERNATE_CONNECTION_URL = "jdbc:h2:mem:test;MODE=MySQL;";
    private static final String HIBERNATE_CONNECTION_USERNAME = "sa";
    private static final String HIBERNATE_CONNECTION_PASSWORD = "";
    private static final String HIBERNATE_CONNECTION_AUTOCOMMIT = "false";

    @Before
    public void setUp() {
        sessionFactory = new Configuration()
                .setProperty("hibernate.dialect", HIBERNATE_DIALECT)
                //with create-drop behavior every time drop and create the User's table
                .setProperty("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO)
                .setProperty("hibernate.show_sql", HIBERNATE_SHOW_SQL)
                .setProperty("hibernate.connection.driver_class", HIBERNATE_CONNECTION_DRIVER_CLASS)
                .setProperty("hibernate.connection.url", HIBERNATE_CONNECTION_URL)
                .setProperty("hibernate.connection.username", HIBERNATE_CONNECTION_USERNAME)
                .setProperty("hibernate.connection.password", HIBERNATE_CONNECTION_PASSWORD)
                .setProperty("hibernate.connection.autocommit", HIBERNATE_CONNECTION_AUTOCOMMIT)
				.addAnnotatedClass(User.class)
				.addAnnotatedClass(Budget.class)
				.addAnnotatedClass(ExpenseItem.class)
                .buildSessionFactory();

        userRepository = new UserRepositorySqlImplementation(sessionFactory);
    }
  
    @After
    public void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
    
    private void deleteUserTable() {
    	Session session = sessionFactory.openSession();
	    session.beginTransaction();
	    session.createNativeQuery("DROP TABLE IF EXISTS users;").executeUpdate();
	    session.getTransaction().commit();
	    session.close();
    }

    private List<User> getUsersFromDatabaseManually() {
        Session session = sessionFactory.openSession();
        List<User> users = session.createQuery("FROM User", User.class).list();
        session.close();
        return users;
    }
    
    private User saveUserManually(String name, String username) {
		User user = new User(name, username);
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Serializable id = session.save(user);
		user.setId((long) id);
		session.getTransaction().commit();
		session.close();
		return user;
	}
    
    @Test
    public void testFindAll() {

    	User user1 = saveUserManually(TEST_USER_1_NAME, TEST_USER_1_SURNAME);
    	User user2 = saveUserManually(TEST_USER_2_NAME, TEST_USER_2_SURNAME);
        
        List<User> users = userRepository.findAll();
        
        assertThat(userRepository.getSession().isOpen()).isFalse();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo(user1.getName());
        assertThat(users.get(0).getSurname()).isEqualTo(user1.getSurname());
        assertThat(users.get(1).getName()).isEqualTo(user2.getName());
        assertThat(users.get(1).getSurname()).isEqualTo(user2.getSurname());
    }
    
    @Test
    public void testFindAllEmptyDb() {
    	List<User> users = userRepository.findAll();
		
    	assertThat(users).isEmpty();
		assertThat(userRepository.getSession().isOpen()).isFalse();
    }
    
    @Test
    public void testFindAllWhenUserTableDontExists() {
    	deleteUserTable();
    	
    	assertThrows(PersistenceException.class, () -> userRepository.findAll());
		assertThat(userRepository.getSession().isOpen()).isFalse();
    }

    @Test
    public void testSaveUser() {
        User user = new User(TEST_USER_1_NAME, TEST_USER_1_SURNAME);

        userRepository.save(user);
        List<User> users = getUsersFromDatabaseManually();
        
        Session session = userRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(session.isOpen()).isFalse();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo(TEST_USER_1_NAME);
        assertThat(users.get(0).getSurname()).isEqualTo(TEST_USER_1_SURNAME);
    }
    
    @Test
	public void testSaveUserWhenUserIsAlreadyInDB() {
		User user = saveUserManually(TEST_USER_1_NAME, TEST_USER_1_SURNAME);
		assertThrows(ConstraintViolationException.class, () -> userRepository.save(user));
		Session session = userRepository.getSession();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
        assertThat(session.isOpen()).isFalse();
		assertThat(getUsersFromDatabaseManually()).hasSize(1);
	}

    @Test
	public void testDeleteUser() {
		User user = saveUserManually(TEST_USER_1_NAME, TEST_USER_1_SURNAME);
		userRepository.delete(user);
		List<User> users = getUsersFromDatabaseManually();
		
		Session session = userRepository.getSession();
		
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(session.isOpen()).isFalse();
        assertThat(users).isEmpty();
	}

	@Test
	public void testDeleteUserThatIsNotInDB() {
		User user = new User(TEST_USER_1_NAME, TEST_USER_1_SURNAME);
		user.setId(1);
		assertThrows(OptimisticLockException.class, () -> userRepository.delete(user));
		Session session = userRepository.getSession();
		assertThat(session.isOpen()).isFalse();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
	}

    
    

    
}
