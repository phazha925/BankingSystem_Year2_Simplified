package bank.model;

public class Messages {
    public static final String INVALID_LOGIN = "Invalid username or password";
    public static final String ACCOUNT_LOCKED = "Account locked, contact the manager";
    public static final String FIELDS_REQUIRED = "Please fill in all required fields";
    public static final String NOT_AUTHORISED = "You are not allowed to do this";
    public static final String SERVICE_UNAVAILABLE = "Service unavailable, please try again later";
    public static final String INVALID_NUMBER = "Enter a valid number";
    public static final String CUSTOMER_NOT_FOUND = "Customer not found, register the customer first";
    public static final String ACCOUNT_NOT_FOUND = "Account not found";
    public static final String ACCOUNT_CLOSED = "Account is closed";
    public static final String UNKNOWN_TYPE = "Unknown account type";
    public static final String MIN_INVESTMENT = "Investment account requires an initial deposit of at least BWP 500.00";
    public static final String EMPLOYER_REQUIRED = "Cheque account requires the employer name and address";
    public static final String AMOUNT_NOT_POSITIVE = "Amount must be greater than 0";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds";
    public static final String SAVINGS_NO_WITHDRAWAL = "Withdrawals are not allowed on Savings accounts";
    public static final String DEPOSIT_FAILED = "Deposit failed, balance unchanged";
    public static final String WITHDRAWAL_FAILED = "Withdrawal failed, balance unchanged";
    public static final String USER_EXISTS = "Username already exists";
}
