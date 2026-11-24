package bank.controller;

import bank.dao.*;

public class AppContext {
    public Database db;
    public Session session;
    public CustomerDAO customerDAO;
    public AccountDAO accountDAO;
    public TransactionDAO transactionDAO;
    public UserDAO userDAO;
    public LoginController login;
    public CustomerController customers;
    public AccountController accounts;
    public UserController users;

    public AppContext(Database db) {
        this.db = db;
        session = new Session();
        customerDAO = new CustomerDAO(db);
        accountDAO = new AccountDAO(db, customerDAO);
        transactionDAO = new TransactionDAO(db);
        userDAO = new UserDAO(db);
        login = new LoginController(userDAO, session);
        customers = new CustomerController(customerDAO, session);
        accounts = new AccountController(db, accountDAO, customerDAO, transactionDAO, session);
        users = new UserController(userDAO, session);
    }

    public static AppContext create() {
        return new AppContext(new Database());
    }
}
