package ast.projects.appbudget.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.Type;
import ast.projects.appbudget.repositories.ExpenseItemRepository;
import ast.projects.appbudget.views.BudgetAppView;

public class ExpenseItemControllerTest {

    @Mock
    private ExpenseItemRepository expenseItemRepository;

    @Mock
    private BudgetAppView budgetAppView;

    @InjectMocks
    private ExpenseItemController expenseItemController;

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
    public void testAllExpenseItemsByBudgetSuccess() {
    	Budget budget = new Budget(1, "title", 1000);
        ExpenseItem expenseItem = new ExpenseItem("Cinema", Type.NEEDS, 10);
        expenseItem.setBudgetId(budget.getId());
        List<ExpenseItem> expenseItems = Arrays.asList(expenseItem);
        when(expenseItemRepository.findByBudgetId(budget.getId())).thenReturn(expenseItems);
        expenseItemController.allExpenseItemsByBudget(budget);
        verify(budgetAppView).refreshExpenseItemsLists(expenseItems);
        verify(budgetAppView).resetExpenseItemErrorMessage();
    }
    
    @Test
    public void testAllExpenseItemsByBudgetFailure() {
    	Budget budget = new Budget(1, "title", 1000);
        doThrow(new RuntimeException()).when(expenseItemRepository).findByBudgetId(1);
        expenseItemController.allExpenseItemsByBudget(budget);
        verify(budgetAppView).showExpenseItemErrorMessage("Error fetching expense items");
    }

    @Test
    public void testAddExpenseItemSuccess() {
        Budget budget = new Budget(1, "title", 1000);
        ExpenseItem expenseItem = new ExpenseItem("Cinema", Type.NEEDS, 10);
        expenseItem.setBudgetId(budget.getId());

        when(expenseItemRepository.findByBudgetId(budget.getId()))
            .thenReturn(Arrays.asList(expenseItem));

        expenseItemController.addExpenseItem(expenseItem);

        verify(expenseItemRepository).save(expenseItem);
        verify(budgetAppView).refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(budget.getId()));
        verify(budgetAppView).resetExpenseItemErrorMessage();
        verify(budgetAppView).clearExpenseItemInputs();
    }

    @Test
    public void testAddExpenseItemFailure() {
        ExpenseItem expenseItem = new ExpenseItem("Cinema", Type.NEEDS, 10);

        doThrow(new RuntimeException()).when(expenseItemRepository).save(any(ExpenseItem.class));

        expenseItemController.addExpenseItem(expenseItem);

        verify(budgetAppView).showExpenseItemErrorMessage("Error adding expense item");
    }

    @Test
    public void testUpdateExpenseItemSuccess() {
        Budget budget = new Budget(1, "title", 1000);
        ExpenseItem expenseItem = new ExpenseItem("Cinema", Type.NEEDS, 10);
        expenseItem.setBudgetId(budget.getId());

        when(expenseItemRepository.findByBudgetId(budget.getId()))
            .thenReturn(Arrays.asList(expenseItem));

        expenseItemController.updateExpenseItem(expenseItem);

        verify(expenseItemRepository).update(expenseItem);
        verify(budgetAppView).refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(budget.getId()));
        verify(budgetAppView).resetExpenseItemErrorMessage();
        verify(budgetAppView).clearExpenseItemInputs();
    }

    @Test
    public void testUpdateExpenseItemFailure() {
        ExpenseItem expenseItem = new ExpenseItem("Cinema", Type.NEEDS, 10);

        doThrow(new RuntimeException()).when(expenseItemRepository).update(any(ExpenseItem.class));

        expenseItemController.updateExpenseItem(expenseItem);

        verify(budgetAppView).showExpenseItemErrorMessage("Error updating expense item");
    }

    @Test
    public void testDeleteExpenseItemSuccess() {
        Budget budget = new Budget(1, "title", 1000);
        ExpenseItem expenseItem = new ExpenseItem("Cinema", Type.NEEDS, 10);
        expenseItem.setBudgetId(budget.getId());

        when(expenseItemRepository.findByBudgetId(budget.getId()))
            .thenReturn(Arrays.asList(expenseItem));

        expenseItemController.deleteExpenseItem(expenseItem);

        verify(expenseItemRepository).delete(expenseItem);
        verify(budgetAppView).refreshExpenseItemsLists(expenseItemRepository.findByBudgetId(budget.getId()));
        verify(budgetAppView).resetExpenseItemErrorMessage();
        verify(budgetAppView).clearExpenseItemInputs();
    }

    @Test
    public void testDeleteExpenseItemFailure() {
        ExpenseItem expenseItem = new ExpenseItem("Cinema", Type.NEEDS, 10);

        doThrow(new RuntimeException()).when(expenseItemRepository).delete(any(ExpenseItem.class));

        expenseItemController.deleteExpenseItem(expenseItem);

        verify(budgetAppView).showExpenseItemErrorMessage("Error deleting expense item");
    }
}
