package bank.model;

public interface Withdrawable {
    Transaction withdraw(double amount);
}
