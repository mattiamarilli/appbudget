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
	    Session session = getSessionFactory().openSession();
	    this.session = session;
	    try {
	        session.beginTransaction();
	        session.save(user);
	        session.getTransaction().commit();
	    } catch (Exception e) {
	    	session.getTransaction().rollback();
	        throw e;
	    } finally {
	        session.close();
	    }
	}
	@Override
	public void delete(User user) {
		Session session = getSessionFactory().openSession();
		this.session = session;
	    try {
	        session.beginTransaction();
	        session.delete(user);
	        session.getTransaction().commit();
	    } catch (Exception e) {
	        session.getTransaction().rollback();
	        throw e;
	    } finally {
	        session.close();
	    }
	}

	@Override
	public List<User> findAll() {
		Session session = getSessionFactory().openSession();
		this.session = session;
	    List<User> users;
	    try {
	        Query<User> query = session.createQuery("FROM User", User.class);
	        users = query.list();
	    } catch (Exception e) {
	    	throw e;
	    } finally {
	        session.close();
	    }
	    return users;
	}
}
