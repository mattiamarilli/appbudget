package ast.projects.appbudget;

import org.apache.logging.log4j.LogManager;

public class App 
{
    public static void main( String[] args )
    {
    	HelloMessage helloMessage = new HelloMessage();
    	LogManager.getLogger().info("This is an info message from src code");
    	System.out.println(helloMessage.getMessage());
    }
}
