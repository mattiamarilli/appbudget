package ast.projects.appbudget.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    public void testAddExpenseItemSuccess() {
        ExpenseItem expenseItem = new ExpenseItem("Cinema",Type.NEEDS,10);
        when(expenseItemRepository.findAll()).thenReturn(Arrays.asList(expenseItem));
        expenseItemController.addExpenseItem(expenseItem);

        verify(expenseItemRepository).save(expenseItem);
        verify(budgetAppView).refreshExpenseItemsLists(expenseItemRepository.findAll());
        verify(budgetAppView).resetExpenseItemErrorMessage();
    }

    @Test
    public void testAddExpenseItemFailure() {
    	ExpenseItem expenseItem = new ExpenseItem("Cinema",Type.NEEDS,10);
        doThrow(new RuntimeException()).when(expenseItemRepository).save(any(ExpenseItem.class));
        expenseItemController.addExpenseItem(expenseItem);
        verify(budgetAppView).showExpenseItemErrorMessage("Error adding expense item");
    }
    
    @Test
    public void testUpdateExpenseItemSuccess() {
    	ExpenseItem expenseItem = new ExpenseItem("Cinema",Type.NEEDS,10);
        when(expenseItemRepository.findAll()).thenReturn(Arrays.asList(expenseItem));
        expenseItemController.updateExpenseItem(expenseItem);

        verify(expenseItemRepository).update(expenseItem);
        verify(budgetAppView).refreshExpenseItemsLists(expenseItemRepository.findAll());
        verify(budgetAppView).resetExpenseItemErrorMessage();
    }

    @Test
    public void testUpdateExpenseItemFailure() {
    	ExpenseItem expenseItem = new ExpenseItem("Cinema",Type.NEEDS,10);
        doThrow(new RuntimeException()).when(expenseItemRepository).update(any(ExpenseItem.class));
        expenseItemController.updateExpenseItem(expenseItem);
        verify(budgetAppView).showExpenseItemErrorMessage("Error updating expense item");
    }

    
    @Test
    public void testDeleteExpenseItemSuccess() {
    	ExpenseItem expenseItem = new ExpenseItem("Cinema",Type.NEEDS,10);
        when(expenseItemRepository.findAll()).thenReturn(Arrays.asList(expenseItem));
        expenseItemController.deleteExpenseItem(expenseItem);
        verify(expenseItemRepository).delete(expenseItem);
        verify(budgetAppView).refreshExpenseItemsLists(expenseItemRepository.findAll());
        verify(budgetAppView).resetExpenseItemErrorMessage();
    }
    
    @Test
    public void testDeleteExpenseItemFailure() {
    	ExpenseItem expenseItem = new ExpenseItem("Cinema",Type.NEEDS,10);
        doThrow(new RuntimeException()).when(expenseItemRepository).delete(any(ExpenseItem.class));
        expenseItemController.deleteExpenseItem(expenseItem);
        verify(budgetAppView).showExpenseItemErrorMessage("Error deleting expense item");
    }

}
