package bank.tools;

import bank.dao.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Run this class (right-click > Run 'OpenH2Console.main()') to look inside the database in your browser.
 * The program can be running at the same time because the URL uses AUTO_SERVER=TRUE.
 */
public class OpenH2Console {
    public static void main(String[] args) throws Exception {
        Connection c = DriverManager.getConnection(DbConfig.URL, DbConfig.USER, DbConfig.PASSWORD);
        org.h2.tools.Server.startWebServer(c);
    }
}
