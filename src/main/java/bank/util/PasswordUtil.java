package bank.util;

public class PasswordUtil {
    private PasswordUtil() { }

    public static boolean checkPassword(String entered, String saved) {
        return entered != null && entered.equals(saved);
    }
}
