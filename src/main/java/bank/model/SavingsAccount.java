package bank.model;

public class SavingsAccount extends Account implements InterestBearing {
    public static final double MONTHLY_RATE = 0.0005;

    public SavingsAccount(Customer owner, String accountNumber, String branch, double initialDeposit) {
        super(owner, accountNumber, branch, initialDeposit);
    }

    public static SavingsAccount restore(Customer owner, String accountNumber, String branch) {
        return new SavingsAccount(owner, accountNumber, branch, 0);
    }

    @Override
    public String getAccountType() { return "SAVINGS"; }

    @Override
    public double calculateMonthlyInterest() {
        return round(getBalance() * MONTHLY_RATE);
    }

    @Override
    public Transaction payInterest() {
        double interest = calculateMonthlyInterest();
        if (interest <= 0) return null;
        checkActive();
        addMoney(interest);
        return new Transaction(TransactionType.INTEREST, interest, getBalance(), "Monthly interest");
    }
}
