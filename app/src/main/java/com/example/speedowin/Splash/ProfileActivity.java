package com.example.speedowin.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedowin.Login.LoginActivity;
import com.example.speedowin.MainActivity;
import com.example.speedowin.R;
import com.example.speedowin.Wallet.WalletActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameText;
    private TextView upiIdInput;
    private Button logoutButton, backBtn; // Declare backBtn here
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameText = findViewById(R.id.usernameText);
        upiIdInput = findViewById(R.id.upiIdInput);
        logoutButton = findViewById(R.id.logoutButton);
        backBtn = findViewById(R.id.backBtn); // Initialize backBtn here

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        loadUserProfile();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Calls the onBackPressed method to finish the activity
            }
        });

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Set "Profile" as the selected item

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.nav_wallet) {
                    startActivity(new Intent(ProfileActivity.this, WalletActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    return true; // Already on Profile, do nothing
                } else {
                    return false; // Default case if no matching item found
                }
            }
        });
    }

    private void loadUserProfile() {
        userRef.child("username").get().addOnSuccessListener(snapshot -> {
            String username = snapshot.getValue(String.class);
            if (username != null) {
                usernameText.setText(username);
            }
        });
        userRef.child("upiId").get().addOnSuccessListener(snapshot -> {
            String upiId = snapshot.getValue(String.class);
            if (upiId != null) {
                upiIdInput.setText(upiId);
            }
        });
    }
}
