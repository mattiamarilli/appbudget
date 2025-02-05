package ast.projects.appbudget.controllers;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.repositories.ExpenseItemRepository;
import ast.projects.appbudget.views.BudgetAppView;

/**
 * This class serves as a controller in the Model-View-Controller pattern,
 * handling interactions related to expense items between the user interface
 * (BudgetAppView) and the data repository (ExpenseItemRepository).
 */
public class ExpenseItemController {

	private ExpenseItemRepository expenseItemRepository;
	private BudgetAppView budgetAppView;

	/**
	 * Constructor for ExpenseItemController.
	 * 
	 * @param budgetAppView         The view for the budget app.
	 * @param expenseItemRepository The repository for expense items.
	 */
	public ExpenseItemController(BudgetAppView budgetAppView, ExpenseItemRepository expenseItemRepository) {
		this.budgetAppView = budgetAppView;
		this.expenseItemRepository = expenseItemRepository;
	}

	/**
	 * This method retrieves all expense items for a specific budget and refreshes
	 * the view with the retrieved data. In case of an error, an error message is
	 * displayed in the view.
	 * 
	 * @param budget The budget for which to retrieve expense items.
	 */
	public void allExpenseItemsByBudget(Budget budget) {
		try {
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(budget.getId()));
			budgetAppView.resetExpenseItemErrorMessage();
		} catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error fetching expense items");
		}
	}

	/**
	 * This method adds a new expense item to the repository and refreshes the view
	 * with the updated data. In case of an error, an error message is displayed in
	 * the view and the input fields are cleared.
	 * 
	 * @param expenseItem The expense item to add.
	 */
	public void addExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.save(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(expenseItem.getBudgetId()));
			budgetAppView.resetExpenseItemErrorMessage();
			budgetAppView.clearExpenseItemInputs();
		} catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error adding expense item");
		}
	}

	/**
	 * This method updates an existing expense item in the repository and refreshes
	 * the view with the updated data. In case of an error, an error message is
	 * displayed in the view and the input fields are cleared. This method is
	 * synchronized to avoid concurrent access issues.
	 * 
	 * @param expenseItem The expense item to update.
	 */
	public synchronized void updateExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.update(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(expenseItem.getBudgetId()));
			budgetAppView.resetExpenseItemErrorMessage();
			budgetAppView.clearExpenseItemInputs();
		} catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error updating expense item");
		}
	}

	/**
	 * This method deletes an expense item from the repository and refreshes the
	 * view with the updated data. In case of an error, an error message is
	 * displayed in the view and the input fields are cleared.
	 * 
	 * @param expenseItem The expense item to delete.
	 */
	public void deleteExpenseItem(ExpenseItem expenseItem) {
		try {
			expenseItemRepository.delete(expenseItem);
			budgetAppView.refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(expenseItem.getBudgetId()));
			budgetAppView.resetExpenseItemErrorMessage();
			budgetAppView.clearExpenseItemInputs();
		} catch (Exception e) {
			budgetAppView.showExpenseItemErrorMessage("Error deleting expense item");
		}
	}
}