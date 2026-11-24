package bank.model;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private TransactionType type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime createdAt;
    private String narration;

    public Transaction(TransactionType type, double amount, double balanceAfter, String narration) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = LocalDateTime.now();
        this.narration = narration;
    }

    public Transaction(int id, TransactionType type, double amount, double balanceAfter,
                       LocalDateTime createdAt, String narration) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
        this.narration = narration;
    }

    public int getId() { return id; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getNarration() { return narration; }
}
