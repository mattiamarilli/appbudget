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
	    Session session = getSessionFactory().openSession();
	    this.session = session;
	    try {
	        session.beginTransaction();
	        session.save(budget);
	        session.getTransaction().commit();
	    } catch (Exception e) {
	        session.getTransaction().rollback();
	        throw e;
	    } finally {
	        session.close();
	    }
	}
	
	@Override
	public void update(Budget budget) {
	    Session session = getSessionFactory().openSession();
	    this.session = session;
	    try {
	        session.beginTransaction();
	        session.update(budget);
	        session.getTransaction().commit();
	    } catch (Exception e) {
	        session.getTransaction().rollback();
	        throw e;
	    } finally {
	        session.close();
	    }
	}

	@Override
	public void delete(Budget budget) {
	    Session session = getSessionFactory().openSession();
	    this.session = session;
	    try {
	        session.beginTransaction();
	        session.delete(budget);
	        session.getTransaction().commit();
	    } catch (Exception e) {
	        session.getTransaction().rollback();
	        throw e;
	    } finally {
	        session.close();
	    }
	}

	@Override
	public List<Budget> findAll() {
	    Session session = getSessionFactory().openSession();
	    this.session = session;
	    List<Budget> budgets;
	    try {
	        Query<Budget> query = session.createQuery("FROM Budget", Budget.class);
	        budgets = query.list();
	    } catch (Exception e) {
	    	throw e;
	    } finally {
	        session.close();
	    }
	    return budgets;
	}
	
	@Override
	public List<Budget> findByUserId(long userId) {
	    Session session = getSessionFactory().openSession();
	    this.session = session;
	    List<Budget> budgets;
	    try {
	        Query<Budget> query = session.createQuery("FROM Budget b WHERE b.user.id = :userId", Budget.class);
	        query.setParameter("userId", userId);
	        budgets = query.list();
	    } catch (Exception e) {
	        throw e;
	    } finally {
	        session.close();
	    }
	    return budgets;
	}

}
