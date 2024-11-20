package ast.projects.appbudget.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.*;
import org.mockito.*;

import ast.projects.appbudget.models.Budget;
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
    public void testAddBudgetSuccess() {
        Budget budget = new Budget("Maggio 2024", 1000);
        when(budgetRepository.findAll()).thenReturn(Arrays.asList(budget));
        budgetController.addBudget(budget);

        verify(budgetRepository).save(budget);
        verify(budgetAppView).refreshBudgetsList(budgetRepository.findAll());
        verify(budgetAppView).resetBudgetErrorMessage();
    }

    @Test
    public void testAddBudgetFailure() {
    	Budget budget = new Budget("Maggio 2024", 1000);
        doThrow(new RuntimeException()).when(budgetRepository).save(any(Budget.class));
        budgetController.addBudget(budget);
        verify(budgetAppView).showBudgetErrorMessage("Error adding new budget");
    }
    
    @Test
    public void testUpdateBudgetSuccess() {
        Budget budget = new Budget("Maggio 2024", 1000);
        when(budgetRepository.findAll()).thenReturn(Arrays.asList(budget));
        budgetController.updateBudget(budget);

        verify(budgetRepository).update(budget);
        verify(budgetAppView).refreshBudgetsList(budgetRepository.findAll());
        verify(budgetAppView).resetBudgetErrorMessage();
    }

    @Test
    public void testUpdateBudgetFailure() {
    	Budget budget = new Budget("Maggio 2024", 1000);
        doThrow(new RuntimeException()).when(budgetRepository).update(any(Budget.class));
        budgetController.updateBudget(budget);
        verify(budgetAppView).showBudgetErrorMessage("Error updating budget");
    }

    
    @Test
    public void testDeleteBudgetSuccess() {
    	Budget budget = new Budget("Maggio 2024", 1000);
        when(budgetRepository.findAll()).thenReturn(Arrays.asList(budget));
        budgetController.deleteBudget(budget);
        verify(budgetRepository).delete(budget);
        verify(budgetAppView).refreshBudgetsList(budgetRepository.findAll());
        verify(budgetAppView).resetBudgetErrorMessage();
    }
    
    @Test
    public void testDeleteBudgetFailure() {
    	Budget budget = new Budget("Maggio 2024", 1000);
        doThrow(new RuntimeException()).when(budgetRepository).delete(any(Budget.class));
        budgetController.deleteBudget(budget);
        verify(budgetAppView).showBudgetErrorMessage("Error deleting budget");
    }
}

