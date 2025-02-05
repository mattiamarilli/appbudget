package ast.projects.appbudget.repositories;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import ast.projects.appbudget.models.Budget;

/**
 * This class implements the BudgetRepository interface using Hibernate for SQL
 * database interactions.
 */
public class BudgetRepositorySqlImplementation implements BudgetRepository {

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
	 * Constructor for BudgetRepositorySqlImplementation.
	 * 
	 * @param sessionFactory The session factory for creating Hibernate sessions.
	 */
	public BudgetRepositorySqlImplementation(SessionFactory sessionFactory) {
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
	 * Saves a budget to the database.
	 * 
	 * @param budget The budget to save.
	 * @throws Exception If an error occurs while saving the budget.
	 */
	@Override
	public void save(Budget budget) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.save(budget);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}

	/**
	 * Updates a budget in the database.
	 * 
	 * @param budget The budget to update.
	 * @throws Exception If an error occurs while updating the budget.
	 */
	@Override
	public void update(Budget budget) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.update(budget);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}

	/**
	 * Deletes a budget from the database.
	 * 
	 * @param budget The budget to delete.
	 * @throws Exception If an error occurs while deleting the budget.
	 */
	@Override
	public void delete(Budget budget) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.delete(budget);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}

	/**
	 * Retrieves all budgets from the database.
	 * 
	 * @return A list of all budgets.
	 * @throws Exception If an error occurs while retrieving budgets.
	 */
	@Override
	public List<Budget> findAll() {
		Session newSession = getSessionFactory().openSession();
		this.session = newSession;
		List<Budget> budgets;
		try {
			Query<Budget> query = newSession.createQuery("FROM Budget", Budget.class);
			budgets = query.list();
		} catch (Exception e) {
			budgets = null;
			throw e;
		} finally {
			newSession.close();
		}
		return budgets;
	}

	/**
	 * Retrieves all budgets for a specific user from the database.
	 * 
	 * @param userId The ID of the user.
	 * @return A list of budgets for the specified user.
	 * @throws Exception If an error occurs while retrieving budgets.
	 */
	@Override
	public List<Budget> findByUserId(long userId) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		List<Budget> budgets;
		try {
			Query<Budget> query = newSession.createQuery("FROM Budget b WHERE b.userId = :userId", Budget.class);
			query.setParameter("userId", userId);
			budgets = query.list();
		} catch (Exception e) {
			budgets = null;
			throw e;
		} finally {
			newSession.close();
		}
		return budgets;
	}

}