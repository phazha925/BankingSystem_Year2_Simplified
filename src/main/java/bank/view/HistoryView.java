package bank.view;

import bank.controller.AppContext;
import bank.model.BankException;
import bank.model.Transaction;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/** FR-14: transaction history of one account. */
public class HistoryView extends VBox {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HistoryView(AppContext ctx) {
        setSpacing(8);
        setPadding(UI.PAGE_PADDING);

        TextField account = new TextField(); account.setPromptText("Account number");
        Button show = new Button("Show history");
        Label status = UI.statusLabel();

        TableView<Transaction> table = new TableView<>();
        table.getColumns().add(column("Date", t -> t.getCreatedAt().format(FORMAT)));
        table.getColumns().add(column("Type", t -> t.getType().name()));
        table.getColumns().add(column("Amount (BWP)", t -> String.format("%.2f", t.getAmount())));
        table.getColumns().add(column("Balance after", t -> String.format("%.2f", t.getBalanceAfter())));
        table.getColumns().add(column("Narration", Transaction::getNarration));

        show.setOnAction(e -> {
            try {
                table.setItems(FXCollections.observableArrayList(ctx.accounts.history(account.getText())));
                status.setText("");
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });

        getChildren().addAll(new Label("Transaction history"), account, show, status, table);
    }

    private static TableColumn<Transaction, String> column(String title, Function<Transaction, String> getter) {
        TableColumn<Transaction, String> col = new TableColumn<>(title);
        col.setCellValueFactory(d -> new SimpleStringProperty(getter.apply(d.getValue())));
        return col;
    }
}
