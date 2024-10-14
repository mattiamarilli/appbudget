/*
 * package ast.projects.appbudget.models;
 * 
 * import static org.assertj.core.api.Assertions.assertThat; import static
 * org.junit.Assert.*;
 * 
 * import org.junit.Test;
 * 
 * public class UserTest {
 * 
 * private static final String VALID_NAME = "Mario"; private static final String
 * VALID_SURNAME = "Rossi"; private static final String NULL_NAME = null;
 * private static final String NULL_SURNAME = null; private static final String
 * EMPTY_STRING = ""; private static final String WHITE_SPACE = " "; private
 * static final String NAME_NULL_ERROR = "Name or surname cannot be null";
 * private static final String NAME_EMPTY_ERROR =
 * "Name or surname cannot be empty"; private static final String
 * EXPECTED_TO_STRING = "John Doe"; private static final String TEST_NAME =
 * "John"; private static final String TEST_SURNAME = "Doe";
 * 
 * @Test public void testNameShouldNotBeNull() { IllegalArgumentException
 * exception = assertThrows(IllegalArgumentException.class, () -> { new
 * User(NULL_NAME, VALID_SURNAME); }); assertEquals(NAME_NULL_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testSurnameShouldNotBeNull() { IllegalArgumentException
 * exception = assertThrows(IllegalArgumentException.class, () -> { new
 * User(VALID_NAME, NULL_SURNAME); }); assertEquals(NAME_NULL_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testNameShouldNotBeEmpty() { IllegalArgumentException
 * exception = assertThrows(IllegalArgumentException.class, () -> { new
 * User(EMPTY_STRING, VALID_SURNAME); }); assertEquals(NAME_EMPTY_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testSurnameShouldNotBeEmpty() { IllegalArgumentException
 * exception = assertThrows(IllegalArgumentException.class, () -> { new
 * User(VALID_NAME, EMPTY_STRING); }); assertEquals(NAME_EMPTY_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testSetNameShouldNotBeNull() { User user = new
 * User(VALID_NAME, VALID_SURNAME); IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> { user.setName(NULL_NAME);
 * }); assertEquals(NAME_NULL_ERROR, exception.getMessage()); }
 * 
 * @Test public void testSetNameShouldNotBeEmpty() { User user = new
 * User(VALID_NAME, VALID_SURNAME); IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> {
 * user.setName(EMPTY_STRING); }); assertEquals(NAME_EMPTY_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testSetSurnameShouldNotBeNull() { User user = new
 * User(VALID_NAME, VALID_SURNAME); IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> {
 * user.setSurname(NULL_SURNAME); }); assertEquals(NAME_NULL_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testSetSurnameShouldNotBeEmpty() { User user = new
 * User(VALID_NAME, VALID_SURNAME); IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> {
 * user.setSurname(EMPTY_STRING); }); assertEquals(NAME_EMPTY_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testSetNameShouldNotBeWhiteSpaceOnly() { User user = new
 * User(VALID_NAME, VALID_SURNAME); IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> {
 * user.setName(WHITE_SPACE); }); assertEquals(NAME_EMPTY_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testSetSurnameShouldNotBeWhiteSpaceOnly() { User user = new
 * User(VALID_NAME, VALID_SURNAME); IllegalArgumentException exception =
 * assertThrows(IllegalArgumentException.class, () -> {
 * user.setSurname(WHITE_SPACE); }); assertEquals(NAME_EMPTY_ERROR,
 * exception.getMessage()); }
 * 
 * @Test public void testToString() { User user = new User(TEST_NAME,
 * TEST_SURNAME); assertThat(user.toString()).isEqualTo(EXPECTED_TO_STRING); } }
 */