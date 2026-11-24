package bank.view;

import bank.controller.AppContext;
import bank.model.BankException;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** UC-03 and UC-04: deposit and withdraw. The screen only collects input and shows the result. */
public class TransactionView extends VBox {
    public TransactionView(AppContext ctx) {
        setSpacing(8);
        setPadding(UI.PAGE_PADDING);

        TextField account = new TextField(); account.setPromptText("Account number e.g. SAV00000001");
        TextField amount = new TextField();  amount.setPromptText("Amount (BWP)");
        Button deposit = new Button("Deposit");
        Button withdraw = new Button("Withdraw");
        Button balance = new Button("Check balance");
        Label status = UI.statusLabel();

        deposit.setOnAction(e -> {
            try {
                UI.ok(status, ctx.accounts.deposit(account.getText(), UI.number(amount.getText())));
                amount.clear();
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });
        withdraw.setOnAction(e -> {
            try {
                UI.ok(status, ctx.accounts.withdraw(account.getText(), UI.number(amount.getText())));
                amount.clear();
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });
        balance.setOnAction(e -> {
            try {
                UI.ok(status, ctx.accounts.getAccount(account.getText()).toString());
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });

        getChildren().addAll(new Label("Deposit or withdraw"), account, amount, new HBox(8, deposit, withdraw, balance), status);
    }
}
