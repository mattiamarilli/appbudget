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
        session = getSessionFactory().openSession();
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
        session = getSessionFactory().openSession();
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
        session = getSessionFactory().openSession();
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
        session = getSessionFactory().openSession();
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
}
