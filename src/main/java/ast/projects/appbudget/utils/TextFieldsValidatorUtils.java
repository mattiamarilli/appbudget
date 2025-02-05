package ast.projects.appbudget.utils;

/**
 * This utility class provides methods for validating the content of various text fields 
 * used within the AppBudget application. 
 * 
 * These validation checks are crucial for ensuring data integrity and user experience.
 */
public class TextFieldsValidatorUtils {

    /**
     * Private constructor to prevent instantiation of this utility class. 
     * This class is intended to be used statically.
     */
    private TextFieldsValidatorUtils() {}

    /**
     * Validates the username text field.
     * 
     * @param value The value of the username field.
     * @return True if the username is not null and not empty after trimming whitespace, 
     *         otherwise false.
     */
    public static boolean validateUserNameTextField(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates the user surname text field.
     * 
     * @param value The value of the surname field.
     * @return True if the surname is not null and not empty after trimming whitespace, 
     *         otherwise false.
     */
    public static boolean validateUserSurnameTextField(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates the budget title text field.
     * 
     * @param value The value of the budget title field.
     * @return True if the budget title is not null and not empty after trimming whitespace, 
     *         otherwise false.
     */
    public static boolean validateBudgetTitleTextField(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates the budget incomes text field.
     * 
     * @param value The value of the budget incomes field.
     * @return True if the value is a valid double and greater than or equal to 1.0, 
     *         otherwise false.
     */
    public static boolean validateBudgetIncomesTextField(String value) {
        try {
            double income = Double.parseDouble(value);
            return income >= 1.0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates the expense item title text field.
     * 
     * @param value The value of the expense item title field.
     * @return True if the expense item title is not null and not empty after trimming whitespace, 
     *         otherwise false.
     */
    public static boolean validateExpenseItemTitleTextField(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates the expense item amount text field.
     * 
     * @param value The value of the expense item amount field.
     * @return True if the value is a valid double and greater than 0, 
     *         otherwise false.
     */
    public static boolean validateExpenseItemAmountTextField(String value) {
        try {
            double amount = Double.parseDouble(value);
            return amount > 0;
        } catch (Exception e) {
            return false;
        }
    }
}