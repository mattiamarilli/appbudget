package ast.projects.appbudget.models;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class BudgetTest {

    @Test
    public void testToStringShouldReturnTitleAndIncomesWhenTitleIsNotNull() {
        Budget budget = new Budget("Monthly Budget", 3000.50);
        String result = budget.toString();
        assertThat(result).isEqualTo("Monthly Budget - 3000.5$");
    }

    @Test
    public void testToStringShouldReturnErrorMessageWhenTitleIsNull() {
        Budget budget = new Budget(null, 3000.50);
        String result = budget.toString();
        assertThat(result).isEqualTo("Titol not valid");
    }
}

