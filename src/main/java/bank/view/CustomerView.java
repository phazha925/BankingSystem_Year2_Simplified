package bank.view;

import bank.controller.AppContext;
import bank.model.BankException;
import bank.model.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/** FR-1: register a customer and list all customers. */
public class CustomerView extends VBox {
    public CustomerView(AppContext ctx) {
        setSpacing(8);
        setPadding(UI.PAGE_PADDING);

        TextField first = new TextField();   first.setPromptText("First name");
        TextField surname = new TextField(); surname.setPromptText("Surname");
        TextField address = new TextField(); address.setPromptText("Address");
        TextField phone = new TextField();   phone.setPromptText("Phone");
        Button register = new Button("Register customer");
        Button refresh = new Button("Refresh list");
        Label status = UI.statusLabel();

        TableView<Customer> table = new TableView<>();
        table.getColumns().add(column("ID", c -> String.valueOf(c.getId())));
        table.getColumns().add(column("First name", Customer::getFirstName));
        table.getColumns().add(column("Surname", Customer::getSurname));
        table.getColumns().add(column("Address", Customer::getAddress));
        table.getColumns().add(column("Phone", Customer::getPhone));

        Runnable load = () -> {
            try {
                table.setItems(FXCollections.observableArrayList(ctx.customers.list()));
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        };
        register.setOnAction(e -> {
            try {
                int id = ctx.customers.register(first.getText(), surname.getText(), address.getText(), phone.getText());
                UI.ok(status, "Customer registered. Customer ID = " + id);
                first.clear(); surname.clear(); address.clear(); phone.clear();
                load.run();
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });
        refresh.setOnAction(e -> load.run());

        getChildren().addAll(new Label("Register a new customer"), first, surname, address, phone, register, status, table);
        load.run();
    }

    private static TableColumn<Customer, String> column(String title, java.util.function.Function<Customer, String> getter) {
        TableColumn<Customer, String> col = new TableColumn<>(title);
        col.setCellValueFactory(d -> new SimpleStringProperty(getter.apply(d.getValue())));
        return col;
    }
}
