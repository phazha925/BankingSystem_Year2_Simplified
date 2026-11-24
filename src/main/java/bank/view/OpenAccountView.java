package bank.view;

import bank.controller.AppContext;
import bank.model.Account;
import bank.model.BankException;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/** UC-02: open a Savings, Investment or Cheque account for an existing customer. */
public class OpenAccountView extends VBox {
    public OpenAccountView(AppContext ctx) {
        setSpacing(8);
        setPadding(UI.PAGE_PADDING);

        TextField customerId = new TextField(); customerId.setPromptText("Customer ID");
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("SAVINGS", "INVESTMENT", "CHEQUE");
        type.setValue("SAVINGS");
        TextField branch = new TextField("Main Branch");
        TextField initial = new TextField(); initial.setPromptText("Initial deposit (BWP)");
        TextField employer = new TextField(); employer.setPromptText("Employer name (Cheque only)");
        TextField employerAddress = new TextField(); employerAddress.setPromptText("Employer address (Cheque only)");
        employer.disableProperty().bind(type.valueProperty().isNotEqualTo("CHEQUE"));
        employerAddress.disableProperty().bind(type.valueProperty().isNotEqualTo("CHEQUE"));
        Button open = new Button("Open account");
        Button showAccounts = new Button("Show this customer's accounts");
        Label status = UI.statusLabel();
        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPrefRowCount(8);

        open.setOnAction(e -> {
            try {
                String number = ctx.accounts.openAccount(UI.integer(customerId.getText()), type.getValue(),
                        initial.getText().isBlank() ? 0 : UI.number(initial.getText()), branch.getText(),
                        employer.getText(), employerAddress.getText());
                UI.ok(status, "Account opened. Account number: " + number);
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });
        showAccounts.setOnAction(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (Account a : ctx.accounts.accountsOfCustomer(UI.integer(customerId.getText()))) sb.append(a).append('\n');
                output.setText(sb.length() == 0 ? "This customer has no accounts yet." : sb.toString());
                status.setText("");
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });

        getChildren().addAll(new Label("Open an account"), customerId, type, branch, initial, employer, employerAddress,
                open, showAccounts, status, output);
    }
}
