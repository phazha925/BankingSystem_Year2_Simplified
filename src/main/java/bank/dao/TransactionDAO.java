package bank.dao;

import bank.model.Transaction;
import bank.model.TransactionType;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private Database db;

    public TransactionDAO(Database db) { this.db = db; }

    public void create(int accountId, Transaction transaction) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO transactions(account_id, type, amount, balance_after, created_at, narration) VALUES(?,?,?,?,?,?)");
        try {
            statement.setInt(1, accountId);
            statement.setString(2, transaction.getType().name());
            statement.setDouble(3, transaction.getAmount());
            statement.setDouble(4, transaction.getBalanceAfter());
            statement.setTimestamp(5, Timestamp.valueOf(transaction.getCreatedAt()));
            statement.setString(6, transaction.getNarration());
            statement.executeUpdate();
        } finally {
            statement.close();
            connection.close();
        }
    }

    public List<Transaction> findByAccount(int accountId) throws SQLException {
        List<Transaction> list = new ArrayList<Transaction>();
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM transactions WHERE account_id = ? ORDER BY id");
        try {
            statement.setInt(1, accountId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(rs.getInt("id"),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getDouble("amount"), rs.getDouble("balance_after"),
                        rs.getTimestamp("created_at").toLocalDateTime(), rs.getString("narration")));
            }
            rs.close();
        } finally {
            statement.close();
            connection.close();
        }
        return list;
    }

    public boolean hasInterestThisMonth(int accountId) throws SQLException {
        LocalDate first = LocalDate.now().withDayOfMonth(1);
        LocalDate next = first.plusMonths(1);
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM transactions WHERE account_id = ? AND type = 'INTEREST' AND created_at >= ? AND created_at < ?");
        try {
            statement.setInt(1, accountId);
            statement.setTimestamp(2, Timestamp.valueOf(first.atStartOfDay()));
            statement.setTimestamp(3, Timestamp.valueOf(next.atStartOfDay()));
            ResultSet rs = statement.executeQuery();
            rs.next();
            boolean found = rs.getInt(1) > 0;
            rs.close();
            return found;
        } finally {
            statement.close();
            connection.close();
        }
    }

    public int count() throws SQLException {
        Connection connection = db.connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM transactions");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            rs.close();
            statement.close();
            connection.close();
        }
    }
}
