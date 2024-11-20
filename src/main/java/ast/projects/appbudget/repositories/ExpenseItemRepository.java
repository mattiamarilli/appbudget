package ast.projects.appbudget.repositories;

import java.util.List;

import ast.projects.appbudget.models.ExpenseItem;

public interface ExpenseItemRepository {

	public List<ExpenseItem> findAll();

	public void save(ExpenseItem expenseItem);
	
	public void update(ExpenseItem expenseItem);

	public void delete(ExpenseItem expenseItem);
}

