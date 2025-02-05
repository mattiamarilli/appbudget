package ast.projects.appbudget.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.*;
import org.mockito.*;

import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.UserRepository;
import ast.projects.appbudget.views.BudgetAppView;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetAppView budgetAppView;

    @InjectMocks
    private UserController userController;

    private AutoCloseable closeable;

    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    
    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testAddUserSuccess() {
        User user = new User("testname", "testsurname");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        
        userController.addUser(user);

        verify(userRepository).save(user);
        verify(budgetAppView).refreshUsersList(userRepository.findAll());
        verify(budgetAppView).resetUserErrorMessage();
        verify(budgetAppView).clearUserInputs();
    }

    @Test
    public void testAddUserFailure() {
        doThrow(new RuntimeException()).when(userRepository).save(any(User.class));
        
        userController.addUser(new User("testname", "testsurname"));
        
        verify(budgetAppView).showUserErrorMessage("Error adding new user");
    }

    @Test
    public void testAllUserSuccess() {
        List<User> users = Arrays.asList(new User("testname", "testsurname"));
        when(userRepository.findAll()).thenReturn(users);
        
        userController.allUsers();
        
        verify(budgetAppView).refreshUsersList(users);
        verify(budgetAppView).resetUserErrorMessage();
    }
    
    @Test
    public void testAllUserFailure() {
        doThrow(new RuntimeException()).when(userRepository).findAll();
        
        userController.allUsers();
        
        verify(budgetAppView).showUserErrorMessage("Error fetching users");
    }
    
    @Test
    public void testUpdateUserSuccess() {
        User user = new User(1, "name", "surname");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        
        userController.updateUser(user);

        verify(userRepository).update(user);
        verify(budgetAppView).refreshUsersList(userRepository.findAll());
        verify(budgetAppView).resetUserErrorMessage();
        verify(budgetAppView).clearUserInputs();
    }

    @Test
    public void testUpdateUserFailure() {
    	User user = new User(1, "name", "surname");
        doThrow(new RuntimeException()).when(userRepository).update(any(User.class));
        
        userController.updateUser(user);
        
        verify(budgetAppView).showUserErrorMessage("Error updating user");
    }
    
    @Test
    public void testDeleteUserSuccess() {
        User user = new User("testname", "testsurname");
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        
        userController.deleteUser(user);
        
        verify(userRepository).delete(user);
        verify(budgetAppView).refreshUsersList(userRepository.findAll());
        verify(budgetAppView).resetUserErrorMessage();
    }
    
    @Test
    public void testDeleteUserFailure() {
        User user = new User("testname", "testsurname");
        doThrow(new RuntimeException()).when(userRepository).delete(any(User.class));
        
        userController.deleteUser(user);
        
        verify(budgetAppView).showUserErrorMessage("Error deleting user");
    }
}
