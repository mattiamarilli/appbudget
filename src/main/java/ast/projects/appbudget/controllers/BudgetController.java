package ast.projects.appbudget.controllers;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.BudgetRepository;
import ast.projects.appbudget.views.BudgetAppView;

/**
 * This class serves as a controller in the Model-View-Controller pattern,
 * handling interactions related to budgets between the user interface
 * (BudgetAppView) and the data repository (BudgetRepository).
 */
public class BudgetController {

	/**
	 * An instance of the BudgetRepository, used to access and manipulate budget
	 * data.
	 */
	private BudgetRepository budgetRepository;

	/**
	 * An instance of the BudgetAppView, used to communicate with the user interface
	 * for budgets.
	 */
	private BudgetAppView budgetAppView;

	/**
	 * Constructor for the BudgetController.
	 *
	 * @param budgetAppView    The BudgetAppView instance for user interface
	 *                         interactions.
	 * @param budgetRepository The BudgetRepository instance for data access.
	 */
	public BudgetController(BudgetAppView budgetAppView, BudgetRepository userRepository) {
		this.budgetAppView = budgetAppView;
		this.budgetRepository = userRepository;
	}

	/**
	 * Retrieves all budgets associated with a specific user and updates the user
	 * interface accordingly. Handles exceptions that may occur during data
	 * retrieval.
	 *
	 * @param user The User object representing the current user.
	 */
	public void allBudgetsByUser(User user) {
		try {
			budgetAppView.refreshBudgetsList(budgetRepository.findByUserId(user.getId()));
			budgetAppView.resetBudgetErrorMessage();
		} catch (Exception e) {
			budgetAppView.showBudgetErrorMessage("Error fetching budgets");
		}
	}

	/**
	 * Adds a new budget to the system. Saves the budget data to the repository,
	 * updates the user interface, resets error messages, and clears input fields
	 * after addition. Handles exceptions.
	 *
	 * @param budget The Budget object representing the new budget to add.
	 */
	public void addBudget(Budget budget) {
		try {
			budgetRepository.save(budget);
			budgetAppView.refreshBudgetsList(budgetRepository.findByUserId(budget.getUserId()));
			budgetAppView.resetBudgetErrorMessage();
			budgetAppView.clearBudgetInputs();
		} catch (Exception e) {
			budgetAppView.showBudgetErrorMessage("Error adding new budget");
		}
	}

	/**
	 * Updates an existing budget in the system. Updates the budget data in the
	 * repository, updates the user interface, resets error messages, and clears
	 * input fields after the update. The method is synchronized to ensure thread
	 * safety during concurrent updates. Handles exceptions.
	 *
	 * @param budget The Budget object with the updated information.
	 */
	public synchronized void updateBudget(Budget budget) {
		try {
			budgetRepository.update(budget);
			budgetAppView.refreshBudgetsList(budgetRepository.findByUserId(budget.getUserId()));
			budgetAppView.resetBudgetErrorMessage();
			budgetAppView.clearBudgetInputs();
		} catch (Exception e) {
			budgetAppView.showBudgetErrorMessage("Error updating budget");
		}
	}

	/**
	 * Deletes a budget from the system. Deletes the budget data from the
	 * repository, updates the user interface, resets error messages, and clears
	 * input fields after deletion. Handles exceptions.
	 *
	 * @param budget The Budget object representing the budget to delete.
	 */
	public void deleteBudget(Budget budget) {
		try {
			budgetRepository.delete(budget);
			budgetAppView.refreshBudgetsList(budgetRepository.findByUserId(budget.getUserId()));
			budgetAppView.resetBudgetErrorMessage();
			budgetAppView.clearBudgetInputs();
		} catch (Exception e) {
			budgetAppView.showBudgetErrorMessage("Error deleting budget");
		}
	}
}