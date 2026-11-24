package bank.controller;

import bank.dao.UserDAO;
import bank.model.*;
import java.sql.SQLException;
import java.util.List;

public class UserController {
    private UserDAO userDAO;
    private Session session;

    public UserController(UserDAO userDAO, Session session) {
        this.userDAO = userDAO;
        this.session = session;
    }

    public void createUser(String username, String password, String roleName, Integer customerId) {
        session.requireManager();
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new BankException(Messages.FIELDS_REQUIRED);
        }

        Role role;
        try {
            role = Role.valueOf(roleName.trim().toUpperCase());
        } catch (Exception e) {
            throw new BankException(Messages.FIELDS_REQUIRED);
        }

        User user = new User(username.trim(), password, role,
                role == Role.CUSTOMER ? customerId : null);
        try {
            if (userDAO.findByUsername(user.getUsername()) != null) {
                throw new BankException(Messages.USER_EXISTS);
            }
            userDAO.create(user);
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    public void unlock(String username) {
        session.requireManager();
        try {
            User user = userDAO.findByUsername(username == null ? "" : username.trim());
            if (user == null) throw new BankException(Messages.INVALID_LOGIN);
            user.resetFailures();
            userDAO.update(user);
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    public List<User> list() {
        session.requireManager();
        try {
            return userDAO.findAll();
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }
}
