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
			userView.resetErrorMessage();
		}
		catch(Exception e) {
			userView.showErrorMessage("Error fetching users");
		}
		
	}

	public synchronized void addUser(String name, String surname) {
		try {
			userRepository.save(new User(name, surname));
			userView.refreshUsersList(userRepository.findAll());
			userView.resetErrorMessage();
		} catch (Exception e) {
			userView.showErrorMessage("Error adding new user");
		}
	}

	public void deleteUser(User user) {
		try {
			userRepository.delete(user);
			userView.refreshUsersList(userRepository.findAll());
			userView.resetErrorMessage();
		}catch (Exception e) {
			userView.showErrorMessage("Error deleting user");
		}
		
	}
}
