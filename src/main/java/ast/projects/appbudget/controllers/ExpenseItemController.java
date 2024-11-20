package ast.projects.appbudget.controllers;

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

	public void addExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.save(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findAll());
			budgetAppView.resetExpenseItemErrorMessage();
		} catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error adding expense item");
		}
	}
	
	public void updateExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.update(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findAll());
			budgetAppView.resetExpenseItemErrorMessage();
		} catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error updating expense item");
		}
	}

	public void deleteExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.delete(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findAll());
			budgetAppView.resetExpenseItemErrorMessage();
		}catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error deleting expense item");
		}
		
	}
}
