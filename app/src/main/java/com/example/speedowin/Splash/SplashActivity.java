package com.example.speedowin.Splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.speedowin.Login.LoginActivity;
import com.example.speedowin.MainActivity; // Import MainActivity
import com.example.speedowin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show splash screen layout here
        setContentView(R.layout.activity_splash);

        // Check if the user is logged in
        new Thread(() -> {
            try {
                Thread.sleep(2000);

                // Check if the user is authenticated
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent;
                if (currentUser != null) {
                    // User is logged in, redirect to MainActivity
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // User is not logged in, redirect to LoginActivity
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
