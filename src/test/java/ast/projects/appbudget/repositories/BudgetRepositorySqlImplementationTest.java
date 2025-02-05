package ast.projects.appbudget.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.User;

import java.io.Serializable;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

public class BudgetRepositorySqlImplementationTest {

    private BudgetRepositorySqlImplementation budgetRepository;
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
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Budget.class)
                .addAnnotatedClass(ExpenseItem.class)
                .buildSessionFactory();

        budgetRepository = new BudgetRepositorySqlImplementation(sessionFactory);
    }

    @After
    public void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
    
    private void deleteBudgetTable() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNativeQuery("DROP TABLE IF EXISTS budgets CASCADE;").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private List<Budget> getBudgetsFromDatabaseManually() {
        Session session = sessionFactory.openSession();
        List<Budget> budgets = session.createQuery("FROM Budget", Budget.class).list();
        session.close();
        return budgets;
    }

    private Budget saveBudgetManually(Budget budget) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Serializable id = session.save(budget);
        budget.setId((long) id);
        session.getTransaction().commit();
        session.close();
        return budget;
    }

    private User saveUserManually(String name, String surname) {
        User user = new User(name, surname);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Serializable id = session.save(user);
        user.setId((long) id);
        session.getTransaction().commit();
        session.close();
        return user;
    }

    @Test
    public void testFindByUserId() {
        User user1 = saveUserManually("testname1", "testsurname1");
        User user2 = saveUserManually("testname2", "testsurname2");
        Budget budget1 = new Budget("testtitle1", 1000);
        budget1.setUserId(user1.getId());
        Budget budget2 = new Budget("testtitle2", 2000);
        budget2.setUserId(user2.getId());
        saveBudgetManually(budget1);
        saveBudgetManually(budget2);

        List<Budget> budgets = budgetRepository.findByUserId(user1.getId());

        assertThat(budgets).hasSize(1);
        assertThat(budgets.get(0).getTitle()).isEqualTo("testtitle1");
        assertThat(budgets.get(0).getIncomes()).isEqualTo(1000);
        assertThat(budgetRepository.getSession().isOpen()).isFalse();
    }

    @Test
    public void testFindByUserIdWhenNoBudgetsExistForUser() {
        User user = saveUserManually("testname", "testsurname");

        List<Budget> budgets = budgetRepository.findByUserId(user.getId());

        assertThat(budgets).isEmpty();
        assertThat(budgetRepository.getSession().isOpen()).isFalse();
    }

    @Test
    public void testFindByUserIdWhenBudgetTableDoesNotExist() {
        deleteBudgetTable();

        User user = saveUserManually("Giovanni", "Bianchi");
        long id = user.getId();

        assertThrows(PersistenceException.class, () -> budgetRepository.findByUserId(id));
        assertThat(budgetRepository.getSession().isOpen()).isFalse();
    }

    @Test
    public void testFindAll() {
        saveBudgetManually(new Budget("testtitle1", 1000));
        saveBudgetManually(new Budget("testtitle2", 2000));

        List<Budget> budgets = budgetRepository.findAll();

        assertThat(budgetRepository.getSession().isOpen()).isFalse();
        assertThat(budgets).hasSize(2);
        assertThat(budgets.get(0).getTitle()).isEqualTo("testtitle1");
        assertThat(budgets.get(0).getIncomes()).isEqualTo(1000);
        assertThat(budgets.get(1).getTitle()).isEqualTo("testtitle2");
        assertThat(budgets.get(1).getIncomes()).isEqualTo(2000);
    }

    @Test
    public void testFindAllEmptyDb() {
        List<Budget> budgets = budgetRepository.findAll();

        assertThat(budgets).isEmpty();
        assertThat(budgetRepository.getSession().isOpen()).isFalse();
    }

    @Test
    public void testFindAllWhenBudgetTableDontExists() {
        deleteBudgetTable();

        assertThrows(PersistenceException.class, () -> budgetRepository.findAll());
        assertThat(budgetRepository.getSession().isOpen()).isFalse();
    }

    @Test
    public void testSaveBudget() {
        Budget budget = new Budget("testtitle1", 1000);

        budgetRepository.save(budget);
        List<Budget> budgets = getBudgetsFromDatabaseManually();
        Session session = budgetRepository.getSession();

        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(session.isOpen()).isFalse();
        assertThat(budgets).hasSize(1);
        assertThat(budgets.get(0).getTitle()).isEqualTo("testtitle1");
        assertThat(budgets.get(0).getIncomes()).isEqualTo(1000);
    }

    @Test
    public void testSaveBudgetWhenBudgetIsAlreadyInDB() {
    	User user = saveUserManually("testname1", "testsurname1");
        Budget budgetToSave = new Budget("testtitle1", 1000);
        budgetToSave.setUserId(user.getId());
        Budget budget = saveBudgetManually(budgetToSave);
        
        assertThrows(ConstraintViolationException.class, () -> budgetRepository.save(budget));
        Session session = budgetRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
        assertThat(session.isOpen()).isFalse();
        assertThat(getBudgetsFromDatabaseManually()).hasSize(1);
    }

    @Test
    public void testDeleteBudget() {
        Budget budget = saveBudgetManually(new Budget("testtitle1", 1000));
        budgetRepository.delete(budget);
        List<Budget> budgets = getBudgetsFromDatabaseManually();

        Session session = budgetRepository.getSession();

        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(session.isOpen()).isFalse();
        assertThat(budgets).isEmpty();
    }

    @Test
    public void testDeleteBudgetThatIsNotInDB() {
        Budget budget = new Budget("testtitle1", 1000);
        budget.setId(1);
        
        assertThrows(OptimisticLockException.class, () -> budgetRepository.delete(budget));
        Session session = budgetRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
        assertThat(session.isOpen()).isFalse();
        
    }
    
    @Test
	public void testDeleteBudgetRollback() {
		SessionFactory spiedFactory = spy(sessionFactory);
		Session spiedSession = spy(spiedFactory.openSession());
		Transaction spiedTransaction = spy(spiedSession.getTransaction());
		BudgetRepositorySqlImplementation spiedBudgetRepository = spy(budgetRepository);
		Budget budget = saveBudgetManually(new Budget("testtitle1", 1000));
		doReturn(spiedFactory).when(spiedBudgetRepository).getSessionFactory();
		doReturn(spiedSession).when(spiedFactory).openSession();
		doReturn(spiedTransaction).when(spiedSession).getTransaction();
		doThrow(new RuntimeException("Simulated exception")).when(spiedTransaction).commit();
		assertThrows(RuntimeException.class, () -> spiedBudgetRepository.delete(budget));
		Session session = spiedBudgetRepository.getSession();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
		assertThat(session.isOpen()).isFalse();
	}

    @Test
    public void testUpdateBudget() {
        Budget budget = saveBudgetManually(new Budget("testtitle1", 1000));
        budget.setTitle("testtitle2");
        budget.setIncomes(2000);
        
        budgetRepository.update(budget);
        List<Budget> budgets = getBudgetsFromDatabaseManually();
        Session session = budgetRepository.getSession();

        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(budgets).hasSize(1);
        assertThat(budgets.get(0).getTitle()).isEqualTo("testtitle2");
        assertThat(budgets.get(0).getIncomes()).isEqualTo(2000);
		assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void testUpdateBudgetThatIsNotInDB() {
        Budget budget = new Budget("testtitle1", 1000);
        budget.setId(1);
        
        assertThrows(OptimisticLockException.class, () -> budgetRepository.update(budget));
        Session session = budgetRepository.getSession();
        
        assertThat(session.isOpen()).isFalse();
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
    }
    
    @Test
	public void testUpdateBudgetRollback() {
		SessionFactory spiedFactory = spy(sessionFactory);
		Session spiedSession = spy(spiedFactory.openSession());
		Transaction spiedTransaction = spy(spiedSession.getTransaction());
		BudgetRepositorySqlImplementation spiedBudgetRepository = spy(budgetRepository);
		Budget budget = saveBudgetManually(new Budget("testtitle1", 1000));
		doReturn(spiedFactory).when(spiedBudgetRepository).getSessionFactory();
		doReturn(spiedSession).when(spiedFactory).openSession();
		doReturn(spiedTransaction).when(spiedSession).getTransaction();
		doThrow(new RuntimeException("Simulated exception")).when(spiedTransaction).commit();
		assertThrows(RuntimeException.class, () -> spiedBudgetRepository.update(budget));
		Session session = spiedBudgetRepository.getSession();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
		assertThat(session.isOpen()).isFalse();
	}
}
