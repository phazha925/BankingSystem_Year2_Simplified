package bank.view;

import bank.model.BankException;
import bank.model.Messages;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

/** Small helpers shared by all screens (colours, number reading). No business logic here. */
public final class UI {
    private UI() { }

    public static Label statusLabel() {
        Label l = new Label();
        l.setWrapText(true);
        return l;
    }

    public static void error(Label l, String message) {
        l.setStyle("-fx-text-fill: #b00020; -fx-font-weight: bold;");
        l.setText(message);
    }

    public static void ok(Label l, String message) {
        l.setStyle("-fx-text-fill: #1b5e20; -fx-font-weight: bold;");
        l.setText(message);
    }

    public static double number(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (RuntimeException e) {
            throw new BankException(Messages.INVALID_NUMBER);
        }
    }

    public static int integer(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (RuntimeException e) {
            throw new BankException(Messages.INVALID_NUMBER);
        }
    }

    public static final Insets PAGE_PADDING = new Insets(15);
}
