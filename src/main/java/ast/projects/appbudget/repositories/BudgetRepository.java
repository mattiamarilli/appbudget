package ast.projects.appbudget.repositories;

import java.util.List;

import ast.projects.appbudget.models.Budget;

public interface BudgetRepository {

	public List<Budget> findAll();

	public void save(Budget budget);
	
	public void update(Budget budget);

	public void delete(Budget budget);
}
