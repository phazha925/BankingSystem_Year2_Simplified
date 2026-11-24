package bank.view;

import bank.controller.AppContext;
import bank.controller.DemoData;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/** The JavaFX application. It connects to H2, creates the tables, loads sample data once, then shows the log-in. */
public class BankApp extends Application {
    @Override
    public void start(Stage stage) {
        try {
            AppContext ctx = AppContext.create();
            ctx.db.init();              // creates the tables in data/bankdb.mv.db if they do not exist
            DemoData.ensure(ctx);       // default users + 10 sample customers on the very first run
            new LoginView(ctx, stage).show();
        } catch (Exception e) {
            e.printStackTrace();
            stage.setScene(new Scene(new Label("Could not start: " + e.getMessage()), 520, 120));
            stage.show();
        }
    }
}
