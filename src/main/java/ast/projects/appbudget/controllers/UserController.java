package ast.projects.appbudget.controllers;

import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.UserRepository;
import ast.projects.appbudget.views.BudgetAppView;

public class UserController {

	private UserRepository userRepository;
	private BudgetAppView budgetAppView;

	public UserController(BudgetAppView userView, UserRepository userRepository) {
		this.budgetAppView = userView;
		this.userRepository = userRepository;
	}

	public void allUsers() {
		try {
			budgetAppView.refreshUsersList(userRepository.findAll());
			budgetAppView.resetUserErrorMessage();
		}
		catch(Exception e) {
			budgetAppView.showUserErrorMessage("Error fetching users");
		}
		
	}

	
	public void addUser(User user) {
		try {
			userRepository.save(user);
			budgetAppView.refreshUsersList(userRepository.findAll());
			budgetAppView.resetUserErrorMessage();
		} catch (Exception e) {
			budgetAppView.showUserErrorMessage("Error adding new user");
		}
	}

	public void deleteUser(User user) {
		try {
			userRepository.delete(user);
			budgetAppView.refreshUsersList(userRepository.findAll());
			budgetAppView.resetUserErrorMessage();
		}catch (Exception e) {
			budgetAppView.showUserErrorMessage("Error deleting user");
		}
		
	}
}
