package bank.dao;

import bank.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private Database db;
    private CustomerDAO customers;

    public AccountDAO(Database db, CustomerDAO customers) {
        this.db = db;
        this.customers = customers;
    }

    public void create(Account account) throws SQLException {
        String employer = null;
        String employerAddress = null;
        if (account instanceof ChequeAccount) {
            ChequeAccount cheque = (ChequeAccount) account;
            employer = cheque.getEmployerName();
            employerAddress = cheque.getEmployerAddress();
        }

        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO accounts(account_number, customer_id, type, balance, branch, date_opened, status, employer_name, employer_address) "
                        + "VALUES(?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setString(1, account.getAccountNumber());
            statement.setInt(2, account.getOwner().getId());
            statement.setString(3, account.getAccountType());
            statement.setDouble(4, account.getBalance());
            statement.setString(5, account.getBranch());
            statement.setDate(6, Date.valueOf(account.getDateOpened()));
            statement.setString(7, account.getStatus().name());
            statement.setString(8, employer);
            statement.setString(9, employerAddress);
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) account.restore(keys.getInt(1), account.getBalance(), account.getDateOpened(), account.getStatus());
            keys.close();
        } finally {
            statement.close();
            connection.close();
        }
    }

    public Account findByNumber(String accountNumber) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE account_number = ?");
        try {
            statement.setString(1, accountNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Customer owner = customers.findById(rs.getInt("customer_id"));
                if (owner == null) {
                    rs.close();
                    return null;
                }
                Account account = build(rs, owner);
                rs.close();
                return account;
            }
            rs.close();
            return null;
        } finally {
            statement.close();
            connection.close();
        }
    }

    public List<Account> findByCustomer(Customer owner) throws SQLException {
        List<Account> accounts = new ArrayList<Account>();
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE customer_id = ? ORDER BY id");
        try {
            statement.setInt(1, owner.getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) accounts.add(build(rs, owner));
            rs.close();
        } finally {
            statement.close();
            connection.close();
        }
        return accounts;
    }

    public List<Account> findAllActive() throws SQLException {
        List<Account> accounts = new ArrayList<Account>();
        Connection connection = db.connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM accounts WHERE status = 'ACTIVE' ORDER BY id");
        try {
            while (rs.next()) {
                Customer owner = customers.findById(rs.getInt("customer_id"));
                if (owner != null) accounts.add(build(rs, owner));
            }
        } finally {
            rs.close();
            statement.close();
            connection.close();
        }
        return accounts;
    }

    public int nextSequence() throws SQLException {
        Connection connection = db.connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM accounts");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            rs.close();
            statement.close();
            connection.close();
        }
    }

    public int count() throws SQLException {
        Connection connection = db.connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM accounts");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            rs.close();
            statement.close();
            connection.close();
        }
    }

    public void update(Account account) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET balance = ?, status = ? WHERE id = ?");
        try {
            statement.setDouble(1, account.getBalance());
            statement.setString(2, account.getStatus().name());
            statement.setInt(3, account.getId());
            statement.executeUpdate();
        } finally {
            statement.close();
            connection.close();
        }
    }

    public void delete(int id) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM accounts WHERE id = ?");
        try {
            statement.setInt(1, id);
            statement.executeUpdate();
        } finally {
            statement.close();
            connection.close();
        }
    }

    private Account build(ResultSet rs, Customer owner) throws SQLException {
        String type = rs.getString("type");
        String number = rs.getString("account_number");
        String branch = rs.getString("branch");
        Account account;

        if (type.equals("INVESTMENT")) {
            account = InvestmentAccount.restore(owner, number, branch);
        } else if (type.equals("CHEQUE")) {
            account = ChequeAccount.restore(owner, number, branch,
                    rs.getString("employer_name"), rs.getString("employer_address"));
        } else {
            account = SavingsAccount.restore(owner, number, branch);
        }

        account.restore(rs.getInt("id"), rs.getDouble("balance"),
                rs.getDate("date_opened").toLocalDate(), AccountStatus.valueOf(rs.getString("status")));
        return account;
    }
}
