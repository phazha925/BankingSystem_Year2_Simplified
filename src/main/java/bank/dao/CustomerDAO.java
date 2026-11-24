package bank.dao;

import bank.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private Database db;

    public CustomerDAO(Database db) { this.db = db; }

    public void create(Customer customer) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO customers(first_name, surname, address, phone) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        try {
            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getSurname());
            statement.setString(3, customer.getAddress());
            statement.setString(4, customer.getPhone());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) customer.setId(keys.getInt(1));
            keys.close();
        } finally {
            statement.close();
            connection.close();
        }
    }

    public Customer findById(int id) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?");
        try {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Customer customer = makeCustomer(rs);
                rs.close();
                return customer;
            }
            rs.close();
            return null;
        } finally {
            statement.close();
            connection.close();
        }
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<Customer>();
        Connection connection = db.connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM customers ORDER BY id");
        try {
            while (rs.next()) customers.add(makeCustomer(rs));
        } finally {
            rs.close();
            statement.close();
            connection.close();
        }
        return customers;
    }

    public int count() throws SQLException {
        Connection connection = db.connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM customers");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            rs.close();
            statement.close();
            connection.close();
        }
    }

    public void updateContact(int id, String address, String phone) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE customers SET address = ?, phone = ? WHERE id = ?");
        try {
            statement.setString(1, address);
            statement.setString(2, phone);
            statement.setInt(3, id);
            statement.executeUpdate();
        } finally {
            statement.close();
            connection.close();
        }
    }

    public void delete(int id) throws SQLException {
        Connection connection = db.connect();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM customers WHERE id = ?");
        try {
            statement.setInt(1, id);
            statement.executeUpdate();
        } finally {
            statement.close();
            connection.close();
        }
    }

    private Customer makeCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer(rs.getString("first_name"), rs.getString("surname"),
                rs.getString("address"), rs.getString("phone"));
        c.setId(rs.getInt("id"));
        return c;
    }
}
