package ast.projects.appbudget.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.*;
import org.mockito.*;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.BudgetRepository;
import ast.projects.appbudget.views.BudgetAppView;

public class BudgetControllerTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private BudgetAppView budgetAppView;

    @InjectMocks
    private BudgetController budgetController;

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
    public void testAllBudgetsByUserSuccess() {
    	User user = new User(1, "name", "surname");
        Budget budget = new Budget(1, "testtitle", 1000.0);
        budget.setUser(user);
        List<Budget> budgets = Arrays.asList(budget);
        when(budgetRepository.findByUserId(user.getId())).thenReturn(budgets);
        budgetController.allBudgetsByUser(user);
        verify(budgetAppView).refreshBudgetsList(budgets);
        verify(budgetAppView).resetBudgetErrorMessage();
    }
    
    @Test
    public void testAllBudgetsFailure() {
    	User user = new User(1, "name", "surname");
        doThrow(new RuntimeException()).when(budgetRepository).findByUserId(1);
        budgetController.allBudgetsByUser(user);
        verify(budgetAppView).showBudgetErrorMessage("Error fetching budgets");
    }

    @Test
    public void testAddBudgetSuccess() {
        User user = new User(1, "name", "surname");
        Budget budget = new Budget(1, "testtitle", 1000.0);
        budget.setUser(user);
        when(budgetRepository.findByUserId(budget.getUser().getId())).thenReturn(Arrays.asList(budget));
        budgetController.addBudget(budget);

        verify(budgetRepository).save(budget);
        verify(budgetAppView).refreshBudgetsList(budgetRepository.findByUserId(budget.getUser().getId()));
        verify(budgetAppView).resetBudgetErrorMessage();
        verify(budgetAppView).clearBudgetInputs();
    }

    @Test
    public void testAddBudgetFailure() {
        Budget budget = new Budget("testtitle", 1000.0);
        doThrow(new RuntimeException()).when(budgetRepository).save(any(Budget.class));
        budgetController.addBudget(budget);
        verify(budgetAppView).showBudgetErrorMessage("Error adding new budget");
    }

    @Test
    public void testUpdateBudgetSuccess() {
        User user = new User(1, "name", "surname");
        Budget budget = new Budget(1, "testtitle", 1000.0);
        budget.setUser(user);
        when(budgetRepository.findByUserId(budget.getUser().getId())).thenReturn(Arrays.asList(budget));
        budgetController.updateBudget(budget);

        verify(budgetRepository).update(budget);
        verify(budgetAppView).refreshBudgetsList(budgetRepository.findByUserId(budget.getUser().getId()));
        verify(budgetAppView).resetBudgetErrorMessage();
        verify(budgetAppView).clearBudgetInputs();
    }

    @Test
    public void testUpdateBudgetFailure() {
        Budget budget = new Budget("testtitle", 1000.0);
        doThrow(new RuntimeException()).when(budgetRepository).update(any(Budget.class));
        budgetController.updateBudget(budget);
        verify(budgetAppView).showBudgetErrorMessage("Error updating budget");
    }

    @Test
    public void testDeleteBudgetSuccess() {
        User user = new User(1, "name", "surname");
        Budget budget = new Budget(1, "testtitle", 1000.0);
        budget.setUser(user);
        when(budgetRepository.findByUserId(budget.getUser().getId())).thenReturn(Arrays.asList(budget));
        budgetController.deleteBudget(budget);
        verify(budgetRepository).delete(budget);
        verify(budgetAppView).refreshBudgetsList(budgetRepository.findByUserId(budget.getUser().getId()));
        verify(budgetAppView).resetBudgetErrorMessage();
        verify(budgetAppView).clearBudgetInputs();
    }

    @Test
    public void testDeleteBudgetFailure() {
        Budget budget = new Budget("testtitle", 1000.0);
        doThrow(new RuntimeException()).when(budgetRepository).delete(any(Budget.class));
        budgetController.deleteBudget(budget);
        verify(budgetAppView).showBudgetErrorMessage("Error deleting budget");
    }
}
