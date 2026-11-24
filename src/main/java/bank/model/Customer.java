package bank.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int id;
    private String firstName;
    private String surname;
    private String address;
    private String phone;
    private List<Account> accounts = new ArrayList<Account>();

    public Customer(String firstName, String surname, String address, String phone) {
        if (isBlank(firstName) || isBlank(surname)) {
            throw new BankException(Messages.FIELDS_REQUIRED);
        }
        this.firstName = firstName.trim();
        this.surname = surname.trim();
        this.address = address == null ? "" : address.trim();
        this.phone = phone == null ? "" : phone.trim();
    }

    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() { return accounts; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String fullName() { return firstName + " " + surname; }

    @Override
    public String toString() { return "#" + id + " " + fullName(); }
}
