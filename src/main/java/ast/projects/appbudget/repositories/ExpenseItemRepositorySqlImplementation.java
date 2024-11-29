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
        Session session = getSessionFactory().openSession();
        this.session = session;
        try {
            session.beginTransaction();
            session.save(expenseItem);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void update(ExpenseItem expenseItem) {
        Session session = getSessionFactory().openSession();
        this.session = session;
        try {
            session.beginTransaction();
            session.update(expenseItem);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void delete(ExpenseItem expenseItem) {
        Session session = getSessionFactory().openSession();
        this.session = session;
        try {
            session.beginTransaction();
            session.delete(expenseItem);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<ExpenseItem> findAll() {
        Session session = getSessionFactory().openSession();
        this.session = session;
        List<ExpenseItem> expenseItems;
        try {
            Query<ExpenseItem> query = session.createQuery("FROM ExpenseItem", ExpenseItem.class);
            expenseItems = query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
        return expenseItems;
    }
    
    @Override
    public List<ExpenseItem> findByBudgetId(long budgetId) {
        Session session = getSessionFactory().openSession();
        this.session = session;
        List<ExpenseItem> expenseItems;
        try {
            Query<ExpenseItem> query = session.createQuery("FROM ExpenseItem e WHERE e.budget.id = :budgetId", ExpenseItem.class);
            query.setParameter("budgetId", budgetId);
            expenseItems = query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
        return expenseItems;
    }

}
