package bank.view;

import bank.controller.AppContext;
import bank.model.BankException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Boundary class: shows the log-in screen and passes the input to LoginController. */
public class LoginView {
    private final AppContext ctx;
    private final Stage stage;

    public LoginView(AppContext ctx, Stage stage) { this.ctx = ctx; this.stage = stage; }

    public void show() {
        Label title = new Label("Banking System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Button login = new Button("Log in");
        login.setDefaultButton(true);
        Label status = UI.statusLabel();
        Label hint = new Label("First run: manager1 / manager123   teller1 / teller123   cust1 / cust123");
        hint.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");

        login.setOnAction(e -> {
            try {
                ctx.login.login(username.getText(), password.getText());
                new MainView(ctx, stage).show();
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
                password.clear();
            }
        });

        VBox box = new VBox(10, title, username, password, login, status, hint);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setMaxWidth(320);
        VBox root = new VBox(box);
        root.setAlignment(Pos.CENTER);
        stage.setTitle("Banking System - Log in");
        stage.setScene(new Scene(root, 480, 380));
        stage.show();
    }
}
