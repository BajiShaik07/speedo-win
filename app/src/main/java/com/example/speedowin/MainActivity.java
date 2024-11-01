package com.example.speedowin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.speedowin.Login.LoginActivity;
import com.example.speedowin.Match.AtoZGameActivity;
import com.example.speedowin.Splash.ProfileActivity;
import com.example.speedowin.Wallet.WalletActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ImageView profileIcon;
    private TextView gameTitle, totalBalance;
    private DatabaseReference userRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        userId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        profileIcon = findViewById(R.id.profileIcon);
        gameTitle = findViewById(R.id.gameTitle);
        totalBalance = findViewById(R.id.totalBalanceM);
        CardView cardGameTitle = findViewById(R.id.cardGameTitle);
        CardView cardDailyBonus = findViewById(R.id.cardDailyBonus);

        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            Toast.makeText(MainActivity.this, "Profile Clicked", Toast.LENGTH_SHORT).show();
        });

        totalBalance.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WalletActivity.class));
            Toast.makeText(MainActivity.this, "Wallet Clicked", Toast.LENGTH_SHORT).show();
        });

        cardGameTitle.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AtoZGameActivity.class));
        });

        cardDailyBonus.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Daily Bonus Clicked", Toast.LENGTH_SHORT).show();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home); // Set "Home" as the default selected item

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true; // Already on Home, do nothing
            } else if (item.getItemId() == R.id.nav_wallet) {
                startActivity(new Intent(MainActivity.this, WalletActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            } else {
                return false; // Default case if no matching item found
            }
        });
    }
}
