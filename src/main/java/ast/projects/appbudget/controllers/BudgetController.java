package ast.projects.appbudget.controllers;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.BudgetRepository;
import ast.projects.appbudget.views.BudgetAppView;

public class BudgetController {

	private BudgetRepository budgetRepository;
	private BudgetAppView budgetAppView;

	public BudgetController(BudgetAppView budgetAppView, BudgetRepository userRepository) {
		this.budgetAppView = budgetAppView;
		this.budgetRepository = userRepository;
	}
	
	public void allBudgetsByUser(User user) {
		try {
			budgetAppView.refreshBudgetsList(budgetRepository.findByUserId(user.getId()));
			budgetAppView.resetBudgetErrorMessage();
		}
		catch(Exception e) {
			budgetAppView.showBudgetErrorMessage("Error fetching budgets");
		}
	}

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

	public void deleteBudget(Budget budget) {
		try {
			budgetRepository.delete(budget);
			budgetAppView.refreshBudgetsList(budgetRepository.findByUserId(budget.getUserId()));
			budgetAppView.resetBudgetErrorMessage();
			budgetAppView.clearBudgetInputs();
		}catch (Exception e) {
			budgetAppView.showBudgetErrorMessage("Error deleting budget");
		}
		
	}
}
