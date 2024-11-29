package ast.projects.appbudget.controllers;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.repositories.ExpenseItemRepository;
import ast.projects.appbudget.views.BudgetAppView;

public class ExpenseItemController {

	private ExpenseItemRepository expenseItemRepository;
	private BudgetAppView budgetAppView;

	public ExpenseItemController(BudgetAppView budgetAppView, ExpenseItemRepository expenseItemRepository) {
		this.budgetAppView = budgetAppView;
		this.expenseItemRepository = expenseItemRepository;
	}
	
	public void allExpenseItemsByBudget(Budget budget) {
		try {
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(budget.getId()));
			budgetAppView.resetExpenseItemErrorMessage();
		}
		catch(Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error fetching expense items");
		}
	}

	public void addExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.save(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(expenseItem.getBudget().getId()));
			budgetAppView.resetExpenseItemErrorMessage();
			budgetAppView.clearExpenseItemInputs();
		} catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error adding expense item");
		}
	}
	
	public synchronized void updateExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.update(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(expenseItem.getBudget().getId()));
			budgetAppView.resetExpenseItemErrorMessage();
			budgetAppView.clearExpenseItemInputs();
		} catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error updating expense item");
		}
	}

	public void deleteExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.delete(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(expenseItem.getBudget().getId()));
			budgetAppView.resetExpenseItemErrorMessage();
			budgetAppView.clearExpenseItemInputs();
		}catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error deleting expense item");
		}
		
	}
}
