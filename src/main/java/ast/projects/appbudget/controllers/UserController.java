package ast.projects.appbudget.controllers;

import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.UserRepository;
import ast.projects.appbudget.views.BudgetAppView;

/**
 * This class serves as a controller in the Model-View-Controller pattern,
 * handling interactions related to users between the user interface
 * (BudgetAppView) and the data repository (UserRepository).
 */
public class UserController {

	private UserRepository userRepository;
	private BudgetAppView budgetAppView;

	/**
	 * Constructor for UserController.
	 * 
	 * @param budgetAppView  The view for the budget app.
	 * @param userRepository The repository for users.
	 */
	public UserController(BudgetAppView budgetAppView, UserRepository userRepository) {
		this.budgetAppView = budgetAppView;
		this.userRepository = userRepository;
	}

	/**
	 * This method retrieves all users from the repository and refreshes the view
	 * with the retrieved data. In case of an error, an error message is displayed
	 * in the view.
	 */
	public void allUsers() {
		try {
			budgetAppView.refreshUsersList(userRepository.findAll());
			budgetAppView.resetUserErrorMessage();
		} catch (Exception e) {
			budgetAppView.showUserErrorMessage("Error fetching users");
		}
	}

	/**
	 * This method adds a new user to the repository and refreshes the view with the
	 * updated data. In case of an error, an error message is displayed in the view.
	 * 
	 * @param user The user to add.
	 */
	public void addUser(User user) {
		try {
			userRepository.save(user);
			budgetAppView.refreshUsersList(userRepository.findAll());
			budgetAppView.resetUserErrorMessage();
		} catch (Exception e) {
			budgetAppView.showUserErrorMessage("Error adding new user");
		}
	}

	/**
	 * This method deletes a user from the repository and refreshes the view with
	 * the updated data. In case of an error, an error message is displayed in the
	 * view.
	 * 
	 * @param user The user to delete.
	 */
	public void deleteUser(User user) {
		try {
			userRepository.delete(user);
			budgetAppView.refreshUsersList(userRepository.findAll());
			budgetAppView.resetUserErrorMessage();
		} catch (Exception e) {
			budgetAppView.showUserErrorMessage("Error deleting user");
		}
	}
}