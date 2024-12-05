package ast.projects.appbudget.repositories;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import ast.projects.appbudget.models.User;

public class UserRepositorySqlImplementation implements UserRepository {

    private SessionFactory sessionFactory;
    private Session session;

	public UserRepositorySqlImplementation(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
	
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}
	
	Session getSession() {
		return session;
	}
	
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
