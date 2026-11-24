package bank.view;

import bank.controller.AppContext;
import bank.model.BankException;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/** UC-05: the manager starts the month-end interest run (the "System Timer" action). */
public class InterestView extends VBox {
    public InterestView(AppContext ctx) {
        setSpacing(8);
        setPadding(UI.PAGE_PADDING);
        Label info = new Label("Pays 5% to Investment accounts and 0.05% to Savings accounts. "
                + "An account is never paid twice in the same month.");
        info.setWrapText(true);
        Button run = new Button("Run month-end interest");
        Label status = UI.statusLabel();
        run.setOnAction(e -> {
            try {
                UI.ok(status, ctx.accounts.runMonthEndInterest());
            } catch (BankException ex) {
                UI.error(status, ex.getMessage());
            }
        });
        getChildren().addAll(new Label("Month-end interest"), info, run, status);
    }
}
