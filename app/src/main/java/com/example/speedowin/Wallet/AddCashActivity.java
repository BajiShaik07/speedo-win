package com.example.speedowin.Wallet;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedowin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddCashActivity extends AppCompatActivity {

    private EditText amountInput, bankingNameInput, transactionIdInput, utrInput, transactionTimeInput;
    private Button addCashButton;
    private ProgressBar progressBar;

    // Initialize Firebase Authentication and Database
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cash); // Update the layout file name accordingly

        // Initialize views
        amountInput = findViewById(R.id.amount);
        bankingNameInput = findViewById(R.id.bankingNameInput);
        transactionIdInput = findViewById(R.id.transactionIdInput);
        utrInput = findViewById(R.id.utrInput);
        transactionTimeInput = findViewById(R.id.transactionTimeInput);
        addCashButton = findViewById(R.id.addCashButton);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase Authentication and Database Reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Set to Users

        // Set OnClickListener for the Add Cash button
        addCashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCash();
            }
        });
    }

    private void addCash() {
        // Get the current user
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "You must be logged in to make a request.", Toast.LENGTH_SHORT).show();
            return;
        }

        String amount = amountInput.getText().toString().trim();
        String bankingName = bankingNameInput.getText().toString().trim();
        String transactionId = transactionIdInput.getText().toString().trim();
        String utr = utrInput.getText().toString().trim();
        String transactionTime = transactionTimeInput.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(bankingName) || TextUtils.isEmpty(transactionId) ||
                TextUtils.isEmpty(utr) || TextUtils.isEmpty(transactionTime)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if amount is greater than 50
        if (Integer.parseInt(amount) <= 50) {
            Toast.makeText(this, "Enter an amount greater than 50", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Create a new user entry or update existing user entry with cash request details
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("amount", amount);
        userUpdates.put("bankingName", bankingName);
        userUpdates.put("transactionId", transactionId);
        userUpdates.put("utr", utr);
        userUpdates.put("transactionTime", transactionTime);
        userUpdates.put("status", "pending"); // Set status to pending for admin approval

        // Save the request to Firebase under the current user ID
        databaseReference.child(userId).updateChildren(userUpdates)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(AddCashActivity.this, "Cash request submitted for approval.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddCashActivity.this, "Failed to submit request. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
