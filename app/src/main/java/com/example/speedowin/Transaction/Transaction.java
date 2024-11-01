package com.example.speedowin.Transaction;


public class Transaction {
    private String transactionId;
    private String userId;
    private int amount;
    private String status; // "pending", "approved", "rejected"
    private long timestamp;

    public Transaction() {
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

    public Transaction(String transactionId, String userId, int amount, String status, long timestamp) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

