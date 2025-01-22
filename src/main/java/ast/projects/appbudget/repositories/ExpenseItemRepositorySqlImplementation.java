package ast.projects.appbudget.repositories;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import ast.projects.appbudget.models.ExpenseItem;

public class ExpenseItemRepositorySqlImplementation implements ExpenseItemRepository{

    private SessionFactory sessionFactory;
    private Session session;

    Session getSession() {
        return session;
    }

    public ExpenseItemRepositorySqlImplementation(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

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
    
    @Override
    public List<ExpenseItem> findByBudgetId(long budgetId) {
        Session newSession = getSessionFactory().openSession();
        session = newSession;
        List<ExpenseItem> expenseItems;
        try {
            Query<ExpenseItem> query = newSession.createQuery("FROM ExpenseItem e WHERE e.budgetId = :budgetId", ExpenseItem.class);
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
