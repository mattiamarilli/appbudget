package ast.projects.appbudget.views;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ast.projects.appbudget.controllers.UserController;
import ast.projects.appbudget.models.User;

import org.junit.Test;

@RunWith(GUITestRunner.class)
public class UserSwingViewTest extends AssertJSwingJUnitTestCase {

    // Component Names
    private static final String NAME_TEXT_BOX = "nameTextBox";
    private static final String SURNAME_TEXT_BOX = "surnameTextBox";
    private static final String ADD_BUTTON = "Add";
    private static final String OPEN_BUDGETS_BUTTON = "Open Budgets";
    private static final String DELETE_USER_BUTTON = "Delete User";
    private static final String USERS_LIST = "usersList";
    private static final String ERROR_MESSAGE_LABEL = "errorMessageLabel";

    // Labels and Messages
    private static final String LABEL_NAME = "Name";
    private static final String LABEL_SURNAME = "Surname";
    private static final String ERROR_MESSAGE = "error message";
    private static final String EMPTY_STRING = "";

    // Test User Data
    private static final String USER_NAME_1 = "test1name";
    private static final String USER_SURNAME_1 = "test1surname";
    private static final String USER_NAME_2 = "test2name";
    private static final String USER_SURNAME_2 = "test2surname";
    private static final String INPUT_NAME = "testname";
    private static final String INPUT_SURNAME = "testsurname";
    private static final String BLANK_STRING = " ";
    private static final String TEST_NAME = "Mario";
    private static final String TEST_SURNAME = "Rossi";

    private FrameFixture window;
    private BudgetAppSwingView budgetAppView;

    @Mock
    private UserController userController;

    private AutoCloseable closeable;

    @Override
    protected void onSetUp() {
        closeable = MockitoAnnotations.openMocks(this);
        GuiActionRunner.execute(() -> {
            budgetAppView = new BudgetAppSwingView();
            budgetAppView.setUserController(userController);
            budgetAppView.maximizeWindow();
            return budgetAppView;
        });
        window = new FrameFixture(robot(), budgetAppView);
        window.show();
    }

    @Override
    protected void onTearDown() throws Exception {
        closeable.close();
    }

    @Test
    @GUITest
    public void testControlsInitialStates() {
        window.label(JLabelMatcher.withText(LABEL_NAME));
        window.textBox(NAME_TEXT_BOX).requireEnabled();
        window.label(JLabelMatcher.withText(LABEL_SURNAME));
        window.textBox(SURNAME_TEXT_BOX).requireEnabled();
        window.button(JButtonMatcher.withText(ADD_BUTTON)).requireDisabled();
        window.list(USERS_LIST);
        window.button(JButtonMatcher.withText(OPEN_BUDGETS_BUTTON)).requireDisabled();
        window.button(JButtonMatcher.withText(DELETE_USER_BUTTON)).requireDisabled();
        window.label(ERROR_MESSAGE_LABEL).requireText(EMPTY_STRING);
    }

    @Test
    public void testWhenNameAndSurnameAreNonEmptyThenAddButtonShouldBeEnabled() {
        window.textBox(NAME_TEXT_BOX).enterText(TEST_NAME);
        window.textBox(SURNAME_TEXT_BOX).enterText(TEST_SURNAME);
        window.button(JButtonMatcher.withText(ADD_BUTTON)).requireEnabled();
    }

    @Test
    public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
        JTextComponentFixture nameTextBox = window.textBox(NAME_TEXT_BOX);
        JTextComponentFixture surnameTextBox = window.textBox(SURNAME_TEXT_BOX);

        nameTextBox.enterText(INPUT_NAME);
        surnameTextBox.enterText(BLANK_STRING);
        window.button(JButtonMatcher.withText(ADD_BUTTON)).requireDisabled();

        nameTextBox.setText(EMPTY_STRING);
        surnameTextBox.setText(EMPTY_STRING);

        nameTextBox.enterText(BLANK_STRING);
        surnameTextBox.enterText(INPUT_SURNAME);
        window.button(JButtonMatcher.withText(ADD_BUTTON)).requireDisabled();
    }

    @Test
    public void testsShowAllUsersShouldAddUserDescriptionsToTheList() {
        User user1 = new User(1, USER_NAME_1, USER_SURNAME_1);
        User user2 = new User(2, USER_NAME_2, USER_SURNAME_2);

        GuiActionRunner.execute(() -> budgetAppView.refreshUsersList(Arrays.asList(user1, user2)));

        String[] listContents = window.list(USERS_LIST).contents();

        assertThat(listContents).containsExactly(user1.toString(), user2.toString());
    }

    @Test
    public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
        GuiActionRunner.execute(() -> budgetAppView.showErrorMessage(ERROR_MESSAGE));
        window.label(ERROR_MESSAGE_LABEL).requireText(ERROR_MESSAGE);
    }

    @Test
    public void testResetErrorMessageShouldResetTheErrorLabel() {
        GuiActionRunner.execute(() -> budgetAppView.showErrorMessage(ERROR_MESSAGE));
        GuiActionRunner.execute(() -> budgetAppView.resetErrorMessage());
        window.label(ERROR_MESSAGE_LABEL).requireText(EMPTY_STRING);
    }

    @Test
    public void testAddButtonShouldDelegateToSchoolControllerNewStudent() {
        window.textBox(NAME_TEXT_BOX).enterText(INPUT_NAME);
        window.textBox(SURNAME_TEXT_BOX).enterText(INPUT_SURNAME);

        window.button(JButtonMatcher.withText(ADD_BUTTON)).click();

        verify(userController).addUser(INPUT_NAME, INPUT_SURNAME);
    }

    @Test
    public void testDeleteButtonShouldDelegateToSchoolControllerDeleteStudent() {
        User user1 = new User(1, USER_NAME_1, USER_SURNAME_1);
        User user2 = new User(2, USER_NAME_2, USER_SURNAME_2);

        GuiActionRunner.execute(() -> {
            DefaultListModel<User> listUsersModel = budgetAppView.getListUsersModel();
            listUsersModel.addElement(user1);
            listUsersModel.addElement(user2);
        });

        window.list(USERS_LIST).selectItem(1);
        window.button(JButtonMatcher.withText(DELETE_USER_BUTTON)).click();

        verify(userController).deleteUser(user2);
    }

    @Test
    public void testOpenAndDeleteButtonShouldBeEnabledOnlyWhenAUserIsSelected() {
        GuiActionRunner.execute(() -> budgetAppView.getListUsersModel().addElement(new User(1, INPUT_NAME, INPUT_SURNAME)));
        window.list(USERS_LIST).selectItem(0);

        JButtonFixture deleteButton = window.button(JButtonMatcher.withText(DELETE_USER_BUTTON));
        JButtonFixture openButton = window.button(JButtonMatcher.withText(OPEN_BUDGETS_BUTTON));

        deleteButton.requireEnabled();
        openButton.requireEnabled();

        window.list(USERS_LIST).clearSelection();
        deleteButton.requireDisabled();
        openButton.requireDisabled();
    }
}
