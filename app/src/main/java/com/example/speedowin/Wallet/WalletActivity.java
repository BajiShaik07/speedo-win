package com.example.speedowin.Wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedowin.MainActivity;
import com.example.speedowin.R;
import com.example.speedowin.Splash.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalletActivity extends AppCompatActivity {

    private TextView totalBalanceView, depositCashView, winningCashView;
    private Button addMoneyButton, contactUsButton, refreshWalletButton,backBtn;
    private DatabaseReference userRef;
    private double depositCash = 0.0, winningAmount = 0.0, spentAmount = 0.0, totalBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        totalBalanceView = findViewById(R.id.totalBalance);
        depositCashView = findViewById(R.id.addCash);
        winningCashView = findViewById(R.id.winningCash);
        addMoneyButton = findViewById(R.id.addMoneyButton);
        contactUsButton = findViewById(R.id.contactUsButton);
        refreshWalletButton = findViewById(R.id.refreshWalletButton);
        backBtn = findViewById(R.id.backBtn);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_wallet); // Set "Home" as the default selected item

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_wallet) {
                return true; // Already on Home, do nothing
            } else if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(WalletActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(WalletActivity.this, ProfileActivity.class));
                return true;
            } else {
                return false; // Default case if no matching item found
            }
        });


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        fetchWalletData();

        refreshWalletButton.setOnClickListener(v -> fetchWalletData());

        addMoneyButton.setOnClickListener(v -> {
            startActivity(new Intent(WalletActivity.this, AddCashActivity.class));
            finish();
        });

        contactUsButton.setOnClickListener(v -> {
            if (winningAmount >= 50) { // Assuming a cost of ₹50
                spendAmount(50);
            } else {
                Toast.makeText(WalletActivity.this, "Insufficient winning amount.", Toast.LENGTH_SHORT).show();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Calls the onBackPressed method to finish the activity
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchWalletData();
    }

    private void fetchWalletData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch the latest values from Firebase
                    Double fetchedDepositCash = snapshot.child("depositCash").getValue(Double.class);
                    Double fetchedWinningAmount = snapshot.child("winningAmount").getValue(Double.class);

                    // Ensure fetched values are not null before assigning
                    depositCash = (fetchedDepositCash != null) ? fetchedDepositCash : 0.0;
                    winningAmount = (fetchedWinningAmount != null) ? fetchedWinningAmount : 0.0;

                    // Update the total balance
                    totalBalance = depositCash + winningAmount;
                    totalBalanceView.setText("Total Balance: ₹" + totalBalance);
                    depositCashView.setText("Deposit Cash: ₹" + depositCash);
                    winningCashView.setText("Winning Amount: ₹" + winningAmount);

                    // Also update the total balance in Firebase
                    userRef.child("totalBalance").setValue(totalBalance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WalletActivity.this, "Failed to load wallet data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateTotalBalance() {
        totalBalance = depositCash + winningAmount - spentAmount; // Subtract spent amount
        totalBalanceView.setText("Total Balance: ₹" + totalBalance); // Update UI
        depositCashView.setText("Deposit Cash: ₹" + depositCash);
        winningCashView.setText("Winning Amount: ₹" + winningAmount);
        userRef.child("totalBalance").setValue(totalBalance); // Update in Firebase
    }

    private void updateDepositCash(double newDepositCash) {
        userRef.child("depositCash").setValue(newDepositCash).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                depositCashView.setText("Deposit Cash: ₹" + newDepositCash);
                Toast.makeText(WalletActivity.this, "Deposit Cash Updated.", Toast.LENGTH_SHORT).show();
                fetchWalletData(); // Refresh data
            } else {
                Toast.makeText(WalletActivity.this, "Error updating Deposit Cash.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void spendAmount(double amount) {
        winningAmount -= amount;
        userRef.child("winningAmount").setValue(winningAmount).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(WalletActivity.this, "Spent ₹" + amount, Toast.LENGTH_SHORT).show();
                fetchWalletData(); // Refresh data after spending
            } else {
                Toast.makeText(WalletActivity.this, "Error spending amount.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
