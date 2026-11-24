package bank.model;

public class ChequeAccount extends Account implements Withdrawable {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(Customer owner, String accountNumber, String branch, double initialDeposit,
                         String employerName, String employerAddress) {
        super(checkEmployer(owner, employerName, employerAddress), accountNumber, branch, initialDeposit);
        this.employerName = employerName.trim();
        this.employerAddress = employerAddress.trim();
    }

    private static Customer checkEmployer(Customer owner, String name, String address) {
        if (Customer.isBlank(name) || Customer.isBlank(address)) {
            throw new BankException(Messages.EMPLOYER_REQUIRED);
        }
        return owner;
    }

    public static ChequeAccount restore(Customer owner, String accountNumber, String branch,
                                        String employerName, String employerAddress) {
        return new ChequeAccount(owner, accountNumber, branch, 0, employerName, employerAddress);
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }

    @Override
    public String getAccountType() { return "CHEQUE"; }

    @Override
    public Transaction withdraw(double amount) { return withdrawMoney(amount); }
}
