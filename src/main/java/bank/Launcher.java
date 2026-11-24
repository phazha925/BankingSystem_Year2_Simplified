package bank;

import bank.view.BankApp;

/**
 * Start the program from THIS class in IntelliJ (right-click > Run 'Launcher.main()').
 * A separate launcher avoids the "JavaFX runtime components are missing" error.
 */
public class Launcher {
    public static void main(String[] args) {
        javafx.application.Application.launch(BankApp.class, args);
    }
}
