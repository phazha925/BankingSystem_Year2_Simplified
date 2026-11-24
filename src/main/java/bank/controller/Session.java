package bank.controller;

import bank.model.BankException;
import bank.model.Messages;
import bank.model.Role;
import bank.model.User;

public class Session {
    private User user;

    public void login(User user) { this.user = user; }
    public void logout() { user = null; }
    public User getUser() { return user; }

    public User require() {
        if (user == null) throw new BankException(Messages.NOT_AUTHORISED);
        return user;
    }

    public void requireStaff() {
        User current = require();
        if (current.getRole() == Role.CUSTOMER) throw new BankException(Messages.NOT_AUTHORISED);
    }

    public void requireManager() {
        if (require().getRole() != Role.MANAGER) throw new BankException(Messages.NOT_AUTHORISED);
    }
}
