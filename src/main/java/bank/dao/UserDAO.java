package bank.dao;

import bank.model.Role;
import bank.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Database db;

    public UserDAO(Database db) { this.db = db; }

    public void create(User user) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users(username, password, role, failed_attempts, customer_id) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().name());
            statement.setInt(4, user.getFailedAttempts());
            if (user.getCustomerId() == null) statement.setNull(5, Types.INTEGER);
            else statement.setInt(5, user.getCustomerId());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) user.setId(keys.getInt(1));
            keys.close();
        } finally {
            statement.close();
            connection.close();
        }
    }

    public User findByUsername(String username) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        try {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                User user = makeUser(rs);
                rs.close();
                return user;
            }
            rs.close();
            return null;
        } finally {
            statement.close();
            connection.close();
        }
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<User>();
        Connection connection = db.connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM users ORDER BY id");
        try {
            while (rs.next()) users.add(makeUser(rs));
        } finally {
            rs.close();
            statement.close();
            connection.close();
        }
        return users;
    }

    public int count() throws SQLException {
        Connection connection = db.connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            rs.close();
            statement.close();
            connection.close();
        }
    }

    public void update(User user) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET failed_attempts = ? WHERE id = ?");
        try {
            statement.setInt(1, user.getFailedAttempts());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
        } finally {
            statement.close();
            connection.close();
        }
    }

    public void delete(int id) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
        try {
            statement.setInt(1, id);
            statement.executeUpdate();
        } finally {
            statement.close();
            connection.close();
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int customerId = rs.getInt("customer_id");
        Integer id = rs.wasNull() ? null : Integer.valueOf(customerId);
        return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                Role.valueOf(rs.getString("role")), rs.getInt("failed_attempts"), id);
    }
}
