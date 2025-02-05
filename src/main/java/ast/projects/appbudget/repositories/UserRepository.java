package ast.projects.appbudget.repositories;

import java.util.List;

import ast.projects.appbudget.models.User;

public interface UserRepository {

	public List<User> findAll();

	public void save(User user);

	public void delete(User user);
	
	public void update(User user);

}
