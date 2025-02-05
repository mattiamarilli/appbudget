package ast.projects.appbudget.models;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ExpenseItemTest {

    @Test
    public void testToStringShouldReturnTitleAmountAndTypeWhenTitleAndTypeAreNotNull() {
        ExpenseItem expenseItem = new ExpenseItem("Groceries", Type.NEEDS, 100.50);
        String result = expenseItem.toString();
        assertThat(result).isEqualTo("Groceries - 100.5$ - NEEDS");
    }

    @Test
    public void testToStringShouldReturnErrorMessageWhenTitleIsNull() {
        ExpenseItem expenseItem = new ExpenseItem(null, Type.NEEDS, 100.50);
        String result = expenseItem.toString();
        assertThat(result).isEqualTo("Title or type not valid");
    }

    @Test
    public void testToStringShouldReturnErrorMessageWhenTypeIsNull() {
        ExpenseItem expenseItem = new ExpenseItem("Groceries", null, 100.50);
        String result = expenseItem.toString();
        assertThat(result).isEqualTo("Title or type not valid");
    }

    @Test
    public void testToStringShouldReturnErrorMessageWhenBothTitleAndTypeAreNull() {
        ExpenseItem expenseItem = new ExpenseItem(null, null, 100.50);
        String result = expenseItem.toString();
        assertThat(result).isEqualTo("Title or type not valid");
    }
}
