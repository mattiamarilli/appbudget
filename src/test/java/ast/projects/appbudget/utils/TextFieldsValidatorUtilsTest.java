package ast.projects.appbudget.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextFieldsValidatorUtilsTest {

    @Test
    public void testValidateUserNameTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateUserNameTextField(null)).isFalse();
    }

    @Test
    public void testValidateUserNameTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateUserNameTextField("")).isFalse();
    }

    @Test
    public void testValidateUserNameTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateUserNameTextField("   ")).isFalse();
    }

    @Test
    public void testValidateUserNameTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateUserNameTextField("John")).isTrue();
    }

    @Test
    public void testValidateUserSurnameTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateUserSurnameTextField(null)).isFalse();
    }

    @Test
    public void testValidateUserSurnameTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateUserSurnameTextField("")).isFalse();
    }

    @Test
    public void testValidateUserSurnameTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateUserSurnameTextField("   ")).isFalse();
    }

    @Test
    public void testValidateUserSurnameTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateUserSurnameTextField("Doe")).isTrue();
    }

    @Test
    public void testValidateBudgetTitleTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetTitleTextField(null)).isFalse();
    }

    @Test
    public void testValidateBudgetTitleTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetTitleTextField("")).isFalse();
    }

    @Test
    public void testValidateBudgetTitleTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateBudgetTitleTextField("   ")).isFalse();
    }

    @Test
    public void testValidateBudgetTitleTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetTitleTextField("Monthly Budget")).isTrue();
    }
    
    @Test
    public void testValidateBudgetIncomesTextFieldvalidValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("1.0")).isTrue();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField(null)).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("")).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("   ")).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithNonNumericValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("abc")).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithNegativeValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("-10")).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithZeroValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("0")).isFalse();
    }
    

    @Test
    public void testValidateBudgetIncomesTextFieldWithLessThanOneValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("0.99")).isFalse();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("10.5")).isTrue();
    }

    @Test
    public void testValidateBudgetIncomesTextFieldWithInvalidSpacedValue() {
        assertThat(TextFieldsValidatorUtils.validateBudgetIncomesTextField("23 23 .2")).isFalse();
    }

    @Test
    public void testValidateExpenseItemTitleTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemTitleTextField(null)).isFalse();
    }

    @Test
    public void testValidateExpenseItemTitleTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemTitleTextField("")).isFalse();
    }

    @Test
    public void testValidateExpenseItemTitleTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemTitleTextField("   ")).isFalse();
    }

    @Test
    public void testValidateExpenseItemTitleTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemTitleTextField("Groceries")).isTrue();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithNullValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField(null)).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithEmptyValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField("")).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithOnlySpaces() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField("   ")).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithNonNumericValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField("abc")).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithNegativeValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField("-10")).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithZeroValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField("0")).isFalse();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithValidValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField("25.75")).isTrue();
    }

    @Test
    public void testValidateExpenseItemAmountTextFieldWithInvalidSpacedValue() {
        assertThat(TextFieldsValidatorUtils.validateExpenseItemAmountTextField("23 23 .2")).isFalse();
    }
}
