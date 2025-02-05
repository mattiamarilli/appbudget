package ast.projects.appbudget.repositories;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

public class UserRepositorySqlImplementationTest {

    private UserRepositorySqlImplementation userRepository;
    private SessionFactory sessionFactory;

    @Before
    public void setUp() {
        sessionFactory = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:test;MODE=MySQL;")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .setProperty("hibernate.connection.autocommit", "false")
                .setProperty("hibernate.current_session_context_class", "thread")
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
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("DROP TABLE IF EXISTS expenseitems;").executeUpdate();
            session.createNativeQuery("DROP TABLE IF EXISTS budgets;").executeUpdate();
            session.createNativeQuery("DROP TABLE IF EXISTS users;").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private List<User> getUsersFromDatabaseManually() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }
    
    private User saveUserManually(String name, String surname) {
        User user = new User(name, surname);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Serializable id = session.save(user);
        user.setId((Long) id);
        session.getTransaction().commit();
        return user;
    }
    
    @Test
    public void testFindAll() {
        User user1 = saveUserManually("testname1", "testsurname1");
        User user2 = saveUserManually("testname2", "testsurname2");
        
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
        User user = new User("testname1", "testsurname1");
        userRepository.save(user);
        List<User> users = getUsersFromDatabaseManually();
        
        Session session = userRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(session.isOpen()).isFalse();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("testname1");
        assertThat(users.get(0).getSurname()).isEqualTo("testsurname1");
    }
    
    @Test
    public void testSaveUserWhenUserIsAlreadyInDB() {
        User user = saveUserManually("testname1", "testsurname1");
        
        assertThrows(ConstraintViolationException.class, () -> userRepository.save(user));
        Session session = userRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
        assertThat(session.isOpen()).isFalse();
        assertThat(getUsersFromDatabaseManually()).hasSize(1);
    }

    @Test
    public void testDeleteUser() {
        User user = saveUserManually("testname1", "testsurname1");
        userRepository.delete(user);
        List<User> users = getUsersFromDatabaseManually();
        
        Session session = userRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(session.isOpen()).isFalse();
        assertThat(users).isEmpty();
    }

    @Test
    public void testDeleteUserThatIsNotInDB() {
        User user = new User("testname1", "testsurname1");
        user.setId(1);
        
        assertThrows(OptimisticLockException.class, () -> userRepository.delete(user));
        Session session = userRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
        assertThat(session.isOpen()).isFalse();
       
    }
    
    @Test
	public void testDeleteUserRollback() {
		SessionFactory spiedFactory = spy(sessionFactory);
		Session spiedSession = spy(spiedFactory.openSession());
		Transaction spiedTransaction = spy(spiedSession.getTransaction());
		UserRepositorySqlImplementation spiedUserRepository = spy(userRepository);
		User user = saveUserManually("testname1", "testsurname1");
		doReturn(spiedFactory).when(spiedUserRepository).getSessionFactory();
		doReturn(spiedSession).when(spiedFactory).openSession();
		doReturn(spiedTransaction).when(spiedSession).getTransaction();
		doThrow(new RuntimeException("Simulated exception")).when(spiedTransaction).commit();
		assertThrows(RuntimeException.class, () -> spiedUserRepository.delete(user));
		Session session = spiedUserRepository.getSession();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
		assertThat(session.isOpen()).isFalse();
	}
    
    @Test
    public void testUpdateUser() {
    	User user = saveUserManually("testname1", "testsurname1");
        user.setName("testname2");
        user.setSurname("testsurname2");
        
        userRepository.update(user);
        List<User> users = getUsersFromDatabaseManually();
        Session session = userRepository.getSession();

        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("testname2");
        assertThat(users.get(0).getSurname()).isEqualTo("testsurname2");
        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void testUpdateUserThatIsNotInDB() {
        User user = new User("testname1", "testsurname1");
        user.setId(1);
        
        assertThrows(OptimisticLockException.class, () -> userRepository.update(user));
        Session session = userRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
        assertThat(session.isOpen()).isFalse();
    }
    
    @Test
	public void testUpdateUserRollback() {
		SessionFactory spiedFactory = spy(sessionFactory);
		Session spiedSession = spy(spiedFactory.openSession());
		
		Transaction spiedTransaction = spy(spiedSession.getTransaction());
		UserRepositorySqlImplementation spiedUserRepository = spy(userRepository);
		
		User user = saveUserManually("testname1", "testsurname1");
		
		doReturn(spiedFactory).when(spiedUserRepository).getSessionFactory();
		doReturn(spiedSession).when(spiedFactory).openSession();
		doReturn(spiedTransaction).when(spiedSession).getTransaction();
		
		doThrow(new RuntimeException("Simulated exception")).when(spiedTransaction).commit();
		
		assertThrows(RuntimeException.class, () -> spiedUserRepository.update(user));
		Session session = spiedUserRepository.getSession();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
		assertThat(session.isOpen()).isFalse();
	}
}
