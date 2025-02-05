package ast.projects.appbudget.models;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class UserTest {

    @Test
    public void testToStringShouldReturnFullNameWhenNameAndSurnameAreNotNull() {
        User user = new User("Mario", "Rossi");
        String result = user.toString();
        assertThat(result).isEqualTo("Mario Rossi");
    }

    @Test
    public void toStringShouldReturnErrorMessageWhenNameIsNull() {
        User user = new User(null, "Rossi");
        String result = user.toString();
        assertThat(result).isEqualTo("Name or surname not valid");
    }

    @Test
    public void testToStringShouldReturnErrorMessageWhenSurnameIsNull() {
        User user = new User("Mario", null);
        String result = user.toString();
        assertThat(result).isEqualTo("Name or surname not valid");
    }

    @Test
    public void testToStringShouldReturnErrorMessageWhenBothNameAndSurnameAreNull() {
        User user = new User(null, null);
        String result = user.toString();
        assertThat(result).isEqualTo("Name or surname not valid");
    }
}
