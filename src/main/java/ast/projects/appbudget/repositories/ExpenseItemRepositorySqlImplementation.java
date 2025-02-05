package ast.projects.appbudget.repositories;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import ast.projects.appbudget.models.ExpenseItem;

/**
 * This class implements the ExpenseItemRepository interface using Hibernate for
 * SQL database interactions.
 */
public class ExpenseItemRepositorySqlImplementation implements ExpenseItemRepository {

	private SessionFactory sessionFactory;
	private Session session;

	/**
	 * Getter method for session.
	 * 
	 * @return The current session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Constructor for ExpenseItemRepositorySqlImplementation.
	 * 
	 * @param sessionFactory The session factory for creating Hibernate sessions.
	 */
	public ExpenseItemRepositorySqlImplementation(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Getter method for sessionFactory.
	 * 
	 * @return The session factory.
	 */
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/**
	 * Saves an expense item to the database.
	 * 
	 * @param expenseItem The expense item to save.
	 * @throws Exception If an error occurs while saving the expense item.
	 */
	public void save(ExpenseItem expenseItem) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.save(expenseItem);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}

	/**
	 * Updates an expense item in the database.
	 * 
	 * @param expenseItem The expense item to update.
	 * @throws Exception If an error occurs while updating the expense item.
	 */
	public void update(ExpenseItem expenseItem) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.update(expenseItem);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}

	/**
	 * Deletes an expense item from the database.
	 * 
	 * @param expenseItem The expense item to delete.
	 * @throws Exception If an error occurs while deleting the expense item.
	 */
	public void delete(ExpenseItem expenseItem) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.delete(expenseItem);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}

	/**
	 * Retrieves all expense items from the database.
	 * 
	 * @return A list of all expense items.
	 * @throws Exception If an error occurs while retrieving expense items.
	 */
	public List<ExpenseItem> findAll() {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		List<ExpenseItem> expenseItems;
		try {
			Query<ExpenseItem> query = newSession.createQuery("FROM ExpenseItem", ExpenseItem.class);
			expenseItems = query.list();
		} catch (Exception e) {
			expenseItems = null;
			throw e;
		} finally {
			newSession.close();
		}
		return expenseItems;
	}

	/**
	 * Retrieves all expense items for a specific budget from the database.
	 * 
	 * @param budgetId The ID of the budget.
	 * @return A list of expense items for the specified budget.
	 * @throws Exception If an error occurs while retrieving expense items.
	 */

	@Override
	public List<ExpenseItem> findByBudgetId(long budgetId) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		List<ExpenseItem> expenseItems;
		try {
			Query<ExpenseItem> query = newSession.createQuery("FROM ExpenseItem e WHERE e.budgetId = :budgetId",
					ExpenseItem.class);
			query.setParameter("budgetId", budgetId);
			expenseItems = query.list();
		} catch (Exception e) {
			expenseItems = null;
			throw e;
		} finally {
			newSession.close();
		}
		return expenseItems;
	}

}
