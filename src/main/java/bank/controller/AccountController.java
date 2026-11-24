package bank.controller;

import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dao.Database;
import bank.dao.TransactionDAO;
import bank.model.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountController {
    private Database db;
    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private TransactionDAO transactionDAO;
    private Session session;

    public AccountController(Database db, AccountDAO accountDAO, CustomerDAO customerDAO,
                             TransactionDAO transactionDAO, Session session) {
        this.db = db;
        this.accountDAO = accountDAO;
        this.customerDAO = customerDAO;
        this.transactionDAO = transactionDAO;
        this.session = session;
    }

    public String openAccount(int customerId, String type, double initialDeposit, String branch,
                              String employerName, String employerAddress) {
        session.requireStaff();
        try {
            Customer owner = customerDAO.findById(customerId);
            if (owner == null) throw new BankException(Messages.CUSTOMER_NOT_FOUND);

            String kind = type == null ? "" : type.trim().toUpperCase();
            String number = newAccountNumber(kind);
            Account account;

            if (kind.equals("SAVINGS")) {
                account = new SavingsAccount(owner, number, branch, initialDeposit);
            } else if (kind.equals("INVESTMENT")) {
                account = new InvestmentAccount(owner, number, branch, initialDeposit);
            } else if (kind.equals("CHEQUE")) {
                account = new ChequeAccount(owner, number, branch, initialDeposit, employerName, employerAddress);
            } else {
                throw new BankException(Messages.UNKNOWN_TYPE);
            }

            accountDAO.create(account);
            if (initialDeposit > 0) {
                transactionDAO.create(account.getId(),
                        new Transaction(TransactionType.DEPOSIT, initialDeposit,
                                account.getBalance(), "Opening deposit"));
            }
            return number;
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    private String newAccountNumber(String type) throws SQLException {
        String prefix = "ACC";
        if (type.equals("SAVINGS")) prefix = "SAV";
        else if (type.equals("INVESTMENT")) prefix = "INV";
        else if (type.equals("CHEQUE")) prefix = "CHQ";
        return prefix + String.format("%08d", accountDAO.nextSequence());
    }

    public String deposit(String accountNumber, double amount) {
        try {
            Account account = load(accountNumber);
            authorise(account);
            Transaction transaction = account.deposit(amount);
            save(account, transaction);
            return receipt("Deposit", transaction, account);
        } catch (SQLException e) {
            throw new BankException(Messages.DEPOSIT_FAILED);
        }
    }

    public String withdraw(String accountNumber, double amount) {
        try {
            Account account = load(accountNumber);
            authorise(account);
            if (!(account instanceof Withdrawable)) {
                throw new BankException(Messages.SAVINGS_NO_WITHDRAWAL);
            }
            Withdrawable withdrawable = (Withdrawable) account;
            Transaction transaction = withdrawable.withdraw(amount);
            save(account, transaction);
            return receipt("Withdrawal", transaction, account);
        } catch (SQLException e) {
            throw new BankException(Messages.WITHDRAWAL_FAILED);
        }
    }

    public Account getAccount(String accountNumber) {
        try {
            Account account = load(accountNumber);
            authorise(account);
            return account;
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    public List<Account> accountsOfCustomer(int customerId) {
        try {
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) throw new BankException(Messages.CUSTOMER_NOT_FOUND);
            List<Account> accounts = accountDAO.findByCustomer(customer);
            List<Account> allowed = new ArrayList<Account>();
            for (Account account : accounts) {
                authorise(account);
                allowed.add(account);
            }
            return allowed;
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    public List<Transaction> history(String accountNumber) {
        try {
            Account account = load(accountNumber);
            authorise(account);
            return transactionDAO.findByAccount(account.getId());
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    public String runMonthEndInterest() {
        session.requireManager();
        int paid = 0;
        int skipped = 0;
        double total = 0;

        try {
            List<Account> accounts = accountDAO.findAllActive();
            for (Account account : accounts) {
                if (!(account instanceof InterestBearing)) {
                    skipped++;
                    continue;
                }
                if (account.getBalance() <= 0 || transactionDAO.hasInterestThisMonth(account.getId())) {
                    skipped++;
                    continue;
                }

                InterestBearing interestAccount = (InterestBearing) account;
                Transaction transaction = interestAccount.payInterest();
                if (transaction != null) {
                    save(account, transaction);
                    paid++;
                    total += transaction.getAmount();
                } else {
                    skipped++;
                }
            }
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }

        return String.format("Interest run complete. Paid: %d, skipped: %d. Total interest: BWP %.2f",
                paid, skipped, total);
    }

    private Account load(String accountNumber) throws SQLException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new BankException(Messages.FIELDS_REQUIRED);
        }
        Account account = accountDAO.findByNumber(accountNumber.trim().toUpperCase());
        if (account == null) throw new BankException(Messages.ACCOUNT_NOT_FOUND);
        return account;
    }

    private void authorise(Account account) {
        User user = session.require();
        if (user.getRole() == Role.CUSTOMER) {
            if (user.getCustomerId() == null || user.getCustomerId().intValue() != account.getOwner().getId()) {
                throw new BankException(Messages.NOT_AUTHORISED);
            }
        }
    }

    private void save(Account account, Transaction transaction) throws SQLException {
        accountDAO.update(account);
        transactionDAO.create(account.getId(), transaction);
    }

    private String receipt(String action, Transaction transaction, Account account) {
        return String.format("%s of BWP %.2f on %s successful. New balance: BWP %.2f",
                action, transaction.getAmount(), account.getAccountNumber(), account.getBalance());
    }
}
