package ast.projects.appbudget.controllers;

import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.UserRepository;
import ast.projects.appbudget.views.BudgetAppView;

public class UserController {

	private UserRepository userRepository;
	private BudgetAppView userView;

	public UserController(BudgetAppView userView, UserRepository userRepository) {
		this.userView = userView;
		this.userRepository = userRepository;
	}

	public void allUsers() {
		try {
			userView.refreshUsersList(userRepository.findAll());
			userView.resetUserErrorMessage();
		}
		catch(Exception e) {
			userView.showUserErrorMessage("Error fetching users");
		}
		
	}

	public void addUser(User user) {
		try {
			userRepository.save(user);
			userView.refreshUsersList(userRepository.findAll());
			userView.resetUserErrorMessage();
		} catch (Exception e) {
			userView.showUserErrorMessage("Error adding new user");
		}
	}

	public void deleteUser(User user) {
		try {
			userRepository.delete(user);
			userView.refreshUsersList(userRepository.findAll());
			userView.resetUserErrorMessage();
		}catch (Exception e) {
			userView.showUserErrorMessage("Error deleting user");
		}
		
	}
}
