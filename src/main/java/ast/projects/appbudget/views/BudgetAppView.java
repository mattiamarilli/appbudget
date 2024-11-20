package ast.projects.appbudget.views;

import java.util.List;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.User;

public interface BudgetAppView{
	
	void refreshUsersList(List<User> users);
	void showUserErrorMessage(String message);
	void resetUserErrorMessage();
	
	void refreshBudgetsList(List<Budget> budgets);
	void showBudgetErrorMessage(String message);
	void resetBudgetErrorMessage();
	
	void refreshExpenseItemsLists(List<ExpenseItem> expenseItems);
	void showExpenseItemErrorMessage(String message);
	void resetExpenseItemErrorMessage();

}
