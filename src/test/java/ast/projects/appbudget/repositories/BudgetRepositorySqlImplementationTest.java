package ast.projects.appbudget.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

public class BudgetRepositorySqlImplementationTest {

    private BudgetRepositorySqlImplementation budgetRepository;
    private SessionFactory sessionFactory;

    // Constants for test values
    private static final String TEST_BUDGET_1_TITLE = "Luglio 2019";
    private static final double TEST_BUDGET_1_INCOMES = 1000;
    private static final String TEST_BUDGET_2_TITLE = "Agosto 2019";
    private static final double TEST_BUDGET_2_INCOMES = 2000;
    
    
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
                //with create-drop behavior every time drop and create the Budget's table
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
    
    //TODO: remove setid
    private Budget saveBudgetManually(String title, double incomes) {
    	Budget budget = new Budget(title, incomes);
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Serializable id = session.save(budget);
		budget.setId((long) id);
		session.getTransaction().commit();
		session.close();
		return budget;
	}
    
    @Test
    public void testFindAll() {

    	Budget budget1 = saveBudgetManually(TEST_BUDGET_1_TITLE, TEST_BUDGET_1_INCOMES);
    	Budget budget2 = saveBudgetManually(TEST_BUDGET_2_TITLE, TEST_BUDGET_2_INCOMES);
        
        List<Budget> budgets = budgetRepository.findAll();
        
        assertThat(budgetRepository.getSession().isOpen()).isFalse();
        assertThat(budgets).hasSize(2);
        assertThat(budgets.get(0).getTitle()).isEqualTo(budget1.getTitle());
        assertThat(budgets.get(0).getIncomes()).isEqualTo(budget1.getIncomes());
        assertThat(budgets.get(1).getTitle()).isEqualTo(budget2.getTitle());
        assertThat(budgets.get(1).getIncomes()).isEqualTo(budget2.getIncomes());
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
        Budget budget = new Budget(TEST_BUDGET_1_TITLE, TEST_BUDGET_1_INCOMES);

        budgetRepository.save(budget);
        List<Budget> budgets = getBudgetsFromDatabaseManually();
        
        Session session = budgetRepository.getSession();
        
        assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(session.isOpen()).isFalse();
        assertThat(budgets).hasSize(1);
        assertThat(budgets.get(0).getTitle()).isEqualTo(TEST_BUDGET_1_TITLE);
        assertThat(budgets.get(0).getIncomes()).isEqualTo(TEST_BUDGET_1_INCOMES);
    }
    
    @Test
	public void testSaveBudgetWhenBudgetIsAlreadyInDB() {
    	Budget budget = saveBudgetManually(TEST_BUDGET_1_TITLE, TEST_BUDGET_1_INCOMES);
		assertThrows(ConstraintViolationException.class, () -> budgetRepository.save(budget));
		Session session = budgetRepository.getSession();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
        assertThat(session.isOpen()).isFalse();
		assertThat(getBudgetsFromDatabaseManually()).hasSize(1);
	}

    @Test
	public void testDeleteBudget() {
    	Budget budget = saveBudgetManually(TEST_BUDGET_1_TITLE, TEST_BUDGET_1_INCOMES);
		budgetRepository.delete(budget);
		List<Budget> budgets = getBudgetsFromDatabaseManually();
		
		Session session = budgetRepository.getSession();
		
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
        assertThat(session.isOpen()).isFalse();
        assertThat(budgets).isEmpty();
	}

	@Test
	public void testDeleteBudgetThatIsNotInDB() {
		Budget budget = new Budget(TEST_BUDGET_1_TITLE, TEST_BUDGET_1_INCOMES);
		budget.setId(1);
		assertThrows(OptimisticLockException.class, () -> budgetRepository.delete(budget));
		Session session = budgetRepository.getSession();
		assertThat(session.isOpen()).isFalse();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
	}
	
    @Test
	public void testUpdateBudget() {
    	Budget budget = saveBudgetManually(TEST_BUDGET_1_TITLE, TEST_BUDGET_1_INCOMES);
    	budget.setTitle(TEST_BUDGET_2_TITLE);
    	budget.setIncomes(TEST_BUDGET_2_INCOMES);
		budgetRepository.update(budget);
		List<Budget> budgets = getBudgetsFromDatabaseManually();
		
		Session session = budgetRepository.getSession();
		
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
		assertThat(budgets).hasSize(1);
	    assertThat(budgets.get(0).getTitle()).isEqualTo(TEST_BUDGET_2_TITLE);
	    assertThat(budgets.get(0).getIncomes()).isEqualTo(TEST_BUDGET_2_INCOMES);
	}

	@Test
	public void testUpdateBudgetThatIsNotInDB() {
		Budget budget = new Budget(TEST_BUDGET_1_TITLE, TEST_BUDGET_1_INCOMES);
		budget.setId(1);
		assertThrows(OptimisticLockException.class, () -> budgetRepository.update(budget));
		Session session = budgetRepository.getSession();
		assertThat(session.isOpen()).isFalse();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
	
    
	}
    
}
