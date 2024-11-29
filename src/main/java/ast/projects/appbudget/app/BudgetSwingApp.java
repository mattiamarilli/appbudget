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

import org.apache.logging.log4j.LogManager;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import ast.projects.appbudget.controllers.BudgetController;
import ast.projects.appbudget.controllers.ExpenseItemController;
import ast.projects.appbudget.controllers.UserController;
import ast.projects.appbudget.models.Budget;
import ast.projects.appbudget.models.ExpenseItem;
import ast.projects.appbudget.models.User;
import ast.projects.appbudget.repositories.BudgetRepositorySqlImplementation;
import ast.projects.appbudget.repositories.ExpenseItemRepositorySqlImplementation;
import ast.projects.appbudget.repositories.UserRepositorySqlImplementation;
import ast.projects.appbudget.views.BudgetAppSwingView;

public class BudgetSwingApp {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
            	
				String dbHost = System.getProperty("app.db_host", "localhost");
				String dbPort = System.getProperty("app.db_port", "3306");
				String dbUsername = System.getProperty("app.mariadb_username", "testuser");
				String dbPassword = System.getProperty("app.mariadb_password", "testpassword");

				String url = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/";
				String sqlFilePath = "initializer.sql";

				initDB(url, "root", "rootpassword", sqlFilePath);
				
				SessionFactory factory = new Configuration()
						.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
						.setProperty("hibernate.connection.url",
								"jdbc:mariadb://" + dbHost + ":" + dbPort + "/appbudget")
						.setProperty("hibernate.connection.username", dbUsername)
						.setProperty("hibernate.connection.password", dbPassword)
						.setProperty("hibernate.hbm2ddl.auto", "update")
						.setProperty("hibernate.show_sql", "true")
						.addAnnotatedClass(User.class)
						.addAnnotatedClass(Budget.class)
						.addAnnotatedClass(ExpenseItem.class)
						.buildSessionFactory();
				
                UserRepositorySqlImplementation userRepository = new UserRepositorySqlImplementation(factory);
                BudgetRepositorySqlImplementation budgetRepository = new BudgetRepositorySqlImplementation(factory);
                ExpenseItemRepositorySqlImplementation expenseItemRepository = new ExpenseItemRepositorySqlImplementation(factory);
                
                BudgetAppSwingView view = new BudgetAppSwingView();
                UserController userController = new UserController(view, userRepository);
                BudgetController budgetController = new BudgetController(view,budgetRepository);
                ExpenseItemController expenseItemController = new ExpenseItemController(view,expenseItemRepository);

                view.setUserController(userController);
                view.setBudgetController(budgetController);
                view.setExpenseItemController(expenseItemController);
                view.setVisible(true);
                LogManager.getLogger().info("App stated");

            } catch (Exception e) {
            	LogManager.getLogger().debug(e.getStackTrace());
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
        	LogManager.getLogger().debug(e.getStackTrace());
        }
    }

}
