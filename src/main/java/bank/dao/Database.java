package bank.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private String url;
    private String user;
    private String password;

    public Database() {
        this(DbConfig.URL, DbConfig.USER, DbConfig.PASSWORD);
    }

    public Database(String url) {
        this(url, DbConfig.USER, DbConfig.PASSWORD);
    }

    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void init() throws SQLException {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        try {
            statement.execute("CREATE TABLE IF NOT EXISTS customers ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "first_name VARCHAR(50) NOT NULL, "
                    + "surname VARCHAR(50) NOT NULL, "
                    + "address VARCHAR(120), "
                    + "phone VARCHAR(20))");

            statement.execute("CREATE TABLE IF NOT EXISTS accounts ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "account_number VARCHAR(20) NOT NULL UNIQUE, "
                    + "customer_id INT NOT NULL, "
                    + "type VARCHAR(12) NOT NULL, "
                    + "balance DECIMAL(15,2) NOT NULL, "
                    + "branch VARCHAR(40), "
                    + "date_opened DATE NOT NULL, "
                    + "status VARCHAR(10) NOT NULL, "
                    + "employer_name VARCHAR(80), "
                    + "employer_address VARCHAR(120), "
                    + "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE)");

            statement.execute("CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "account_id INT NOT NULL, "
                    + "type VARCHAR(12) NOT NULL, "
                    + "amount DECIMAL(15,2) NOT NULL, "
                    + "balance_after DECIMAL(15,2) NOT NULL, "
                    + "created_at TIMESTAMP NOT NULL, "
                    + "narration VARCHAR(120), "
                    + "FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE)");

            statement.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(30) NOT NULL UNIQUE, "
                    + "password VARCHAR(50) NOT NULL, "
                    + "role VARCHAR(10) NOT NULL, "
                    + "failed_attempts INT DEFAULT 0 NOT NULL, "
                    + "customer_id INT)");
        } finally {
            statement.close();
            connection.close();
        }
    }
}
