package bank.view;

import bank.controller.AppContext;
import bank.model.BankException;
import bank.model.User;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** FR-16: the manager creates users and unlocks locked users. */
public class UserView extends VBox {
    public UserView(AppContext ctx) {
        setSpacing(8);
        setPadding(UI.PAGE_PADDING);

        TextField username = new TextField(); username.setPromptText("Username");
        PasswordField password = new PasswordField(); password.setPromptText("Password");
        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("TELLER", "MANAGER", "CUSTOMER");
        role.setValue("TELLER");
        TextField customerId = new TextField(); customerId.setPromptText("Customer ID (CUSTOMER users only)");
        customerId.disableProperty().bind(role.valueProperty().isNotEqualTo("CUSTOMER"));
        Button create = new Button("Create user");
        Button unlock = new Button("Unlock username");
        Button list = new Button("List users");
        Label status = UI.statusLabel();
        TextArea output = new TextArea();
        output.setEditable(false);

        create.setOnAction(e -> {
            try {
                Integer cid = customerId.getText().isBlank() ? null : UI.integer(customerId.getText());
                ctx.users.createUser(username.getText(), password.getText(), role.getValue(), cid);
                UI.ok(status, "User created");
                password.clear();
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });
        unlock.setOnAction(e -> {
            try {
                ctx.users.unlock(username.getText());
                UI.ok(status, "User unlocked");
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });
        list.setOnAction(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (User u : ctx.users.list())
                    sb.append(u.getUsername()).append("  ").append(u.getRole())
                      .append(u.isLocked() ? "  LOCKED" : "").append('\n');
                output.setText(sb.toString());
                status.setText("");
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });

        getChildren().addAll(new Label("Manage users"), username, password, role, customerId,
                new HBox(8, create, unlock, list), status, output);
    }
}
