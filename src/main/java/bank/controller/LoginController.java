package bank.controller;

import bank.dao.UserDAO;
import bank.model.BankException;
import bank.model.Messages;
import bank.model.User;
import java.sql.SQLException;

public class LoginController {
    private UserDAO userDAO;
    private Session session;

    public LoginController(UserDAO userDAO, Session session) {
        this.userDAO = userDAO;
        this.session = session;
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new BankException(Messages.FIELDS_REQUIRED);
        }

        try {
            User user = userDAO.findByUsername(username.trim());
            if (user == null) throw new BankException(Messages.INVALID_LOGIN);
            if (user.isLocked()) throw new BankException(Messages.ACCOUNT_LOCKED);

            if (!user.authenticate(password)) {
                user.registerFailure();
                userDAO.update(user);
                if (user.isLocked()) throw new BankException(Messages.ACCOUNT_LOCKED);
                throw new BankException(Messages.INVALID_LOGIN);
            }

            user.resetFailures();
            userDAO.update(user);
            session.login(user);
            return user;
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    public void logout() { session.logout(); }
}
