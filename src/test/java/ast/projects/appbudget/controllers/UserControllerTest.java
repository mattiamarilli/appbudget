package ast.projects.appbudget.controllers;

import static org.junit.Assert.assertEquals;
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

    private static final String TEST_USER_NAME = "John";
    private static final String TEST_USER_SURNAME = "Doe";
    private static final String ERROR_MESSAGE_SAVE_FAILED = "Error adding new user";
    private static final String ERROR_MESSAGE_FETCH_FAILED = "Error fetching users";
    private static final String ERROR_MESSAGE_DELETE_FAILED = "Error deleting user";

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
        User user = new User(TEST_USER_NAME, TEST_USER_SURNAME);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        userController.addUser(TEST_USER_NAME, TEST_USER_SURNAME);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals(TEST_USER_NAME, userCaptor.getValue().getName());
        assertEquals(TEST_USER_SURNAME, userCaptor.getValue().getSurname());

        verify(budgetAppView).refreshUsersList(userRepository.findAll());
        verify(budgetAppView).resetErrorMessage();
    }

    @Test
    public void testAddUserFailure() {
        doThrow(new RuntimeException()).when(userRepository).save(any(User.class));
        userController.addUser(TEST_USER_NAME, TEST_USER_SURNAME);
        verify(budgetAppView).showErrorMessage(ERROR_MESSAGE_SAVE_FAILED);
    }

    @Test
    public void testAllUserSuccess() {
        List<User> users = Arrays.asList(new User(TEST_USER_NAME, TEST_USER_SURNAME));
        when(userRepository.findAll()).thenReturn(users);
        userController.allUsers();
        verify(budgetAppView).refreshUsersList(users);
        verify(budgetAppView).resetErrorMessage();
    }
    
    @Test
    public void testAllUserFailure() {
        doThrow(new RuntimeException()).when(userRepository).findAll();
        userController.allUsers();
        verify(budgetAppView).showErrorMessage(ERROR_MESSAGE_FETCH_FAILED);
    }
    
    @Test
    public void testDeleteUserSuccess() {
        User user = new User(TEST_USER_NAME, TEST_USER_SURNAME);
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        userController.deleteUser(user);
        verify(userRepository).delete(user);
        verify(budgetAppView).refreshUsersList(userRepository.findAll());
        verify(budgetAppView).resetErrorMessage();
    }
    
    @Test
    public void testDeleteUserFailure() {
        User user = new User(TEST_USER_NAME, TEST_USER_SURNAME);
        doThrow(new RuntimeException()).when(userRepository).delete(any(User.class));
        userController.deleteUser(user);
        verify(budgetAppView).showErrorMessage(ERROR_MESSAGE_DELETE_FAILED);
    }
}

