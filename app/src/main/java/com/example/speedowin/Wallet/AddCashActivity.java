package com.example.speedowin.Wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedowin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCashActivity extends AppCompatActivity {

    private EditText bankingNameEditText;
    private EditText upiIdEditText;
    private EditText transactionIdEditText;
    private EditText utrEditText;
    private EditText transactionTimeEditText;
    private Button addCashButton;

    private DatabaseReference userRef;
    private DatabaseReference transactionsRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cash);

        bankingNameEditText = findViewById(R.id.bankingName);
        upiIdEditText = findViewById(R.id.upiId);
        transactionIdEditText = findViewById(R.id.transactionId);
        utrEditText = findViewById(R.id.utr);
        transactionTimeEditText = findViewById(R.id.transactionTime);
        addCashButton = findViewById(R.id.addCashButton);

        // Get the current user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // Get user ID
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId); // Reference to user
            transactionsRef = FirebaseDatabase.getInstance().getReference("Transactions"); // Reference to transactions
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the user is not authenticated
        }

        addCashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCash();
            }
        });
    }

    private void addCash() {
        String bankingName = bankingNameEditText.getText().toString().trim();
        String upiId = upiIdEditText.getText().toString().trim();
        String transactionId = transactionIdEditText.getText().toString().trim();
        String utr = utrEditText.getText().toString().trim();
        String transactionTime = transactionTimeEditText.getText().toString().trim();

        // Validate inputs
        if (bankingName.isEmpty() || upiId.isEmpty() || transactionId.isEmpty() || utr.isEmpty() || transactionTime.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a transaction object with a pending approval status
        Transaction transaction = new Transaction(bankingName, upiId, transactionId, utr, transactionTime, "pending");

        // Save the transaction to the user's record in the Realtime Database
        userRef.child("addcash").push().setValue(transaction) // Save in user-specific addcash node
                .addOnSuccessListener(aVoid -> {
                    // Save the transaction also in a general Transactions node
                    transactionsRef.push().setValue(transaction)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(AddCashActivity.this, "Transaction added successfully! Waiting for admin approval.", Toast.LENGTH_SHORT).show();
                                clearFields();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddCashActivity.this, "Failed to save transaction in Transactions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddCashActivity.this, "Failed to add transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        bankingNameEditText.setText("");
        upiIdEditText.setText("");
        transactionIdEditText.setText("");
        utrEditText.setText("");
        transactionTimeEditText.setText("");
    }

    // Transaction class
    public static class Transaction {
        String bankingName;
        String upiId;
        String transactionId;
        String utr;
        String time;
        String status;

        public Transaction(String bankingName, String upiId, String transactionId, String utr, String time, String status) {
            this.bankingName = bankingName;
            this.upiId = upiId;
            this.transactionId = transactionId;
            this.utr = utr;
            this.time = time;
            this.status = status;
        }
    }
}
