package bank.model;

public class User {
    public static final int MAX_FAILED_ATTEMPTS = 3;

    private int id;
    private String username;
    private String password;
    private Role role;
    private int failedAttempts;
    private Integer customerId;

    public User(int id, String username, String password, Role role, int failedAttempts, Integer customerId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.failedAttempts = failedAttempts;
        this.customerId = customerId;
    }

    public User(String username, String password, Role role, Integer customerId) {
        this(0, username, password, role, 0, customerId);
    }

    public boolean authenticate(String enteredPassword) {
        return password != null && password.equals(enteredPassword);
    }

    public boolean isLocked() { return failedAttempts >= MAX_FAILED_ATTEMPTS; }
    public void registerFailure() { failedAttempts++; }
    public void resetFailures() { failedAttempts = 0; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public int getFailedAttempts() { return failedAttempts; }
    public Integer getCustomerId() { return customerId; }
}
