package com.example.speedowin.Match;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedowin.Login.LoginActivity;
import com.example.speedowin.R;
import com.example.speedowin.Wallet.WalletActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AtoZGameActivity extends AppCompatActivity {

    private TextView totalBalanceText, depositCashText, winningCashText;
    private DatabaseReference userRef;
    private String userId;
    private double currentBalance, depositCash, winningAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ato_zgame);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(AtoZGameActivity.this, LoginActivity.class));
            finish();
            return;
        }

        userId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Initialize UI components
        totalBalanceText = findViewById(R.id.totalBalanceText);
        depositCashText = findViewById(R.id.depositCashText);
        winningCashText = findViewById(R.id.winningCashText);

        // Add Refresh Button
        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> loadTotalBalance()); // Refresh the balance

        // Load the total balance and other amounts
        loadTotalBalance();

        // Set up button click listeners
        setupCardButtons();
    }

    private void setupCardButtons() {
        Button freeCard = findViewById(R.id.freeCard);
        Button bid5Card = findViewById(R.id.bid5Card);
        Button bid10Card = findViewById(R.id.bid10Card);
        Button bid20Card = findViewById(R.id.bid20Card);

        freeCard.setOnClickListener(v -> spendAmount(0));
        bid5Card.setOnClickListener(v -> spendAmount(5));
        bid10Card.setOnClickListener(v -> spendAmount(10));
        bid20Card.setOnClickListener(v -> spendAmount(20));
    }

    private void loadTotalBalance() {
        userRef.child("totalBalance").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    currentBalance = snapshot.getValue(Double.class);
                    totalBalanceText.setText("Total Balance: ₹" + currentBalance);
                    Log.d("AtoZGameActivity", "Total Balance fetched: " + currentBalance);
                } else {
                    Log.d("AtoZGameActivity", "Total Balance does not exist.");
                    currentBalance = 0.0;
                    totalBalanceText.setText("Total Balance: ₹" + currentBalance);
                }
            } else {
                Log.e("AtoZGameActivity", "Error fetching balance", task.getException());
                Toast.makeText(AtoZGameActivity.this, "Error fetching balance", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch depositCash and winningAmount...
        userRef.child("depositCash").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    depositCash = snapshot.getValue(Double.class);
                    depositCashText.setText("Deposit Cash: ₹" + depositCash);
                }
            }
        });

        userRef.child("winningAmount").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    winningAmount = snapshot.getValue(Double.class);
                    winningCashText.setText("Winning Amount: ₹" + winningAmount);
                }
            }
        });
    }

    private void spendAmount(double amount) {
        if (currentBalance >= amount) {
            // Deduct from depositCash first, if available; otherwise, deduct from winningAmount
            if (depositCash >= amount) {
                depositCash -= amount;
            } else {
                // If depositCash isn't enough, take the remaining amount from winningAmount
                double remainingAmount = amount - depositCash;
                depositCash = 0;
                winningAmount -= remainingAmount;
            }

            // Update the total balance to reflect the new sum
            currentBalance = depositCash + winningAmount;

            // Update Firebase database with new values
            userRef.child("depositCash").setValue(depositCash);
            userRef.child("winningAmount").setValue(winningAmount);
            userRef.child("totalBalance").setValue(currentBalance).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    totalBalanceText.setText("Total Balance: ₹" + currentBalance);
                    Toast.makeText(AtoZGameActivity.this, "You spent ₹" + amount, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AtoZGameActivity.this, "Error updating balance", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AtoZGameActivity.this, "Insufficient balance!", Toast.LENGTH_SHORT).show();
        }
    }
}
