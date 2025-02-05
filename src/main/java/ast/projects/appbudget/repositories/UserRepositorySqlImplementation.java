package ast.projects.appbudget.repositories;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import ast.projects.appbudget.models.User;

/**
 * This class implements the UserRepository interface using Hibernate for SQL
 * database interactions.
 */
public class UserRepositorySqlImplementation implements UserRepository {

	private SessionFactory sessionFactory;
	private Session session;

	/**
	 * Constructor for UserRepositorySqlImplementation.
	 * 
	 * @param sessionFactory The session factory for creating Hibernate sessions.
	 */
	public UserRepositorySqlImplementation(SessionFactory sessionFactory) {
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
	 * Getter method for session.
	 * 
	 * @return The current session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Saves a user to the database.
	 * 
	 * @param user The user to save.
	 * @throws Exception If an error occurs while saving the user.
	 */
	@Override
	public void save(User user) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.save(user);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}
	
	
	/**
	 * Updates a user in the database.
	 * 
	 * @param user The user to update.
	 * @throws Exception If an error occurs while updating the budget.
	 */
	@Override
	public void update(User user) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.update(user);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}

	/**
	 * Deletes a user from the database.
	 * 
	 * @param user The user to delete.
	 * @throws Exception If an error occurs while deleting the user.
	 */
	@Override
	public void delete(User user) {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		try {
			newSession.beginTransaction();
			newSession.delete(user);
			newSession.getTransaction().commit();
		} catch (Exception e) {
			newSession.getTransaction().rollback();
			throw e;
		} finally {
			newSession.close();
		}
	}

	/**
	 * Retrieves all users from the database.
	 * 
	 * @return A list of all users.
	 * @throws Exception If an error occurs while retrieving users.
	 */
	@Override
	public List<User> findAll() {
		Session newSession = getSessionFactory().openSession();
		session = newSession;
		List<User> users;
		try {
			Query<User> query = newSession.createQuery("FROM User", User.class);
			users = query.list();
		} catch (Exception e) {
			users = null;
			throw e;
		} finally {
			newSession.close();
		}
		return users;
	}
}