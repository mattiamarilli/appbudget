package ast.project.appbudget.utils;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TextFieldsValidatorUtilsTest {

    private static final String NULL_VALUE = null;
    private static final String EMPTY_VALUE = "";
    private static final String ONLY_SPACES_VALUE = "   ";
    private static final String VALID_NAME = "John";
    private static final String VALID_SURNAME = "Doe";
    private static final String VALID_BUDGET_TITLE = "Monthly Budget";
    private static final String VALID_EXPENSE_TITLE = "Groceries";
    private static final String NON_NUMERIC_VALUE = "abc";
    private static final String NEGATIVE_VALUE = "-10";
    private static final String ZERO_VALUE = "0";
    private static final String LESS_THAN_ONE_VALUE = "0.99";
    private static final String VALID_INCOME_VALUE = "10.5";
    private static final String VALID_EXPENSE_AMOUNT = "25.75";
    private static final String INVALID_SPACED_VALUE = "23 23 .2";

    @Test
    public void testValidateUserNameTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateUserNameTextField(NULL_VALUE)).isFalse();
    }

    @Test
    public void testValidateUserNameTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateUserNameTextField(EMPTY_VALUE)).isFalse();
    }

    @Test
    public void testValidateUserNameTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateUserNameTextField(ONLY_SPACES_VALUE)).isFalse();
    }

    @Test
    public void testValidateUserNameTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateUserNameTextField(VALID_NAME)).isTrue();
    }

    @Test
    public void testValidateUserSurnameTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateUserSurnameTextField(NULL_VALUE)).isFalse();
    }

    @Test
    public void testValidateUserSurnameTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateUserSurnameTextField(EMPTY_VALUE)).isFalse();
    }

    @Test
    public void testValidateUserSurnameTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateUserSurnameTextField(ONLY_SPACES_VALUE)).isFalse();
    }

    @Test
    public void testValidateUserSurnameTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateUserSurnameTextField(VALID_SURNAME)).isTrue();
    }

    @Test
    public void testValidateBudgetTitleTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetTitleTextField(NULL_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetTitleTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetTitleTextField(EMPTY_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetTitleTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateBudgetTitleTextField(ONLY_SPACES_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetTitleTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetTitleTextField(VALID_BUDGET_TITLE)).isTrue();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(NULL_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(EMPTY_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(ONLY_SPACES_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithNonNumericValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(NON_NUMERIC_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithNegativeValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(NEGATIVE_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithZeroValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(ZERO_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithLessThanOneValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(LESS_THAN_ONE_VALUE)).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(VALID_INCOME_VALUE)).isTrue();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithInvalidSpacedValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(INVALID_SPACED_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemTitleTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemTitleTextField(NULL_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemTitleTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemTitleTextField(EMPTY_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemTitleTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemTitleTextField(ONLY_SPACES_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemTitleTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemTitleTextField(VALID_EXPENSE_TITLE)).isTrue();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(NULL_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(EMPTY_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(ONLY_SPACES_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithNonNumericValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(NON_NUMERIC_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithNegativeValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(NEGATIVE_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithZeroValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(ZERO_VALUE)).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(VALID_EXPENSE_AMOUNT)).isTrue();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithInvalidSpacedValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(INVALID_SPACED_VALUE)).isFalse();
    }
}
