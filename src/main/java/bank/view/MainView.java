package bank.view;

import bank.controller.AppContext;
import bank.model.Role;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/** Main window. Which tabs appear depends on the role of the logged-in user. */
public class MainView {
    private final AppContext ctx;
    private final Stage stage;

    public MainView(AppContext ctx, Stage stage) { this.ctx = ctx; this.stage = stage; }

    public void show() {
        Role role = ctx.session.getUser().getRole();
        TabPane tabs = new TabPane();
        if (role != Role.CUSTOMER) {
            tabs.getTabs().add(tab("Customers", new CustomerView(ctx)));
            tabs.getTabs().add(tab("Open Account", new OpenAccountView(ctx)));
        }
        tabs.getTabs().add(tab("Deposit / Withdraw", new TransactionView(ctx)));
        tabs.getTabs().add(tab("History", new HistoryView(ctx)));
        if (role == Role.MANAGER) {
            tabs.getTabs().add(tab("Interest", new InterestView(ctx)));
            tabs.getTabs().add(tab("Users", new UserView(ctx)));
        }

        Label who = new Label("Logged in as " + ctx.session.getUser().getUsername() + " (" + role + ")");
        Button logout = new Button("Log out");
        logout.setOnAction(e -> {
            ctx.login.logout();
            new LoginView(ctx, stage).show();
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox top = new HBox(10, who, spacer, logout);
        top.setPadding(new Insets(8, 12, 8, 12));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(tabs);
        stage.setTitle("Banking System");
        stage.setScene(new Scene(root, 820, 600));
        stage.show();
    }

    private Tab tab(String title, javafx.scene.Node content) {
        Tab t = new Tab(title, content);
        t.setClosable(false);
        return t;
    }
}
