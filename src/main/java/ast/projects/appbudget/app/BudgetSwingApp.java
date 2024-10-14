package ast.projects.appbudget.app;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import ast.projects.appbudget.controllers.UserController;
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;
import ast.projects.appbudget.views.BudgetAppSwingView;

public class BudgetSwingApp {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
            	ClassLoader loader = Thread.currentThread().getContextClassLoader();
				InputStream input = loader.getResourceAsStream("app.properties");

				Properties prop = new Properties();

				prop.load(input);
				prop.putAll(System.getProperties());

				String dbHost = System.getProperty("app.db_host", "db");
				String dbPort = System.getProperty("app.db_port", "3306");
				String dbUsername = System.getProperty("app.mariadb_username", "testuser");
				String dbPassword = System.getProperty("app.mariadb_password", "testpassword");

				String url = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/";
				String sqlFilePath = "initializer.sql";

				initDB(url, "root", "", sqlFilePath);
				

				SessionFactory factory = new Configuration()
						.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
						.setProperty("hibernate.connection.url",
								"jdbc:mariadb://" + dbHost + ":" + dbPort + "/appbudget")
						.setProperty("hibernate.connection.username", dbUsername)
						.setProperty("hibernate.connection.password", dbPassword)
						.setProperty("hibernate.hbm2ddl.auto", "update")
						.setProperty("hibernate.show_sql", "true")
						.addAnnotatedClass(User.class)
						.buildSessionFactory();
				
                UserRepositorySqlImplementation usrRepo = new UserRepositorySqlImplementation(factory);
                BudgetAppSwingView view = new BudgetAppSwingView();
                UserController userController = new UserController(view, usrRepo);

                view.setUserController(userController);
                view.setVisible(true);
                System.out.println("App stated");

            } catch (Exception e) {
                e.printStackTrace();
            }
            
        });
    }

    private static void initDB(String url, String username, String password, String sqlFileName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             InputStream inputStream = loader.getResourceAsStream(sqlFileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new FileNotFoundException("File " + sqlFileName + " not found in resources");
            }

            String sql;
            while ((sql = reader.readLine()) != null) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

}
