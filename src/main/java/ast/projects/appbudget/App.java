package ast.projects.appbudget;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class App {
	//private static final Logger logger = LogManager.getLogger(App.class);

	public static void main(String[] args) throws InterruptedException {
		String url = "jdbc:mariadb://mariadb:3306/appbudgetdb";
		String username = "root";
		String password = "rootpassword";

		try (
				Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT 'Pippo' AS message")) {

			if (resultSet.next()) {
				System.out.println(resultSet.getString("message"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
