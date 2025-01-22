package ast.projects.appbudget.repositories;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import ast.projects.appbudget.models.Budget;

public class BudgetRepositorySqlImplementation implements BudgetRepository {

    private SessionFactory sessionFactory;
    private Session session;

    Session getSession() {
		return session;
	}

	public BudgetRepositorySqlImplementation(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
	
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

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
