package bank.model;

public class InvestmentAccount extends Account implements InterestBearing, Withdrawable {
    public static final double MONTHLY_RATE = 0.05;
    public static final double MIN_OPENING_DEPOSIT = 500.00;

    public InvestmentAccount(Customer owner, String accountNumber, String branch, double initialDeposit) {
        super(owner, accountNumber, branch, checkMinimum(initialDeposit));
    }

    public static InvestmentAccount restore(Customer owner, String accountNumber, String branch) {
        return new InvestmentAccount(owner, accountNumber, branch, 500);
    }

    private static double checkMinimum(double amount) {
        if (amount < MIN_OPENING_DEPOSIT) throw new BankException(Messages.MIN_INVESTMENT);
        return amount;
    }

    @Override
    public String getAccountType() { return "INVESTMENT"; }

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

    @Override
    public Transaction withdraw(double amount) { return withdrawMoney(amount); }
}
