package ast.projects.appbudget.views;

import java.util.List;

import ast.projects.appbudget.models.User;

public interface BudgetAppView{
	
	void refreshUsersList(List<User> users);
	void showErrorMessage(String message);
	void resetErrorMessage();


}
