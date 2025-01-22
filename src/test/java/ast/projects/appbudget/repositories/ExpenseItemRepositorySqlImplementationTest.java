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
import ast.projects.appbudget.models.Type;
import ast.projects.appbudget.models.User;

import java.io.Serializable;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class ExpenseItemRepositorySqlImplementationTest {

	private ExpenseItemRepositorySqlImplementation expenseItemRepository;
	private SessionFactory sessionFactory;

	@Before
	public void setUp() {
		sessionFactory = new Configuration().setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
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

		expenseItemRepository = new ExpenseItemRepositorySqlImplementation(sessionFactory);
	}

	@After
	public void tearDown() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	// Helper functions for ExpenseItem

	private void deleteExpenseItemTable() {
	    Session session = sessionFactory.openSession();
	    session.beginTransaction();
	    session.createNativeQuery("DROP TABLE IF EXISTS expenseitems CASCADE;").executeUpdate();
	    session.getTransaction().commit();
	    session.close();
	}

	private List<ExpenseItem> getExpenseItemsFromDatabaseManually() {
	    Session session = sessionFactory.openSession();
	    List<ExpenseItem> expenseItems = session.createQuery("FROM ExpenseItem", ExpenseItem.class).list();
	    session.close();
	    return expenseItems;
	}

	private ExpenseItem saveExpenseItemManually(ExpenseItem expenseItem) {
	    Session session = sessionFactory.openSession();
	    session.beginTransaction();
	    Serializable id = session.save(expenseItem);
	    expenseItem.setId((long) id);
	    session.getTransaction().commit();
	    session.close();
	    return expenseItem;
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

	@Test
	public void testFindByBudgetId() {

	    Budget budget1 = new Budget("testtitle1", 1000);
	    Budget budget2 = new Budget("testtitle2", 2000);

	    saveBudgetManually(budget1);
	    saveBudgetManually(budget2);

	    ExpenseItem expenseItem1 = new ExpenseItem("testtitle1", Type.NEEDS, 50);
	    ExpenseItem expenseItem2 = new ExpenseItem("testtitle2", Type.WANTS, 15);

	    expenseItem1.setBudgetId(budget1.getId());
	    expenseItem2.setBudgetId(budget2.getId());

	    saveExpenseItemManually(expenseItem1);
	    saveExpenseItemManually(expenseItem2);

	    List<ExpenseItem> expenseItems = expenseItemRepository.findByBudgetId(budget1.getId());

	    assertThat(expenseItems).hasSize(1);
	    assertThat(expenseItems.get(0).getTitle()).isEqualTo(expenseItem1.getTitle());
	    assertThat(expenseItems.get(0).getAmount()).isEqualTo(expenseItem1.getAmount());
	    assertThat(expenseItemRepository.getSession().isOpen()).isFalse();
	}

	@Test
	public void testFindByBudgetIdWhenNoExpenseItemsExistForBudget() {

		Budget budget = new Budget("testtitle1", 1000);
	    saveBudgetManually(budget);

	    List<ExpenseItem> expenseItems = expenseItemRepository.findByBudgetId(budget.getId());

	    assertThat(expenseItems).isEmpty();
	    assertThat(expenseItemRepository.getSession().isOpen()).isFalse();
	}

	@Test
	public void testFindByBudgetIdWhenExpenseItemTableDoesNotExist() {
	    deleteExpenseItemTable();

	    Budget budget = new Budget("testtitle1", 1000);
	    saveBudgetManually(budget);
	    long id = budget.getId();

	    assertThrows(PersistenceException.class, () -> expenseItemRepository.findByBudgetId(id));
	    assertThat(expenseItemRepository.getSession().isOpen()).isFalse();
	}


	@Test
	public void testFindAll() {

		ExpenseItem expenseItem1 = saveExpenseItemManually(new ExpenseItem("testtitle1", Type.NEEDS, 50));
		ExpenseItem expenseItem2 = saveExpenseItemManually(new ExpenseItem("testtitle2", Type.WANTS, 15));

		List<ExpenseItem> expenseItems = expenseItemRepository.findAll();

		assertThat(expenseItemRepository.getSession().isOpen()).isFalse();
		assertThat(expenseItems).hasSize(2);
		assertThat(expenseItems.get(0).getTitle()).isEqualTo(expenseItem1.getTitle());
		assertThat(expenseItems.get(0).getAmount()).isEqualTo(expenseItem1.getAmount());
		assertThat(expenseItems.get(0).getType()).isEqualTo(expenseItem1.getType());
		assertThat(expenseItems.get(1).getTitle()).isEqualTo(expenseItem2.getTitle());
		assertThat(expenseItems.get(1).getAmount()).isEqualTo(expenseItem2.getAmount());
		assertThat(expenseItems.get(1).getType()).isEqualTo(expenseItem2.getType());

	}

	@Test
	public void testFindAllEmptyDb() {
		List<ExpenseItem> expenseItems = expenseItemRepository.findAll();

		assertThat(expenseItems).isEmpty();
		assertThat(expenseItemRepository.getSession().isOpen()).isFalse();
	}

	@Test
	public void testFindAllWhenExpenseItemTableDontExists() {
		deleteExpenseItemTable();

		assertThrows(PersistenceException.class, () -> expenseItemRepository.findAll());
		assertThat(expenseItemRepository.getSession().isOpen()).isFalse();
	}

	@Test
	public void testSaveExpenseItem() {
		ExpenseItem expenseItem = new ExpenseItem("testtitle1", Type.NEEDS, 50);

		expenseItemRepository.save(expenseItem);
		List<ExpenseItem> expenseItems = getExpenseItemsFromDatabaseManually();

		Session session = expenseItemRepository.getSession();

		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
		assertThat(session.isOpen()).isFalse();
		assertThat(expenseItems).hasSize(1);
		assertThat(expenseItems.get(0).getTitle()).isEqualTo("testtitle1");
		assertThat(expenseItems.get(0).getAmount()).isEqualTo(50);
		assertThat(expenseItems.get(0).getType()).isEqualTo(Type.NEEDS);
	}

	@Test
	public void testSaveExpenseItemWhenExpenseItemIsAlreadyInDB() {
		
	    Budget budget = new Budget("testtitle1", 1000);
	    saveBudgetManually(budget);
	    
	    ExpenseItem expenseItemToSave = new ExpenseItem("testtitle1", Type.NEEDS, 50);
	    
	    expenseItemToSave.setBudgetId(budget.getId());	
		
		ExpenseItem expenseItem = saveExpenseItemManually(expenseItemToSave);
		
		assertThrows(ConstraintViolationException.class, () -> expenseItemRepository.save(expenseItem));
		Session session = expenseItemRepository.getSession();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
		assertThat(session.isOpen()).isFalse();
		assertThat(getExpenseItemsFromDatabaseManually()).hasSize(1);
	}

	@Test
	public void testDeleteExpenseItem() {
		ExpenseItem expenseItem = saveExpenseItemManually(new ExpenseItem("testtitle1", Type.NEEDS, 50));
		expenseItemRepository.delete(expenseItem);
		List<ExpenseItem> expenseItems = getExpenseItemsFromDatabaseManually();

		Session session = expenseItemRepository.getSession();

		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
		assertThat(session.isOpen()).isFalse();
		assertThat(expenseItems).isEmpty();
	}

	@Test
	public void testDeleteExpenseItemThatIsNotInDB() {
		ExpenseItem expenseItem = new ExpenseItem("testtitle1", Type.NEEDS, 50);
		expenseItem.setId(1);
		assertThrows(OptimisticLockException.class, () -> expenseItemRepository.delete(expenseItem));
		Session session = expenseItemRepository.getSession();
		assertThat(session.isOpen()).isFalse();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
	}

	@Test
	public void testUpdateExpenseItem() {
		ExpenseItem expenseItem = saveExpenseItemManually(new ExpenseItem("testtitle1", Type.NEEDS, 50));
		expenseItem.setTitle("updatedtitle");
		expenseItem.setAmount(100);
		expenseItemRepository.update(expenseItem);
		ExpenseItem updatedExpenseItem = getExpenseItemsFromDatabaseManually().get(0);

		assertThat(expenseItemRepository.getSession().isOpen()).isFalse();
		assertThat(updatedExpenseItem.getTitle()).isEqualTo("updatedtitle");
		assertThat(updatedExpenseItem.getAmount()).isEqualTo(100);
	}

	@Test
	public void testUpdateExpenseItemNotInDB() {
		ExpenseItem expenseItem = new ExpenseItem("testtitle1", Type.NEEDS, 50);
		expenseItem.setId(1);
		assertThrows(OptimisticLockException.class, () -> expenseItemRepository.update(expenseItem));
		Session session = expenseItemRepository.getSession();
		assertThat(session.isOpen()).isFalse();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
	}
}
