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

	// Constants for test values
	private static final String TEST_EXPENSEITEM_1_TITLE = "Rent";
	private static final double TEST_EXPENSEITEM_1_AMOUNT = 500;
	private static final Type TEST_EXPENSEITEM_1_TYPE = Type.NEEDS;
	private static final String TEST_EXPENSEITEM_2_TITLE = "Groceries";
	private static final double TEST_EXPENSEITEM_2_AMOUNT = 150;
	private static final Type TEST_EXPENSEITEM_2_TYPE = Type.WANTS;

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
		sessionFactory = new Configuration().setProperty("hibernate.dialect", HIBERNATE_DIALECT)
				// with create-drop behavior every time drop and create the ExpenseItem's table
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

		expenseItemRepository = new ExpenseItemRepositorySqlImplementation(sessionFactory);
	}

	@After
	public void tearDown() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

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

	private ExpenseItem saveExpenseItemManually(String title, Type type, double amount) {
		ExpenseItem expenseItem = new ExpenseItem(title, type, amount);
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Serializable id = session.save(expenseItem);
		expenseItem.setId((long) id);
		session.getTransaction().commit();
		session.close();
		return expenseItem;
	}

	@Test
	public void testFindAll() {

		ExpenseItem expenseItem1 = saveExpenseItemManually(TEST_EXPENSEITEM_1_TITLE, TEST_EXPENSEITEM_1_TYPE,
				TEST_EXPENSEITEM_1_AMOUNT);
		ExpenseItem expenseItem2 = saveExpenseItemManually(TEST_EXPENSEITEM_2_TITLE, TEST_EXPENSEITEM_2_TYPE,
				TEST_EXPENSEITEM_2_AMOUNT);

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
		ExpenseItem expenseItem = new ExpenseItem(TEST_EXPENSEITEM_1_TITLE, TEST_EXPENSEITEM_1_TYPE,
				TEST_EXPENSEITEM_1_AMOUNT);

		expenseItemRepository.save(expenseItem);
		List<ExpenseItem> expenseItems = getExpenseItemsFromDatabaseManually();

		Session session = expenseItemRepository.getSession();

		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
		assertThat(session.isOpen()).isFalse();
		assertThat(expenseItems).hasSize(1);
		assertThat(expenseItems.get(0).getTitle()).isEqualTo(TEST_EXPENSEITEM_1_TITLE);
		assertThat(expenseItems.get(0).getAmount()).isEqualTo(TEST_EXPENSEITEM_1_AMOUNT);
		assertThat(expenseItems.get(0).getType()).isEqualTo(TEST_EXPENSEITEM_1_TYPE);
	}

	@Test
	public void testSaveExpenseItemWhenExpenseItemIsAlreadyInDB() {
		ExpenseItem expenseItem = saveExpenseItemManually(TEST_EXPENSEITEM_1_TITLE, TEST_EXPENSEITEM_1_TYPE,
				TEST_EXPENSEITEM_1_AMOUNT);
		assertThrows(ConstraintViolationException.class, () -> expenseItemRepository.save(expenseItem));
		Session session = expenseItemRepository.getSession();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
		assertThat(session.isOpen()).isFalse();
		assertThat(getExpenseItemsFromDatabaseManually()).hasSize(1);
	}

	@Test
	public void testDeleteExpenseItem() {
		ExpenseItem expenseItem = saveExpenseItemManually(TEST_EXPENSEITEM_1_TITLE, TEST_EXPENSEITEM_1_TYPE,
				TEST_EXPENSEITEM_1_AMOUNT);
		expenseItemRepository.delete(expenseItem);
		List<ExpenseItem> expenseItems = getExpenseItemsFromDatabaseManually();

		Session session = expenseItemRepository.getSession();

		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
		assertThat(session.isOpen()).isFalse();
		assertThat(expenseItems).isEmpty();
	}

	@Test
	public void testDeleteExpenseItemThatIsNotInDB() {
		ExpenseItem expenseItem = new ExpenseItem(TEST_EXPENSEITEM_1_TITLE, TEST_EXPENSEITEM_1_TYPE,
				TEST_EXPENSEITEM_1_AMOUNT);
		expenseItem.setId(1);
		assertThrows(OptimisticLockException.class, () -> expenseItemRepository.delete(expenseItem));
		Session session = expenseItemRepository.getSession();
		assertThat(session.isOpen()).isFalse();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);
	}

	@Test
	public void testUpdateExpenseItem() {
		ExpenseItem expenseItem = saveExpenseItemManually(TEST_EXPENSEITEM_1_TITLE, TEST_EXPENSEITEM_1_TYPE,
				TEST_EXPENSEITEM_1_AMOUNT);
		expenseItem.setTitle(TEST_EXPENSEITEM_2_TITLE);
		expenseItem.setType(TEST_EXPENSEITEM_2_TYPE);
		expenseItem.setAmount(TEST_EXPENSEITEM_2_AMOUNT);
		expenseItemRepository.update(expenseItem);
		List<ExpenseItem> expenseItems = getExpenseItemsFromDatabaseManually();

		Session session = expenseItemRepository.getSession();

		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.COMMITTED);
		assertThat(expenseItems.get(0).getTitle()).isEqualTo(TEST_EXPENSEITEM_2_TITLE);
		assertThat(expenseItems.get(0).getAmount()).isEqualTo(TEST_EXPENSEITEM_2_AMOUNT);
		assertThat(expenseItems.get(0).getType()).isEqualTo(TEST_EXPENSEITEM_2_TYPE);
	}

	@Test
	public void testUpdateExpenseItemThatIsNotInDB() {
		ExpenseItem expenseItem = new ExpenseItem(TEST_EXPENSEITEM_1_TITLE, TEST_EXPENSEITEM_1_TYPE,
				TEST_EXPENSEITEM_1_AMOUNT);
		expenseItem.setId(1);
		assertThrows(OptimisticLockException.class, () -> expenseItemRepository.update(expenseItem));
		Session session = expenseItemRepository.getSession();
		assertThat(session.isOpen()).isFalse();
		assertThat(session.getTransaction().getStatus()).isEqualTo(TransactionStatus.ROLLED_BACK);

	}

}
