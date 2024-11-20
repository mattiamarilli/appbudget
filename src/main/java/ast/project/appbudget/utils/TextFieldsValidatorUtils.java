package ast.project.appbudget.utils;

public class TextFieldsValidatorUtils {

    private TextFieldsValidatorUtils() {}

    public static boolean validateUserNameTextField(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean validateUserSurnameTextField(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean validateBudgetTitleTextField(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean validateBudgetIncomesTextField(String value) {
        try {
            double income = Double.parseDouble(value);
            return income >= 1.0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateExpenseItemTitleTextField(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean validateExpenseItemAmountTextField(String value) {
        try {
            double amount = Double.parseDouble(value);
            return amount > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
