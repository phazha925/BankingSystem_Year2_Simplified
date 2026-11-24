package bank.model;

import java.time.LocalDate;

public abstract class Account {
    private int id;
    private String accountNumber;
    private double balance;
    private String branch;
    private LocalDate dateOpened;
    private AccountStatus status;
    private Customer owner;

    protected Account(Customer owner, String accountNumber, String branch, double initialBalance) {
        if (owner == null) throw new BankException(Messages.CUSTOMER_NOT_FOUND);
        if (Customer.isBlank(accountNumber)) throw new BankException(Messages.FIELDS_REQUIRED);
        if (initialBalance < 0) throw new BankException(Messages.AMOUNT_NOT_POSITIVE);

        this.owner = owner;
        this.accountNumber = accountNumber;
        this.branch = Customer.isBlank(branch) ? "Main Branch" : branch.trim();
        this.balance = round(initialBalance);
        this.dateOpened = LocalDate.now();
        this.status = AccountStatus.ACTIVE;
        owner.addAccount(this);
    }

    public abstract String getAccountType();

    public Transaction deposit(double amount) {
        return deposit(amount, "Deposit");
    }

    public Transaction deposit(double amount, String narration) {
        checkActive();
        checkPositive(amount);
        balance = round(balance + amount);
        return new Transaction(TransactionType.DEPOSIT, amount, balance, narration);
    }

    protected Transaction withdrawMoney(double amount) {
        return withdrawMoney(amount, "Withdrawal");
    }

    protected Transaction withdrawMoney(double amount, String narration) {
        checkActive();
        checkPositive(amount);
        if (amount > balance) throw new BankException(Messages.INSUFFICIENT_FUNDS);
        balance = round(balance - amount);
        return new Transaction(TransactionType.WITHDRAWAL, amount, balance, narration);
    }

    protected void addMoney(double amount) { balance = round(balance + amount); }

    protected void checkActive() {
        if (status == AccountStatus.CLOSED) throw new BankException(Messages.ACCOUNT_CLOSED);
    }

    protected void checkPositive(double amount) {
        if (amount <= 0) throw new BankException(Messages.AMOUNT_NOT_POSITIVE);
    }

    protected double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public void close() { status = AccountStatus.CLOSED; }

    public void restore(int id, double balance, LocalDate dateOpened, AccountStatus status) {
        this.id = id;
        this.balance = balance;
        this.dateOpened = dateOpened;
        this.status = status;
    }

    public int getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public LocalDate getDateOpened() { return dateOpened; }
    public AccountStatus getStatus() { return status; }
    public Customer getOwner() { return owner; }

    @Override
    public String toString() {
        return getAccountType() + " " + accountNumber + " | " + owner.fullName()
                + " | BWP " + String.format("%.2f", balance) + " | " + status;
    }
}
