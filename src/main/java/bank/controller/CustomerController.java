package bank.controller;

import bank.dao.CustomerDAO;
import bank.model.BankException;
import bank.model.Customer;
import bank.model.Messages;
import java.sql.SQLException;
import java.util.List;

public class CustomerController {
    private CustomerDAO customerDAO;
    private Session session;

    public CustomerController(CustomerDAO customerDAO, Session session) {
        this.customerDAO = customerDAO;
        this.session = session;
    }

    public int register(String firstName, String surname, String address, String phone) {
        session.requireStaff();
        Customer customer = new Customer(firstName, surname, address, phone);
        try {
            customerDAO.create(customer);
            return customer.getId();
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    public List<Customer> list() {
        session.requireStaff();
        try {
            return customerDAO.findAll();
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }

    public Customer find(int id) {
        session.requireStaff();
        try {
            Customer customer = customerDAO.findById(id);
            if (customer == null) throw new BankException(Messages.CUSTOMER_NOT_FOUND);
            return customer;
        } catch (SQLException e) {
            throw new BankException(Messages.SERVICE_UNAVAILABLE);
        }
    }
}
